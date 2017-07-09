package org.the.force.jdbc.partition.resource.sql;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.engine.executor.plan.SqlExecutionPlan;
import org.the.force.jdbc.partition.engine.executor.plan.SqlPlanMatcher;
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
public class SqlExecutionPlanManager {

    private static Log logger = LogFactory.getLog(SqlExecutionPlanManager.class);

    private final LogicDbConfig logicDbConfig;

    private final LoadingCache<SqlKey, SqlExecutionPlan> loadingCache;


    public SqlExecutionPlanManager(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
        loadingCache = CacheBuilder.newBuilder().maximumSize(2048).concurrencyLevel(1024).initialCapacity(1024).build(new CacheLoader<SqlKey, SqlExecutionPlan>() {
            public SqlExecutionPlan load(SqlKey sqlKey) throws Exception {
                return init(sqlKey);
            }
        });
    }

    public SqlExecutionPlan getSqlExecutionPlan(String sql) throws SQLException {
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

    public SqlExecutionPlan init(SqlKey sqlKey) throws SQLException {
        try {
            String sql = sqlKey.getSql();
            SqlDialect sqlDialect = logicDbConfig.getSqlDialect();
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, sqlDialect.getDruidSqlDialect());
            SqlPlanMatcher sqlPlanMatcher = new SqlPlanMatcher(logicDbConfig);
            SQLStatement sqlStatement = stmtList.get(0);
            sqlStatement.accept(sqlPlanMatcher);
            SqlExecutionPlan sqlExecutionPlan = sqlPlanMatcher.getSqlPlan();
            logger.info(MessageFormat.format("\n\t\t\t\tlogic sql:{0} \n\t\t\t\tsql execution plan:{1}", sql, sqlExecutionPlan.toString()));
            return sqlExecutionPlan;
        } catch (Exception e) {
            logger.error("logic plan:" + sqlKey.getSql(), e);
            if (e.getCause() instanceof SQLException) {
                throw new SqlParseException(sqlKey.getSql(), e.getCause());
            } else {
                throw new SqlParseException(sqlKey.getSql(), e);
            }
        }
    }


}
