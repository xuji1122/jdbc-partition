package org.the.force.jdbc.partition.engine.evaluator;

import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.Command;
import org.the.force.jdbc.partition.engine.sqlelements.SqlRefer;
import org.the.force.jdbc.partition.engine.sqlelements.sqltable.ExprConditionalSqlTable;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/18.
 * executor expr求值运算期间可选的参数
 */
public class SqlExprEvalContext {

    private final LogicSqlParameterHolder logicSqlParameterHolder;

    private Map<String, ExprConditionalSqlTable> sqlTableMap;//ownerName和tableName的匹配关系

    private Map<SqlRefer, Integer> sqlReferIntegerMap;//当index 为-1时使用结果集的columnName匹配

    private Map<SQLExpr, List<Object[]>> sqlExprInValuesMap;//子查询的结果集  kye是SQLInSubQueryExpr

    private Command command;

    public SqlExprEvalContext(LogicSqlParameterHolder logicSqlParameterHolder) {
        this.logicSqlParameterHolder = logicSqlParameterHolder;
    }

    public LogicSqlParameterHolder getLogicSqlParameterHolder() {
        return logicSqlParameterHolder;
    }

    public Map<SqlRefer, Integer> getSqlReferIntegerMap() {
        return sqlReferIntegerMap;
    }

    public void setSqlReferIntegerMap(Map<SqlRefer, Integer> sqlReferIntegerMap) {
        this.sqlReferIntegerMap = sqlReferIntegerMap;
    }

    public Map<String, ExprConditionalSqlTable> getSqlTableMap() {
        return sqlTableMap;
    }

    public void setSqlTableMap(Map<String, ExprConditionalSqlTable> sqlTableMap) {
        this.sqlTableMap = sqlTableMap;
    }

    public Map<SQLExpr, List<Object[]>> getSqlExprInValuesMap() {
        return sqlExprInValuesMap;
    }

    public void setSqlExprInValuesMap(Map<SQLExpr, List<Object[]>> sqlExprInValuesMap) {
        this.sqlExprInValuesMap = sqlExprInValuesMap;
    }


    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
