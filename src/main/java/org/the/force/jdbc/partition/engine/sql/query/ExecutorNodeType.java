package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;

/**
 * Created by xuji on 2017/7/20.
 */
public class ExecutorNodeType {

    private final boolean isLogic;//true代表client执行sql,false代表db执行sql

    private final ExprConditionalSqlTable exprConditionalSqlTable;//如果是db执行sql,则必须指定相关的逻辑表及其条件等


    public ExecutorNodeType(boolean isLogic, ExprConditionalSqlTable exprConditionalSqlTable) {
        this.isLogic = isLogic;
        this.exprConditionalSqlTable = exprConditionalSqlTable;
    }

    public boolean isLogic() {
        return isLogic;
    }

    public ExprConditionalSqlTable getExprConditionalSqlTable() {
        return exprConditionalSqlTable;
    }


}
