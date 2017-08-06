package org.the.force.jdbc.partition.engine.executor.ast;

import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.LinedSql;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecDbNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecPStmtNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecParamLineNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecPhysicNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecStmtNode;
import org.the.force.jdbc.partition.engine.rewrite.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.stmt.SqlTablePartition;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/8/5.
 */
public abstract class AbstractExecutableAst implements BatchExecutableAst {

    protected final LogicDbConfig logicDbConfig;

    public AbstractExecutableAst(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    public final void addExecPhysicNode(SqlExecDbNode sqlExecDbNode, SqlLineExecRequest sqlLineExecRequest) throws SQLException {
        Map<Partition, SqlTablePartition> subsMap = doRoute(sqlLineExecRequest);
        int lineNumber = sqlLineExecRequest.getSqlLineParameter().getLineNumber();
        for (Map.Entry<Partition, SqlTablePartition> entry : subsMap.entrySet()) {
            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, logicDbConfig, sqlLineExecRequest, entry.getValue());
            getOriginStatement().accept(mySqlPartitionSqlOutput);
            String sql = sqlSb.toString();
            Partition sqlTablePartition = entry.getKey();
            String physicDbName = logicDbConfig.getPhysicDbConfig(sqlTablePartition.getPhysicDbName()).getPhysicDbName();
            SqlExecPhysicNode sqlExecuteRouter = sqlExecDbNode.get(physicDbName);
            if (mySqlPartitionSqlOutput.isParametric()) {//假定所有的分库分表结果只有两种模式，有参数的和无参数的
                if (sqlExecuteRouter == null) {
                    sqlExecuteRouter = new SqlExecPStmtNode();
                    sqlExecDbNode.put(physicDbName, sqlExecuteRouter);
                }
                SqlExecParamLineNode sqlExecParamLineNode = (SqlExecParamLineNode) sqlExecuteRouter.get(sql);
                if (sqlExecParamLineNode == null) {
                    sqlExecParamLineNode = new SqlExecParamLineNode(sql, physicDbName);
                    sqlExecuteRouter.put(sql, sqlExecParamLineNode);
                }
                List<SqlParameter> sqlParameterList = mySqlPartitionSqlOutput.getSqlParameterList();
                sqlExecParamLineNode.addParamLine(new LinedParameters(lineNumber, sqlParameterList));
            } else {
                if (sqlExecuteRouter == null) {
                    sqlExecuteRouter = new SqlExecStmtNode(physicDbName);
                    sqlExecDbNode.put(physicDbName, sqlExecuteRouter);
                }
                SqlExecStmtNode sqlExecStmtNode = (SqlExecStmtNode) sqlExecuteRouter;
                sqlExecStmtNode.addSql(new LinedSql(lineNumber,sql));
            }
        }
    }

    public abstract Map<Partition, SqlTablePartition> doRoute(SqlLineExecRequest sqlLineExecRequest)throws SQLException;

    public abstract SQLStatement getOriginStatement();
}
