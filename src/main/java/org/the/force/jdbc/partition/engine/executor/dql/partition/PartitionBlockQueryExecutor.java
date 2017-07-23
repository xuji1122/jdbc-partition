package org.the.force.jdbc.partition.engine.executor.dql.partition;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.AggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.AvgAggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.factory.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SelectLabelParser;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.query.PartitionSelectTable;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectGroupByClause;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/18.
 * 交给db执行的节点,支持router(路由，sql改写)和merge分区结果
 * <p>
 * ===================适用范围============================
 * 目前适用的范围是
 * from是单表的查询 无论from嵌套多少子查询，只要操作的逻辑表只有一个即可。
 * 如果查询的条件中含有子查询，那么同样支持（实际就是输出物理sql时延迟做子查询）
 * <p>
 * ==================sql执行规则===============================
 * 为了让客户端能够merge结果集,同时兼顾性能等综合因素考虑，约定规则如下
 * 1,group by的列都必须放入select的结果集中，如果没有则自动改写sql放入
 * 2,order by的条件涉及的列必须在select的结果集中，如果没有则自动改写sql放入
 * 3,如果结果集中含有 avg聚合,那么对avg的入参 结果集中必须有同样参数的count和sum，如果没有自动改写sql放入
 * 4,limit最后一个执行，在router输出物理sql时对每个分区改写为从0开始的endRows
 * 5,如果有group by的列，那么结果集优先依据group by的列排序
 * 5.1 order by条件对数据库失效不输出，交给client实现
 * 5.2 limit的条件对数据库失效不输出，交给client实现
 * 6,如果有having，那么having的过滤交给客户端执行实现，因此，having的条件涉及的aggregation必须放入select的结果集中
 * 6.1 order by条件对数据库失效不输出，交给client实现
 * 6.2 limit的条件对数据库失效不输出，交给client实现
 * 7，如果结果集是distinct
 * 7.1 如果结果集含有聚合操作，
 * 7.1.1 distinct对数据库失效不输出，交给client实现
 * 7.1.2 order by条件对数据库失效不输出，交给client实现
 * 7.1.3 limit的条件对数据库失效不输出，交给client实现
 * 7.2 如果结果集没有聚合操作，那么如同查询的结果集全列做group by，交给规则5处理
 * <p>
 * ====================例外规则==========================================================================
 * 8,如果分区路由的结果只有一个物理sql需要执行则1-7的规则都无效
 * 9，如果路由的结果的表有多个但是在同一个库，那么物理sql改写为union,1-7的规则同样无效
 */
public class PartitionBlockQueryExecutor extends SQLSelectQueryBlock implements BlockQueryExecutor {

    private final LogicDbConfig logicDbConfig;

    private final ExprConditionalSqlTable innerExprSqlTable;

    private final ConditionalSqlTable outerSqlTable;

    //原始的originalSqlSelectQueryBlock 不去修改，在符合例外条件时使用此对象输出sql
    private final SQLSelectQueryBlock originalSqlSelectQueryBlock;

    private final SQLSelectQueryBlock sqlSelectQueryBlock;

    private final TableRouter tableRouter;

    private final PartitionSelectTable selectTable;

    public PartitionBlockQueryExecutor(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock, ExprConditionalSqlTable innerExprSqlTable) {
        this.logicDbConfig = logicDbConfig;
        originalSqlSelectQueryBlock = sqlSelectQueryBlock;
        this.sqlSelectQueryBlock = new SqlObjCopier().copy(sqlSelectQueryBlock);
        //最底层的sqlTable，和sqlSelectQueryBlock的tableSource未必是直接对应的
        this.innerExprSqlTable = innerExprSqlTable;
        tableRouter = new DefaultTableRouter(logicDbConfig, sqlSelectQueryBlock, innerExprSqlTable);
        outerSqlTable = new SqlTableParser(logicDbConfig).getSqlTable(sqlSelectQueryBlock.getFrom());
        if (sqlSelectQueryBlock.getDistionOption() > 0) {
            selectTable = new PartitionSelectTable(outerSqlTable, true);
        } else {
            selectTable = new PartitionSelectTable(outerSqlTable, false);
        }
        init();
    }

    public void init() {
        extendAllColumns();
        resolveItems();
        resolveAggregate();
    }

    protected void resolveItems() {
        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        int size = sqlSelectItems.size();
        boolean hasAggregate = false;
        List<AvgAggregateEvaluator> avgAggregates = new ArrayList<>();
        List<AvgAggregateEvaluator> countAggregates = new ArrayList<>();
        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        for (int i = 0; i < size; i++) {
            SQLSelectItem item = sqlSelectItems.get(i);
            SQLExpr itemExpr = item.getExpr();
            SqlRefer sqlRefer = null;
            String label = item.getAlias();
            SqlExprEvaluator sqlExprEvaluator;
            if (itemExpr instanceof SQLAllColumnExpr) {
                selectTable.addAllColumnItem(item);
                continue;
            } else if (itemExpr instanceof SQLPropertyExpr) {
                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) itemExpr;
                sqlRefer = new SqlRefer(sqlPropertyExpr);
                if (sqlRefer.getName().equals("*")) {
                    selectTable.addAllColumnItem(item);
                    continue;
                }
            } else if (itemExpr instanceof SQLName) {
                SQLName sqlName = (SQLName) itemExpr;
                sqlRefer = new SqlRefer(sqlName);
            }
            if (sqlRefer != null) {
                sqlExprEvaluator = sqlRefer;
                if (label == null) {
                    label = sqlRefer.getName();
                }
            } else {
                if (label == null) {
                    label = itemExpr.toString();
                }
                sqlExprEvaluator = sqlExprEvaluatorFactory.matchSqlExprEvaluator(itemExpr);
            }
            if (sqlExprEvaluator instanceof AggregateEvaluator) {
                hasAggregate = true;
            }
            if (sqlExprEvaluator instanceof AvgAggregateEvaluator) {

            }

            selectTable.addValueNode(label, sqlExprEvaluator);
        }
        //group by
        //order by
        //limit
    }

    protected void resolveAggregate() {
        SQLSelectGroupByClause sqlSelectGroupByClause = sqlSelectQueryBlock.getGroupBy();
        List<SQLExpr> items = sqlSelectGroupByClause.getItems();
    }

    public void extendAllColumns() {
        SelectLabelParser selectLabelParser = new SelectLabelParser(logicDbConfig);
        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        int size = sqlSelectItems.size();
        String tableAlias = outerSqlTable.getAlias();
        List<SQLSelectItem> newItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SQLSelectItem oldItem = sqlSelectItems.get(i);
            SQLExpr itemExpr = oldItem.getExpr();
            if (itemExpr instanceof SQLAllColumnExpr) {
                if (oldItem.getAlias() != null) {
                    throw new SqlParseException("oldItem.getAlias()!=null and SQLAllColumnExpr");
                }
                List<String> labels = selectLabelParser.getAllColumns(outerSqlTable.getSQLTableSource(), null);
                if (labels == null || labels.isEmpty()) {
                    newItems.add(oldItem);
                    continue;
                }
                for (String lable : labels) {
                    SQLSelectItem newItem = new SQLSelectItem();
                    newItems.add(newItem);
                    if (tableAlias != null) {
                        newItem.setExpr(new SQLPropertyExpr(tableAlias, lable));
                    }
                }
            } else if (itemExpr instanceof SQLPropertyExpr) {
                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) itemExpr;
                SqlRefer sqlRefer = new SqlRefer(sqlPropertyExpr);
                if (sqlRefer.getName().equals("*")) {
                    List<String> labels = selectLabelParser.getAllColumns(outerSqlTable.getSQLTableSource(), sqlRefer.getOwnerName());
                    if (labels == null || labels.isEmpty()) {
                        newItems.add(oldItem);
                        continue;
                    }
                    for (String lable : labels) {
                        SQLSelectItem newItem = new SQLSelectItem();
                        newItems.add(newItem);
                        newItem.setExpr(new SQLPropertyExpr(sqlRefer.getOwnerName(), lable));
                    }
                } else {
                    newItems.add(oldItem);
                }
            } else {
                newItems.add(oldItem);
            }
        }
        sqlSelectQueryBlock.getSelectList().clear();
        sqlSelectQueryBlock.getSelectList().addAll(newItems);
    }

    public void setLimit(SQLLimit limit) {
        //响应 limit的改变做出改变
        sqlSelectQueryBlock.setLimit(limit);
    }

    protected void accept0(SQLASTVisitor visitor) {
        sqlSelectQueryBlock.accept(visitor);
    }

    public SQLSelectQuery getStatement() {
        return sqlSelectQueryBlock;
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
