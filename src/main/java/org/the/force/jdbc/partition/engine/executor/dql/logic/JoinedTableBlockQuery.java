package org.the.force.jdbc.partition.engine.executor.dql.logic;

import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/7/26.
 */
public class JoinedTableBlockQuery extends LogicBlockQueryExecutor {

    public JoinedTableBlockQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock) {
        super(logicDbConfig, sqlSelectQueryBlock);
    }

    protected void accept0(SQLASTVisitor visitor) {
        //只在打印调试的时候使用
        sqlSelectQueryBlock.accept(visitor);
    }

    public String getAlias() {
        return null;
    }

    public SQLTableSource getOriginalSqlTableSource() {
        return null;
    }


}
