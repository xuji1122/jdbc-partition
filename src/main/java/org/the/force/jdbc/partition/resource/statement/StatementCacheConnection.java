package org.the.force.jdbc.partition.resource.statement;

import org.the.force.jdbc.partition.common.LRUCache;
import org.the.force.jdbc.partition.resource.connection.WrappedConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Random;

/**
 * Created by xuji on 2017/5/29.
 */
public class StatementCacheConnection extends WrappedConnection {

    private int statementCacheSize = 4;

    private int preparedStatementCacheSize = 16;
    /**
     * 由系统控制的的物理物理连接，同一个逻辑的connection的某个物理链接范围内的statement只会单线程执行
     */
    private final LRUCache<String, StatementHolder> statementCache;

    private final LRUCache<String, PreparedStatementHolder> preparedStatementCache;

    public StatementCacheConnection(Connection connection) {
        super(connection);
        preparedStatementCache = new LRUCache(preparedStatementCacheSize);
        statementCache = new LRUCache(statementCacheSize);
    }

    public  Statement createStatement() throws SQLException {
        String key = "stmt;" + (new Random().nextInt(statementCacheSize));
        StatementHolder holder = statementCache.get(key);
        if (holder == null || holder.isClosed()) {
            holder = new StatementHolder(super.createStatement(), statementCache, key);
        }
        return holder;
    }

    public  PreparedStatement prepareStatement(String sql) throws SQLException {
        //TODO 测试sql大小写敏感性  `` sql标识符
        String key = sql + ";pStmt";
        PreparedStatementHolder holder = preparedStatementCache.get(key);
        if (holder == null || holder.isClosed()) {
            holder = new PreparedStatementHolder(super.prepareStatement(sql), preparedStatementCache, key);
        }
        return holder;
    }

    public  PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
            return this.prepareStatement(sql);
        }
        String key = sql + ";pStmt;" + autoGeneratedKeys;
        PreparedStatementHolder holder = preparedStatementCache.get(key);
        if (holder == null || holder.isClosed()) {
            holder = new PreparedStatementHolder(super.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), preparedStatementCache, key);
        }
        return holder;
    }

    //TODO 带有其他参数的prepareStatement的支持
    public  void close() throws SQLException {
        for (Map.Entry<String, PreparedStatementHolder> entry : preparedStatementCache.entrySet()) {
            entry.getValue().getStatement().close();
        }
        preparedStatementCache.clear();
        for (Map.Entry<String, StatementHolder> entry : statementCache.entrySet()) {
            entry.getValue().getStatement().close();
        }
        statementCache.clear();
        super.close();
    }
}
