package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;

/**
 * Created by xuji on 2017/7/14.
 */
public abstract class PartitionAbstractVisitor extends AbstractVisitor implements PartitionSqlASTVisitor{

    public boolean visit(SubQueriedExpr x) {
        return isContinue();
    }

//    public boolean visit(SQLInSubQueriedExpr x) {
//        return isContinue();
//    }

    public boolean visit(ParallelJoinedTableSource parallelJoinedTableSource){
        return isContinue();
    }

}
