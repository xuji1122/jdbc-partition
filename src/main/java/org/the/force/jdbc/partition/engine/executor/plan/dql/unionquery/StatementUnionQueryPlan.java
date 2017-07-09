package org.the.force.jdbc.partition.engine.executor.plan.dql.unionquery;

import org.the.force.jdbc.partition.engine.executor.plan.QueryPlan;
import org.the.force.jdbc.partition.engine.executor.plan.dql.UnionQueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

/**
 * Created by xuji on 2017/6/3.
 * 不同的逻辑表之间union  不支持  说明表关系设计得不好，只支持典型的关系型数据库
 */
public class StatementUnionQueryPlan extends UnionQueryPlan implements QueryPlan {

    private final SQLUnionQuery sqlUnionQuery;
    private final SQLExpr outerCondition;
    private SQLOrderBy orderBy;
    private SQLLimit mysqlLimit;


    public StatementUnionQueryPlan(LogicDbConfig logicDbConfig, SQLUnionQuery sqlUnionQuery) {
        this(logicDbConfig, sqlUnionQuery, null);
    }

    public StatementUnionQueryPlan(LogicDbConfig logicDbConfig, SQLUnionQuery sqlUnionQuery, SQLExpr outerCondition) {
        super(logicDbConfig);
        this.sqlUnionQuery = sqlUnionQuery;
        this.outerCondition = outerCondition;
        //TODO 拆解为多个
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public SQLLimit getMysqlLimit() {
        return mysqlLimit;
    }

    public void setMysqlLimit(SQLLimit mysqlLimit) {
        this.mysqlLimit = mysqlLimit;
    }


    protected void accept0(SQLASTVisitor visitor) {

    }

    public String getAlias() {
        return null;
    }

    public void setAlias(String alias) {

    }

    @Override
    public List<SQLHint> getHints() {
        return null;
    }


    @Override
    public String computeAlias() {
        return null;
    }

    @Override
    public SQLExpr getFlashback() {
        return null;
    }

    @Override
    public void setFlashback(SQLExpr flashback) {

    }


}
