/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.the.force.thirdparty.druid.sql.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLConstraint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateTableStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableElement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLColumnDefinition;
import org.the.force.thirdparty.druid.util.JdbcConstants;

import java.util.List;

public class SQLCreateTableParser extends SQLDDLParser {

    public SQLCreateTableParser(String sql) {
        super(sql);
    }

    public SQLCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLCreateTableStatement parseCrateTable() {
        List<String> comments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            comments = lexer.readAndResetComments();
        }

        SQLCreateTableStatement stmt = parseCrateTable(true);
        if (comments != null) {
            stmt.addBeforeComment(comments);
        }

        return stmt;
    }

    public SQLCreateTableStatement parseCrateTable(boolean acceptCreate) {
        SQLCreateTableStatement createTable = newCreateStatement();

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                createTable.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        if (identifierEquals("GLOBAL")) {
            lexer.nextToken();

            if (identifierEquals("TEMPORARY")) {
                lexer.nextToken();
                createTable.setType(SQLCreateTableStatement.Type.GLOBAL_TEMPORARY);
            } else {
                throw new ParserException("syntax error " + lexer.token() + " " + lexer.stringVal());
            }
        } else if (lexer.token() == Token.IDENTIFIER && lexer.stringVal().equalsIgnoreCase("LOCAL")) {
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER && lexer.stringVal().equalsIgnoreCase("TEMPORAY")) {
                lexer.nextToken();
                createTable.setType(SQLCreateTableStatement.Type.LOCAL_TEMPORARY);
            } else {
                throw new ParserException("syntax error");
            }
        }

        accept(Token.TABLE);

        createTable.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                Token token = lexer.token();
                if (token == Token.IDENTIFIER
                        && lexer.stringVal().equalsIgnoreCase("SUPPLEMENTAL")
                        && JdbcConstants.ORACLE.equals(dbType)) {
                    this.parseCreateTableSupplementalLogingProps(createTable);
                } else if (token == Token.IDENTIFIER //
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    createTable.getTableElementList().add(column);
                } else if (token == Token.PRIMARY //
                        || token == Token.UNIQUE //
                        || token == Token.CHECK //
                        || token == Token.CONSTRAINT
                        || token == Token.FOREIGN) {
                    SQLConstraint constraint = this.exprParser.parseConstaint();
                    constraint.setParent(createTable);
                    createTable.getTableElementList().add((SQLTableElement) constraint);
                } else if (token == Token.TABLESPACE) {
                    throw new ParserException("TODO " + token);
                } else {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    createTable.getTableElementList().add(column);
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();

                    if (lexer.token() == Token.RPAREN) { // compatible for executor server
                        break;
                    }
                    continue;
                }

                break;
            }

            // while
            // (this.tokenList.current().equals(OracleToken.ConstraintToken)) {
            // parseConstaint(executor.getConstraints());
            //
            // if (this.tokenList.current().equals(OracleToken.CommaToken))
            // ;
            // lexer.nextToken();
            // }

            accept(Token.RPAREN);

            if (identifierEquals("INHERITS")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLName inherits = this.exprParser.name();
                createTable.setInherits(new SQLExprTableSource(inherits));
                accept(Token.RPAREN);
            }
        }

        return createTable;
    }

    protected void parseCreateTableSupplementalLogingProps(SQLCreateTableStatement stmt) {
        throw new ParserException("TODO " + lexer.info());
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new SQLCreateTableStatement(getDbType());
    }
}
