package org.the.force.jdbc.partition.driver;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.config.ConfigUrl;
import org.the.force.jdbc.partition.config.YmlFileConfigUrl;
import org.the.force.jdbc.partition.config.ZookeeperConfigUrl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/1
 * //多种配置文件协议的支持  格式范例
 * jdbc:partition:sqlDialect@zookeeper://ip:port,ip:port/logic/db/path?p=a
 * jdbc:partition:sqlDialect@yml_file://logic/db/path/xxxdb.yml?p=a
 */
public class JdbcPartitionUrl {

    private static final String PREFIX = "jdbc:partition:";


    private final SqlDialect sqlDialect;
    private final String logicDbKey;
    private final String paramStr;

    private final ConfigUrl configUrl;

    private JdbcPartitionUrl(SqlDialect sqlDialect, String logicDbKey, String paramStr, ConfigUrl configUrl) {
        this.sqlDialect = sqlDialect;
        this.logicDbKey = logicDbKey;
        this.paramStr = paramStr;
        this.configUrl = configUrl;
    }

    public static boolean acceptsURL(String url) throws SQLException {
        String start = PREFIX;
        if (!url.startsWith(start)) {
            return false;
        }
        int atIndex = url.indexOf("@", start.length());
        if (atIndex < 0) {
            return false;
        }
        String sqlDialect = url.substring(start.length(), atIndex);
        SqlDialect sqlDialectEnum = SqlDialect.getByName(sqlDialect);
        if (sqlDialectEnum == null) {
            return false;
        }
        int protocolIndex = url.indexOf("://", atIndex);
        if (protocolIndex < 0) {
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
        int protocolIndex = url.indexOf("://", atIndex);
        if (protocolIndex < 0) {
            return null;
        }
        //协议类型
        String protocol = url.substring(atIndex + 1, protocolIndex).toLowerCase();
        ConfigUrl configUrl;
        //协议的完整url，包括协议头
        String protocolUrl = url.substring(atIndex + 1, paramIndex).trim();
        if (protocolUrl.endsWith("/")) {
            protocolUrl = protocolUrl.substring(0, protocolUrl.length() - 1);
        }
        String className = System.getProperty("org.the.force.jdbc.partition.config.protocol." + protocol.toLowerCase() + ".impl");
        Class<? extends ConfigUrl> clazz;
        if (className != null && className.length() > 0) {
            try {
                clazz = (Class<? extends ConfigUrl>) PartitionSqlUtils.loadClass(className.trim());
            } catch (ClassNotFoundException e) {
                throw new SQLException("数据库配置协议实现类未找到:" + className, e);
            } catch (ClassCastException e) {
                throw new SQLException("数据库配置协议" + className + "实现类未实现 " + ConfigUrl.class.getName(), e);
            }
            try {
                Constructor<? extends ConfigUrl> constructor = clazz.getConstructor(String.class);
                configUrl = constructor.newInstance(protocolUrl);
            } catch (NoSuchMethodException e) {
                throw new SQLException("配置协议实现类" + className + "缺少一个java.lang.String参数的构造方法", e);
            } catch (InvocationTargetException e) {
                throw new SQLException("数据库配置协议初始化错误", e.getCause());
            } catch (Exception e) {
                throw new SQLException("无法加载数据库配置协议", e);
            }
        } else {
            if (protocol.equalsIgnoreCase("zookeeper")) {
                configUrl = new ZookeeperConfigUrl(protocolUrl);
            } else if (protocol.equalsIgnoreCase("yml_file")) {
                configUrl = new YmlFileConfigUrl(protocolUrl);
            } else {
                throw new SQLException("无法识别数据库配置协议");
            }
        }
        String logicDbKey = protocolUrl.substring(protocolUrl.lastIndexOf('/') + 1).toLowerCase();
        return new JdbcPartitionUrl(sqlDialectEnum, logicDbKey, paramStr, configUrl);
    }

    public ConfigUrl getConfigUrl() {
        return configUrl;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JdbcPartitionUrl jdbcPartitionUrl = (JdbcPartitionUrl) o;
        return logicDbKey.equals(jdbcPartitionUrl.logicDbKey);
    }


    public int hashCode() {
        return logicDbKey.hashCode();
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }


    public String getParamStr() {
        return paramStr;
    }

    public String toString() {

        String url = PREFIX + sqlDialect.name().toLowerCase() + "@" + configUrl.toString() + "/" + logicDbKey;
        if (paramStr != null) {
            url = url + "?" + paramStr;
        }
        return url;
    }


}
