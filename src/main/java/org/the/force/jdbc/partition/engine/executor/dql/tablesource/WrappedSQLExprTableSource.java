package org.the.force.jdbc.partition.engine.executor.dql.tablesource;

import org.the.force.jdbc.partition.engine.sqlelements.sqltable.ExprConditionalSqlTable;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.repository.SchemaObject;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/14.
 */
public class WrappedSQLExprTableSource extends SQLExprTableSource {

    private final ExprConditionalSqlTable sqlTable;
    private final SQLExprTableSource sqlExprTableSource;

    public WrappedSQLExprTableSource(ExprConditionalSqlTable sqlTable) {
        this.sqlTable = sqlTable;
        this.sqlExprTableSource = (SQLExprTableSource) sqlTable.getSQLTableSource();
        this.setParent(sqlExprTableSource.getParent());
    }


    protected void accept0(SQLASTVisitor visitor) {
        sqlExprTableSource.accept(visitor);
    }

    public void setAlias(String alias) {
        //sqlExprTableSource.setAlias(alias);
    }

    @Override
    public String getAlias() {
        return sqlExprTableSource.getAlias();
    }


    public ExprConditionalSqlTable getSqlTable() {
        return sqlTable;
    }

    public SQLExprTableSource getSqlExprTableSource() {
        return sqlExprTableSource;
    }



    @Override
    public int getHintsSize() {
        return sqlExprTableSource.getHintsSize();
    }

    @Override
    public SQLExpr getExpr() {
        return sqlExprTableSource.getExpr();
    }

    @Override
    public void setExpr(SQLExpr expr) {
        sqlExprTableSource.setExpr(expr);
    }

    @Override
    public List<SQLHint> getHints() {
        return sqlExprTableSource.getHints();
    }

    @Override
    public void setHints(List<SQLHint> hints) {
        sqlExprTableSource.setHints(hints);
    }

    @Override
    public List<SQLName> getPartitions() {
        return sqlExprTableSource.getPartitions();
    }

    @Override
    public int getPartitionSize() {
        return sqlExprTableSource.getPartitionSize();
    }

    @Override
    public String toString() {
        return sqlExprTableSource.toString();
    }

    @Override
    public SQLExpr getFlashback() {
        return sqlExprTableSource.getFlashback();
    }

    @Override
    public void setFlashback(SQLExpr flashback) {
        sqlExprTableSource.setFlashback(flashback);
    }

    @Override
    public SQLObject getParent() {
        return sqlExprTableSource.getParent();
    }

    @Override
    public void addPartition(SQLName partition) {
        sqlExprTableSource.addPartition(partition);
    }

    @Override
    public void setParent(SQLObject parent) {
        sqlExprTableSource.setParent(parent);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return sqlExprTableSource.getAttributes();
    }



    @Override
    public Object getAttribute(String name) {
        return sqlExprTableSource.getAttribute(name);
    }

    @Override
    public void putAttribute(String name, Object value) {
        sqlExprTableSource.putAttribute(name, value);
    }

    @Override
    public void output(StringBuffer buf) {
        sqlExprTableSource.output(buf);
    }

    @Override
    public boolean equals(Object o) {
        return sqlExprTableSource.equals(o);
    }

    @Override
    public Map<String, Object> getAttributesDirect() {
        return sqlExprTableSource.getAttributesDirect();
    }

    @Override
    public void addBeforeComment(String comment) {
        sqlExprTableSource.addBeforeComment(comment);
    }

    @Override
    public int hashCode() {
        return sqlExprTableSource.hashCode();
    }

    @Override
    public String computeAlias() {
        return sqlExprTableSource.computeAlias();
    }

    @Override
    public void addBeforeComment(List<String> comments) {
        sqlExprTableSource.addBeforeComment(comments);
    }

    @Override
    public SQLExprTableSource clone() {
        return sqlExprTableSource.clone();
    }

    @Override
    public void cloneTo(SQLExprTableSource x) {
        sqlExprTableSource.cloneTo(x);
    }

    @Override
    public List<String> getBeforeCommentsDirect() {
        return sqlExprTableSource.getBeforeCommentsDirect();
    }

    @Override
    public SchemaObject getSchemaObject() {
        return sqlExprTableSource.getSchemaObject();
    }

    @Override
    public void setSchemaObject(SchemaObject schemaObject) {
        sqlExprTableSource.setSchemaObject(schemaObject);
    }

    @Override
    public void addAfterComment(String comment) {
        sqlExprTableSource.addAfterComment(comment);
    }

    @Override
    public void addAfterComment(List<String> comments) {
        sqlExprTableSource.addAfterComment(comments);
    }

    @Override
    public List<String> getAfterCommentsDirect() {
        return sqlExprTableSource.getAfterCommentsDirect();
    }

    @Override
    public boolean hasBeforeComment() {
        return sqlExprTableSource.hasBeforeComment();
    }

    @Override
    public boolean hasAfterComment() {
        return sqlExprTableSource.hasAfterComment();
    }
}
