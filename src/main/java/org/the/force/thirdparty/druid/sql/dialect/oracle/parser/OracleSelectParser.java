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
package org.the.force.thirdparty.druid.sql.dialect.oracle.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLFlashbackExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.ModelClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.SampleClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import org.the.force.thirdparty.druid.sql.parser.Token;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLSetQuantifier;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionOperator;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLWithSubqueryClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.CycleClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.SearchClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableSource;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.parser.SQLExprParser;
import org.the.force.thirdparty.druid.sql.parser.SQLSelectParser;

import java.util.List;

public class OracleSelectParser extends SQLSelectParser {

    public OracleSelectParser(String sql){
        super(new OracleExprParser(sql));
    }

    public OracleSelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLSelect select() {
        SQLSelect select = new SQLSelect();

        withSubquery(select);

        SQLSelectQuery query = query();
        select.setQuery(query);

        SQLOrderBy orderBy = this.parseOrderBy();
        select.setOrderBy(orderBy);

        OracleSelectQueryBlock queryBlock = null;
        if (query instanceof SQLSelectQueryBlock) {
            queryBlock = (OracleSelectQueryBlock) query;
            if (orderBy != null) {
                parseFetchClause(queryBlock);
            }
        }

        if (lexer.token() == (Token.FOR)) {
            if (queryBlock == null) {
                throw new ParserException("TODO");
            }

            lexer.nextToken();
            accept(Token.UPDATE);

            queryBlock.setForUpdate(true);

            // OracleSelectForUpdate forUpdate = new OracleSelectForUpdate();

            if (lexer.token() == Token.OF) {
                lexer.nextToken();
                this.exprParser.exprList(queryBlock.getForUpdateOf(), queryBlock);
            }

            if (lexer.token() == Token.NOWAIT) {
                lexer.nextToken();
                queryBlock.setNoWait(true);
            } else if (lexer.token() == Token.WAIT) {
                lexer.nextToken();
                queryBlock.setWaitTime(this.exprParser.primary());
            } else if (identifierEquals("SKIP")) {
                lexer.nextToken();
                acceptIdentifier("LOCKED");
                queryBlock.setSkipLocked(true);
            }
        }

        if (select.getOrderBy() == null) {
            select.setOrderBy(this.exprParser.parseOrderBy());
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();

            if (identifierEquals("READ")) {
                lexer.nextToken();

                if (identifierEquals("ONLY")) {
                    lexer.nextToken();
                } else {
                    throw new ParserException("syntax error");
                }

                select.setRestriction(new OracleSelectRestriction.ReadOnly());
            } else if (lexer.token() == (Token.CHECK)) {
                lexer.nextToken();

                if (identifierEquals("OPTION")) {
                    lexer.nextToken();
                } else {
                    throw new ParserException("syntax error");
                }

                OracleSelectRestriction.CheckOption checkOption = new OracleSelectRestriction.CheckOption();

                if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();
                    throw new ParserException("TODO");
                }

                select.setRestriction(checkOption);
            } else {
                throw new ParserException("syntax error. " + lexer.info());
            }
        }

        return select;
    }

    protected void withSubquery(SQLSelect select) {
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();

            SQLWithSubqueryClause subqueryFactoringClause = new SQLWithSubqueryClause();
            for (;;) {
                OracleWithSubqueryEntry entry = new OracleWithSubqueryEntry();
                entry.setName((SQLIdentifierExpr) this.exprParser.name());

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    exprParser.names(entry.getColumns());
                    accept(Token.RPAREN);
                }

                accept(Token.AS);
                accept(Token.LPAREN);
                entry.setSubQuery(select());
                accept(Token.RPAREN);

                if (identifierEquals("SEARCH")) {
                    lexer.nextToken();
                    SearchClause searchClause = new SearchClause();

                    if (lexer.token() != Token.IDENTIFIER) {
                        throw new ParserException("syntax erorr : " + lexer.token());
                    }

                    searchClause.setType(SearchClause.Type.valueOf(lexer.stringVal()));
                    lexer.nextToken();

                    acceptIdentifier("FIRST");
                    accept(Token.BY);

                    searchClause.addItem(exprParser.parseSelectOrderByItem());

                    while (lexer.token() == (Token.COMMA)) {
                        lexer.nextToken();
                        searchClause.addItem(exprParser.parseSelectOrderByItem());
                    }

                    accept(Token.SET);

                    searchClause.setOrderingColumn((SQLIdentifierExpr) exprParser.name());

                    entry.setSearchClause(searchClause);
                }

                if (identifierEquals("CYCLE")) {
                    lexer.nextToken();
                    CycleClause cycleClause = new CycleClause();
                    exprParser.exprList(cycleClause.getAliases(), cycleClause);
                    accept(Token.SET);
                    cycleClause.setMark(exprParser.expr());
                    accept(Token.TO);
                    cycleClause.setValue(exprParser.expr());
                    accept(Token.DEFAULT);
                    cycleClause.setDefaultValue(exprParser.expr());
                    entry.setCycleClause(cycleClause);
                }

                subqueryFactoringClause.addEntry(entry);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

            select.setWithSubQuery(subqueryFactoringClause);
        }
    }

    public SQLSelectQuery query() {
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        OracleSelectQueryBlock queryBlock = new OracleSelectQueryBlock();
        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            parseHints(queryBlock);

            if (lexer.token() == (Token.DISTINCT)) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (lexer.token() == (Token.UNIQUE)) {
                queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
                lexer.nextToken();
            } else if (lexer.token() == (Token.ALL)) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                lexer.nextToken();
            }

            this.exprParser.parseHints(queryBlock.getHints());

            parseSelectList(queryBlock);
        }

        parseInto(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        parseGroupBy(queryBlock);

        parseModelClause(queryBlock);

        parseFetchClause(queryBlock);

        return queryRest(queryBlock);
    }

    public SQLSelectQuery queryRest(SQLSelectQuery selectQuery) {
        if (lexer.token() == (Token.UNION)) {
            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            lexer.nextToken();

            if (lexer.token() == (Token.ALL)) {
                union.setOperator(SQLUnionOperator.UNION_ALL);
                lexer.nextToken();
            } else if (lexer.token() == (Token.DISTINCT)) {
                union.setOperator(SQLUnionOperator.DISTINCT);
                lexer.nextToken();
            }

            SQLSelectQuery right = query();

            union.setRight(right);

            return queryRest(union);
        }

        if (lexer.token() == Token.INTERSECT) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            union.setOperator(SQLUnionOperator.INTERSECT);

            SQLSelectQuery right = this.query();
            union.setRight(right);

            return union;
        }

        if (lexer.token() == Token.MINUS) {
            lexer.nextToken();

            SQLUnionQuery union = new SQLUnionQuery();
            union.setLeft(selectQuery);

            union.setOperator(SQLUnionOperator.MINUS);

            SQLSelectQuery right = this.query();
            union.setRight(right);

            return union;
        }

        return selectQuery;
    }

    private void parseModelClause(OracleSelectQueryBlock queryBlock) {
        if (lexer.token() != Token.MODEL) {
            return;
        }

        lexer.nextToken();

        ModelClause model = new ModelClause();
        parseCellReferenceOptions(model.getCellReferenceOptions());

        if (identifierEquals("RETURN")) {
            lexer.nextToken();
            ModelClause.ReturnRowsClause returnRowsClause = new ModelClause.ReturnRowsClause();
            if (lexer.token() == Token.ALL) {
                lexer.nextToken();
                returnRowsClause.setAll(true);
            } else {
                acceptIdentifier("UPDATED");
            }
            acceptIdentifier("ROWS");

            model.setReturnRowsClause(returnRowsClause);
        }

        while (identifierEquals("REFERENCE")) {
            ModelClause.ReferenceModelClause referenceModelClause = new ModelClause.ReferenceModelClause();
            lexer.nextToken();

            SQLExpr name = expr();
            referenceModelClause.setName(name);

            accept(Token.ON);
            accept(Token.LPAREN);
            SQLSelect subQuery = this.select();
            accept(Token.RPAREN);
            referenceModelClause.setSubQuery(subQuery);

            parseModelColumnClause(referenceModelClause);

            parseCellReferenceOptions(referenceModelClause.getCellReferenceOptions());

            model.getReferenceModelClauses().add(referenceModelClause);
        }

        parseMainModelClause(model);

        queryBlock.setModelClause(model);
    }

    private void parseMainModelClause(ModelClause modelClause) {
        ModelClause.MainModelClause mainModel = new ModelClause.MainModelClause();

        if (identifierEquals("MAIN")) {
            lexer.nextToken();
            mainModel.setMainModelName(expr());
        }

        ModelClause.ModelColumnClause modelColumnClause = new ModelClause.ModelColumnClause();
        parseQueryPartitionClause(modelColumnClause);
        mainModel.setModelColumnClause(modelColumnClause);

        acceptIdentifier("DIMENSION");
        accept(Token.BY);
        accept(Token.LPAREN);
        for (;;) {
            if (lexer.token() == Token.RPAREN) {
                lexer.nextToken();
                break;
            }

            ModelClause.ModelColumn column = new ModelClause.ModelColumn();
            column.setExpr(expr());
            column.setAlias(as());
            modelColumnClause.getDimensionByColumns().add(column);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
        }

        acceptIdentifier("MEASURES");
        accept(Token.LPAREN);
        for (;;) {
            if (lexer.token() == Token.RPAREN) {
                lexer.nextToken();
                break;
            }

            ModelClause.ModelColumn column = new ModelClause.ModelColumn();
            column.setExpr(expr());
            column.setAlias(as());
            modelColumnClause.getMeasuresColumns().add(column);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
        }
        mainModel.setModelColumnClause(modelColumnClause);

        parseCellReferenceOptions(mainModel.getCellReferenceOptions());

        parseModelRulesClause(mainModel);

        modelClause.setMainModel(mainModel);
    }

    private void parseModelRulesClause(ModelClause.MainModelClause mainModel) {
        ModelClause.ModelRulesClause modelRulesClause = new ModelClause.ModelRulesClause();
        if (identifierEquals("RULES")) {
            lexer.nextToken();
            if (lexer.token() == Token.UPDATE) {
                modelRulesClause.getOptions().add(ModelClause.ModelRuleOption.UPDATE);
                lexer.nextToken();
            } else if (identifierEquals("UPSERT")) {
                modelRulesClause.getOptions().add(ModelClause.ModelRuleOption.UPSERT);
                lexer.nextToken();
            }

            if (identifierEquals("AUTOMATIC")) {
                lexer.nextToken();
                accept(Token.ORDER);
                modelRulesClause.getOptions().add(ModelClause.ModelRuleOption.AUTOMATIC_ORDER);
            } else if (identifierEquals("SEQUENTIAL")) {
                lexer.nextToken();
                accept(Token.ORDER);
                modelRulesClause.getOptions().add(ModelClause.ModelRuleOption.SEQUENTIAL_ORDER);
            }
        }

        if (identifierEquals("ITERATE")) {
            lexer.nextToken();
            accept(Token.LPAREN);
            modelRulesClause.setIterate(expr());
            accept(Token.RPAREN);

            if (identifierEquals("UNTIL")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                modelRulesClause.setUntil(expr());
                accept(Token.RPAREN);
            }
        }

        accept(Token.LPAREN);
        for (;;) {
            if (lexer.token() == Token.RPAREN) {
                lexer.nextToken();
                break;
            }

            ModelClause.CellAssignmentItem item = new ModelClause.CellAssignmentItem();
            if (lexer.token() == Token.UPDATE) {
                item.setOption(ModelClause.ModelRuleOption.UPDATE);
            } else if (identifierEquals("UPSERT")) {
                item.setOption(ModelClause.ModelRuleOption.UPSERT);
            }

            item.setCellAssignment(parseCellAssignment());
            item.setOrderBy(this.parseOrderBy());
            accept(Token.EQ);
            item.setExpr(expr());

            modelRulesClause.getCellAssignmentItems().add(item);
        }

        mainModel.setModelRulesClause(modelRulesClause);
    }

    private ModelClause.CellAssignment parseCellAssignment() {
        ModelClause.CellAssignment cellAssignment = new ModelClause.CellAssignment();

        cellAssignment.setMeasureColumn(expr());
        accept(Token.LBRACKET);
        this.exprParser.exprList(cellAssignment.getConditions(), cellAssignment);
        accept(Token.RBRACKET);

        return cellAssignment;
    }

    private void parseQueryPartitionClause(ModelClause.ModelColumnClause modelColumnClause) {
        if (lexer.token() == Token.PARTITION) {
            ModelClause.QueryPartitionClause queryPartitionClause = new ModelClause.QueryPartitionClause();

            lexer.nextToken();
            accept(Token.BY);
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                exprParser.exprList(queryPartitionClause.getExprList(), queryPartitionClause);
                accept(Token.RPAREN);
            } else {
                exprParser.exprList(queryPartitionClause.getExprList(), queryPartitionClause);
            }
            modelColumnClause.setQueryPartitionClause(queryPartitionClause);
        }
    }

    private void parseModelColumnClause(ModelClause.ReferenceModelClause referenceModelClause) {
        throw new ParserException();
    }

    private void parseCellReferenceOptions(List<ModelClause.CellReferenceOption> options) {
        if (identifierEquals("IGNORE")) {
            lexer.nextToken();
            acceptIdentifier("NAV");
            options.add(ModelClause.CellReferenceOption.IgnoreNav);
        } else if (identifierEquals("KEEP")) {
            lexer.nextToken();
            acceptIdentifier("NAV");
            options.add(ModelClause.CellReferenceOption.KeepNav);
        }

        if (lexer.token() == Token.UNIQUE) {
            lexer.nextToken();
            if (identifierEquals("DIMENSION")) {
                lexer.nextToken();
                options.add(ModelClause.CellReferenceOption.UniqueDimension);
            } else {
                acceptIdentifier("SINGLE");
                acceptIdentifier("REFERENCE");
                options.add(ModelClause.CellReferenceOption.UniqueDimension);
            }
        }
    }

    protected String as() {
        return super.as();
    }

    @Override
    public SQLTableSource parseTableSource() {
        SQLTableSource tableSource = parseTableSourcePrimary();

        if (tableSource instanceof OracleSelectTableSource) {
            return parseTableSourceRest((OracleSelectTableSource) tableSource);
        }

        return parseTableSourceRest(tableSource);
    }

    public SQLTableSource parseTableSourcePrimary() {
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            OracleSelectSubqueryTableSource tableSource;
            if (lexer.token() == Token.SELECT || lexer.token() == Token.WITH) {
                tableSource = new OracleSelectSubqueryTableSource(select());
            } else if (lexer.token() == (Token.LPAREN)) {
                tableSource = new OracleSelectSubqueryTableSource(select());
            } else {
                throw new ParserException("TODO :" + lexer.token());
            }
            accept(Token.RPAREN);

            parsePivot((OracleSelectTableSource) tableSource);

            return tableSource;
        }

        if (lexer.token() == (Token.SELECT)) {
            throw new ParserException("TODO");
        }

        OracleSelectTableReference tableReference = new OracleSelectTableReference();

        if (identifierEquals("ONLY")) {
            lexer.nextToken();
            tableReference.setOnly(true);
            accept(Token.LPAREN);
            parseTableSourceQueryTableExpr(tableReference);
            accept(Token.RPAREN);
        } else {
            parseTableSourceQueryTableExpr(tableReference);
            parsePivot(tableReference);
        }

        return tableReference;
    }

    private void parseTableSourceQueryTableExpr(OracleSelectTableReference tableReference) {
        tableReference.setExpr(this.exprParser.expr());

//        {
//            FlashbackQueryClause clause = flashback();
//            tableReference.setFlashback(clause);
//        }

        if (identifierEquals("SAMPLE")) {
            lexer.nextToken();

            SampleClause sample = new SampleClause();

            if (identifierEquals("BLOCK")) {
                sample.setBlock(true);
                lexer.nextToken();
            }

            accept(Token.LPAREN);
            this.exprParser.exprList(sample.getPercent(), sample);
            accept(Token.RPAREN);

            if (identifierEquals("SEED")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                sample.setSeedValue(expr());
                accept(Token.RPAREN);
            }

            tableReference.setSampleClause(sample);
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            PartitionExtensionClause partition = new PartitionExtensionClause();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                partition.setPartition(exprParser.name());
                accept(Token.RPAREN);
            } else {
                accept(Token.FOR);
                accept(Token.LPAREN);
                exprParser.names(partition.getFor());
                accept(Token.RPAREN);
            }

            tableReference.setPartition(partition);
        }

        if (identifierEquals("SUBPARTITION")) {
            lexer.nextToken();
            PartitionExtensionClause partition = new PartitionExtensionClause();
            partition.setSubPartition(true);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                partition.setPartition(exprParser.name());
                accept(Token.RPAREN);
            } else {
                accept(Token.FOR);
                accept(Token.LPAREN);
                exprParser.names(partition.getFor());
                accept(Token.RPAREN);
            }

            tableReference.setPartition(partition);
        }

        if (identifierEquals("VERSIONS")) {
            SQLBetweenExpr betweenExpr = new SQLBetweenExpr();
            betweenExpr.setTestExpr(new SQLIdentifierExpr("VERSIONS"));
            lexer.nextToken();

            accept(Token.BETWEEN);


            SQLFlashbackExpr start = new SQLFlashbackExpr();
            if (identifierEquals("SCN")) {
                lexer.nextToken();
                start.setType(SQLFlashbackExpr.Type.SCN);
            } else {
                acceptIdentifier("TIMESTAMP");
                start.setType(SQLFlashbackExpr.Type.TIMESTAMP);
            }

            SQLBinaryOpExpr binaryExpr = (SQLBinaryOpExpr) exprParser.expr();
            if (binaryExpr.getOperator() != SQLBinaryOperator.BooleanAnd) {
                throw new ParserException("syntax error : " + binaryExpr.getOperator());
            }

            start.setExpr(binaryExpr.getLeft());

            betweenExpr.setBeginExpr(start);
            betweenExpr.setEndExpr(binaryExpr.getRight());

            tableReference.setFlashback(betweenExpr);
        }

    }

    private SQLExpr flashback() {
        accept(Token.OF);
        if (identifierEquals("SCN")) {
            lexer.nextToken();
            return new SQLFlashbackExpr(SQLFlashbackExpr.Type.SCN, this.expr());
        } else if (identifierEquals("SNAPSHOT")) {
            return this.expr();
        } else {
            lexer.nextToken();
            return new SQLFlashbackExpr(SQLFlashbackExpr.Type.TIMESTAMP, this.expr());
        }
    }

    protected SQLTableSource primaryTableSourceRest(SQLTableSource tableSource) {
        if (tableSource instanceof OracleSelectTableSource) {
            if (lexer.token() == Token.AS) {
                lexer.nextToken();

                if (lexer.token() == Token.OF) {
                    ((OracleSelectTableSource)tableSource).setFlashback(flashback());
                }

                tableSource.setAlias(tableAlias());
            }
        }

        return tableSource;
    }

    protected SQLTableSource parseTableSourceRest(OracleSelectTableSource tableSource) {
        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            if (lexer.token() == Token.OF) {
                tableSource.setFlashback(flashback());
                return parseTableSourceRest(tableSource);
            }

            tableSource.setAlias(tableAlias(true));
        } else if ((tableSource.getAlias() == null) || (tableSource.getAlias().length() == 0)) {
            if (lexer.token() != Token.LEFT && lexer.token() != Token.RIGHT && lexer.token() != Token.FULL) {
                tableSource.setAlias(tableAlias());
            }
        }

        if (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(tableSource.getHints());
        }

        SQLJoinTableSource.JoinType joinType = null;

        if (lexer.token() == Token.LEFT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.LEFT_OUTER_JOIN;
        }

        if (lexer.token() == Token.RIGHT) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN;
        }

        if (lexer.token() == Token.FULL) {
            lexer.nextToken();
            if (lexer.token() == Token.OUTER) {
                lexer.nextToken();
            }
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.FULL_OUTER_JOIN;
        }

        if (lexer.token() == Token.INNER) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.INNER_JOIN;
        }
        if (lexer.token() == Token.CROSS) {
            lexer.nextToken();
            accept(Token.JOIN);
            joinType = SQLJoinTableSource.JoinType.CROSS_JOIN;
        }

        if (lexer.token() == Token.JOIN) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.JOIN;
        }

        if (lexer.token() == (Token.COMMA)) {
            lexer.nextToken();
            joinType = SQLJoinTableSource.JoinType.COMMA;
        }

        if (joinType != null) {
            OracleSelectJoin join = new OracleSelectJoin();
            join.setLeft(tableSource);
            join.setJoinType(joinType);

            SQLTableSource right;
            right = parseTableSourcePrimary();
            right.setAlias(this.tableAlias());
            join.setRight(right);

            if (lexer.token() == Token.ON) {
                lexer.nextToken();
                join.setCondition(this.exprParser.expr());
            } else if (lexer.token() == Token.USING) {
                lexer.nextToken();
                accept(Token.LPAREN);
                this.exprParser.exprList(join.getUsing(), join);
                accept(Token.RPAREN);
            }

            return parseTableSourceRest(join);
        }

        return tableSource;
    }

    private void parsePivot(OracleSelectTableSource tableSource) {
        OracleSelectPivot.Item item;
        if (identifierEquals("PIVOT")) {
            lexer.nextToken();

            OracleSelectPivot pivot = new OracleSelectPivot();

            if (identifierEquals("XML")) {
                lexer.nextToken();
                pivot.setXml(true);
            }

            accept(Token.LPAREN);
            while (true) {
                item = new OracleSelectPivot.Item();
                item.setExpr((SQLAggregateExpr) this.exprParser.expr());
                item.setAlias(as());
                pivot.addItem(item);

                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                }
                lexer.nextToken();
            }

            accept(Token.FOR);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                while (true) {
                    pivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                    lexer.nextToken();

                    if (!(lexer.token() == (Token.COMMA))) {
                        break;
                    }
                    lexer.nextToken();
                }

                accept(Token.RPAREN);
            } else {
                pivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            accept(Token.IN);
            accept(Token.LPAREN);
            if (lexer.token() == (Token.LPAREN)) {
                throw new ParserException("TODO");
            }

            if (lexer.token() == (Token.SELECT)) {
                throw new ParserException("TODO");
            }

            for (;;) {
                item = new OracleSelectPivot.Item();
                item.setExpr(this.exprParser.expr());
                item.setAlias(as());
                pivot.getPivotIn().add(item);

                if (lexer.token() != Token.COMMA) {
                    break;
                }

                lexer.nextToken();
            }

            accept(Token.RPAREN);

            accept(Token.RPAREN);

            tableSource.setPivot(pivot);
        } else if (identifierEquals("UNPIVOT")) {
            lexer.nextToken();

            OracleSelectUnPivot unPivot = new OracleSelectUnPivot();
            if (identifierEquals("INCLUDE")) {
                lexer.nextToken();
                acceptIdentifier("NULLS");
                unPivot.setNullsIncludeType(OracleSelectUnPivot.NullsIncludeType.INCLUDE_NULLS);
            } else if (identifierEquals("EXCLUDE")) {
                lexer.nextToken();
                acceptIdentifier("NULLS");
                unPivot.setNullsIncludeType(OracleSelectUnPivot.NullsIncludeType.EXCLUDE_NULLS);
            }

            accept(Token.LPAREN);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                this.exprParser.exprList(unPivot.getItems(), unPivot);
                accept(Token.RPAREN);
            } else {
                unPivot.addItem(this.exprParser.expr());
            }

            accept(Token.FOR);

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                while (true) {
                    unPivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                    lexer.nextToken();

                    if (!(lexer.token() == (Token.COMMA))) {
                        break;
                    }
                    lexer.nextToken();
                }

                accept(Token.RPAREN);
            } else {
                unPivot.getPivotFor().add(new SQLIdentifierExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            accept(Token.IN);
            accept(Token.LPAREN);
            if (lexer.token() == (Token.LPAREN)) {
                throw new ParserException("TODO");
            }

            if (lexer.token() == (Token.SELECT)) {
                throw new ParserException("TODO");
            }

            for (;;) {
                item = new OracleSelectPivot.Item();
                item.setExpr(this.exprParser.expr());
                item.setAlias(as());
                unPivot.getPivotIn().add(item);

                if (lexer.token() != Token.COMMA) {
                    break;
                }

                lexer.nextToken();
            }

            accept(Token.RPAREN);

            accept(Token.RPAREN);

            tableSource.setPivot(unPivot);
        }
    }

    protected void parseInto(OracleSelectQueryBlock x) {
        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
            SQLExpr expr = expr();
            if (lexer.token() != Token.COMMA) {
                x.setInto(expr);
                return;
            }
            SQLListExpr list = new SQLListExpr();
            list.addItem(expr);
            while (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                list.addItem(expr());
            }
            x.setInto(list);
        }
    }

    private void parseHints(OracleSelectQueryBlock queryBlock) {
        this.exprParser.parseHints(queryBlock.getHints());
    }
}
