package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr.SQLInSubQueriedExpr;

/**
 * Created by xuji on 2017/6/9.
 */
public interface PartitionSqlASTVisitor extends MySqlASTVisitor {


    boolean visit(ExitsSubQueriedExpr x);

    boolean visit(SQLInSubQueriedExpr x);

}
