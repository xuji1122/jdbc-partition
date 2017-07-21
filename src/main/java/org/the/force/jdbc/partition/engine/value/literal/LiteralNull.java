package org.the.force.jdbc.partition.engine.value.literal;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlLiteral;
import org.the.force.jdbc.partition.engine.value.SqlNull;
import org.the.force.jdbc.partition.engine.value.SqlValue;

/**
 * Created by xuji on 2017/7/20.
 */
public class LiteralNull extends AbstractSqlValue implements SqlLiteral,SqlNull {


    public Object getValue() {
        return NULL;
    }

    public String toSql() {
        return " NULL ";
    }

    public SqlValue clone(SqlValue sqlValue) {
        return new LiteralNull();
    }
}
