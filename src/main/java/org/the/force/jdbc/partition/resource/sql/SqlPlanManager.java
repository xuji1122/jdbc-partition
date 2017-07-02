package org.the.force.jdbc.partition.resource.sql;

import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.engine.plan.SqlPlanMatcher;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.engine.plan.SqlPlan;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by xuji on 2017/5/31.
 */
public class SqlPlanManager {

    private static Logger logger = LoggerFactory.getLogger(SqlPlanManager.class);

    private final LogicDbConfig logicDbConfig;

    private final LoadingCache<SqlKey, SqlPlan> loadingCache;


    public SqlPlanManager(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
        loadingCache = CacheBuilder.newBuilder().maximumSize(2048).concurrencyLevel(1024).initialCapacity(1024).build(new CacheLoader<SqlKey, SqlPlan>() {
            public SqlPlan load(SqlKey sqlKey) throws Exception {
                return init(sqlKey);
            }
        });
    }

    public SqlPlan getSqlPlan(String sql) throws SQLException {
        try {
            return loadingCache.get(new SqlKey(sql));
        } catch (ExecutionException e) {
            Throwable t = e.getCause();
            if (t instanceof SQLException) {
                throw (SQLException) t;
            } else {
                throw new SqlParseException(t);
            }
        }
    }

    public SqlPlan init(SqlKey sqlKey) throws SQLException {
        String sql = sqlKey.getSql();
        try {
            SqlDialect sqlDialect = logicDbConfig.getSqlDialect();
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, sqlDialect.getDruidSqlDialect());
            SqlPlanMatcher sqlPlanMatcher = new SqlPlanMatcher(logicDbConfig);
            SQLStatement sqlStatement = stmtList.get(0);
            sqlStatement.accept(sqlPlanMatcher);
            SqlPlan sqlPlan = sqlPlanMatcher.getSqlPlan();
            logger.info("\n\t\t\t\tlogic sql:{} \n\t\t\t\tsql execution plan:{}", sql, sqlPlan.toString());
            return sqlPlan;
        } catch (Exception e) {
            logger.error("logic plan:" + sql, e);
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else {
                throw new SqlParseException(sql, e);
            }
        }
    }
}
