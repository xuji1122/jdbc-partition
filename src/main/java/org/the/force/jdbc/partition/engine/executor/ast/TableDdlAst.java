package org.the.force.jdbc.partition.engine.executor.ast;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.LinedSql;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecDbNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecPStmtNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecParamLineNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecPhysicNode;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecStmtNode;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
import org.the.force.jdbc.partition.engine.rewrite.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.stmt.SqlTablePartition;
import org.the.force.jdbc.partition.engine.stmt.table.DdlSqlTable;
import org.the.force.jdbc.partition.engine.stmt.table.ExprSqlTable;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;

import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by xuji on 2017/5/18.
 */
public class TableDdlAst implements BatchExecutableAst {

    protected final LogicDbConfig logicDbConfig;

    private final SQLExprTableSource tableSource;

    private final SQLStatement sqlStatement;

    protected final ExprSqlTable exprSqlTable;

    protected final LogicTableConfig logicTableConfig;


    public TableDdlAst(LogicDbConfig logicDbConfig, SQLStatement sqlStatement, SQLExprTableSource tableSource) {
        this.logicDbConfig = logicDbConfig;
        this.sqlStatement = sqlStatement;
        this.tableSource = tableSource;
        exprSqlTable = new DdlSqlTable(logicDbConfig, this.tableSource);
        logicTableConfig = logicDbConfig.getLogicTableManager(exprSqlTable.getTableName()).getLogicTableConfig()[0];
    }

    public void addExecPhysicNode(SqlExecDbNode sqlExecDbNode, SqlLineExecRequest sqlLineExecRequest) throws SQLException {
        SortedSet<Partition> partitions = logicTableConfig.getPartitions();
        int lineNumber = sqlLineExecRequest.getSqlLineParameter().getLineNumber();
        for (Partition partition : partitions) {
            StringBuilder sb = new StringBuilder();
            SqlTablePartition sqlTablePartition = new SqlTablePartition(exprSqlTable, partition);
            SQLExprTableSource newTableSource = new SQLExprTableSource();
            if (!exprSqlTable.getSchema().equals(PartitionJdbcConstants.EMPTY_NAME)) {
                newTableSource.setExpr(new SQLPropertyExpr(new SQLIdentifierExpr(partition.getPhysicDbName()), partition.getPhysicTableName()));
            } else {
                newTableSource.setExpr(new SQLIdentifierExpr(partition.getPhysicTableName()));
            }
            newTableSource.setAlias(exprSqlTable.getAlias());
            SqlObjCopier sqlObjCopier = new SqlObjCopier();
            sqlObjCopier.addReplaceObj(tableSource, newTableSource);
            SQLStatement sqlStatement = sqlObjCopier.copy(this.sqlStatement);
            MySqlPartitionSqlOutput output = new MySqlPartitionSqlOutput(sb, logicDbConfig, sqlLineExecRequest, sqlTablePartition);
            sqlStatement.accept(output);
            String sql = sb.toString();
            SqlExecPhysicNode sqlExecuteRouter = sqlExecDbNode.get(partition.getPhysicDbName());
            if (output.isParametric()) {//假定所有的分库分表结果只有两种模式，有参数的和无参数的
                if (sqlExecuteRouter == null) {
                    sqlExecuteRouter = new SqlExecPStmtNode();
                    sqlExecDbNode.put(partition.getPhysicDbName(), sqlExecuteRouter);
                }
                SqlExecParamLineNode sqlExecParamLineNode = (SqlExecParamLineNode) sqlExecuteRouter.get(sql);
                if (sqlExecParamLineNode == null) {
                    sqlExecParamLineNode = new SqlExecParamLineNode(sql, partition.getPhysicDbName());
                    sqlExecuteRouter.put(sql, sqlExecParamLineNode);
                }
                List<SqlParameter> sqlParameterList = output.getSqlParameterList();
                sqlExecParamLineNode.addParamLine(new LinedParameters(lineNumber, sqlParameterList));
            } else {
                if (sqlExecuteRouter == null) {
                    sqlExecuteRouter = new SqlExecStmtNode(partition.getPhysicDbName());
                    sqlExecDbNode.put(partition.getPhysicDbName(), sqlExecuteRouter);
                }
                SqlExecStmtNode sqlExecStmtNode = (SqlExecStmtNode) sqlExecuteRouter;
                sqlExecStmtNode.addSql(new LinedSql(lineNumber,sql));
            }
        }
    }



}
