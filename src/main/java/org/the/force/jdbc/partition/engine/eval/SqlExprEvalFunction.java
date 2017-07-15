package org.the.force.jdbc.partition.engine.eval;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/13.
 */
public abstract class SqlExprEvalFunction implements SQLExpr {

    private SQLExpr originalSqlExpr;


    public SqlExprEvalFunction(SQLExpr originalSqlExpr) {
        this.originalSqlExpr = originalSqlExpr;
    }


    public abstract Object getValue(SqlValueEvalContext sqlValueEvalContext,LogicSqlParameterHolder logicSqlParameterHolder,Object data) throws SQLException;

    public SQLExpr getOriginalSqlExpr() {
        return originalSqlExpr;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SqlExprEvalFunction that = (SqlExprEvalFunction) o;
        return originalSqlExpr.equals(that.originalSqlExpr);
    }

    public int hashCode() {
        return originalSqlExpr.hashCode();
    }

    public SQLExpr clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    public final void accept(SQLASTVisitor visitor) {
        originalSqlExpr.accept(visitor);
    }

    public SQLObject getParent() {
        return originalSqlExpr.getParent();
    }

    public void setParent(SQLObject parent) {
        originalSqlExpr.setParent(parent);
    }

    public Map<String, Object> getAttributes() {
        return originalSqlExpr.getAttributes();
    }

    public Object getAttribute(String name) {
        return originalSqlExpr.getAttribute(name);
    }

    public void putAttribute(String name, Object value) {
        originalSqlExpr.putAttribute(name, value);
    }

    public Map<String, Object> getAttributesDirect() {
        return originalSqlExpr.getAttributesDirect();
    }

    @Override
    public void addBeforeComment(String comment) {
        originalSqlExpr.addBeforeComment(comment);
    }

    @Override
    public void addBeforeComment(List<String> comments) {
        originalSqlExpr.addBeforeComment(comments);
    }

    @Override
    public List<String> getBeforeCommentsDirect() {
        return originalSqlExpr.getBeforeCommentsDirect();
    }

    @Override
    public void addAfterComment(String comment) {
        originalSqlExpr.addAfterComment(comment);
    }

    @Override
    public void addAfterComment(List<String> comments) {
        originalSqlExpr.addAfterComment(comments);
    }

    public List<String> getAfterCommentsDirect() {
        return originalSqlExpr.getAfterCommentsDirect();
    }

    public boolean hasBeforeComment() {
        return originalSqlExpr.hasBeforeComment();
    }

    public boolean hasAfterComment() {
        return originalSqlExpr.hasAfterComment();
    }

    public void output(StringBuffer buf) {
        originalSqlExpr.output(buf);
    }
}
