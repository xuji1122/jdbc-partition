package org.the.force.jdbc.partition.engine.result;

import java.sql.SQLException;
import java.util.Map;

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
    Map<String,Integer> getResultSetMetaMap();

    int[] getSqlTypes();

    void close() throws SQLException;

}
