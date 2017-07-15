package org.the.force.jdbc.partition.engine.parser.output;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.executor.dql.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.UnionQueriedTableSource;
import org.the.force.jdbc.partition.engine.parameter.IntegerSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.executor.eval.SqlValueEvalContext;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTablePartition;
import org.the.force.jdbc.partition.engine.parser.router.RouteEvent;
import org.the.force.jdbc.partition.engine.executor.eval.SqlExprEvalFunctionFactory;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCharExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import org.the.force.thirdparty.druid.sql.visitor.ExportParameterVisitor;
import org.the.force.thirdparty.druid.sql.visitor.ExportParameterVisitorUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        if (x instanceof SQLInSubQueriedExpr) {
            //TODO 子查询的结果集
        }
        Iterator<Pair<SQLInListExpr, List<SQLExpr>>> inListExprListPairIte = sqlTablePartition.getSubInListExpr().iterator();
        List<SQLExpr> targetList = null;
        while (inListExprListPairIte.hasNext()) {
            Pair<SQLInListExpr, List<SQLExpr>> pair = inListExprListPairIte.next();
            if (pair.getLeft() == x) {
                inListExprListPairIte.remove();
                targetList = pair.getRight();
            }
        }
        if (targetList == null) {
            return super.visit(x);
        }
        if (this.parameterized) {
            boolean changed = true;
            if (targetList.size() == 1 && targetList.get(0) instanceof SQLVariantRefExpr) {
                changed = false;
            }

            x.getExpr().accept(this);

            if (x.isNot()) {
                print(isUppCase() ? " NOT IN (?)" : " not in (?)");
            } else {
                print(isUppCase() ? " IN (?)" : " in (?)");
            }

            if (changed) {
                incrementReplaceCunt();
                if (this instanceof ExportParameterVisitor || this.parameters != null) {
                    if (parameterizedMergeInList) {
                        List<Object> subList = new ArrayList<Object>(x.getTargetList().size());
                        for (SQLExpr target : x.getTargetList()) {
                            ExportParameterVisitorUtils.exportParameter(subList, target);
                        }
                        if (subList != null) {
                            parameters.add(subList);
                        }
                    } else {
                        for (SQLExpr target : x.getTargetList()) {
                            ExportParameterVisitorUtils.exportParameter(this.parameters, target);
                        }
                    }
                }
            }

            return false;
        }

        x.getExpr().accept(this);

        if (x.isNot()) {
            print0(ucase ? " NOT IN (" : " not in (");
        } else {
            print0(ucase ? " IN (" : " in (");
        }

        final List<SQLExpr> list = targetList;

        boolean printLn = false;
        if (list.size() > 5) {
            printLn = true;
            for (int i = 0, size = list.size(); i < size; ++i) {
                if (!(list.get(i) instanceof SQLCharExpr)) {
                    printLn = false;
                    break;
                }
            }
        }

        if (printLn) {
            incrementIndent();
            println();
            for (int i = 0, size = list.size(); i < size; ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                list.get(i).accept(this);
            }
            decrementIndent();
            println();
        } else {
            printAndAccept(list, ", ");
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
        SqlValueEvalContext sqlValueEvalContext = new SqlValueEvalContext(logicDbConfig);
        SqlExprEvalFunctionFactory sqlExprEvalFunctionFactory = SqlExprEvalFunctionFactory.getSingleton();
        int from = 1;
        int rowCount;

        if (x.getOffset() != null) {
            try {
                Object v = sqlExprEvalFunctionFactory.matchSqlValueFunction(x.getOffset()).getValue(sqlValueEvalContext, routeEvent.getLogicSqlParameterHolder(), null);
                from = Integer.parseInt(v.toString());
            } catch (SQLException e) {
                throw new SqlParseException("limit不是数字不正确", e);
            }
        }
        try {
            Object v = sqlExprEvalFunctionFactory.matchSqlValueFunction(x.getRowCount()).getValue(sqlValueEvalContext, routeEvent.getLogicSqlParameterHolder(), null);
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

    public boolean visit(ExitsSubQueriedExpr x) {
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
