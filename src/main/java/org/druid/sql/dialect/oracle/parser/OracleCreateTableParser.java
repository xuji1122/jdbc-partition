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
package org.druid.sql.dialect.oracle.parser;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLName;
import org.druid.sql.ast.SQLPartition;
import org.druid.sql.ast.SQLPartitionBy;
import org.druid.sql.ast.SQLPartitionByHash;
import org.druid.sql.ast.SQLPartitionByList;
import org.druid.sql.ast.SQLPartitionByRange;
import org.druid.sql.ast.SQLSubPartition;
import org.druid.sql.ast.SQLSubPartitionBy;
import org.druid.sql.ast.SQLSubPartitionByHash;
import org.druid.sql.ast.SQLSubPartitionByList;
import org.druid.sql.ast.expr.SQLIdentifierExpr;
import org.druid.sql.ast.expr.SQLIntegerExpr;
import org.druid.sql.ast.expr.SQLNumericLiteralExpr;
import org.druid.sql.ast.statement.SQLCreateTableStatement;
import org.druid.sql.ast.statement.SQLSelect;
import org.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import org.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import org.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import org.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement.DeferredSegmentCreation;
import org.druid.sql.dialect.oracle.ast.stmt.OracleSupplementalIdKey;
import org.druid.sql.dialect.oracle.ast.stmt.OracleSupplementalLogGrp;
import org.druid.sql.dialect.oracle.parser.OracleExprParser;
import org.druid.sql.dialect.oracle.parser.OracleSelectParser;
import org.druid.sql.parser.Lexer;
import org.druid.sql.parser.ParserException;
import org.druid.sql.parser.SQLCreateTableParser;
import org.druid.sql.parser.Token;

public class OracleCreateTableParser extends SQLCreateTableParser {

    public OracleCreateTableParser(Lexer lexer){
        super(new OracleExprParser(lexer));
    }

    public OracleCreateTableParser(String sql){
        super(new OracleExprParser(sql));
    }

    protected OracleCreateTableStatement newCreateStatement() {
        return new OracleCreateTableStatement();
    }

    public OracleCreateTableStatement parseCrateTable(boolean acceptCreate) {
        OracleCreateTableStatement stmt = (OracleCreateTableStatement) super.parseCrateTable(acceptCreate);

        if (lexer.token() == Token.OF) {
            lexer.nextToken();
            stmt.setOf(this.exprParser.name());

            if (identifierEquals("OIDINDEX")) {
                lexer.nextToken();

                OracleCreateTableStatement.OIDIndex oidIndex = new OracleCreateTableStatement.OIDIndex();

                if (lexer.token() != Token.LPAREN) {
                    oidIndex.setName(this.exprParser.name());
                }
                accept(Token.LPAREN);
                this.getExprParser().parseSegmentAttributes(oidIndex);
                accept(Token.RPAREN);

                stmt.setOidIndex(oidIndex);
            }
        }

        for (;;) {
            this.getExprParser().parseSegmentAttributes(stmt);

            if (identifierEquals("IN_MEMORY_METADATA")) {
                lexer.nextToken();
                stmt.setInMemoryMetadata(true);
                continue;
            } else if (identifierEquals("CURSOR_SPECIFIC_SEGMENT")) {
                lexer.nextToken();
                stmt.setCursorSpecificSegment(true);
                continue;
            } else if (identifierEquals("NOPARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(false);
                continue;
            } else if (lexer.token() == Token.CACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.NOCACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                if (lexer.token() == Token.ROW) {
                    lexer.nextToken();
                    acceptIdentifier("MOVEMENT");
                    stmt.setEnableRowMovement(Boolean.TRUE);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
                //stmt.setEnable(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();
                if (lexer.token() == Token.ROW) {
                    lexer.nextToken();
                    acceptIdentifier("MOVEMENT");
                    stmt.setEnableRowMovement(Boolean.FALSE);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
                //stmt.setEnable(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ON) {
                lexer.nextToken();
                accept(Token.COMMIT);
                stmt.setOnCommit(true);
                continue;
            } else if (identifierEquals("PRESERVE")) {
                lexer.nextToken();
                acceptIdentifier("ROWS");
                stmt.setPreserveRows(true);
                continue;
            } else if (identifierEquals("STORAGE")) {
                OracleStorageClause storage = ((OracleExprParser) this.exprParser).parseStorage();
                stmt.setStorage(storage);
                continue;
            } else if (identifierEquals("ORGANIZATION")) {
                parseOrganization(stmt);
                continue;
            } else if (identifierEquals("CLUSTER")) {
                lexer.nextToken();
                SQLName cluster = this.exprParser.name();
                stmt.setCluster(cluster);
                accept(Token.LPAREN);
                this.exprParser.names(stmt.getClusterColumns(), cluster);
                accept(Token.RPAREN);
                continue;
//            } else if (lexer.token() == Token.STORAGE) {
//                OracleStorageClause storage = ((OracleExprParser) this.exprParser).parseStorage();
//                stmt.setStorage(storage);
//                continue;
            } else if (lexer.token() == Token.LOB) {
                OracleLobStorageClause lobStorage = ((OracleExprParser) this.exprParser).parseLobStorage();
                stmt.setLobStorage(lobStorage);
                continue;
            } else if (lexer.token() == Token.SEGMENT) {
                lexer.nextToken();
                accept(Token.CREATION);
                if (lexer.token() == Token.IMMEDIATE) {
                    lexer.nextToken();
                    stmt.setDeferredSegmentCreation(DeferredSegmentCreation.IMMEDIATE);
                } else {
                    accept(Token.DEFERRED);
                    stmt.setDeferredSegmentCreation(DeferredSegmentCreation.DEFERRED);
                }
                continue;
            } else if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();

                accept(Token.BY);

                if (identifierEquals("RANGE")) {
                    SQLPartitionByRange partitionByRange = partitionByRange();
                    partitionClauseRest(partitionByRange);
                    stmt.setPartitioning(partitionByRange);
                    continue;
                } else if (identifierEquals("HASH")) {
                    SQLPartitionByHash partitionByHash = partitionByHash();
                    partitionClauseRest(partitionByHash);

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        for (;;) {
                            SQLPartition partition = this.getExprParser().parsePartition();
                            partitionByHash.addPartition(partition);
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            } else if (lexer.token() == Token.RPAREN) {
                                lexer.nextToken();
                                break;
                            }
                            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                        }
                    }
                    stmt.setPartitioning(partitionByHash);
                    continue;
                } else if (identifierEquals("LIST")) {
                    SQLPartitionByList partitionByList = partitionByList();
                    partitionClauseRest(partitionByList);
                    stmt.setPartitioning(partitionByList);
                    continue;
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            }
            break;
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            SQLSelect select = new OracleSelectParser(exprParser).select();
            stmt.setSelect(select);
        }

        return stmt;
    }

    private void parseOrganization(OracleCreateTableStatement stmt) {
        OracleCreateTableStatement.Organization organization = new OracleCreateTableStatement.Organization();
        acceptIdentifier("ORGANIZATION");
        if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
            organization.setType("INDEX");
            this.getExprParser().parseSegmentAttributes(organization);

            // index_org_table_clause http://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_7002.htm#i2129638
            if (identifierEquals("PCTTHRESHOLD")) {
                lexer.nextToken();

                if (lexer.token() == Token.LITERAL_INT) {
                    int pctthreshold = ((SQLNumericLiteralExpr) this.exprParser.primary()).getNumber().intValue();
                    organization.setPctthreshold(pctthreshold);
                }
            }
        } else if (identifierEquals("HEAP")) {
            lexer.nextToken();
            organization.setType("HEAP");
            this.getExprParser().parseSegmentAttributes(organization);
        } else if (identifierEquals("EXTERNAL")) {
            lexer.nextToken();
            organization.setType("EXTERNAL");
            accept(Token.LPAREN);

            if (identifierEquals("TYPE")) {
                lexer.nextToken();
                organization.setExternalType(this.exprParser.name());
            }

            accept(Token.DEFAULT);
            acceptIdentifier("DIRECTORY");

            organization.setExternalDirectory(this.exprParser.expr());

            if (identifierEquals("ACCESS")) {
                lexer.nextToken();
                acceptIdentifier("PARAMETERS");

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

                    OracleCreateTableStatement.OracleExternalRecordFormat recordFormat = new OracleCreateTableStatement.OracleExternalRecordFormat();

                    if (identifierEquals("RECORDS")) {
                        lexer.nextToken();


                        if (identifierEquals("DELIMITED")) {
                            lexer.nextToken();
                            accept(Token.BY);

                            if (identifierEquals("NEWLINE")) {
                                lexer.nextToken();
                                recordFormat.setDelimitedBy(new SQLIdentifierExpr("NEWLINE"));
                            } else {
                                throw new ParserException("TODO " + lexer.info());
                            }
                        } else {
                            throw new ParserException("TODO " + lexer.info());
                        }
                    }

                    if (identifierEquals("FIELDS")) {
                        lexer.nextToken();

                        if (identifierEquals("TERMINATED")) {
                            lexer.nextToken();
                            accept(Token.BY);
                            recordFormat.setTerminatedBy(this.exprParser.primary());
                        } else {
                            throw new ParserException("TODO " + lexer.info());
                        }
                    }

                    organization.setExternalDirectoryRecordFormat(recordFormat);
                    accept(Token.RPAREN);
                } else if (lexer.token() == Token.USING) {
                    lexer.nextToken();
                    acceptIdentifier("CLOB");
                    throw new ParserException("TODO " + lexer.info());
                }
            }

            acceptIdentifier("LOCATION");
            accept(Token.LPAREN);
            this.exprParser.exprList(organization.getExternalDirectoryLocation(), organization);
            accept(Token.RPAREN);

            accept(Token.RPAREN);

            if (lexer.token() == Token.REJECT) {
                lexer.nextToken();
                accept(Token.LIMIT);

                organization.setExternalRejectLimit(this.exprParser.primary());
            }
            //
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
        stmt.setOrganization(organization);
    }

    protected SQLPartitionByList partitionByList() {
        acceptIdentifier("LIST");
        SQLPartitionByList partitionByList = new SQLPartitionByList();

        accept(Token.LPAREN);
        partitionByList.setExpr(this.exprParser.expr());
        accept(Token.RPAREN);

        parsePartitionByRest(partitionByList);

        return partitionByList;
    }

    protected SQLPartitionByHash partitionByHash() {
        acceptIdentifier("HASH");
        SQLPartitionByHash partitionByHash = new SQLPartitionByHash();

        if (lexer.token() == Token.KEY) {
            lexer.nextToken();
            partitionByHash.setKey(true);
        }

        accept(Token.LPAREN);
        partitionByHash.setExpr(this.exprParser.expr());
        accept(Token.RPAREN);
        return partitionByHash;
    }

    protected SQLPartitionByRange partitionByRange() {
        acceptIdentifier("RANGE");
        accept(Token.LPAREN);
        SQLPartitionByRange clause = new SQLPartitionByRange();
        for (;;) {
            SQLName column = this.exprParser.name();
            clause.addColumn(column);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }
        accept(Token.RPAREN);

        if (lexer.token() == Token.INTERVAL) {
            lexer.nextToken();
            accept(Token.LPAREN);
            clause.setInterval(this.exprParser.expr());
            accept(Token.RPAREN);
        }

        parsePartitionByRest(clause);

        return clause;
    }

    protected void parsePartitionByRest(SQLPartitionBy clause) {
        if (lexer.token() == Token.STORE) {
            lexer.nextToken();
            accept(Token.IN);
            accept(Token.LPAREN);
            for (;;) {
                SQLName tablespace = this.exprParser.name();
                clause.getStoreIn().add(tablespace);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
            accept(Token.RPAREN);
        }

        if (identifierEquals("SUBPARTITION")) {
            SQLSubPartitionBy subPartitionBy = subPartitionBy();
            clause.setSubPartitionBy(subPartitionBy);
        }


        accept(Token.LPAREN);

        for (;;) {
            SQLPartition partition = this.getExprParser().parsePartition();

            clause.addPartition(partition);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        accept(Token.RPAREN);
    }

    protected void partitionClauseRest(SQLPartitionBy clause) {
        if (identifierEquals("PARTITIONS")) {
            lexer.nextToken();

            SQLIntegerExpr countExpr = this.exprParser.integerExpr();
            clause.setPartitionsCount(countExpr);
        }

        if (lexer.token() == Token.STORE) {
            lexer.nextToken();
            accept(Token.IN);
            accept(Token.LPAREN);
            this.exprParser.names(clause.getStoreIn(), clause);
            accept(Token.RPAREN);
        }
    }

    protected SQLSubPartitionBy subPartitionBy() {
        lexer.nextToken();
        accept(Token.BY);

        if (identifierEquals("HASH")) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLSubPartitionByHash byHash = new SQLSubPartitionByHash();
            SQLExpr expr = this.exprParser.expr();
            byHash.setExpr(expr);
            accept(Token.RPAREN);

            return byHash;
        } else if (identifierEquals("LIST")) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLSubPartitionByList byList = new SQLSubPartitionByList();
            SQLName column = this.exprParser.name();
            byList.setColumn(column);
            accept(Token.RPAREN);

            if (identifierEquals("SUBPARTITION")) {
                lexer.nextToken();
                acceptIdentifier("TEMPLATE");
                accept(Token.LPAREN);
                
                for (;;) {
                    SQLSubPartition subPartition = this.getExprParser().parseSubPartition();
                    subPartition.setParent(byList);
                    byList.getSubPartitionTemplate().add(subPartition);
                    
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }

            return byList;
        }

        throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
    }

    protected void parseCreateTableSupplementalLogingProps(SQLCreateTableStatement stmt) {
        acceptIdentifier("SUPPLEMENTAL");
        acceptIdentifier("LOG");

        if (lexer.token() == Token.GROUP) {
            lexer.nextToken();

            OracleSupplementalLogGrp logGrp = new OracleSupplementalLogGrp();
            logGrp.setGroup(this.exprParser.name());

            accept(Token.LPAREN);
            for (;;) {
                SQLName column = this.exprParser.name();

                if (identifierEquals("NO")) {
                    lexer.nextToken();
                    acceptIdentifier("LOG");
                    column.putAttribute("NO LOG", Boolean.TRUE);
                }

                logGrp.addColumn(column);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                if (lexer.token() == Token.RPAREN) {
                    break;
                }

                throw new ParserException("TODO " + lexer.info());
            }
            accept(Token.RPAREN);

            if (identifierEquals("ALWAYS")) {
                lexer.nextToken();
                logGrp.setAlways(true);
            }

            logGrp.setParent(stmt);
            stmt.getTableElementList().add(logGrp);
        } else {
            acceptIdentifier("DATA");

            OracleSupplementalIdKey idKey = new OracleSupplementalIdKey();
            accept(Token.LPAREN);
            for (;;) {
                if (lexer.token() == Token.ALL) {
                    lexer.nextToken();
                    idKey.setAll(true);
                } else if (lexer.token() == Token.PRIMARY) {
                    lexer.nextToken();
                    accept(Token.KEY);
                    idKey.setPrimaryKey(true);
                } else if (lexer.token() == Token.UNIQUE) {
                    lexer.nextToken();

                    if (lexer.token() == Token.INDEX) {
                        lexer.nextToken();
                        idKey.setUniqueIndex(true);
                    } else {
                        idKey.setUnique(true);
                    }
                } else if (lexer.token() == Token.FOREIGN) {
                    lexer.nextToken();
                    accept(Token.KEY);
                    idKey.setForeignKey(true);
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                if (lexer.token() == Token.RPAREN) {
                    break;
                }

                throw new ParserException("TODO " + lexer.info());
            }
            accept(Token.RPAREN);
            acceptIdentifier("COLUMNS");
            idKey.setParent(stmt);
            stmt.getTableElementList().add(idKey);
        }
    }

    public OracleExprParser getExprParser() {
        return (OracleExprParser) exprParser;
    }


}
