package org.the.force.jdbc.partition.engine.executor.dql.partition;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.evaluator.ExprGatherConfig;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.AggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.AvgAggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.factory.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.evaluator.row.RsIndexEvaluator;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.LinedSql;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecParamLineNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecPhysicNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecStmtNode;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
import org.the.force.jdbc.partition.engine.parser.select.SelectItemParser;
import org.the.force.jdbc.partition.engine.rewrite.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.router.RouteEvent;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.stmt.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.stmt.SqlRefer;
import org.the.force.jdbc.partition.engine.stmt.SqlTablePartition;
import org.the.force.jdbc.partition.engine.stmt.query.GroupBy;
import org.the.force.jdbc.partition.engine.stmt.query.GroupByItem;
import org.the.force.jdbc.partition.engine.stmt.query.OrderBy;
import org.the.force.jdbc.partition.engine.stmt.query.OrderByItem;
import org.the.force.jdbc.partition.engine.stmt.query.PartitionSelectTable;
import org.the.force.jdbc.partition.engine.stmt.query.ResultLimit;
import org.the.force.jdbc.partition.engine.stmt.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        this(logicDbConfig, inputSqlSelectQueryBlock, innerExprSqlTable, innerExprSqlTable);
    }

    public PartitionBlockQueryExecutor(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputSqlSelectQueryBlock, ExprConditionalSqlTable innerExprSqlTable,
        ConditionalSqlTable outerSqlTable) {
        this.logicDbConfig = logicDbConfig;
        versionForRule6 = inputSqlSelectQueryBlock;//保留入参的原始的版本
        //最底层的sqlTable，和sqlSelectQueryBlock的tableSource未必是直接对应的
        this.innerExprSqlTable = innerExprSqlTable;
        this.outerSqlTable = outerSqlTable;
        tableRouter = new DefaultTableRouter(logicDbConfig, innerExprSqlTable);
        selectTable = new PartitionSelectTable(outerSqlTable, inputSqlSelectQueryBlock.getDistionOption() > 0);
    }

    public ResultSet execute(SqlLineExecRequest sqlLineExecRequest) throws SQLException {
        RouteEvent routeEvent =
            new RouteEvent(logicDbConfig.getLogicTableManager(innerExprSqlTable.getTableName()).getLogicTableConfig()[0], PartitionEvent.EventType.SELECT, sqlLineExecRequest);
        Map<Partition, SqlTablePartition> partitionSqlTableMap = tableRouter.route(routeEvent);
        if (partitionSqlTableMap.size() == 1) {
            //最简单的情况
            Map.Entry<Partition, SqlTablePartition> entry = partitionSqlTableMap.entrySet().iterator().next();
            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, logicDbConfig, sqlLineExecRequest, entry.getValue());
            versionForRule6.accept(mySqlPartitionSqlOutput);
            SqlExecPhysicNode sqlExecPhysicNode;
            if (mySqlPartitionSqlOutput.isParametric()) {
                sqlExecPhysicNode =
                    new SqlExecParamLineNode(sqlSb.toString(), entry.getKey().getPhysicDbName(), new LinedParameters(1, mySqlPartitionSqlOutput.getSqlParameterList()));

            } else {
                sqlExecPhysicNode = new SqlExecStmtNode(entry.getKey().getPhysicDbName(), new LinedSql(1, sqlSb.toString()));
            }
            return null;
        } else {
            //判断是否都在同一个物理库
            Map<String, List<SqlTablePartition>> physicDbMap = new LinkedHashMap<>();
            partitionSqlTableMap.forEach((key, v) -> {
                List<SqlTablePartition> list = physicDbMap.get(key.getPhysicDbName());
                if (list == null) {
                    list = new ArrayList<>();
                    physicDbMap.put(key.getPhysicDbName(), list);
                }
                list.add(v);
            });
            if (physicDbMap.size() == 1) {
                //选用版本七
                //最终是一个物理库 ，通过sql改写实现所有的merge
                //按照物理库 union 查询语句
            } else {
                //选用最终版本
                //多库多表合并
                //1,按照物理库 union 查询语句  合并多表的结果
                //2,按照逻辑规则 合并多个库的结果
            }
        }
        return null;
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

            adaptGroupBy();
            //检查是否有avg聚合查询，如果有则改变其逻辑
            adaptAvgAggregate();

            //检查是否需要distinct all  distinct all只能由jdbc客户端执行，并且会导致order by和limit对数据库失效
            adaptDistinctAll();
            //
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
                rsIndexEvaluator = new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), outerSqlTable, selectTable.getNormalValueNodeSize() - 1,
                    selectTable.getSelectLabel(selectTable.getNormalValueNodeSize() - 1));
            } else {
                rsIndexEvaluator = new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), outerSqlTable, index, selectTable.getSelectLabel(index));
            }
            orderBy.getOrderByItems().add(new OrderByItem(rsIndexEvaluator, orderByItem.getType()));
        }
        orderBy.setSortedIndexTo(orderBy.getOrderByItems().size());
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
        List<SqlRefer> sqlRefers = new ArrayList<>();
        for (SQLExpr sqlExpr : items) {
            if (!(sqlExpr instanceof SQLName)) {
                logger.warn("group by的列不是SQLName实例");
                continue;
            }
            SqlRefer sqlRefer = new SqlRefer((SQLName) sqlExpr);
            sqlRefers.add(sqlRefer);
        }
        selectTable.setGroupBy(groupBy);
        //确保group by的列在select的结果集中
        int groupByItemSize = sqlRefers.size();
        //重置group by的列函数
        for (int i = 0; i < groupByItemSize; i++) {
            SqlRefer sqlExprEvaluator = sqlRefers.get(i);
            int selectTableLabelIndex = selectTable.getIndex(sqlExprEvaluator);
            if (selectTableLabelIndex < 0) {
                SQLSelectItem newItem = new SQLSelectItem();
                newItem.setExpr(sqlExprEvaluator.getOriginalSqlExpr());
                selectTable.addValueNode(newItem, sqlExprEvaluator);
                GroupByItem groupByItem = new GroupByItem(new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), outerSqlTable, selectTable.getNormalValueNodeSize() - 1,
                    selectTable.getSelectLabel(selectTable.getNormalValueNodeSize() - 1)));
                groupBy.addItem(groupByItem);
            } else {
                GroupByItem groupByItem = new GroupByItem(
                    new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), outerSqlTable, selectTableLabelIndex, selectTable.getSelectLabel(selectTableLabelIndex)));
                groupBy.addItem(groupByItem);
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
        boolean sortedToAdd = false;
        if (orderBy != null) {
            //原始的order by存在
            sortedToAdd = true;
            orderBy.setSortedIndexTo(0);
            groupBy.sortGroupByExprFrom(selectTable, orderBy);
        }
        for (int i = 0; i < groupByItemSize; i++) {
            GroupByItem groupByItem = groupBy.getGroupByItem(i);
            SQLName sqlName = (SQLName) groupByItem.getItemExprEvaluator().getOriginalSqlExpr();
            SQLSelectOrderByItem sqlSelectOrderByItem = new SQLSelectOrderByItem(sqlName);
            //判断group by的列排序兼容order by的列的部分
            if (orderBy != null && i < orderBy.getItemSize()) {
                OrderByItem orderByItem = orderBy.getOrderByItem(i);
                if (selectTable.checkEquals(groupByItem.getItemExprEvaluator(), orderByItem.getRsIndexEvaluator())) {
                    sqlSelectOrderByItem.setType(orderByItem.getSqlOrderingSpecification());
                    groupByItem.setSqlOrderingSpecification(orderByItem.getSqlOrderingSpecification());
                    groupBy.updateSqlExprEvaluator(i, groupByItem);
                    if (sortedToAdd) {
                        orderBy.setSortedIndexTo(i + 1);
                    }
                } else {
                    sortedToAdd = false;
                    sqlSelectOrderByItem.setType(groupByItem.getSqlOrderingSpecification());
                }
            } else {
                sortedToAdd = false;
                sqlSelectOrderByItem.setType(groupByItem.getSqlOrderingSpecification());
            }
            sqlOrderBy.getItems().add(sqlSelectOrderByItem);
        }
        //设置新的排序规则到currentSqlSelectQueryBlock中，后续不再改变
        currentSqlSelectQueryBlock.setOrderBy(sqlOrderBy);
        groupBy.setSortedIndexTo(groupBy.getItemSize());
        //判断是否需要让limit失效
        if (orderBy != null) {
            if (orderBy.getSortedIndexTo() < orderBy.getItemSize()) {
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
                    new RsIndexEvaluator(subExpr, outerSqlTable, selectTable.getNormalValueNodeSize() - 1, selectTable.getSelectLabel(selectTable.getNormalValueNodeSize() - 1)));
            } else {
                sqlObjCopier.addReplaceObj(aggregateEvaluator, new RsIndexEvaluator(subExpr, outerSqlTable, index, selectTable.getSelectLabel(index)));
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
                List<SQLExpr> exprArgs = children.stream().map(SqlExprEvaluator::getOriginalSqlExpr).collect(Collectors.toList());

                SQLAggregateExpr avg = (SQLAggregateExpr) avgAggregateEvaluator.getOriginalSqlExpr();

                SQLAggregateExpr sum = new SQLAggregateExpr("SUM");
                sum.setOption(avg.getOption());
                sum.getArguments().addAll(exprArgs);
                SQLAggregateExpr count = new SQLAggregateExpr("COUNT");
                count.setOption(avg.getOption());
                count.getArguments().addAll(exprArgs);
                SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr(sum, count, SQLBinaryOperator.Divide);
                sqlObjCopier.addReplaceObj(avgAggregateEvaluator.getOriginalSqlExpr(), sqlBinaryOpExpr);
            }
            SQLExpr newAvgExpr = sqlObjCopier.copy(originalAvgExpr);
            SqlExprEvaluator newSqlExprEvaluator = sqlExprEvaluatorFactory.matchSqlExprEvaluator(newAvgExpr);
            newSqlExprEvaluator.setFromSQLExpr(originalAvgExpr);
            selectTable.updateSqlExprEvaluator(i, newSqlExprEvaluator, newAvgExpr);
        }
    }

    protected void resetSelectList() {
        currentSqlSelectQueryBlock.getSelectList().clear();
        int size = selectTable.getNormalValueNodeSize();
        for (int i = 0; i < size; i++) {
            SqlExprEvaluator sqlExprEvaluator = selectTable.getSelectValueNode(i);
            currentSqlSelectQueryBlock.getSelectList().add(selectTable.getNormalSelectItem(i));
            RsIndexEvaluator rsIndexEvaluator = new RsIndexEvaluator(sqlExprEvaluator.getOriginalSqlExpr(), outerSqlTable, i, selectTable.getSelectLabel(i));
            selectTable.updateSqlExprEvaluator(i, rsIndexEvaluator);
        }
    }

    protected void adaptDistinctAll() {
        if (!selectTable.isDistinctAll()) {
            return;
        }
        //转为全列做group by
        currentSqlSelectQueryBlock.setDistionOption(0);
        currentSqlSelectQueryBlock.setLimit(null);
        GroupBy distinctAllGroupBy = new GroupBy();
        for (int i = 0; i < selectTable.getQueryBound(); i++) {
            GroupByItem groupByItem =
                new GroupByItem(new RsIndexEvaluator(selectTable.getSelectValueNode(i).getOriginalSqlExpr(), outerSqlTable, i, selectTable.getSelectLabel(i)));
            distinctAllGroupBy.addItem(groupByItem);
        }
        selectTable.setDistinctAllGroupBy(distinctAllGroupBy);
        /**
         * 1，实现多个分区做distinct all
         * 2，兼容group by的需求(聚合 having过滤等)
         * 3，兼容order by的排序需求
         */
        GroupBy groupBy = selectTable.getGroupBy();
        if (groupBy != null) {
            // 对group by的结果进行distinct all的场景  group by优先通过数据库执行，distinct all基于jdbc实现

            //group by的列可能是隐藏列，隐藏列对客户端而言是不可见的，对distinct不起作用
            //由group by决定的排序规则
            distinctAllGroupBy.sortGroupByExprFrom(selectTable, groupBy);
            int size = distinctAllGroupBy.getItemSize();
            boolean sortedToAdd = true;
            for (int i = 0; i < size; i++) {
                GroupByItem groupByItem = distinctAllGroupBy.getGroupByItem(i);
                if (i < groupBy.getItemSize() && selectTable.checkEquals(groupByItem.getItemExprEvaluator(), groupBy.getGroupByItem(i).getItemExprEvaluator())) {
                    groupByItem.setSqlOrderingSpecification(groupBy.getGroupByItem(i).getSqlOrderingSpecification());
                    distinctAllGroupBy.updateSqlExprEvaluator(i, groupByItem);
                    if (sortedToAdd) {
                        distinctAllGroupBy.setSortedIndexTo(i + 1);
                    }
                } else {
                    sortedToAdd = false;
                }
            }

            if (distinctAllGroupBy.getSortedIndexTo() <= 0 && selectTable.getOrderBy() != null) {
                distinctAllGroupBy.sortGroupByExprFrom(selectTable, selectTable.getOrderBy());
            }
        }
        //，但是在内存中还是优先于order by执行的，因此还要根据order 排序操作
        //order by在distinctGroupBy之后执行  distinctGroupBy通过数据库实现
        OrderBy orderBy = selectTable.getOrderBy();
        if (orderBy != null) {
            if (distinctAllGroupBy.getSortedIndexTo() <= 0) {//没有匹配到排序的group by的列
                distinctAllGroupBy.sortGroupByExprFrom(selectTable, orderBy);
                int size = distinctAllGroupBy.getItemSize();
                for (int i = 0; i < size; i++) {
                    GroupByItem groupByItem = distinctAllGroupBy.getGroupByItem(i);
                    if (i < orderBy.getItemSize() && selectTable.checkEquals(groupByItem.getItemExprEvaluator(), orderBy.getOrderByItem(i).getRsIndexEvaluator())) {
                        groupByItem.setSqlOrderingSpecification(orderBy.getOrderByItem(i).getSqlOrderingSpecification());
                        distinctAllGroupBy.updateSqlExprEvaluator(i, groupByItem);
                    }
                }
            }
            //调整order by的隐藏列
            Iterator<OrderByItem> itemIterator = orderBy.getOrderByItems().iterator();
            while (itemIterator.hasNext()) {
                OrderByItem orderByItem = itemIterator.next();
                int index = selectTable.getIndex(orderByItem.getRsIndexEvaluator());
                if (index >= selectTable.getQueryBound()) {
                    itemIterator.remove();
                }
            }
            if (orderBy.getSortedIndexTo() > selectTable.getQueryBound()) {
                orderBy.setSortedIndexTo(selectTable.getQueryBound());
            }
        }
        if (groupBy == null) {//group by为空时  distinct all的依赖的排序输出到物理sql中
            SQLOrderBy sqlOrderBy = new SQLOrderBy();
            int size = distinctAllGroupBy.getItemSize();
            for (int i = 0; i < size; i++) {
                GroupByItem groupByItem = distinctAllGroupBy.getGroupByItem(i);
                SQLSelectOrderByItem item;
                String label = selectTable.getSelectLabel(selectTable.getIndex(groupByItem.getItemExprEvaluator()));
                if (label != null) {
                    item = new SQLSelectOrderByItem(new SQLIdentifierExpr(label));
                } else {
                    SQLExpr sqlExpr = groupByItem.getItemExprEvaluator().getOriginalSqlExpr();
                    if (sqlExpr instanceof SQLName) {
                        item = new SQLSelectOrderByItem(sqlExpr);
                    } else {
                        item = new SQLSelectOrderByItem(new SQLIdentifierExpr("`" + sqlExpr.toString() + "`"));
                    }
                }
                item.setType(groupByItem.getSqlOrderingSpecification());
                sqlOrderBy.getItems().add(item);
            }
            distinctAllGroupBy.setSortedIndexTo(distinctAllGroupBy.getItemSize());
            currentSqlSelectQueryBlock.setOrderBy(sqlOrderBy);
        }
    }


    public void setLimit(SQLLimit limit) {
        //响应 limit的改变做出改变
        versionForRule6.setLimit(limit);
    }

    // ==============ast接口=======

    protected void accept0(SQLASTVisitor visitor) {
        currentSqlSelectQueryBlock.accept(visitor);
    }

    public SQLSelectQuery getStatement() {
        return currentSqlSelectQueryBlock;
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
