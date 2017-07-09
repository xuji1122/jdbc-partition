package org.the.force.jdbc.partition.engine.executor.plan.dml;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.jdbc.partition.rule.PartitionEvent;

/**
 * Created by xuji on 2017/6/1.
 */
public interface UpdateDelParserAdapter {

    SQLExprTableSource getSQLExprTableSource();

    void setTableSource(SQLExprTableSource sqlExprTableSource);

    SQLStatement getSQLStatement();

    SQLExpr getCondition();

    PartitionEvent.EventType getEventType();

}
