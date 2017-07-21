package org.the.force.jdbc.partition.engine;

import org.the.force.jdbc.partition.driver.JdbcPartitionConnection;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecutor;
import org.the.force.jdbc.partition.engine.executor.ExecutorConfig;
import org.the.force.jdbc.partition.engine.executor.WriteCommand;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.result.UpdateMerger;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;
import org.the.force.jdbc.partition.resource.statement.AbstractPreparedStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by xuji on 2017/5/28.
 */
public class BathAbleSqlEngine extends AbstractPreparedStatement {

    private static Log logger = LogFactory.getLog(BathAbleSqlEngine.class);

    protected final JdbcPartitionConnection jdbcPartitionConnection;

    protected final BatchAbleSqlExecutor batchAbleSqlExecutor;

    private boolean retrierveGeneratedKeys = false;

    private boolean close = false;

    private boolean closeOnCompletion = false;

    private final PhysicDbExecutor physicDbExecutor = new PhysicDbExecutor();

    public BathAbleSqlEngine(JdbcPartitionConnection jdbcPartitionConnection, BatchAbleSqlExecutor batchAbleSqlExecutor) throws SQLException {
        super();
        this.jdbcPartitionConnection = jdbcPartitionConnection;
        this.batchAbleSqlExecutor = batchAbleSqlExecutor;
    }

    public BathAbleSqlEngine(JdbcPartitionConnection jdbcPartitionConnection, BatchAbleSqlExecutor batchAbleSqlExecutor, boolean retrierveGeneratedKeys) throws SQLException {
        super();
        this.jdbcPartitionConnection = jdbcPartitionConnection;
        this.batchAbleSqlExecutor = batchAbleSqlExecutor;
        this.retrierveGeneratedKeys = retrierveGeneratedKeys;
    }

    private WriteCommand buildWriteCommand(ExecutorConfig executorConfig, UpdateMerger updateMerger) {
        WriteCommand command = new WriteCommand(jdbcPartitionConnection.getConnectionAdapter(), jdbcPartitionConnection.getThreadPoolExecutor(), executorConfig, updateMerger) {
            public int[] invokeWrite(Statement statement, String sql, List<Integer> lineNumMap) throws SQLException {
                if (lineNumMap.size() > 1) {
                    return statement.executeBatch();
                } else {
                    if (sql == null) {
                        int result = ((PreparedStatement) statement).executeUpdate();
                        return new int[] {result};
                    } else {
                        int result = statement.executeUpdate(sql);
                        return new int[] {result};
                    }
                }
            }

            public boolean returnGeneralKeys() {
                return retrierveGeneratedKeys;
            }
        };
        return command;
    }

    public int executeUpdate() throws SQLException {
        boolean forceTransaction = false;
        try {
            batchAbleSqlExecutor.addSqlLine(physicDbExecutor, logicSqlParameterHolder);
            if (logger.isDebugEnabled()) {
                logger.debug(MessageFormat.format("sql解析结果:{0}", physicDbExecutor.toString()));
            }
            if (jdbcPartitionConnection.getAutoCommit() && physicDbExecutor.sqlSize() > 1) {
                forceTransaction = true;
                jdbcPartitionConnection.setAutoCommit(false);
            }
            ExecutorConfig executorConfig = new ExecutorConfig();
            UpdateMerger updateMerger = new UpdateMerger(1);
            WriteCommand template = buildWriteCommand(executorConfig, updateMerger);
            physicDbExecutor.executeWrite(template);
            if (forceTransaction) {
                jdbcPartitionConnection.commit();
            }
            return updateMerger.toInt();
        } catch (Exception e) {
            if (forceTransaction) {
                jdbcPartitionConnection.rollback();
            }
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else {
                throw new SqlParseException(e);
            }
        } finally {
            logicSqlParameterHolder.resetLineNumber();
            if (forceTransaction) {
                jdbcPartitionConnection.setAutoCommit(false);
            }
        }

    }

    public void addBatch() throws SQLException {

        try {
            batchAbleSqlExecutor.addSqlLine(physicDbExecutor, logicSqlParameterHolder);
            logicSqlParameterHolder.addLineNumber();
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else {
                throw new SqlParseException(e);
            }
        }
    }

    public int[] executeBatch() throws SQLException {
        boolean forceTransaction = false;
        try {
            if (logicSqlParameterHolder.getLineNumber() < 1) {
                return new int[0];
            }
            if (jdbcPartitionConnection.getAutoCommit() && physicDbExecutor.sqlSize() > 1) {
                forceTransaction = true;
                jdbcPartitionConnection.setAutoCommit(false);
            }
            //java.factory.BatchUpdateException 异常切换匹配
            ExecutorConfig executorConfig = new ExecutorConfig();
            UpdateMerger updateMerger = new UpdateMerger(logicSqlParameterHolder.getLineNumber());
            WriteCommand template = buildWriteCommand(executorConfig, updateMerger);
            logicSqlParameterHolder.resetLineNumber();
            if (logger.isDebugEnabled()) {
                //logger.debug(MessageFormat.format("sql解析结果:{0}", physicDbExecutor.toString()));
            }
            physicDbExecutor.executeWrite(template);
            if (forceTransaction) {
                jdbcPartitionConnection.commit();
            }
            int[] result = updateMerger.toArray();
            return result;
        } catch (Exception e) {
            if (forceTransaction) {
                jdbcPartitionConnection.rollback();
            }
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else {
                throw new SqlParseException(e);
            }
        } finally {
            logicSqlParameterHolder.resetLineNumber();
            if (forceTransaction) {
                jdbcPartitionConnection.setAutoCommit(false);
            }
        }
    }

    public boolean execute() throws SQLException {
        boolean forceTransaction = false;
        try {
            batchAbleSqlExecutor.addSqlLine(physicDbExecutor, logicSqlParameterHolder);
            if (logger.isDebugEnabled()) {
                logger.debug(MessageFormat.format("sql解析结果:{0}", physicDbExecutor.toString()));
            }
            if (jdbcPartitionConnection.getAutoCommit() && physicDbExecutor.sqlSize() > 1) {
                forceTransaction = true;
                jdbcPartitionConnection.setAutoCommit(false);
            }
            ExecutorConfig executorConfig = new ExecutorConfig();
            UpdateMerger updateMerger = new UpdateMerger(logicSqlParameterHolder.getLineNumber());
            WriteCommand template = buildWriteCommand(executorConfig, updateMerger);
            physicDbExecutor.executeWrite(template);
            if (forceTransaction) {
                jdbcPartitionConnection.commit();
            }
            return updateMerger.toInt() > 0;
        } catch (Exception e) {
            if (forceTransaction) {
                jdbcPartitionConnection.rollback();
            }
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else {
                throw new SqlParseException(e);
            }
        } finally {
            logicSqlParameterHolder.resetLineNumber();
            if (forceTransaction) {
                jdbcPartitionConnection.setAutoCommit(false);
            }
        }
    }

    public void clearParameters() throws SQLException {
        physicDbExecutor.clearParameters(logicSqlParameterHolder.getLineNumber());
        logicSqlParameterHolder.clearParameters();

    }

    public void clearBatch() throws SQLException {
        physicDbExecutor.clearBatch();
    }

    //TODO auto dml keys
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public void close() throws SQLException {
        this.close = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return close;
    }


    @Override
    public void closeOnCompletion() throws SQLException {
        closeOnCompletion = true;
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return closeOnCompletion;
    }

}
