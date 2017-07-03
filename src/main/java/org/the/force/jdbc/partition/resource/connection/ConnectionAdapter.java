package org.the.force.jdbc.partition.resource.connection;

import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.PhysicDbConfig;
import org.the.force.jdbc.partition.resource.statement.StatementCacheConnection;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by xuji on 2017/5/29.
 * 物理链接的管理和物理statement的管理（通过StatementCacheConnection 包装Connection）
 * 由于jdbc-partition对外保留的connection和statement并没有实际的资源消耗，因此连接管理的主要目的是做好物理资源回收即可
 */
public class ConnectionAdapter {

    private Log logger = LogFactory.getLog(ConnectionAdapter.class);

    private volatile boolean closed = false;

    private boolean autoCommit = true;

    private boolean readOnly = false;

    private int transactionIsolation = java.sql.Connection.TRANSACTION_REPEATABLE_READ;

    private Map<String, Connection> connectionMap;

    private Set<String> initDbSet;

    private final LogicDbConfig logicDbConfig;

    public ConnectionAdapter(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
        connectionMap = new ConcurrentHashMap<>(logicDbConfig.getPhysicDbSize() / 2 + 1);
        initDbSet = new ConcurrentSkipListSet<>();
    }

    private void checkClosed() throws SQLException {
        if (closed) {
            //TODO check 异常
        }
    }

    /**
     * 由DbExecutorRouter调用
     *
     * @param physicDbName
     * @throws SQLException
     */
    public void initConnection(String physicDbName) throws SQLException {
        checkClosed();
        physicDbName = physicDbName.toLowerCase();
        if (initDbSet.add(physicDbName)) {
            logger.info(MessageFormat.format("init connection for {0}", physicDbName));
            doInit(physicDbName);
        }
    }

    public void doInit(String physicDbName) throws SQLException {
        java.sql.Connection connection = connectionMap.get(physicDbName);
        if (connection == null || connection.isClosed()) {
            PhysicDbConfig physicDbConfig = logicDbConfig.getPhysicDbConfig(physicDbName);
            String url = physicDbConfig.getUrl();
            if (logicDbConfig.getParamStr() != null && logicDbConfig.getParamStr().length() > 0) {
                if (url.indexOf('?') > -1) {
                    url = url + "&" + logicDbConfig.getParamStr();
                } else {
                    url = url + "?" + logicDbConfig.getParamStr();
                }
            }
            connection = DriverManager.getConnection(url, logicDbConfig.getInfo());
            connection = new StatementCacheConnection(connection);
            connectionMap.put(physicDbName, connection);
        }
        if (autoCommit != connection.getAutoCommit()) {
            connection.setAutoCommit(autoCommit);
        }
        if (readOnly != connection.isReadOnly()) {
            connection.setReadOnly(readOnly);
        }
        if (transactionIsolation != connection.getTransactionIsolation()) {
            connection.setTransactionIsolation(transactionIsolation);
        }
    }

    /**
     * 双重检查加锁
     *
     * @param physicDbName
     * @return
     * @throws SQLException
     */
    public Connection getConnection(String physicDbName) throws SQLException {
        checkClosed();
        initConnection(physicDbName);
        physicDbName = physicDbName.toLowerCase();
        return connectionMap.get(physicDbName);
    }

    public void closeConnection() throws SQLException {
        closed = true;
        try {
            initDbSet.clear();
            connectionMap.values().parallelStream().forEach(connection -> {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            connectionMap.clear();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    public boolean isClosed() throws SQLException {
        return closed;
    }

    public void commit() throws SQLException {
        checkClosed();
        if (autoCommit) {
            return;
        }
        SQLException exception = null;
        Iterator<String> iterator = initDbSet.iterator();
        while (iterator.hasNext()) {
            String physicDbName = iterator.next();
            try {
                connectionMap.get(physicDbName).commit();
                iterator.remove();//移出已经提交的connection
            } catch (SQLException e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.setNextException(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    public void rollback() throws SQLException {
        if (autoCommit) {
            return;
        }
        Iterator<String> iterator = initDbSet.iterator();
        while (iterator.hasNext()) {
            String physicDbName = iterator.next();
            connectionMap.get(physicDbName).rollback();
            iterator.remove();
        }

    }



    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
    }

    public boolean getAutoCommit() throws SQLException {
        return this.autoCommit;
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() throws SQLException {
        return this.readOnly;
    }

    public int getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(int transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }
}
