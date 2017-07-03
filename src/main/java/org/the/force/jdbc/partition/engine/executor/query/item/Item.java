package org.the.force.jdbc.partition.engine.executor.query.item;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/8.
 */
public interface Item {

    String getColumnLabel() throws SQLException;
    
    String getColumnName() throws SQLException;

    String getSchemaName() throws SQLException;

    String getTableName() throws SQLException;

    String getCatalogName() throws SQLException;



}
