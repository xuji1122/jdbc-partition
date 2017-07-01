package org.the.force.jdbc.partition.driver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuji on 2017/5/14.
 */
public class JdbcPartitionDriver implements Driver {

    private ConcurrentHashMap<JdbcPartitionUrl, DbDriverInstance> driverInstanceMap = new ConcurrentHashMap<>(32);

    static {
        try {
            DriverManager.registerDriver(new JdbcPartitionDriver());
        } catch (SQLException var1) {
            throw new RuntimeException("Can\'t register driver!");
        }
    }

    public Connection connect(String url, final Properties info) throws SQLException {

        JdbcPartitionUrl jdbcPartitionUrl = JdbcPartitionUrl.getInstance(url);
        if (jdbcPartitionUrl == null) {
            return null;
        }
        DbDriverInstance instance = driverInstanceMap.get(jdbcPartitionUrl);
        if (instance == null) {
            try {
                instance = driverInstanceMap.computeIfAbsent(jdbcPartitionUrl, key -> {
                    try {
                        return new DbDriverInstance(key, info);
                    } catch (SQLException sqlEx) {
                        throw new RuntimeException(sqlEx);
                    }
                });
            } catch (RuntimeException e) {
                if (e.getCause() instanceof SQLException) {
                    throw (SQLException) e.getCause();
                }
                throw e;
            }
        }
        return instance.newConnection();
    }


    public boolean acceptsURL(String url) throws SQLException {
        return JdbcPartitionUrl.acceptsURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
