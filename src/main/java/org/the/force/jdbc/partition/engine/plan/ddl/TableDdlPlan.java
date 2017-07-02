package org.the.force.jdbc.partition.engine.plan.ddl;

import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.LinedSql;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PreparedPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.StaticPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.parser.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlNameParser;
import org.the.force.jdbc.partition.engine.plan.PhysicSqlPlan;
import org.the.force.jdbc.partition.engine.plan.model.SqlExprTable;
import org.the.force.jdbc.partition.engine.plan.model.SqlTablePartition;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;

import java.util.SortedSet;

/**
 * Created by xuji on 2017/5/18.
 */
public class TableDdlPlan implements PhysicSqlPlan {

    protected final LogicDbConfig logicDbConfig;

    private final SQLExprTableSource tableSource;

    private final SQLStatement sqlStatement;


    protected final SqlExprTable sqlExprTable;

    protected final LogicTableConfig logicTableConfig;

    public TableDdlPlan(LogicDbConfig logicDbConfig, SQLStatement sqlStatement, SQLExprTableSource tableSource) {
        this.logicDbConfig = logicDbConfig;
        this.sqlStatement = sqlStatement;
        this.tableSource = tableSource;
        sqlExprTable = SqlNameParser.getSQLExprTable(this.tableSource);
        logicTableConfig = logicDbConfig.getLogicTableManager(sqlExprTable.getTableName()).getLogicTableConfig()[0];
    }

    public void setParameters(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        SortedSet<Partition> partitions = logicTableConfig.getPartitions();
        for (Partition partition : partitions) {
            StringBuilder sb = new StringBuilder();
            SqlTablePartition sqlTablePartition = new SqlTablePartition(sqlExprTable, partition);
            SQLExprTableSource newTableSource = new SQLExprTableSource();
            if (!sqlExprTable.getSchema().equals(PartitionJdbcConstants.EMPTY_NAME)) {
                newTableSource.setExpr(new SQLPropertyExpr(new SQLIdentifierExpr(partition.getPhysicDbName()), partition.getPhysicTableName()));
            } else {
                newTableSource.setExpr(new SQLIdentifierExpr(partition.getPhysicTableName()));
            }
            newTableSource.setAlias(sqlExprTable.getAlias());
            SqlObjCopier sqlObjCopier = new SqlObjCopier();
            sqlObjCopier.addReplaceObj(tableSource,newTableSource);
            SQLStatement sqlStatement = sqlObjCopier.copy(this.sqlStatement);
            MySqlPartitionSqlOutput output = new MySqlPartitionSqlOutput(sb, sqlTablePartition, logicSqlParameterHolder);
            sqlStatement.accept(output);
            String sql = sb.toString();
            if (output.isParametric()) {
                PreparedPhysicSqlExecutor preparedPartitionSqlExecutor = new PreparedPhysicSqlExecutor(sql, partition.getPhysicDbName());
                preparedPartitionSqlExecutor.addParameters(new LinedParameters(logicSqlParameterHolder.getLineNumber(), output.getSqlParameterList()));
                physicDbExecutor.get(partition.getPhysicDbName()).add(preparedPartitionSqlExecutor);
            } else {
                StaticPhysicSqlExecutor staticPartitionSqlExecutor = new StaticPhysicSqlExecutor(partition.getPhysicDbName());
                staticPartitionSqlExecutor.addSql(new LinedSql(logicSqlParameterHolder.getLineNumber(), sql));
                physicDbExecutor.get(partition.getPhysicDbName()).add(staticPartitionSqlExecutor);
            }
        }
    }

}
