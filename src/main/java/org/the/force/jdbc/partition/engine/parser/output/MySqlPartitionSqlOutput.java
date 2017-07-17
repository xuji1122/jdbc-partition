package org.the.force.jdbc.partition.engine.parser.output;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.UnionQueriedTableSource;
import org.the.force.jdbc.partition.engine.parameter.IntegerSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.ObjectSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTablePartition;
import org.the.force.jdbc.partition.engine.parser.router.RouteEvent;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.Partition;
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

    public boolean visit(SQLExprTableSource x) {

        ExprSqlTable exprSqlTable = sqlTablePartition.getExprSqlTable();
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

    protected void printValuesList(MySqlInsertStatement x) {

        List<SQLInsertStatement.ValuesClause> valuesList = sqlTablePartition.getValuesClauses();

        if (parameterized) {
            print0(ucase ? "VALUES " : "values ");
            incrementIndent();
            valuesList.get(0).accept(this);
            decrementIndent();
            if (valuesList.size() > 1) {
                this.incrementReplaceCunt();
            }
            return;
        }

        print0(ucase ? "VALUES " : "values ");
        if (x.getValuesList().size() > 1) {
            incrementIndent();
        }
        for (int i = 0, size = valuesList.size(); i < size; ++i) {
            if (i != 0) {
                print(',');
                println();
            }
            valuesList.get(i).accept(this);
        }
        if (valuesList.size() > 1) {
            decrementIndent();
        }
    }

    public boolean visit(SQLInListExpr x) {
        if (sqlTablePartition.getSubInListExpr() == null || sqlTablePartition.getSubInListExpr().isEmpty()) {
            return super.visit(x);
        }
        //        if (x instanceof SQLInSubQueriedExpr) {
        //            //TODO 子查询的结果集
        //        }
        Map<SQLInListExpr, List<Object[]>> map = sqlTablePartition.getSubInListExpr();
        List<Object[]> targetList = map.get(x);

        if (targetList == null) {
            if(x instanceof SQLInSubQueriedExpr){
                //TODO 触发子查询
            }else{
                return super.visit(x);
            }
        }
        x.getExpr().accept(this);

        if (x.isNot()) {
            print0(ucase ? " NOT IN (" : " not in (");
        } else {
            print0(ucase ? " IN (" : " in (");
        }

        final List<Object[]> list = targetList;
        int rowSize = list.size();

        for (int rowCount = 0; rowCount < rowSize; rowCount++) {
            if(rowCount>0){
                print0(",");
            }
            Object[] columnArray = list.get(rowCount);
            if (rowSize > 1) {
                print0("(");
            }
            for (int columnCount = 0; columnCount < columnArray.length; columnCount++) {
                if (columnCount > 0) {
                    print0(",");
                }
                print0("?");
                sqlParameterList.add(new ObjectSqlParameter(columnArray[columnCount]));
            }
            if (rowSize > 1) {
                print0(")");
            }
        }

        print(')');
        return false;
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
        sqlParameterList.add(new IntegerSqlParameter(rowCount));
        return false;
    }

    public boolean visit(SubQueriedExpr x) {
        return false;
    }

    public boolean visit(SQLInSubQueriedExpr x) {
        return false;
    }

    public boolean isParametric() {
        return parametric;
    }

    public RouteEvent getRouteEvent() {
        return routeEvent;
    }


    public boolean visit(ParallelJoinedTableSource parallelJoinedTableSource) {
        return false;
    }

    public boolean visit(SubQueriedTableSource subQueriedTableSource) {
        return false;
    }

    public boolean visit(UnionQueriedTableSource unionQueriedTableSource) {
        return false;
    }
}
