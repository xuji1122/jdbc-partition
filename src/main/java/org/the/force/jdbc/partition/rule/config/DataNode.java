package org.the.force.jdbc.partition.rule.config;

import java.util.List;

/**
 * Created by xuji on 2017/5/21.
 */
public interface DataNode {

    String getKey();

    String getData() throws Exception;

    List<DataNode> children() throws Exception;

    DataNode children(String key) throws Exception;

}
