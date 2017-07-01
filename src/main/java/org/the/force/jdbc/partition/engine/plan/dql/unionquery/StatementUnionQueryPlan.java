package org.the.force.jdbc.partition.engine.plan.dql.unionquery;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLHint;
import org.druid.sql.ast.SQLLimit;
import org.druid.sql.ast.SQLObject;
import org.druid.sql.ast.SQLObjectImpl;
import org.druid.sql.ast.SQLOrderBy;
import org.druid.sql.ast.statement.SQLTableSourceImpl;
import org.druid.sql.ast.statement.SQLUnionQuery;
import org.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.engine.plan.QueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.List;

/**
 * Created by xuji on 2017/6/3.
 * 不同的逻辑表之间union  不支持  说明表关系设计得不好，只支持典型的关系型数据库
 */
public class StatementUnionQueryPlan extends SQLTableSourceImpl implements QueryPlan {

    private final LogicDbConfig logicDbConfig;
    private final SQLUnionQuery sqlUnionQuery;
    private SQLOrderBy orderBy;
    private SQLLimit mysqlLimit;


    public StatementUnionQueryPlan(LogicDbConfig logicDbConfig, SQLUnionQuery sqlUnionQuery) {
        this.logicDbConfig = logicDbConfig;
        this.sqlUnionQuery = sqlUnionQuery;
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
