package org.the.force.jdbc.partition.engine.parser.value;

import org.druid.sql.ast.SQLExpr;
import org.the.force.jdbc.partition.engine.parser.SqlParserContext;
import org.the.force.jdbc.partition.exception.UnsupportedExprException;

/**
 * Created by xuji on 2017/1/2.
 */
public interface SqlValueFunction {

    SqlValue getSqlValue(SQLExpr sqlExpr, SqlParserContext sqlParserContext) throws UnsupportedExprException;

}
