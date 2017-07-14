package org.the.force.jdbc.partition.engine.executor.query.elements;

import org.the.force.jdbc.partition.engine.executor.query.value.SelfRowValueFunction;
import org.the.force.jdbc.partition.engine.result.DataItemRow;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLExprImpl;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/7/13.
 */
public abstract class SelectItem extends SQLExprImpl implements SelfRowValueFunction {

    private final SQLExpr sqlExpr;

    private final int index;

    private final String label;


    public SelectItem(SQLExpr sqlExpr, int index, String label) {
        this.sqlExpr = sqlExpr;
        this.index = index;
        this.label = label;
    }
    public Object getValue(DataItemRow rows) {
        return rows.getValue(getIndex());
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SelectItem that = (SelectItem) o;

        if (getIndex() != that.getIndex())
            return false;
        if (!getSqlExpr().equals(that.getSqlExpr()))
            return false;
        return getLabel().equals(that.getLabel());

    }

    @Override
    public int hashCode() {
        int result = getSqlExpr().hashCode();
        result = 31 * result + getIndex();
        result = 31 * result + getLabel().hashCode();
        return result;
    }

    public SQLExpr getSqlExpr() {
        return sqlExpr;
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }



}
