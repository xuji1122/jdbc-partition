package org.the.force.jdbc.partition.resource.db;

import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.engine.evaluator.factory.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.resource.table.LogicTableManager;

import java.util.Properties;
import java.util.SortedSet;

/**
 * Created by xuji on 2017/5/19.
 */
public interface LogicDbConfig {

    SqlDialect getSqlDialect();

    String getLogicDbName();

    String getParamStr();

    Properties getInfo();

    PhysicDbConfig getPhysicDbConfig(String physicDbName);

    int getPhysicDbSize();

    SortedSet<String> getLogicTables();

    LogicTableManager getLogicTableManager(String logicTableName);

    /**
     * 获取sql表达式取值函数的factory
     * @return
     */
    SqlExprEvaluatorFactory getSqlExprEvaluatorFactory();

}
