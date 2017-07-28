package org.the.force.jdbc.partition.engine.executor.dql.logic;

import org.the.force.jdbc.partition.engine.executor.SqlExecutionContext;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/26.
 */
public  class PassiveBlockQuery extends LogicBlockQueryExecutor {

    private final ConditionalSqlTable originalConditionalSqlTable;

    public PassiveBlockQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock, ConditionalSqlTable originalConditionalSqlTable) {
        super(logicDbConfig, sqlSelectQueryBlock);
        this.originalConditionalSqlTable = originalConditionalSqlTable;
    }

    public String getAlias() {
        return originalConditionalSqlTable.getSQLTableSource().getAlias();
    }


    public SQLTableSource getOriginalSqlTableSource() {
        return originalConditionalSqlTable.getSQLTableSource();
    }


    public ResultSet execute(SqlExecutionContext sqlExecutionContext) throws SQLException {

        return null;
    }


    protected void accept0(SQLASTVisitor visitor) {
        //只在打印调试的时候使用
        SQLSelectQueryBlock sqlSelectQueryBlock = this.sqlSelectQueryBlock;
        SQLTableSource sqlTableSource = sqlSelectQueryBlock.getFrom();
        BlockQueryExecutor blockQueryExecutor = (BlockQueryExecutor) sqlTableSource;
        SQLSubqueryTableSource subqueryTableSource = new SQLSubqueryTableSource(new SQLSelect(blockQueryExecutor), originalConditionalSqlTable.getAlias());
        SqlObjCopier sqlObjCopier = new SqlObjCopier();
        sqlObjCopier.addReplaceObj(sqlSelectQueryBlock.getFrom(), subqueryTableSource);
        sqlSelectQueryBlock = sqlObjCopier.copy(sqlSelectQueryBlock);
        sqlSelectQueryBlock.accept(visitor);
    }
}
