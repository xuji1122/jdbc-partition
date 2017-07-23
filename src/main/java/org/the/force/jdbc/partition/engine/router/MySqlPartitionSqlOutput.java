package org.the.force.jdbc.partition.engine.router;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.factory.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ExecutableJoinedTableSource;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.engine.sql.SqlTablePartition;
import org.the.force.jdbc.partition.engine.sql.table.ExprSqlTable;
import org.the.force.jdbc.partition.engine.value.SqlLiteral;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.types.IntValue;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/9.
 */
public class MySqlPartitionSqlOutput extends MySqlOutputVisitor implements PartitionSqlASTVisitor {

    private final LogicDbConfig logicDbConfig;

    private final RouteEvent routeEvent;

    private final SqlTablePartition sqlTablePartition;

    private final List<SqlParameter> sqlParameterList = new ArrayList<>();

    private boolean parametric;

    public MySqlPartitionSqlOutput(Appendable appender, LogicDbConfig logicDbConfig, RouteEvent routeEvent, SqlTablePartition sqlTablePartition) {
        super(appender, false);
        super.setShardingSupport(false);
        super.setPrettyFormat(false);
        this.logicDbConfig = logicDbConfig;
        this.routeEvent = routeEvent;
        this.sqlTablePartition = sqlTablePartition;
    }

    public List<SqlParameter> getSqlParameterList() {
        return sqlParameterList;
    }

    /**
     * tableSource重写的逻辑
     *
     * @param x
     * @return
     */
    public boolean visit(SQLExprTableSource x) {

        ExprSqlTable exprSqlTable = sqlTablePartition.getExprSqlTable();
        if (!exprSqlTable.getSQLTableSource().equals(x)) {
            return super.visit(x);
        }
        Partition partition = sqlTablePartition.getPartition();
        if (!exprSqlTable.getSchema().equals(PartitionJdbcConstants.EMPTY_NAME)) {
            print(partition.getPhysicDbName());
            print(".");
        }
        print(partition.getPhysicTableName());
        if (exprSqlTable.getAlias() != null) {
            println();//分割符
            print(exprSqlTable.getAlias());
        }
        return false;
    }

    /**
     * insert values的输出的逻辑
     *
     * @param x
     * @return
     */
    protected List<SQLInsertStatement.ValuesClause> getValuesList(MySqlInsertStatement x) {
        return sqlTablePartition.getValuesClauses();
    }

    /**
     * in 表达式输出的逻辑
     *
     * @param x
     * @return
     */
    public boolean visit(SQLInListExpr x) {
        Map<SQLExpr, List<Object[]>> map = sqlTablePartition.getSubInListExpr();
        List<Object[]> targetList = map.get(x);
        if (targetList == null) {
            return super.visit(x);
        }
        x.getExpr().accept(this);
        if (x.isNot()) {
            print0(ucase ? " NOT IN (" : " not in (");
        } else {
            print0(ucase ? " IN (" : " in (");
        }
        final List<Object[]> list = targetList;
        printInValues(list);
        return false;
    }

    protected void printInValues(final List<Object[]> list) {
        int rowSize = list.size();

        for (int rowCount = 0; rowCount < rowSize; rowCount++) {
            if (rowCount > 0) {
                print0(",");
            }
            Object[] columnArray = list.get(rowCount);
            if (rowSize > 1 && columnArray.length > 1) {
                print0("(");
            }
            for (int columnCount = 0; columnCount < columnArray.length; columnCount++) {
                if (columnCount > 0) {
                    print0(",");
                }
                Object value = columnArray[columnCount];
                if (value instanceof SqlParameter) {
                    print0("?");
                    parametric = true;
                    sqlParameterList.add((SqlParameter) value);
                } else if (value instanceof SqlLiteral) {
                    print0(((SqlLiteral) value).toSql());
                } else {
                    throw new SqlParseException("无法解析sql参数");
                }
            }
            if (rowSize > 1 && columnArray.length > 1) {
                print0(")");
            }
        }

        print(')');
    }

    public boolean visit(SQLVariantRefExpr x) {
        parametric = true;
        SqlParameter sqlParameter = routeEvent.getLogicSqlParameterHolder().getSqlParameter(x.getIndex());
        sqlParameterList.add(sqlParameter);
        return super.visit(x);
    }

    public boolean visit(SQLLimit x) {
        if (sqlTablePartition.getTotalPartitions() <= 1) {
            return super.visit(x);
        }
        print0(ucase ? "LIMIT " : "limit ");
        SqlExprEvalContext sqlExprEvalContext = new SqlExprEvalContext(routeEvent.getLogicSqlParameterHolder());
        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        int from = 1;
        int rowCount;

        if (x.getOffset() != null) {
            try {
                Object v = sqlExprEvaluatorFactory.matchSqlExprEvaluator(x.getOffset()).eval(sqlExprEvalContext, null);
                from = Integer.parseInt(v.toString());
            } catch (SQLException e) {
                throw new SqlParseException("limit不是数字不正确", e);
            }
        }
        try {
            Object v = sqlExprEvaluatorFactory.matchSqlExprEvaluator(x.getRowCount()).eval(sqlExprEvalContext, null);
            rowCount = Integer.parseInt(v.toString());
        } catch (SQLException e) {
            throw new SqlParseException("limit不是数字不正确", e);
        }
        rowCount = (from - 1 + rowCount) * sqlTablePartition.getTotalPartitions() + 1;
        print0("? ");
        parametric = true;
        sqlParameterList.add(new IntValue(rowCount));
        return false;
    }


    public boolean isParametric() {
        return parametric;
    }

    public RouteEvent getRouteEvent() {
        return routeEvent;
    }

    /**
     * TODO
     * 需要执行查询，
     * 查询的结果需要关联语境输出sql?
     * 一行一列
     * 一行多列
     * 多行多列
     *
     * @param x
     * @return
     */
    public boolean visit(SubQueriedExpr x) {
        return false;
    }

    /**
     * TODO
     *
     * @param x
     * @return
     */
    public boolean visit(SQLInSubQueriedExpr x) {
        Map<SQLExpr, List<Object[]>> map = sqlTablePartition.getSubInListExpr();
        List<Object[]> targetList = map.get(x);
        if (targetList == null) {

        } else {

        }
        x.getExpr().accept(this);
        return false;
    }

    public boolean visit(ExecutableJoinedTableSource executableJoinedTableSource) {
        return false;
    }

}
