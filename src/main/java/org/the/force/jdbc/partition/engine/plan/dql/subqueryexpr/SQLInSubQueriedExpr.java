package org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr;

import org.druid.sql.SQLUtils;
import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.expr.SQLInListExpr;
import org.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/4.
 * 叶子节点
 */
public class SQLInSubQueriedExpr extends SQLInListExpr implements SQLExpr {
    private final LogicDbConfig logicDbConfig;
    private final SQLInSubQueryExpr sqlInSubQueryExpr;

    public SQLInSubQueriedExpr(LogicDbConfig logicDbConfig, SQLInSubQueryExpr sqlInSubQueryExpr) {
        this.logicDbConfig = logicDbConfig;
        this.sqlInSubQueryExpr = sqlInSubQueryExpr;
        super.setParent(sqlInSubQueryExpr.getParent());
        super.attributes = sqlInSubQueryExpr.getAttributesDirect();
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            PartitionSqlASTVisitor partitionSqlASTVisitor = (PartitionSqlASTVisitor) visitor;
            partitionSqlASTVisitor.visit(this);
        } else {
            visitor.visit(sqlInSubQueryExpr);
        }
    }

    public SQLInSubQueryExpr getSqlInSubQueryExpr() {
        return sqlInSubQueryExpr;
    }


    public boolean equals(Object o) {
        if (o instanceof SQLInSubQueriedExpr) {
            return sqlInSubQueryExpr.equals(((SQLInSubQueriedExpr) o).getSqlInSubQueryExpr());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return sqlInSubQueryExpr.hashCode();
    }

    public boolean isNot() {
        return sqlInSubQueryExpr.isNot();
    }

    public void setNot(boolean not) {

    }

    public SQLExpr getExpr() {
        return sqlInSubQueryExpr.getExpr();
    }

    public void setExpr(SQLExpr expr) {

    }

    public List<SQLExpr> getTargetList() {
        return new ArrayList<>();
    }

    public void setTargetList(List<SQLExpr> targetList) {

    }

    public String toString() {
        return SQLUtils.toMySqlString(sqlInSubQueryExpr);
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }
}
