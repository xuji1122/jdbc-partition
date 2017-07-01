package org.the.force.jdbc.partition.engine.plan.model;

import java.util.Set;

/**
 * Created by xuji on 2017/6/14.
 */
public interface SqlTable {


    boolean equals(Object o);

    int hashCode();

    String getTableName();

    String getAlias();

    void setAlias(String alias);

    Set<String> getColumns();

}
