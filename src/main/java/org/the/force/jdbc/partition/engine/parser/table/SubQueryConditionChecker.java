package org.the.force.jdbc.partition.engine.parser.table;

import org.the.force.jdbc.partition.engine.executor.dql.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionAbstractVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNotExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLQueryExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/14.
 */
public class SubQueryConditionChecker extends PartitionAbstractVisitor {

    private final LogicDbConfig logicDbConfig;

    private List<SQLExpr> subQueryList = new ArrayList<>();

    public SubQueryConditionChecker(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    //替换子查询的类型
    public void preVisit(SQLObject x) {
        if (x instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) x;
            if (sqlBinaryOpExpr.getOperator().isLogical()) {
                SQLExpr left = sqlBinaryOpExpr.getLeft();
                SQLExpr right = sqlBinaryOpExpr.getRight();
                SQLExpr newExpr = checkSubExpr(left);
                if (newExpr != null) {
                    sqlBinaryOpExpr.setLeft(newExpr);
                }
                newExpr = checkSubExpr(right);
                if (newExpr != null) {
                    sqlBinaryOpExpr.setRight(newExpr);
                }
            }
        }
    }
    // ======子查询 check相关====

    public SQLExpr checkSubExpr(SQLExpr x) {
        //保证幂等操作，多个tableSource可能会重复调用
        if (x instanceof ExitsSubQueriedExpr) {
            return null;
        }
        if (x instanceof SQLInSubQueriedExpr) {
            return null;
        }
        if (x instanceof SQLInSubQueryExpr) {
            return new SQLInSubQueriedExpr(logicDbConfig, (SQLInSubQueryExpr) x);
        } else if (x instanceof SQLNotExpr) {
            SQLNotExpr sqlNotExpr = (SQLNotExpr) x;
            SQLExpr sqlExpr = sqlNotExpr.getExpr();
            if (sqlExpr instanceof SQLMethodInvokeExpr) {
                return checkExitsQuery((SQLMethodInvokeExpr) sqlExpr, true);
                //将exitsSubQueriedExpr 设置到x的parent下
            }
        } else if (x instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) x;
            return checkExitsQuery(methodInvokeExpr, false);
        }
        return null;
    }

    public ExitsSubQueriedExpr checkExitsQuery(SQLMethodInvokeExpr methodInvokeExpr, boolean not) {
        if (methodInvokeExpr.getMethodName().equalsIgnoreCase("exits")) {
            List<SQLExpr> parameters = methodInvokeExpr.getParameters();
            if (!parameters.isEmpty() && parameters.size() == 1) {
                SQLExpr pExpr = parameters.get(0);
                if (pExpr instanceof SQLQueryExpr) {
                    SQLQueryExpr sqlQueryExpr = (SQLQueryExpr) pExpr;
                    ExitsSubQueriedExpr r = new ExitsSubQueriedExpr(logicDbConfig, sqlQueryExpr, methodInvokeExpr, not);
                    return r;
                }
            }
        }
        return null;
    }

    public boolean visit(ExitsSubQueriedExpr x) {
        subQueryList.add(x);
        return false;
    }

    public boolean visit(SQLInSubQueriedExpr x) {
        subQueryList.add(x);
        return false;
    }

    public boolean isHasSubQuery() {
        return !subQueryList.isEmpty();
    }

    public List<SQLExpr> getSubQueryList() {
        return subQueryList;
    }
}
