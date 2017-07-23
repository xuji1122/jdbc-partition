package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlQueryExpr;
import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * Created by xuji on 2017/6/9.
 */
public interface PartitionSqlASTVisitor extends MySqlASTVisitor {


    boolean visit(SqlQueryExpr x);

    boolean visit(SqlInSubQueriedExpr x);


}
