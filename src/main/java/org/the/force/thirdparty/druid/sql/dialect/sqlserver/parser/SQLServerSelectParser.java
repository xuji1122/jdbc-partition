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
package org.the.force.thirdparty.druid.sql.dialect.sqlserver.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLSetQuantifier;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import org.the.force.thirdparty.druid.sql.parser.SQLExprParser;
import org.the.force.thirdparty.druid.sql.parser.SQLSelectParser;
import org.the.force.thirdparty.druid.sql.parser.Token;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.parser.ParserException;

public class SQLServerSelectParser extends SQLSelectParser {

    public SQLServerSelectParser(String sql){
        super(new SQLServerExprParser(sql));
    }

    public SQLServerSelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLSelect select() {
        SQLSelect select = new SQLSelect();

        withSubquery(select);

        select.setQuery(query());
        select.setOrderBy(parseOrderBy());

        if (select.getOrderBy() == null) {
            select.setOrderBy(parseOrderBy());
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();

            if (identifierEquals("BROWSE")) {
                lexer.nextToken();
                select.setForBrowse(true);
            } else if (identifierEquals("XML")) {
                lexer.nextToken();

                for (;;) {
                    if (identifierEquals("AUTO") //
                        || identifierEquals("TYPE") //
                        || identifierEquals("XMLSCHEMA") //
                    ) {
                        select.getForXmlOptions().add(lexer.stringVal());
                        lexer.nextToken();
                    } else if (identifierEquals("ELEMENTS")) {
                        lexer.nextToken();
                        if (identifierEquals("XSINIL")) {
                            lexer.nextToken();
                            select.getForXmlOptions().add("ELEMENTS XSINIL");
                        } else {
                            select.getForXmlOptions().add("ELEMENTS");
                        }
                    } else if (identifierEquals("PATH")) {
                        SQLExpr xmlPath = this.exprParser.expr();
                        select.setXmlPath(xmlPath);
                    } else {
                        break;
                    }

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    } else {
                        break;
                    }
                }
            } else {
                throw new ParserException("syntax error, not support option : " + lexer.token());
            }
        }

        if (identifierEquals("OFFSET")) {
            lexer.nextToken();
            SQLExpr offset = this.expr();

            acceptIdentifier("ROWS");
            select.setOffset(offset);

            if (lexer.token() == Token.FETCH) {
                lexer.nextToken();
                acceptIdentifier("NEXT");

                SQLExpr rowCount = expr();
                acceptIdentifier("ROWS");
                acceptIdentifier("ONLY");
                select.setRowCount(rowCount);
            }
        }

        return select;
    }

    public SQLSelectQuery query() {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        SQLServerSelectQueryBlock queryBlock = new SQLServerSelectQueryBlock();

        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.DISTINCT) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (lexer.token() == Token.ALL) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                lexer.nextToken();
            }

            if (lexer.token() == Token.TOP) {
                SQLServerTop top = this.createExprParser().parseTop();
                queryBlock.setTop(top);
            }

            parseSelectList(queryBlock);
        }

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();

            SQLTableSource into = this.parseTableSource();
            queryBlock.setInto((SQLExprTableSource) into);
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        parseFetchClause(queryBlock);

        return queryRest(queryBlock);
    }

    protected SQLServerExprParser createExprParser() {
        return new SQLServerExprParser(lexer);
    }

    protected SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            accept(Token.LPAREN);

            for (;;) {
                SQLExpr expr = this.expr();
                SQLExprHint hint = new SQLExprHint(expr);
                hint.setParent(tableSource);
                tableSource.getHints().add(hint);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }

            accept(Token.RPAREN);
        }

        return super.parseTableSourceRest(tableSource);
    }
}
