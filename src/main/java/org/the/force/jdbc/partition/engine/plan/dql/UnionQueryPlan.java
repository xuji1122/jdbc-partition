package org.the.force.jdbc.partition.engine.plan.dql;

import org.druid.sql.ast.SQLHint;
import org.druid.sql.ast.SQLObjectImpl;
import org.druid.sql.ast.statement.SQLTableSourceImpl;
import org.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.engine.plan.QueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.List;

/**
 * Created by xuji on 2017/7/1.
 */
public class UnionQueryPlan extends SQLTableSourceImpl implements QueryPlan {

    private final LogicDbConfig logicDbConfig;

    public UnionQueryPlan(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    protected void accept0(SQLASTVisitor visitor) {

    }

    public String getAlias() {
        return null;
    }

    public void setAlias(String alias) {

    }

    public List<SQLHint> getHints() {
        return null;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }
}
