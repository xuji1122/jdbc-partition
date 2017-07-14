package org.the.force.jdbc.partition.engine.executor.dql.value;

import org.the.force.jdbc.partition.engine.executor.dql.elements.ValueItem;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/14.
 */
public abstract class AggregateBase extends ValueItem implements SQLExpr, SelfAggregateFunction, ReferAggregateFunction {

    protected final SQLAggregateExpr sqlExpr;

    private List<SQLExpr> arguments = null;


    public AggregateBase(SQLAggregateExpr sqlExpr, int index, String label) {
        super(index, label);
        this.sqlExpr = sqlExpr;
    }

    public List<SQLExpr> getArguments() {
        if (arguments == null) {
            arguments = new ArrayList<>();
        }
        return arguments;
    }

    public  void accept(SQLASTVisitor visitor) {
        sqlExpr.accept(visitor);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AggregateBase that = (AggregateBase) o;

        return sqlExpr.equals(that.sqlExpr);

    }

    public int hashCode() {
        return sqlExpr.hashCode();
    }

    public SQLExpr clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
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
