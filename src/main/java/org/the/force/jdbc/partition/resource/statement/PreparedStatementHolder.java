package org.the.force.jdbc.partition.resource.statement;

import org.the.force.jdbc.partition.common.cache.CachedResource;
import org.the.force.jdbc.partition.common.cache.LRUCache;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/2.
 */
public class PreparedStatementHolder extends WrappedPrepareStatement implements CachedResource {


    final String key;

    private final LRUCache<String, PreparedStatementHolder> cache;

    boolean closed = false;//代理对象的状态  真实对象的状态关闭则代理对象必须关闭，真实对象没有关闭，代理对象可以假关闭



    public PreparedStatementHolder(PreparedStatement pStmt, LRUCache<String, PreparedStatementHolder> cache, String key) {
        super(pStmt);
        this.cache = cache;
        this.key = key;
        cache.put(key, this);
    }

    public void close() throws SQLException {
        closed = true;
        if (!cache.containsKey(key)) {//不在缓存中
            super.preparedStatement.close();
        }
        //do not close
    }

    //从缓存中获取到
    public void getFromCache() {
        try {
            if (!super.preparedStatement.isClosed()) {//真实对象的状态
                closed = false;
            }
        } catch (Exception e) {
            closed = true;
            try {
                super.getStatement().close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public boolean isClosed() throws SQLException {
        return closed;
    }


    public void expireFromCache() {
        try {
            if (closed) {
                super.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
