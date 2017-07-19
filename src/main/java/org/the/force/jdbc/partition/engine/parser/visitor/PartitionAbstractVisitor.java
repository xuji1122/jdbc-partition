package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.JoinedTableSourceExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.executor.LogicBlockQueryExecutor;

/**
 * Created by xuji on 2017/7/14.
 */
public abstract class PartitionAbstractVisitor extends AbstractVisitor implements PartitionSqlASTVisitor {

    public boolean visit(SubQueriedExpr x) {
        return isContinue();
    }

    //    public boolean visit(SQLInSubQueriedExpr x) {
    //        return isContinue();
    //    }

    public boolean visit(JoinedTableSourceExecutor joinedTableSourceExecutor) {
        return isContinue();
    }


    public boolean visit(LogicBlockQueryExecutor logicBlockQueryExecutor) {
        return isContinue();
    }

}
