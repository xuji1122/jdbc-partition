package org.the.force.jdbc.partition.engine.executor.ast;

import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.stmt.SqlTablePartition;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/5/18.
 */
public class MyExecutableReplaceIntoAst extends AbstractExecutableAst {

    private final MySqlReplaceStatement mySqlReplaceStatement;

    public MyExecutableReplaceIntoAst(LogicDbConfig logicDbConfig, MySqlReplaceStatement sqlStatement) throws Exception {
        super(logicDbConfig);
        this.mySqlReplaceStatement = sqlStatement;
    }

    public Map<Partition, SqlTablePartition> doRoute(SqlLineExecRequest sqlLineExecRequest) throws SQLException {
        return null;
    }

    public SQLStatement getOriginStatement() {
        return mySqlReplaceStatement;
    }
}
