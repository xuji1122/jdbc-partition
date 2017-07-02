package org.the.force.jdbc.partition.engine.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/14.
 */
public class SubQueryConditionChecker extends AbstractVisitor {


    private List<SQLExpr> subQueryList = new ArrayList<>();

    public boolean visit(ExitsSubQueriedExpr x) {
        subQueryList.add(x);
        return false;
    }

    public boolean visit(SQLInSubQueriedExpr x) {
        subQueryList.add(x);
        return false;
    }

    public boolean isHasSubQuery() {
        return !subQueryList.isEmpty();
    }

    public List<SQLExpr> getSubQueryList() {
        return subQueryList;
    }
}
