package org.the.force.jdbc.partition.resource.table.impl;

import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableManager;
import org.the.force.jdbc.partition.resource.table.impl.LogicTableConfigImpl;
import org.the.force.jdbc.partition.rule.config.DataNode;

/**
 * Created by xuji on 2017/6/7.
 */
public class LogicTableManagerImpl implements LogicTableManager {

    private final String logicTableName;

    private final SqlDialect sqlDialect;

    private final DataNode logicTableStatusNode;

    //copy on write
    private volatile LogicTableConfigImpl[] logicTableConfig;

    private static String OLD_PATH = "old";

    private static String NEW_PATH = "new";

    public LogicTableManagerImpl(SqlDialect sqlDialect, String logicTableName, final DataNode logicTableStatusNode) throws Exception {
        this.sqlDialect = sqlDialect;
        this.logicTableStatusNode = logicTableStatusNode;
        this.logicTableName = logicTableName;
        init();
    }

    private synchronized void init() throws Exception {
        DataNode oldConfig = logicTableStatusNode.children(OLD_PATH);
        LogicTableConfigImpl[] logicTableConfig = new LogicTableConfigImpl[2];
        logicTableConfig[0] = new LogicTableConfigImpl(sqlDialect, logicTableName, oldConfig);
        setArray(logicTableConfig);
        dataChanged();
        //
    }

    public String getLogicTableName() {
        return logicTableName;
    }

    public LogicTableConfig[] getLogicTableConfig() {
        LogicTableConfig[] array = new LogicTableConfig[2];
        LogicTableConfigImpl[] logicTableConfig = getArray();
        array[0] = logicTableConfig[0];
        array[1] = logicTableConfig[1];
        return array;
    }

    public synchronized void dataChanged() throws Exception {
        DataNode newConfig = logicTableStatusNode.children(NEW_PATH);
        LogicTableConfigImpl temp = null;
        if (newConfig != null) {
            temp = new LogicTableConfigImpl(sqlDialect, logicTableName, newConfig);
        }
        if (temp == null) {
            return;
        }

        LogicTableConfigImpl[] oldLogicTableConfig = getArray();

        if (oldLogicTableConfig[1] == null && temp.getVersion() > oldLogicTableConfig[0].getVersion() && temp.getPartitions().size() >= oldLogicTableConfig[0].getPartitions()
            .size()) {
            LogicTableConfigImpl[] logicTableConfigNew = new LogicTableConfigImpl[2];
            logicTableConfigNew[0] = oldLogicTableConfig[0];
            logicTableConfigNew[1] = temp;
            setArray(logicTableConfigNew);
        }
    }


    private LogicTableConfigImpl[] getArray() {
        return this.logicTableConfig;
    }


    private void setArray(LogicTableConfigImpl[] array) {
        this.logicTableConfig = array;
    }


    private synchronized void checkToExtand() {

    }

    private synchronized void checkToNormal() {

    }



}
