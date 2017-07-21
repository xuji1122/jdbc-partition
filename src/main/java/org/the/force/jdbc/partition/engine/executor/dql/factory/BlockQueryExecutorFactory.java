package org.the.force.jdbc.partition.engine.executor.dql.factory;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.partition.PartitionBlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.logic.LogicBlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.factory.QueryExecutorFactory;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.table.SubQueryResetParser;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.engine.sql.query.ExecutorNodeType;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
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
 * 处理select tableSource的嵌套关系，依据此关系为主判断查询执行器的类型
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

    public BlockQueryExecutor build() {
        return build(this.selectQueryBlock);
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
          第一步：遍历tableSource的嵌套关系
                 探测selectQueryBlock是交给db执行还是要通过client实现，依据就是找到最底层的tableSource是单表的还是多表的
         */
        ExecutorNodeType executorNodeType = detectExecutorNodeType(selectQueryBlock, sqlSelectQueryBlockList);
        /*
         第二步：build执行顺序
         */
        if (executorNodeType.isLogic()) {
            //需要client端自己处理结果集的嵌套关系
            LogicBlockQueryExecutor root = null;//返回的结果
            for (int i = sqlSelectQueryBlockList.size() - 1; i > -1; i--) {//从最底层的查询开始build
                selectQueryBlock = sqlSelectQueryBlockList.get(i);
                if (root == null) {
                    root = new LogicBlockQueryExecutor(logicDbConfig, selectQueryBlock);
                } else {
                    //嵌套
                    selectQueryBlock.setFrom(root);
                    //改变指针，进入下一轮build
                    root = new LogicBlockQueryExecutor(logicDbConfig, selectQueryBlock);
                }
            }
            return root;
        } else {
            //由db处理结果集的嵌套关系，只需要将最外层的查询发给db即可。
            return new PartitionBlockQueryExecutor(logicDbConfig, sqlSelectQueryBlockList.get(0), executorNodeType.getExprConditionalSqlTable());
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

        do {
            sqlSelectQueryBlockList.add(selectQueryBlock);
            if (selectQueryBlock.getFrom() instanceof SQLExprTableSource) {
                return checkExprTableSource(selectQueryBlock);
            } else if (selectQueryBlock.getFrom() instanceof SQLJoinTableSource) {
                return checkJoinTableSource(selectQueryBlock);
            } else if (!(selectQueryBlock.getFrom() instanceof SQLSubqueryTableSource)) {
                //TODO
                throw new SqlParseException(
                    "不支持的tableSource:" + PartitionSqlUtils.toSql(selectQueryBlock, logicDbConfig.getSqlDialect()) + " : from=" + selectQueryBlock.getFrom().getClass().getName());
            }
            selectQueryBlock = checkSQLSubqueryTableSource(selectQueryBlock);
        } while (true);
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
        if(selectQueryBlock.getWhere()!=null){
            TableConditionParser tableConditionParser = new TableConditionParser(logicDbConfig, exprConditionalSqlTable, selectQueryBlock.getWhere());
            selectQueryBlock.setWhere(tableConditionParser.getSubQueryResetWhere());
        }
        return new ExecutorNodeType(false, exprConditionalSqlTable);
    }

    /**
     *判断table join的场景是否需要client自己执行
     * （目前都是client自己执行join的逻辑，未来考虑到小表广播的功能，可能还是交给db执行）
     * @param selectQueryBlock
     * @return
     */
    protected ExecutorNodeType checkJoinTableSource(SQLSelectQueryBlock selectQueryBlock) {
        JoinedTableSourceFactory joinedTableSourceFactory =
            new JoinedTableSourceFactory(logicDbConfig, (SQLJoinTableSource) selectQueryBlock.getFrom(), selectQueryBlock.getWhere());
        SQLExpr newWhere = joinedTableSourceFactory.getOtherCondition(); //tableSource特有的条件过滤掉之后剩余的条件
        //剩余的where条件是否有子查询
        if (newWhere != null) {
            newWhere = (SQLExpr) new SubQueryResetParser(logicDbConfig, newWhere).getSubQueryResetSqlObject();
        }
        selectQueryBlock.setFrom(joinedTableSourceFactory.getJoinedTableSourceExecutor());
        selectQueryBlock.setWhere(newWhere);
        return new ExecutorNodeType(true, null);
    }
}