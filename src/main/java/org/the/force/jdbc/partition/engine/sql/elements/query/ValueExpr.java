package org.the.force.jdbc.partition.engine.sql.elements.query;

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

        ValueExpr valueExpr = (ValueExpr) o;

        return getSqlExpr().equals(valueExpr.getSqlExpr());

    }

    @Override
    public int hashCode() {
        return getSqlExpr().hashCode();
    }
}
