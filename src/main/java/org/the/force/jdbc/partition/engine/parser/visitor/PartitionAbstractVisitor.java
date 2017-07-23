package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlQueryExpr;

/**
 * Created by xuji on 2017/7/14.
 */
public abstract class PartitionAbstractVisitor extends AbstractVisitor implements PartitionSqlASTVisitor {

    public boolean visit(SqlQueryExpr x) {
        return isContinue();
    }

    public boolean visit(SqlInSubQueriedExpr x) {
        return isContinue();
    }

}
