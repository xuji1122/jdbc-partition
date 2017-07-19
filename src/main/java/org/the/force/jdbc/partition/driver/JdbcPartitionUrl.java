package org.the.force.jdbc.partition.driver;

import org.the.force.jdbc.partition.exception.PartitionConfigException;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/1.
 * jdbc:partition:sqlDialect@ip:port,ip:port/logic/executor/path?p=a
 */
public class JdbcPartitionUrl {

    private static final String PREFIX = "jdbc:partition:";

    private final SqlDialect sqlDialect;
    private final String connectString;
    private final String namespace;
    private final String logicDbName;
    private final String paramStr;

    private JdbcPartitionUrl(SqlDialect sqlDialect, String connectString, String namespace, String logicDbName, String paramStr) {
        this.sqlDialect = sqlDialect;
        this.connectString = connectString;
        this.namespace = namespace;
        this.logicDbName = logicDbName;
        this.paramStr = paramStr;
    }

    public static boolean acceptsURL(String url) throws SQLException {
        String start = PREFIX;
        if (!url.startsWith(start)) {
            return false;
        }
        int index = url.indexOf("@", start.length());
        if (index < 0) {
            return false;
        }
        String sqlDialect = url.substring(start.length(), index);
        SqlDialect sqlDialectEnum = SqlDialect.getByName(sqlDialect);
        if (sqlDialectEnum == null) {
            return false;
        }
        return true;
    }

    public static JdbcPartitionUrl getInstance(String url) throws SQLException {
        if (!url.startsWith(PREFIX)) {
            return null;
        }
        int atIndex = url.indexOf("@", PREFIX.length());
        if (atIndex < 0) {
            return null;
        }
        String sqlDialect = url.substring(PREFIX.length(), atIndex);
        SqlDialect sqlDialectEnum = SqlDialect.getByName(sqlDialect);
        if (sqlDialectEnum == null) {
            return null;
        }
        int paramIndex = url.indexOf('?');
        String paramStr = "";
        if (paramIndex > -1) {
            paramStr = url.substring(paramIndex + 1);
        } else {
            paramIndex = url.length();
        }
        int pathIndex = url.indexOf("/");
        if (pathIndex < 0) {
            throw new PartitionConfigException("pathIndex < 0");
        }
        String connectStr = url.substring(atIndex + 1, pathIndex);
        String logicDbPath = url.substring(pathIndex + 1, paramIndex);
        if (logicDbPath.endsWith("/")) {
            logicDbPath = logicDbPath.substring(0, logicDbPath.length() - 1);
        }
        if (logicDbPath.startsWith("/")) {
            logicDbPath = logicDbPath.substring(1);
        }
        int index = logicDbPath.lastIndexOf('/');
        String namespace = null;
        if (index > -1) {
            namespace = logicDbPath.substring(0, index);
        }
        String logicDbName = logicDbPath.substring(index + 1).toLowerCase();
        return new JdbcPartitionUrl(sqlDialectEnum, connectStr, namespace, logicDbName, paramStr);
    }


    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JdbcPartitionUrl jdbcPartitionUrl = (JdbcPartitionUrl) o;
        return logicDbName.equals(jdbcPartitionUrl.logicDbName);
    }

    public int hashCode() {
        return logicDbName.hashCode();
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public String getConnectString() {
        return connectString;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getLogicDbName() {
        return logicDbName;
    }

    public String getParamStr() {
        return paramStr;
    }

    public String toString() {
        return PREFIX + sqlDialect.name().toLowerCase() + "@" + connectString + "/" + namespace + "/" + logicDbName;
    }

}
