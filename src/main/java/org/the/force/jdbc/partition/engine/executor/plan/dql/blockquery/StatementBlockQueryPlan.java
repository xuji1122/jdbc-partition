package org.the.force.jdbc.partition.engine.executor.plan.dql.blockquery;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.plan.dql.BlockQueryPlan;
import org.the.force.jdbc.partition.engine.executor.plan.dql.tablesource.JoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.plan.dql.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.plan.dql.tablesource.UnionQueriedTableSource;
import org.the.force.jdbc.partition.engine.parser.SubQueryConditionVisitor;
import org.the.force.jdbc.partition.engine.parser.TableConditionParser;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlTableParser;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.parser.ParserException;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/3.
 * 执行顺序 sqlTableSource --> 子查询 --> 自身（newWhere和聚合条件等）
 * 如果sqlTableSource是单表 则 子查询 --> 自身（tableSource newWhere和聚合条件等）
 * 如果没有子查询 自身（tableSource newWhere和聚合条件等）
 */
public class StatementBlockQueryPlan extends BlockQueryPlan {

    private final SQLSelectQueryBlock queryBlock;

    private SQLTableSource sqlTableSource;

    private final ExprSqlTable sqlTable;

    private final Map<SqlColumn, SQLExpr> currentTableColumnValueMap;

    private final Map<SqlColumn, SQLInListExpr> currentTableColumnInValuesMap;

    private final List<SQLExpr> subQuerys;

    private final SQLExpr newWhere;

    private final SQLExpr outerCondition;

    public StatementBlockQueryPlan(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQuery) {
        this(logicDbConfig, selectQuery, null);
    }

    public StatementBlockQueryPlan(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQuery, SQLExpr outerCondition) {
        super(logicDbConfig);
        this.queryBlock = selectQuery;
        this.outerCondition = outerCondition;
        SQLTableSource from = selectQuery.getFrom();
        if (from instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) from;
            this.sqlTable = SqlTableParser.getSQLExprTable(sqlExprTableSource, logicDbConfig);
            this.sqlTableSource = sqlExprTableSource;
            TableConditionParser tableConditionParser = new TableConditionParser(getLogicDbConfig(), sqlTable, selectQuery.getWhere());
            currentTableColumnValueMap = tableConditionParser.getCurrentTableColumnValueMap();
            currentTableColumnInValuesMap = tableConditionParser.getCurrentTableColumnInValuesMap();
            //先做掉子查询，然后转为数据库的sql语句到数据库执行sql
            subQuerys = tableConditionParser.getSubQueryList();
            this.newWhere = tableConditionParser.getSubQueryResetWhere();
        } else {
            //tableSource先执行，再根据tableSource的结果生成查询结果集
            sqlTable = null;
            currentTableColumnValueMap = null;
            currentTableColumnInValuesMap = null;
            if (from instanceof SQLJoinTableSource) {
                //tableSource先执行，再根据tableSource的结果生成查询结果集
                JoinedTableSource joinedTableSource = new JoinedTableSource(logicDbConfig, (SQLJoinTableSource) from, selectQuery.getWhere());
                this.sqlTableSource = joinedTableSource;
                this.newWhere = joinedTableSource.getNewWhere(); //tableSource特有的条件过滤掉之后剩余的条件
                //剩余的where条件是否有子查询
                if (this.newWhere != null) {
                    SubQueryConditionVisitor conditionChecker = new SubQueryConditionVisitor(logicDbConfig);
                    newWhere.accept(conditionChecker);
                    subQuerys = conditionChecker.getSubQueryList();
                } else {
                    subQuerys = null;
                }
            } else {
                SqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(from);
                TableConditionParser parser = new TableConditionParser(logicDbConfig, sqlTable, selectQuery.getWhere());
                subQuerys = parser.getSubQueryList();
                this.newWhere = parser.getOtherCondition();
                if (from instanceof SQLSubqueryTableSource) {
                    SubQueriedTableSource subQueriedTableSource =
                        new SubQueriedTableSource(logicDbConfig, (SQLSubqueryTableSource) from, sqlTable, parser.getCurrentTableOwnCondition());
                    this.sqlTableSource = subQueriedTableSource;
                } else if (from instanceof SQLUnionQueryTableSource) {
                    UnionQueriedTableSource unionQueriedTableSource =
                        new UnionQueriedTableSource(logicDbConfig, (SQLUnionQueryTableSource) from, sqlTable, parser.getCurrentTableOwnCondition());
                    this.sqlTableSource = unionQueriedTableSource;
                } else {
                    //TODO
                    throw new ParserException("无法识别的tableSource:" + PartitionSqlUtils.toSql(selectQuery, logicDbConfig.getSqlDialect()) + " : from=" + from.getClass().getName());
                }
            }
        }

    }

    public SQLSelectQueryBlock getQueryBlock() {
        return queryBlock;
    }

    public SQLTableSource getSqlTableSource() {
        return sqlTableSource;
    }

    public ExprSqlTable getSqlTable() {
        return sqlTable;
    }

}
