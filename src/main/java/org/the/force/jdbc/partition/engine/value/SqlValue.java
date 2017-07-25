package org.the.force.jdbc.partition.engine.value;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/17.
 */
public interface SqlValue {

    Object NULL = (Comparable<?>) o -> -1;

    Object getValue();

    /**
     * +
     *
     * @param sqlValue
     * @return
     */
    SqlValue add(SqlValue sqlValue) throws SQLException;

    SqlValue subtract(SqlValue sqlValue) throws SQLException;

    SqlValue multiply(SqlValue sqlValue) throws SQLException;

    SqlValue divide(SqlValue sqlValue) throws SQLException;

    SqlValue mod(SqlValue sqlValue) throws SQLException;

}
