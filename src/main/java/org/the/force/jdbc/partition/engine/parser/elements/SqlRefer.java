package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.thirdparty.druid.sql.ast.SQLExprImpl;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/5/27.
 * 在sql文中，对列的引用或者对表格的引用
 */
public class SqlRefer extends SQLExprImpl{
    private final String ownerName;
    private final String name;

    public SqlRefer(String ownerName, String name) {
        this.ownerName = ownerName;
        this.name = name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SqlRefer that = (SqlRefer) o;
        if (getOwnerName() != null) {
            if (!getOwnerName().equals(that.getOwnerName()))
                return false;
        }
        return getName().equals(that.getName());

    }

    public int hashCode() {
        int result = getName().hashCode();
        if (getOwnerName() != null) {
            result = 31 * result + getOwnerName().hashCode();
        }
        return result;
    }

    protected void accept0(SQLASTVisitor visitor) {

    }
}
