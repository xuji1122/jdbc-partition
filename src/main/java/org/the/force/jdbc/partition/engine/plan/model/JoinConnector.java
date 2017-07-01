package org.the.force.jdbc.partition.engine.plan.model;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.statement.SQLJoinTableSource;

/**
 * Created by xuji on 2017/6/11.
 */
public class JoinConnector {

    protected final SQLJoinTableSource.JoinType joinType;
    protected final SQLExpr joinCondition;

    public JoinConnector(SQLJoinTableSource.JoinType joinType, SQLExpr joinCondition) {
        this.joinType = joinType;
        this.joinCondition = joinCondition;
    }

    public SQLJoinTableSource.JoinType getJoinType() {
        return joinType;
    }

    public SQLExpr getJoinCondition() {
        return joinCondition;
    }
}
