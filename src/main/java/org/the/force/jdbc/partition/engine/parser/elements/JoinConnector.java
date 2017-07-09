package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;

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
