package org.the.force.jdbc.partition.engine.executor.dml;

import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecution;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDeleteStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

/**
 * Created by xuji on 2017/5/18.
 */
public class DeleteExecutor implements BatchAbleSqlExecution {

    private static Log logger = LogFactory.getLog(DeleteExecutor.class);

    private final LogicDbConfig logicDbConfig;

    private final SQLDeleteStatement sqlDeleteStatement;


    private UpdateDelParser updateDelParser;

    public DeleteExecutor(LogicDbConfig logicDbConfig, SQLDeleteStatement sqlStatement) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.sqlDeleteStatement = sqlStatement;
        prepare();
    }

    public void prepare() throws Exception {
        updateDelParser = new UpdateDelParser(logicDbConfig, new UpdateDelParserAdapter() {
            public SQLExprTableSource getSQLExprTableSource() {
                return (SQLExprTableSource) sqlDeleteStatement.getTableSource();
            }

            public void setTableSource(SQLExprTableSource sqlExprTableSource) {
                sqlDeleteStatement.setTableSource(sqlExprTableSource);
            }

            public SQLStatement getSQLStatement() {
                return sqlDeleteStatement;
            }

            public SQLExpr getCondition() {
                return sqlDeleteStatement.getWhere();
            }

            public PartitionEvent.EventType getEventType() {
                return PartitionEvent.EventType.DELETE;
            }
        });

    }


    public void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        updateDelParser.addSqlLine(physicDbExecutor, logicSqlParameterHolder);
    }

    @Override
    public String toString() {
        return "DeleteExecutor{" + "updateDelParser=" + updateDelParser + '}';
    }


}
