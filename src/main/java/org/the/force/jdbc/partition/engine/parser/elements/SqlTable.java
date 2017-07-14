package org.the.force.jdbc.partition.engine.parser.elements;

import java.util.Set;

/**
 * Created by xuji on 2017/6/14.
 */
public interface SqlTable {

    String getTableName();

    String getAlias();

    void setAlias(String alias);

    /**
     * 返回可以被引用的列名集合
     * @return
     */
    Set<String> getReferLabels();

}
