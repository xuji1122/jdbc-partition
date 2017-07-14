package org.the.force.jdbc.partition.engine.executor.dql.value;

import org.the.force.jdbc.partition.engine.executor.dql.elements.ValueItem;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/13.
 */
public class ExprValueItem extends ValueItem implements SQLExpr, ReferRowValueFunction {

    private SQLExpr sqlExpr;

    public ExprValueItem(SQLExpr sqlExpr, int index, String label) {
        super(index, label);
        this.sqlExpr = sqlExpr;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ExprValueItem that = (ExprValueItem) o;

        return sqlExpr.equals(that.sqlExpr);

    }

    public int hashCode() {
        return sqlExpr.hashCode();
    }

    public SQLExpr clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    public final void accept(SQLASTVisitor visitor) {
        sqlExpr.accept(visitor);
    }

    public SQLObject getParent() {
        return sqlExpr.getParent();
    }

    public void setParent(SQLObject parent) {
        sqlExpr.setParent(parent);
    }

    public Map<String, Object> getAttributes() {
        return sqlExpr.getAttributes();
    }

    public Object getAttribute(String name) {
        return sqlExpr.getAttribute(name);
    }

    public void putAttribute(String name, Object value) {
        sqlExpr.putAttribute(name, value);
    }

    public Map<String, Object> getAttributesDirect() {
        return sqlExpr.getAttributesDirect();
    }

    @Override
    public void addBeforeComment(String comment) {
        sqlExpr.addBeforeComment(comment);
    }

    @Override
    public void addBeforeComment(List<String> comments) {
        sqlExpr.addBeforeComment(comments);
    }

    @Override
    public List<String> getBeforeCommentsDirect() {
        return sqlExpr.getBeforeCommentsDirect();
    }

    @Override
    public void addAfterComment(String comment) {
        sqlExpr.addAfterComment(comment);
    }

    @Override
    public void addAfterComment(List<String> comments) {
        sqlExpr.addAfterComment(comments);
    }

    public List<String> getAfterCommentsDirect() {
        return sqlExpr.getAfterCommentsDirect();
    }

    public boolean hasBeforeComment() {
        return sqlExpr.hasBeforeComment();
    }

    public boolean hasAfterComment() {
        return sqlExpr.hasAfterComment();
    }

    public void output(StringBuffer buf) {
        sqlExpr.output(buf);
    }
}
