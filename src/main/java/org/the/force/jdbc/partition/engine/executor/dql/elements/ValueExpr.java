package org.the.force.jdbc.partition.engine.executor.dql.elements;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

/**
 * Created by xuji on 2017/7/15.
 */
public class ValueExpr {

    private final SQLExpr sqlExpr;

    public ValueExpr(SQLExpr sqlExpr) {
        this.sqlExpr = sqlExpr;
    }

    public SQLExpr getSqlExpr() {
        return sqlExpr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ValueExprItem valueExprItem = (ValueExprItem) o;

        return getSqlExpr().equals(valueExprItem.getSqlExpr());

    }

    @Override
    public int hashCode() {
        return getSqlExpr().hashCode();
    }
}
