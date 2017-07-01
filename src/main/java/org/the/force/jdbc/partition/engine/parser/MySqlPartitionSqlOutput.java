package org.the.force.jdbc.partition.engine.parser;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.expr.SQLCharExpr;
import org.druid.sql.ast.expr.SQLInListExpr;
import org.druid.sql.ast.expr.SQLVariantRefExpr;
import org.druid.sql.ast.statement.SQLExprTableSource;
import org.druid.sql.ast.statement.SQLInsertStatement;
import org.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import org.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import org.druid.sql.visitor.ExportParameterVisitor;
import org.druid.sql.visitor.ExportParameterVisitorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.the.force.jdbc.partition.engine.plan.model.SqlExprTable;
import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.plan.model.SqlTablePartition;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.rule.Partition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xuji on 2017/6/9.
 */
public class MySqlPartitionSqlOutput extends MySqlOutputVisitor implements PartitionSqlASTVisitor {

    private final SqlTablePartition sqlTablePartition;

    private final LogicSqlParameterHolder logicSqlParameterHolder;

    private List<SQLInsertStatement.ValuesClause> valuesClauses = new ArrayList<>();

    private List<Pair<SQLInListExpr, List<SQLExpr>>> inListExprCollection = new ArrayList<>();

    private final List<SqlParameter> sqlParameterList = new ArrayList<>();

    private boolean parametric;

    public MySqlPartitionSqlOutput(Appendable appender, SqlTablePartition sqlTablePartition, LogicSqlParameterHolder logicSqlParameterHolder) {
        super(appender, false);
        super.setShardingSupport(false);
        super.setPrettyFormat(false);
        this.sqlTablePartition = sqlTablePartition;
        this.logicSqlParameterHolder = logicSqlParameterHolder;
    }

    public List<SQLInsertStatement.ValuesClause> getValuesClauses() {
        return valuesClauses;
    }

    public List<SqlParameter> getSqlParameterList() {
        return sqlParameterList;
    }

    public void setInListExprCollection(List<Pair<SQLInListExpr, List<SQLExpr>>> inListExprCollection) {
        this.inListExprCollection = inListExprCollection;
    }


    public boolean visit(SQLExprTableSource x) {

        SqlExprTable sqlExprTable = sqlTablePartition.getSqlExprTable();
        Partition partition = sqlTablePartition.getPartition();
        if (!sqlExprTable.getSchema().equals(PartitionJdbcConstants.EMPTY_NAME)) {
            print(partition.getPhysicDbName());
            print(".");
        }
        print(partition.getPhysicTableName());
        if (sqlExprTable.getAlias() != null) {
            println();//分割符
            print(sqlExprTable.getAlias());
        }
        return false;
    }

    protected void printValuesList(MySqlInsertStatement x) {

        List<SQLInsertStatement.ValuesClause> valuesList = valuesClauses;

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
        if (inListExprCollection == null) {
            return super.visit(x);
        }
        if (x instanceof SQLInSubQueriedExpr) {
            //TODO 子查询的结果集
        }
        Iterator<Pair<SQLInListExpr, List<SQLExpr>>> inListExprListPairIte = inListExprCollection.iterator();
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
        SqlParameter sqlParameter = logicSqlParameterHolder.getSqlParameter(x.getIndex());
        sqlParameterList.add(sqlParameter);
        return super.visit(x);
    }

    @Override
    public boolean visit(ExitsSubQueriedExpr x) {
        return false;
    }

    @Override
    public boolean visit(SQLInSubQueriedExpr x) {
        return false;
    }

    public boolean isParametric() {
        return parametric;
    }
}
