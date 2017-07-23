package org.the.force.jdbc.partition.engine.parser.table;

import org.the.force.jdbc.partition.engine.executor.dql.partition.PartitionBlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.JoinedTableSource;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SelectLabelParser;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.table.QueriedSqlTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/5.
 */
public class SqlTableParser {

    private final LogicDbConfig logicDbConfig;

    public SqlTableParser(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    //主要目的是获取tableSource包括哪些列，从而为sql条件归集提供必要的依据
    public ConditionalSqlTable getSqlTable(SQLTableSource tableSource) {
        if (tableSource instanceof SQLExprTableSource) {
            ExprConditionalSqlTable sqlTable = new ExprConditionalSqlTable(logicDbConfig, (SQLExprTableSource) tableSource);
            return sqlTable;
        } else if (tableSource instanceof JoinedTableSource) {
            throw new SqlParseException("JoinedTableSource 不能用于获取SqlTable");
        } else if (tableSource instanceof SQLJoinTableSource) {
            throw new SqlParseException("SQLJoinTableSource 不能用于获取SqlTable");
        }
        if (tableSource.getAlias() == null) {
            throw new SqlParseException("tableSource.getAlias()==null)");
        } else if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) tableSource;
            SQLSelectQuery sqlSelectQuery = sqlSubqueryTableSource.getSelect().getQuery();
            return new QueriedSqlTable(tableSource) {
                public List<String> getAllReferAbleLabels() {
                    return new SelectLabelParser(logicDbConfig).parseSelectLabels(sqlSelectQuery);
                }
            };
        } else if (tableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource) tableSource;
            return new QueriedSqlTable(tableSource) {
                public List<String> getAllReferAbleLabels() {
                    return new SelectLabelParser(logicDbConfig).parseSelectLabels(sqlUnionQueryTableSource.getUnion());
                }
            };
        } else {
            //TODO
            throw new SqlParseException("无法识别的tableSource类型" + tableSource.getClass().getName());
        }
    }


}
