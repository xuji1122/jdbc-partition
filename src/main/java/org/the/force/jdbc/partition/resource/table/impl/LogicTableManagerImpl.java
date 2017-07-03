package org.the.force.jdbc.partition.resource.table.impl;

import org.the.force.jdbc.partition.resource.connection.ConnectionAdapter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.mysql.MySqlDdMetaDataImpl;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableManager;
import org.the.force.jdbc.partition.resource.table.model.LogicTable;
import org.the.force.jdbc.partition.rule.config.DataNode;

import java.sql.DatabaseMetaData;

/**
 * Created by xuji on 2017/6/7.
 */
public class LogicTableManagerImpl implements LogicTableManager {

    private static String OLD_PATH = "old";

    private static String NEW_PATH = "new";

    private final String logicTableName;

    private final DataNode logicTableStatusNode;

    //copy on write
    private volatile LogicTableConfigImpl[] logicTableConfig;

    private final LogicDbConfig logicDbConfig;

    private volatile LogicTable logicTable;

    public LogicTableManagerImpl(LogicDbConfig logicDbConfig, String logicTableName, final DataNode logicTableStatusNode) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.logicTableStatusNode = logicTableStatusNode;
        this.logicTableName = logicTableName;
        init();
    }

    private void init() throws Exception {
        DataNode oldConfig = logicTableStatusNode.children(OLD_PATH);
        LogicTableConfigImpl[] logicTableConfig = new LogicTableConfigImpl[2];
        logicTableConfig[0] = new LogicTableConfigImpl(logicDbConfig.getSqlDialect(), logicTableName, oldConfig);
        setArray(logicTableConfig);
        dataChanged();
    }

    public void initDbMetaData() throws Exception {
        ConnectionAdapter connectionAdapter = null;
        try {
            connectionAdapter = new ConnectionAdapter(logicDbConfig);
            DatabaseMetaData databaseMetaData = new MySqlDdMetaDataImpl(logicDbConfig, connectionAdapter);
            logicTable = new LogicTable(logicDbConfig.getLogicDbName(), null, logicTableName, databaseMetaData);
        } finally {
            if (connectionAdapter != null) {
                connectionAdapter.closeConnection();
            }
        }

    }

    public String getLogicTableName() {
        return logicTableName;
    }

    public LogicTable getLogicTable() {
        return logicTable;
    }

    public LogicTableConfig[] getLogicTableConfig() {
        LogicTableConfig[] array = new LogicTableConfig[2];
        LogicTableConfigImpl[] logicTableConfig = array();
        array[0] = logicTableConfig[0];
        array[1] = logicTableConfig[1];
        return array;
    }

    public synchronized void dataChanged() throws Exception {
        DataNode newConfig = logicTableStatusNode.children(NEW_PATH);
        LogicTableConfigImpl temp = null;
        if (newConfig != null) {
            temp = new LogicTableConfigImpl(logicDbConfig.getSqlDialect(), logicTableName, newConfig);
        }
        if (temp == null) {
            return;
        }

        LogicTableConfigImpl[] oldLogicTableConfig = array();

        if (oldLogicTableConfig[1] == null && temp.getVersion() > oldLogicTableConfig[0].getVersion() && temp.getPartitions().size() >= oldLogicTableConfig[0].getPartitions()
            .size()) {
            LogicTableConfigImpl[] logicTableConfigNew = new LogicTableConfigImpl[2];
            logicTableConfigNew[0] = oldLogicTableConfig[0];
            logicTableConfigNew[1] = temp;
            setArray(logicTableConfigNew);
        }
    }


    private LogicTableConfigImpl[] array() {
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
