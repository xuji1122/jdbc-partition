package org.the.force.jdbc.partition.engine.plan.dml;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLStatement;
import org.druid.sql.ast.statement.SQLExprTableSource;
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
