package org.the.force.jdbc.partition.engine.executor.dql.factory;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.logic.JoinedTableBlockQuery;
import org.the.force.jdbc.partition.engine.executor.dql.logic.LogicBlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.logic.PassiveBlockQuery;
import org.the.force.jdbc.partition.engine.executor.dql.partition.PartitionBlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.factory.QueryExecutorFactory;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.query.ExecutorNodeType;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/3.
 * 处理select tableSource的嵌套关系，依据此关系为主判断执行节点的类型，构造执行节点之间的树形关系
 * 执行节点的细节交给具体的执行节点解析执行
 * <p>
 * 逻辑的union查询不支持，执行节点就两种类型
 * 一个是由数据库实现的sql操作  实现类是{@link PartitionBlockQueryExecutor}
 * 一个是client实现的sql操作   实现类是{@link LogicBlockQueryExecutor}
 */
public class BlockQueryExecutorFactory implements QueryExecutorFactory {

    private final LogicDbConfig logicDbConfig;

    private final SQLSelectQueryBlock selectQueryBlock;

    /**
     * @param logicDbConfig
     * @param selectQueryBlock
     */
    public BlockQueryExecutorFactory(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQueryBlock) {
        if (selectQueryBlock.getFrom() instanceof SQLUnionQueryTableSource) {
            throw new SqlParseException("not supported SQLUnionQueryTableSource");
        }
        this.logicDbConfig = logicDbConfig;
        this.selectQueryBlock = selectQueryBlock;
    }

    public BlockQueryExecutor buildQueryExecutor() {
        BlockQueryExecutor blockQueryExecutor = build(this.selectQueryBlock);
        blockQueryExecutor.init();
        return blockQueryExecutor;
    }

    /**
     * 生成查询的执行顺序   BlockQueryExecutor（包装者模式的结构，树型结构）
     *
     * @param selectQueryBlock
     * @return
     */
    public BlockQueryExecutor build(SQLSelectQueryBlock selectQueryBlock) {

        List<SQLSelectQueryBlock> sqlSelectQueryBlockList = new ArrayList<>();//用于从root开始保存嵌套的SQLSelectQueryBlock
        /*
          第一步：遍历tableSource的嵌套关系,确定执行节点的类型
                 探测selectQueryBlock是交给db执行还是要通过client实现，依据就是找到最底层的tableSource是单表的还是多表的
         */
        ExecutorNodeType executorNodeType = detectExecutorNodeType(selectQueryBlock, sqlSelectQueryBlockList);
        /*
         第二步：build执行顺序
         */
        if (executorNodeType.isLogic()) {
            //需要client端自己处理结果集的嵌套关系
            BlockQueryExecutor root = null;//返回root
            for (int i = sqlSelectQueryBlockList.size() - 1; i > -1; i--) {//从最底层的查询开始build
                selectQueryBlock = sqlSelectQueryBlockList.get(i);
                if (root == null) {
                    if (selectQueryBlock instanceof BlockQueryExecutor) {
                        root = (BlockQueryExecutor) selectQueryBlock;
                    } else {
                        root = new JoinedTableBlockQuery(logicDbConfig, selectQueryBlock);
                    }
                } else {
                    //嵌套
                    ConditionalSqlTable conditionalSqlTable = new SqlTableParser(logicDbConfig).getSqlTable(selectQueryBlock.getFrom());
                    if (selectQueryBlock.getWhere() != null) {
                        new TableConditionParser(logicDbConfig, conditionalSqlTable, selectQueryBlock.getWhere());
                    }
                    selectQueryBlock.setFrom(root);
                    //改变指针 root最终指向最外层的query
                    root = new PassiveBlockQuery(logicDbConfig, selectQueryBlock, conditionalSqlTable);
                }
            }
            return root;
        } else {

            /**
             *由db处理结果集的嵌套关系，只需要将最外层的查询发给db即可
             */
            SQLSelectQueryBlock root = sqlSelectQueryBlockList.get(0);
            if (sqlSelectQueryBlockList.size() > 1) {
                ConditionalSqlTable conditionalSqlTable = new SqlTableParser(logicDbConfig).getSqlTable(root.getFrom());
                new SqlTableReferParser(logicDbConfig, root, conditionalSqlTable);
                return new PartitionBlockQueryExecutor(logicDbConfig, root, executorNodeType.getExprConditionalSqlTable(), conditionalSqlTable);
            } else {
                return new PartitionBlockQueryExecutor(logicDbConfig, root, executorNodeType.getExprConditionalSqlTable());
            }
        }
    }

    /**
     * 遍历tableSource的嵌套关系
     * 探测selectQueryBlock是交给db执行还是要通过client实现，依据就是找到最底层的tableSource是单表的还是多表的
     * 从root到底部，依次将SQLSelectQueryBlock add到sqlSelectQueryBlockList
     *
     * @param selectQueryBlock
     * @param sqlSelectQueryBlockList
     * @return
     */
    private ExecutorNodeType detectExecutorNodeType(SQLSelectQueryBlock selectQueryBlock, List<SQLSelectQueryBlock> sqlSelectQueryBlockList) {
        int maxSubQuery = 64;
        int subQueryCount = 0;
        do {
            sqlSelectQueryBlockList.add(selectQueryBlock);
            if (selectQueryBlock.getFrom() instanceof SQLJoinTableSource) {
                return checkJoinTableSource(selectQueryBlock);
            } else if (selectQueryBlock.getFrom() instanceof SQLExprTableSource) {
                return checkExprTableSource(selectQueryBlock);
            } else if (selectQueryBlock.getLimit() != null) {
                SQLLimit copy = selectQueryBlock.getLimit();
                selectQueryBlock.setLimit(null);
                BlockQueryExecutor blockQueryExecutor = build(selectQueryBlock);
                if (blockQueryExecutor instanceof SQLSelectQueryBlock) {
                    selectQueryBlock = (SQLSelectQueryBlock) blockQueryExecutor;
                    selectQueryBlock.setLimit(copy);
                    blockQueryExecutor.init();
                    sqlSelectQueryBlockList.set(sqlSelectQueryBlockList.size() - 1, selectQueryBlock);
                } else {
                    throw new SqlParseException("buildQueryExecutor result is not SQLSelectQueryBlock");
                }
                return new ExecutorNodeType(true, null);
            } else if (!(selectQueryBlock.getFrom() instanceof SQLSubqueryTableSource)) {
                //TODO
                throw new SqlParseException(
                    "不支持的tableSource:" + PartitionSqlUtils.toSql(selectQueryBlock, logicDbConfig.getSqlDialect()) + " : from=" + selectQueryBlock.getFrom().getClass().getName());
            }
            selectQueryBlock = checkSQLSubqueryTableSource(selectQueryBlock);
        } while (++subQueryCount <= maxSubQuery);
        throw new SqlParseException("子查询嵌套太多");
    }

    private SQLSelectQueryBlock checkSQLSubqueryTableSource(SQLSelectQueryBlock selectQueryBlock) {
        SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) selectQueryBlock.getFrom();
        SQLSelectQuery sqlSelectQuery = subqueryTableSource.getSelect().getQuery();
        if (sqlSelectQuery == null) {
            throw new SqlParseException("query sqlSelectQuery == null");
        }
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) sqlSelectQuery;
        } else {
            throw new SqlParseException("unsupported select query:" + PartitionSqlUtils.toSql(sqlSelectQuery, logicDbConfig.getSqlDialect()));
        }
    }

    protected ExecutorNodeType checkExprTableSource(SQLSelectQueryBlock selectQueryBlock) {
        ExprConditionalSqlTable exprConditionalSqlTable = (ExprConditionalSqlTable) new SqlTableParser(logicDbConfig).getSqlTable(selectQueryBlock.getFrom());
        if (selectQueryBlock.getWhere() != null) {
            new TableConditionParser(logicDbConfig, exprConditionalSqlTable, selectQueryBlock.getWhere());
        }
        //最底层的query,由于partitionBlockQuery不去检测，所以为了确保alias被正确设置，从底层检测
        new SqlTableReferParser(logicDbConfig, selectQueryBlock, exprConditionalSqlTable);
        return new ExecutorNodeType(false, exprConditionalSqlTable);
    }

    /**
     * 判断table join的场景是否需要client自己执行
     * （目前都是client自己执行join的逻辑，未来考虑到小表广播的功能，可能还是交给db执行）
     *
     * @param selectQueryBlock
     * @return
     */
    protected ExecutorNodeType checkJoinTableSource(SQLSelectQueryBlock selectQueryBlock) {
        JoinedTableSourceFactory joinedTableSourceFactory =
            new JoinedTableSourceFactory(logicDbConfig, (SQLJoinTableSource) selectQueryBlock.getFrom(), selectQueryBlock.getWhere());
        SQLExpr newWhere = joinedTableSourceFactory.getNewWhereCondition(); //tableSource特有的条件过滤掉之后剩余的条件
        selectQueryBlock.setFrom(joinedTableSourceFactory.getExecutableJoinedTableSource());
        selectQueryBlock.setWhere(newWhere);
        return new ExecutorNodeType(true, null);
    }
}
