package org.the.force.jdbc.partition.resource.executor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.engine.executor.factory.SqlExecutorFactory;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by xuji on 2017/5/31.
 */
public class SqlExecutorManager {

    private static Log logger = LogFactory.getLog(SqlExecutorManager.class);

    private final LogicDbConfig logicDbConfig;

    private final LoadingCache<SqlKey, SqlExecutor> loadingCache;


    public SqlExecutorManager(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
        loadingCache = CacheBuilder.newBuilder().maximumSize(2048).concurrencyLevel(1024).initialCapacity(1024).build(new CacheLoader<SqlKey, SqlExecutor>() {
            public SqlExecutor load(SqlKey sqlKey) throws Exception {
                return init(sqlKey);
            }
        });
    }

    public SqlExecutor getSqlExecutor(String sql) throws SQLException {
        try {
            return loadingCache.get(new SqlKey(sql));
        } catch (ExecutionException e) {
            Throwable t = e.getCause();
            if (t instanceof SqlParseException) {
                throw (SqlParseException) t;
            }
            if (t instanceof SQLException) {
                throw (SQLException) t;
            } else {
                throw new SqlParseException(t);
            }
        }
    }

    public SqlExecutor init(SqlKey sqlKey) throws SQLException {
        try {
            String sql = sqlKey.getSql();
            SqlDialect sqlDialect = logicDbConfig.getSqlDialect();
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, sqlDialect.getDruidSqlDialect());
            SqlExecutorFactory sqlExecutorFactory = new SqlExecutorFactory(logicDbConfig);
            SQLStatement sqlStatement = stmtList.get(0);
            sqlStatement.accept(sqlExecutorFactory);
            SqlExecutor sqlExecutor = sqlExecutorFactory.getSqlExecutor();
            logger.info(MessageFormat.format("\n\t\t\t\tlogic executor:{0} \n\t\t\t\texecutor execution factory:{1}", sql, sqlExecutor.toString()));
            return sqlExecutor;
        } catch (Exception e) {
            logger.error("logic factory:" + sqlKey.getSql(), e);
            if (e.getCause() instanceof SQLException) {
                throw new SqlParseException(sqlKey.getSql(), e.getCause());
            } else {
                throw new SqlParseException(sqlKey.getSql(), e);
            }
        }
    }


}
