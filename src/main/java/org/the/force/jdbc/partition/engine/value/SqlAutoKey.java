package org.the.force.jdbc.partition.engine.value;

/**
 * Created by xuji on 2017/7/29.
 * 自增长列
 * 自定义的自增长列，实际上就是插入到db的参数
 * 标识性接口
 * 在输出物理db参数时执行
 */
public interface SqlAutoKey {

    void general();

    SqlParameter toSqlParameter();

    SqlLiteral toSqlLiteral();

    int columnIndex();

    String columnName();

    String tableName();

}
