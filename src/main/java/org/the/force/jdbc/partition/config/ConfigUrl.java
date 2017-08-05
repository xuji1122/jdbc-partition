package org.the.force.jdbc.partition.config;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by xuji on 2017/7/29.
 */
public interface ConfigUrl {

    DataNode getLogicDbConfigNode(Properties info) throws SQLException;

}
