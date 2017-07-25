package org.the.force.jdbc.partition.engine.executor.dql.partition;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.evaluator.ExprGatherConfig;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.AggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.AvgAggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.factory.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.evaluator.row.RsIndexEvaluator;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
import org.the.force.jdbc.partition.engine.parser.select.SelectItemParser;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.query.GroupBy;
import org.the.force.jdbc.partition.engine.sql.query.OrderBy;
import org.the.force.jdbc.partition.engine.sql.query.OrderByItem;
import org.the.force.jdbc.partition.engine.sql.query.PartitionSelectTable;
import org.the.force.jdbc.partition.engine.sql.query.ResultLimit;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderingSpecification;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectGroupByClause;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectOrderByItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/18.
 * 交给db执行的节点,支持router(路由，sql改写)和merge分区结果
 * ===================适用范围============================
 * 目前适用的范围是
 * from是单表的查询 无论from嵌套多少子查询，只要操作的逻辑表只有一个即可。
 * 如果查询的条件中含有子查询，那么同样支持（实际就是输出物理sql时延迟做子查询）
 * ==================sql执行规则===============================
 * 为了让jdbc能够merge结果集,同时兼顾性能等综合因素考虑，约定规则如下
 * 1,order by的条件涉及的列必须在select的结果集中，如果没有则自动改写sql放入
 * 2,limit最后一个执行，在router输出物理sql时对每个分区改写为从0开始的endRows
 * 3，如果结果集是distinct all
 * 3.1 结果集中不能含有聚合操作，group by子句不支持（对数据库来说distinct就是group by）
 * 3.2 limit的条件对数据库失效不输出，交给client实现
 * 4,如果结果集中含有avg聚合,那么对avg的入参 使用同样参数的sum(param)/count(param)替换avg表达式
 * 5,如果有group by子句
 * 5.1 group by的列都必须放入select的结果集中，如果没有则自动改写sql放入
 * 5.2 结果集优先依据group by的列排序 并尽量兼容order by的排序条件，不兼容的部分交给jdbc实现
 * 5.3 如果order by和group by存在不兼容的情况，那么，limit的条件对数据库失效不输出，交给jdbc实现
 * 5.4,如果有having，having的条件涉及的aggregation必须放入select的结果集中,having的过滤交给jdbc实现
 * limit条件对数据库失效，交给jdbc实现
 * ====================例外规则==========================================================================
 * 6,如果分区路由的结果只有一个物理sql需要执行则1-5的规则都无效，包括两种情况
 * 6.1 路由的结果只有一个物理表
 * 6.2 路由的结果有多个物理表但是在同一个库，那么改写为union实现
 * 7,如果group by 的列的顺序是分库分表的列优先（排在前面），那么规则4、5失效,jdbc不需要特殊处理
 */
public class PartitionBlockQueryExecutor extends SQLSelectQueryBlock implements BlockQueryExecutor {

    private static Log logger = LogFactory.getLog(PartitionBlockQueryExecutor.class);

    private final LogicDbConfig logicDbConfig;

    private final ExprConditionalSqlTable innerExprSqlTable;

    private final ConditionalSqlTable outerSqlTable;

    //原始的originalSqlSelectQueryBlock 不去修改，在符合例外条件时(规则6)使用此对象输出sql
    private final SQLSelectQueryBlock versionForRule6;

    private final TableRouter tableRouter;

    private final PartitionSelectTable selectTable;

    private boolean inited = false;

    private SQLSelectQueryBlock versionForRule7;

    private SQLSelectQueryBlock currentSqlSelectQueryBlock;

    public PartitionBlockQueryExecutor(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputSqlSelectQueryBlock, ExprConditionalSqlTable innerExprSqlTable) {
        this.logicDbConfig = logicDbConfig;
        versionForRule6 = inputSqlSelectQueryBlock;//保留入参的原始的版本
        //最底层的sqlTable，和sqlSelectQueryBlock的tableSource未必是直接对应的
        this.innerExprSqlTable = innerExprSqlTable;
        tableRouter = new DefaultTableRouter(logicDbConfig, inputSqlSelectQueryBlock, innerExprSqlTable);
        outerSqlTable = new SqlTableParser(logicDbConfig).getSqlTable(inputSqlSelectQueryBlock.getFrom());
        selectTable = new PartitionSelectTable(outerSqlTable, inputSqlSelectQueryBlock.getDistionOption() > 0);
    }

    public void init() {
        if (inited) {
            return;
        }
        //尽量展开所有*的列
        this.currentSqlSelectQueryBlock = new SqlObjCopier().copy(versionForRule6);
        SelectItemParser selectItemParser = new SelectItemParser(logicDbConfig);
        selectItemParser.extendAllColumns(currentSqlSelectQueryBlock, outerSqlTable.getSQLTableSource());
        //解析原始的每个select item
        selectItemParser.parseItems(currentSqlSelectQueryBlock, selectTable);

        if (selectTable.getAllColumnItems().isEmpty()) {
            //如果结果集中含有*，并且无法拉取数据库元信息获取*的列，那么不支持所承诺的功能
            //标识用户查询的列的边界，后面的列可能是因为merge的需要而添加的
            selectTable.setQueryBound(selectTable.getNormalValueNodeSize());
            //适配 order by子句
            adaptOrderBy();
            //适配limit子句
            adaptLimit();
            //保存仅仅适配了order by和limit条件的版本
            versionForRule7 = new SqlObjCopier().copy(currentSqlSelectQueryBlock);
            if (selectTable.isDistinctAll()) {
                //distinct all时版本到这里中止
                currentSqlSelectQueryBlock.setLimit(null);
            } else {
                //版本往下走
                adaptGroupBy();
                //检查是否有avg聚合查询，如果有则改变其逻辑
                adaptAvgAggregate();
            }
            //按照重置selectTable的SelectValueNode的顺序重置currentSqlSelectQueryBlock的selectList
            resetSelectList();
        }
        inited = true;
    }

    protected void adaptOrderBy() {
        SQLOrderBy sqlOrderBy = currentSqlSelectQueryBlock.getOrderBy();
        if (sqlOrderBy == null) {
            return;
        }
        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        OrderBy orderBy = new OrderBy();
        List<SQLSelectOrderByItem> items = sqlOrderBy.getItems();

        for (int i = 0; i < items.size(); i++) {
            SQLSelectOrderByItem orderByItem = items.get(i);
            SqlExprEvaluator sqlExprEvaluator = sqlExprEvaluatorFactory.matchSqlExprEvaluator(orderByItem.getExpr());
            int index = selectTable.getIndex(sqlExprEvaluator);
            RsIndexEvaluator rsIndexEvaluator;
            if (index < 0) {
                SQLSelectItem newItem = new SQLSelectItem();
                newItem.setExpr(sqlExprEvaluator.getOriginalSqlExpr());
                selectTable.addValueNode(newItem, sqlExprEvaluator);
                rsIndexEvaluator = new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), selectTable.getNormalValueNodeSize() - 1,
                    selectTable.getSelectLabel(selectTable.getNormalValueNodeSize() - 1));
            } else {
                rsIndexEvaluator = new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), index, selectTable.getSelectLabel(index));
            }
            orderBy.getOrderByItems().add(new OrderByItem(rsIndexEvaluator, orderByItem.getType()));
        }
        orderBy.setSortedIndexFrom(orderBy.getOrderByItems().size() - 1);
        selectTable.setOrderBy(orderBy);
    }

    protected void adaptLimit() {
        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        SQLLimit sqlLimit = currentSqlSelectQueryBlock.getLimit();
        if (sqlLimit == null) {
            return;
        }
        ResultLimit resultLimit = new ResultLimit(sqlExprEvaluatorFactory.matchSqlExprEvaluator(sqlLimit.getRowCount()));
        if (sqlLimit.getOffset() != null) {
            resultLimit.setOffset(sqlExprEvaluatorFactory.matchSqlExprEvaluator(sqlLimit.getOffset()));
        }
        selectTable.setResultLimit(resultLimit);
    }

    protected void adaptGroupBy() {
        SQLSelectGroupByClause sqlSelectGroupByClause = currentSqlSelectQueryBlock.getGroupBy();
        if (sqlSelectGroupByClause == null) {
            return;
        }
        GroupBy groupBy = new GroupBy();
        //referList
        List<SQLExpr> items = sqlSelectGroupByClause.getItems();
        for (SQLExpr sqlExpr : items) {
            if (!(sqlExpr instanceof SQLName)) {
                logger.warn("group by的列不是SQLName实例");
                continue;
            }
            SqlRefer sqlRefer = new SqlRefer((SQLName) sqlExpr);
            groupBy.addItem(sqlRefer);
        }
        if (selectTable.isDistinctAll()) {
            logger.warn("聚合查询时不能 distinct all result column:" + PartitionSqlUtils.toSql(versionForRule6, logicDbConfig.getSqlDialect()));
            return;
        }
        selectTable.setGroupBy(groupBy);
        //确保group by的列在select的结果集中
        int groupByItemSize = groupBy.getItemSize();
        //重置group by的列函数
        for (int i = 0; i < groupByItemSize; i++) {
            SqlExprEvaluator sqlExprEvaluator = groupBy.getSqlExprEvaluator(i);
            int selectTableLabelIndex = selectTable.getIndex(sqlExprEvaluator);
            if (selectTableLabelIndex < 0) {
                SQLSelectItem newItem = new SQLSelectItem();
                newItem.setExpr(sqlExprEvaluator.getOriginalSqlExpr());
                selectTable.addValueNode(newItem, sqlExprEvaluator);
                groupBy.updateSqlExprEvaluator(i, new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), selectTable.getNormalValueNodeSize() - 1,
                    selectTable.getSelectLabel(selectTable.getNormalValueNodeSize() - 1)));
            } else {
                groupBy.updateSqlExprEvaluator(i,
                    new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), selectTableLabelIndex, selectTable.getSelectLabel(selectTableLabelIndex)));
            }
        }
        //group by排序优先的实现
        resetOrderBy(groupBy);
        //适配having过滤
        adaptHaving(groupBy, sqlSelectGroupByClause);
    }

    protected void resetOrderBy(GroupBy groupBy) {
        int groupByItemSize = groupBy.getItemSize();
        OrderBy orderBy = selectTable.getOrderBy();
        SQLOrderBy sqlOrderBy = new SQLOrderBy();
        boolean lastMatch = false;
        if (orderBy != null) {
            //原始的order by存在
            lastMatch = true;
            orderBy.setSortedIndexFrom(-1);
            groupBy.sortFromOrderBy(selectTable, orderBy);
        }
        for (int i = 0; i < groupByItemSize; i++) {
            SQLName sqlName = (SQLName) groupBy.getSqlExprEvaluator(i).getOriginalSqlExpr();
            SQLSelectOrderByItem sqlSelectOrderByItem = new SQLSelectOrderByItem(sqlName);
            //判断group by的列排序兼容order by的列的部分
            if (lastMatch && i < orderBy.getOrderByItems().size()) {
                OrderByItem orderByItem = orderBy.getOrderByItems().get(i);

                if (selectTable.checkEquals(groupBy.getSqlExprEvaluator(i), orderByItem.getRsIndexEvaluator())) {
                    sqlSelectOrderByItem.setType(orderByItem.getSqlOrderingSpecification());
                    orderBy.setSortedIndexFrom(i);
                } else {
                    lastMatch = false;
                    sqlSelectOrderByItem.setType(SQLOrderingSpecification.ASC);
                }
            } else {
                lastMatch = false;
                sqlSelectOrderByItem.setType(SQLOrderingSpecification.ASC);
            }
            sqlOrderBy.getItems().add(sqlSelectOrderByItem);
        }
        //设置新的排序规则
        currentSqlSelectQueryBlock.setOrderBy(sqlOrderBy);
        //判断是否需要让limit失效
        if (orderBy != null) {
            if (orderBy.getSortedIndexFrom() < orderBy.getOrderByItems().size() - 1) {
                currentSqlSelectQueryBlock.setLimit(null);
            } else {
                //order by和group by完全兼容，limit不必因此而无效
            }
        }
    }

    protected void adaptHaving(GroupBy groupBy, SQLSelectGroupByClause sqlSelectGroupByClause) {
        //重置having的聚合操作条件，client实现having过滤
        SQLExpr havingExpr = sqlSelectGroupByClause.getHaving();
        if (havingExpr == null) {
            return;
        }
        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        List<AggregateEvaluator> aggregateEvaluatorsInHaving = new ArrayList<>();
        SqlExprEvaluator havingSqlExprEvaluator = sqlExprEvaluatorFactory.matchSqlExprEvaluator(havingExpr);
        havingSqlExprEvaluator.gatherExprEvaluator(AggregateEvaluator.class, new ExprGatherConfig(true), aggregateEvaluatorsInHaving);
        if (aggregateEvaluatorsInHaving.isEmpty()) {
            logger.warn("having没有包含聚合查询");
            return;
        }
        SqlObjCopier sqlObjCopier = new SqlObjCopier();
        for (AggregateEvaluator aggregateEvaluator : aggregateEvaluatorsInHaving) {
            int index = selectTable.getIndex(aggregateEvaluator);
            SQLExpr subExpr = aggregateEvaluator.getOriginalSqlExpr();
            if (index < 0) {
                SQLSelectItem newItem = new SQLSelectItem(subExpr);
                selectTable.addValueNode(newItem, aggregateEvaluator);
                sqlObjCopier.addReplaceObj(aggregateEvaluator,
                    new RsIndexEvaluator(subExpr, selectTable.getNormalValueNodeSize() - 1, selectTable.getSelectLabel(selectTable.getNormalValueNodeSize() - 1)));
            } else {
                sqlObjCopier.addReplaceObj(aggregateEvaluator, new RsIndexEvaluator(subExpr, index, selectTable.getSelectLabel(index)));
            }
        }
        havingSqlExprEvaluator = sqlObjCopier.copy(havingSqlExprEvaluator);
        groupBy.setHaving(havingSqlExprEvaluator);
        sqlSelectGroupByClause.setHaving(null);
        //having影响返回的记录数，导致返回的记录数增加，因此原始sql设置的limit判断对数据库执行的sql失效
        currentSqlSelectQueryBlock.setLimit(null);
    }

    protected void adaptAvgAggregate() {
        int size = selectTable.getNormalValueNodeSize();
        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        w:
        for (int i = 0; i < size; i++) {
            SqlExprEvaluator sqlExprEvaluator = selectTable.getSelectValueNode(i);
            SQLExpr originalAvgExpr = sqlExprEvaluator.getOriginalSqlExpr();
            List<AvgAggregateEvaluator> list = new ArrayList<>();
            sqlExprEvaluator.gatherExprEvaluator(AvgAggregateEvaluator.class, new ExprGatherConfig(false), list);
            if (list.isEmpty()) {
                continue;
            }
            SqlObjCopier sqlObjCopier = new SqlObjCopier();
            for (AvgAggregateEvaluator avgAggregateEvaluator : list) {
                List<SqlExprEvaluator> children = avgAggregateEvaluator.children();
                if (children.size() != 1) {
                    logger.error("avgAggregateEvaluator children.size() != 1:" + PartitionSqlUtils.toSql(versionForRule6, logicDbConfig.getSqlDialect()));
                    continue w;
                }
                SQLMethodInvokeExpr sum = new SQLMethodInvokeExpr("SUM");
                sum.getParameters().add(children.get(0).getOriginalSqlExpr());
                SQLMethodInvokeExpr count = new SQLMethodInvokeExpr("COUNT");
                count.getParameters().add(children.get(0).getOriginalSqlExpr());
                SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr(sum, count, SQLBinaryOperator.Divide);
                sqlObjCopier.addReplaceObj(avgAggregateEvaluator.getOriginalSqlExpr(), sqlBinaryOpExpr);
            }
            SQLExpr newAvgExpr = sqlObjCopier.copy(originalAvgExpr);
            SqlExprEvaluator newSqlExprEvaluator = sqlExprEvaluatorFactory.matchSqlExprEvaluator(newAvgExpr);
            selectTable.getNormalSelectItem(i).setExpr(newAvgExpr);
            //强制更新结果集计算方式
            selectTable.updateSqlExprEvaluator(i, newSqlExprEvaluator, true);
        }
    }

    protected void resetSelectList() {
        currentSqlSelectQueryBlock.getSelectList().clear();
        int size = selectTable.getNormalValueNodeSize();
        for (int i = 0; i < size; i++) {
            SqlExprEvaluator sqlExprEvaluator = selectTable.getSelectValueNode(i);
            currentSqlSelectQueryBlock.getSelectList().add(selectTable.getNormalSelectItem(i));
            RsIndexEvaluator rsIndexEvaluator = new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), i, selectTable.getSelectLabel(i));
            selectTable.updateSqlExprEvaluator(i, rsIndexEvaluator);
        }

    }

    public void setLimit(SQLLimit limit) {
        //响应 limit的改变做出改变
        versionForRule6.setLimit(limit);
    }

    protected void accept0(SQLASTVisitor visitor) {
        currentSqlSelectQueryBlock.accept(visitor);
    }

    public SQLSelectQuery getStatement() {
        return currentSqlSelectQueryBlock;
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {

        return null;
    }

    public SQLExpr getExpr() {
        throw new UnsupportedOperationException("getExpr()");
    }

    public void setExpr(SQLExpr expr) {

    }

    public String getAlias() {
        throw new UnsupportedOperationException("getAlias()");
    }

    public void setAlias(String alias) {

    }

    public int getHintsSize() {
        throw new UnsupportedOperationException("getHintsSize()");
    }

    public List<SQLHint> getHints() {
        throw new UnsupportedOperationException("getHints()");
    }

    public void setHints(List<SQLHint> hints) {

    }

    public SQLExprTableSource clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public String computeAlias() {
        throw new UnsupportedOperationException("computeAlias()");
    }

    public SQLExpr getFlashback() {
        throw new UnsupportedOperationException("getFlashback()");
    }

    public void setFlashback(SQLExpr flashback) {
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public String toString() {
        return PartitionSqlUtils.toSql(this, logicDbConfig.getSqlDialect());
    }

    public ExprConditionalSqlTable getInnerExprSqlTable() {
        return innerExprSqlTable;
    }

    public ConditionalSqlTable getOuterSqlTable() {
        return outerSqlTable;
    }
}
