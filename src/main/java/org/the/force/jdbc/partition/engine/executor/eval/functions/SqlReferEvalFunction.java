package org.the.force.jdbc.partition.engine.executor.eval.functions;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.eval.SqlExprEvalFunction;
import org.the.force.jdbc.partition.engine.executor.eval.SqlValueEvalContext;
import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.engine.result.DataItemRow;
import org.the.force.thirdparty.druid.sql.ast.SQLName;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/15.
 */
public class SqlReferEvalFunction extends SqlExprEvalFunction {

    private final SqlRefer sqlRefer;

    public SqlReferEvalFunction(SQLName originalSqlExpr) {
        super(originalSqlExpr);
        sqlRefer = SqlReferParser.getSqlRefer(originalSqlExpr);
    }

    public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object  rows) throws SQLException {
        DataItemRow dataItemRow = (DataItemRow)rows;
        Integer integer = sqlValueEvalContext.getSqlReferIntegerMap().get(sqlRefer);
        if (integer != null && integer > -1) {
            return dataItemRow.getValue(integer);
        } else {
            if (sqlRefer.getOwnerName() == null) {
                return dataItemRow.getValue(sqlRefer.getName());
            } else {
                SqlTable sqlTable = sqlValueEvalContext.getSqlTableMap().get(sqlRefer.getOwnerName());
                if (sqlTable.getTableName() == null) {
                    return dataItemRow.getValue(sqlRefer.getName());
                } else {
                    return dataItemRow.getValue(sqlTable.getTableName() + "." + sqlRefer.getName());
                }
            }
        }
    }
}
