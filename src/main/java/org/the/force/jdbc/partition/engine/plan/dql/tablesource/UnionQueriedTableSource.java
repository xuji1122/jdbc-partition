package org.the.force.jdbc.partition.engine.plan.dql.tablesource;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLHint;
import org.druid.sql.ast.statement.SQLTableSource;
import org.druid.sql.ast.statement.SQLTableSourceImpl;
import org.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.List;

/**
 * Created by xuji on 2017/7/1.
 */
public class UnionQueriedTableSource extends SQLTableSourceImpl implements SQLTableSource {


    private final LogicDbConfig logicDbConfig;

    private final SQLUnionQueryTableSource sqlUnionQueryTableSource;

    private final SQLExpr originalWhere;


    public UnionQueriedTableSource(LogicDbConfig logicDbConfig, SQLUnionQueryTableSource sqlUnionQueryTableSource, SQLExpr originalWhere) {
        this.logicDbConfig = logicDbConfig;
        this.sqlUnionQueryTableSource = sqlUnionQueryTableSource;
        this.originalWhere = originalWhere;
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

    public SQLUnionQueryTableSource getSqlUnionQueryTableSource() {
        return sqlUnionQueryTableSource;
    }

    public SQLExpr getOriginalWhere() {
        return originalWhere;
    }
}
