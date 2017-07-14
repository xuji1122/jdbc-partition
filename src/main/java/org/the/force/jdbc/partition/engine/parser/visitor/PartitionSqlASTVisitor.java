package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.UnionQueriedTableSource;
import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import org.the.force.jdbc.partition.engine.executor.dql.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.dql.subqueryexpr.SQLInSubQueriedExpr;

/**
 * Created by xuji on 2017/6/9.
 */
public interface PartitionSqlASTVisitor extends MySqlASTVisitor {


    boolean visit(ExitsSubQueriedExpr x);

    boolean visit(SQLInSubQueriedExpr x);

    boolean visit(ParallelJoinedTableSource parallelJoinedTableSource);

    boolean visit(SubQueriedTableSource subQueriedTableSource);

    boolean visit(UnionQueriedTableSource unionQueriedTableSource);



}
