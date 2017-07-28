package org.the.force.jdbc.partition.engine.executor.dql.logic;

import org.the.force.jdbc.partition.engine.executor.SqlExecutionContext;
import org.the.force.jdbc.partition.engine.sql.query.LogicSelectTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/7/26.
 */
public class JoinedTableBlockQuery extends LogicBlockQueryExecutor {

    private Map<String, LogicSelectTable> selectTableMap = new HashMap<>();

    public JoinedTableBlockQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock) {
        super(logicDbConfig, sqlSelectQueryBlock);
    }

    public ResultSet execute(SqlExecutionContext sqlExecutionContext) throws SQLException {

        return null;


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
