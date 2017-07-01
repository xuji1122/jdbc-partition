package org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr;

import org.druid.sql.SQLUtils;
import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLExprImpl;
import org.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.druid.sql.ast.expr.SQLNotExpr;
import org.druid.sql.ast.expr.SQLQueryExpr;
import org.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;

/**
 * Created by xuji on 2017/6/3.
 * 作为叶子节点
 */
public class ExitsSubQueriedExpr extends SQLExprImpl implements SQLExpr {

    private final LogicDbConfig logicDbConfig;

    private final SQLQueryExpr sqlQueryExpr;

    private final boolean not;

    private final SQLMethodInvokeExpr sqlMethodInvokeExpr;

    public ExitsSubQueriedExpr(LogicDbConfig logicDbConfig,SQLQueryExpr sqlQueryExpr, SQLMethodInvokeExpr sqlMethodInvokeExpr, boolean not) {
        this.logicDbConfig = logicDbConfig;
        this.sqlQueryExpr = sqlQueryExpr;
        super.setParent(sqlQueryExpr.getParent());
        attributes = sqlQueryExpr.getAttributesDirect();
        this.sqlMethodInvokeExpr = sqlMethodInvokeExpr;
        this.not = not;
    }

    public boolean isNot() {
        return not;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            PartitionSqlASTVisitor partitionSqlASTVisitor = (PartitionSqlASTVisitor) visitor;
            partitionSqlASTVisitor.visit(this);
        } else {
            if (not) {
                SQLNotExpr sqlNotExpr = new SQLNotExpr();
                sqlNotExpr.setExpr(sqlMethodInvokeExpr);
                sqlNotExpr.setParent(this.getParent());
                visitor.visit(sqlNotExpr);
            } else {
                visitor.visit(sqlMethodInvokeExpr);
            }

        }
    }

    public SQLQueryExpr getSqlQueryExpr() {
        return sqlQueryExpr;
    }


    public boolean equals(Object o) {
        if (o instanceof ExitsSubQueriedExpr) {
            return sqlQueryExpr.equals(((ExitsSubQueriedExpr) o).getSqlQueryExpr());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return sqlQueryExpr.hashCode();
    }


    public String toString() {
        return not + ":" + SQLUtils.toMySqlString(sqlQueryExpr);
    }
}
