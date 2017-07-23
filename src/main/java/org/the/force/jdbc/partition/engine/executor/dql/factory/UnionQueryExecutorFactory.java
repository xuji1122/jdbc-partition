package org.the.force.jdbc.partition.engine.executor.dql.factory;

import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.factory.QueryExecutorFactory;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/6/3.
 * 不同的逻辑表之间union  不支持  说明表关系设计得不好，只支持典型的关系型数据库
 */
public class UnionQueryExecutorFactory implements QueryExecutorFactory {


    public UnionQueryExecutorFactory(LogicDbConfig logicDbConfig, SQLUnionQuery sqlUnionQuery) {
        //TODO 拆解为多个
    }

    protected void accept0(SQLASTVisitor visitor) {

    }

    public String getAlias() {
        return null;
    }

    public void setAlias(String alias) {

    }

    public QueryExecutor buildQueryExecutor() {
        return null;
    }
}
