package org.the.force.jdbc.partition.engine.result;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public interface RowCursor {


    /**
     * 数据集结束这返回null
     *
     * @return
     */
    DataItemRow next() throws SQLException;

    /**
     * 返回字段定义对象
     *
     * @return
     */
    ResultSetMetaData getResultSetMetaData();

    void close() throws SQLException;

}
