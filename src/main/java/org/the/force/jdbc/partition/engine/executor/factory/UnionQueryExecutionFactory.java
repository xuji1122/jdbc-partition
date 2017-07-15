package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.engine.executor.QueryExecution;
import org.the.force.jdbc.partition.engine.executor.dql.filter.QueryReferFilter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/6/3.
 * 不同的逻辑表之间union  不支持  说明表关系设计得不好，只支持典型的关系型数据库
 */
public class UnionQueryExecutionFactory implements QueryExecutionFactory {

    private QueryExecution queryExecution;

    public UnionQueryExecutionFactory(LogicDbConfig logicDbConfig, SQLUnionQuery sqlUnionQuery) {
        this(logicDbConfig, sqlUnionQuery, null);
    }

    public UnionQueryExecutionFactory(LogicDbConfig logicDbConfig, SQLUnionQuery sqlUnionQuery, QueryReferFilter queryReferFilter) {
        //TODO 拆解为多个
    }


    protected void accept0(SQLASTVisitor visitor) {

    }

    public String getAlias() {
        return null;
    }

    public void setAlias(String alias) {

    }

    @Override
    public QueryExecution getQueryExecution() {
        return queryExecution;
    }
}
