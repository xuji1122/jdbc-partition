package org.the.force.jdbc.partition.engine.plan.dml;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLStatement;
import org.druid.sql.ast.statement.SQLDeleteStatement;
import org.druid.sql.ast.statement.SQLExprTableSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.plan.PhysicSqlPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.rule.PartitionEvent;

/**
 * Created by xuji on 2017/5/18.
 */
public class DeletePlan implements PhysicSqlPlan {

    private static Logger logger = LoggerFactory.getLogger(DeletePlan.class);

    private final LogicDbConfig logicDbConfig;

    private final SQLDeleteStatement sqlDeleteStatement;


    private UpdateDelParser updateDelParser;

    public DeletePlan(LogicDbConfig logicDbConfig, SQLDeleteStatement sqlStatement) throws Exception {
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


    public void setParameters(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        updateDelParser.addParameters(physicDbExecutor, logicSqlParameterHolder);
    }

    @Override
    public String toString() {
        return "DeletePlan{" + "updateDelParser=" + updateDelParser + '}';
    }


}
