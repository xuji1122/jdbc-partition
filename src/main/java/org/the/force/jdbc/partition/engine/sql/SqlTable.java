package org.the.force.jdbc.partition.engine.sql;

import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

import java.util.List;

/**
 * Created by xuji on 2017/6/14.
 */
public interface SqlTable {

    String getTableName();

    String getAlias();

    void setAlias(String alias);

    String getRelativeKey();

    /**
     * 返回可以被引用的列名集合
     * @return
     */
    List<String> getAllReferAbleLabels();

    SQLTableSource getSQLTableSource();

}
