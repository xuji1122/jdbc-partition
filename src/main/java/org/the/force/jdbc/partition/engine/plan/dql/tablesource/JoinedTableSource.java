package org.the.force.jdbc.partition.engine.plan.dql.tablesource;

import org.the.force.jdbc.partition.engine.parser.sqlName.SelectLabelParser;
import org.the.force.jdbc.partition.engine.plan.model.QueriedSqlTable;
import org.the.force.jdbc.partition.engine.plan.model.SqlExprTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.table.model.LogicTable;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSourceImpl;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlNameParser;
import org.the.force.jdbc.partition.engine.plan.model.JoinConnector;
import org.the.force.jdbc.partition.engine.plan.model.SqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xuji on 2017/6/4.
 */
public class JoinedTableSource extends SQLTableSourceImpl {

    private final LogicDbConfig logicDbConfig;

    private final SQLExpr originalWhere;

    private final List<SQLTableSource> tableSources = new ArrayList<>();

    private final List<SqlTable> sqlTables = new ArrayList<>();

    private final List<JoinConnector> joinConnectors = new ArrayList<>();

    private SQLExpr newWhere;


    public JoinedTableSource(LogicDbConfig logicDbConfig, SQLJoinTableSource sqlJoinTableSource, SQLExpr originalWhere) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.originalWhere = originalWhere;
        parseTableSource(sqlJoinTableSource);

    }

    private void parseTableSource(SQLJoinTableSource sqlJoinTableSource) throws Exception {
        SQLTableSource left = sqlJoinTableSource.getLeft();
        SQLTableSource right = sqlJoinTableSource.getRight();
        if (left instanceof SQLJoinTableSource) {
            parseTableSource((SQLJoinTableSource) left);
        } else {
            SqlTable sqlTable = getSqlTable(left);
            sqlTables.add(sqlTable);
            tableSources.add(left);
        }
        SqlTable sqlTable = getSqlTable(right);
        sqlTables.add(sqlTable);
        tableSources.add(right);
        JoinConnector joinConnector = new JoinConnector(sqlJoinTableSource.getJoinType(), sqlJoinTableSource.getCondition());
        joinConnectors.add(joinConnector);
    }

    //主要目的是获取tableSource包括哪些列，从而为sql条件归集提供必要的依据
    private SqlTable getSqlTable(SQLTableSource tableSource) throws Exception {
        if (tableSource instanceof SQLExprTableSource) {
            SqlExprTable sqlTable = SqlNameParser.getSQLExprTable((SQLExprTableSource) tableSource);
            LogicTable logicTable = logicDbConfig.getLogicTableManager(sqlTable.getTableName()).getLogicTable();
            sqlTable.setLogicTable(logicTable);
            return sqlTable;
        }
        if (tableSource.getAlias() == null) {
            throw new SqlParseException("tableSource.getAlias()==null)");
        }
        if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) tableSource;
            SQLTableSource left = sqlJoinTableSource.getLeft();
            SqlTable leftSqlTable = getSqlTable(left);
            SQLTableSource right = sqlJoinTableSource.getRight();
            SqlTable rightSqlTable = getSqlTable(right);
            Set<String> mergeColumns = new LinkedHashSet<>();
            mergeColumns.addAll(leftSqlTable.getColumns());
            mergeColumns.addAll(rightSqlTable.getColumns());
            return new QueriedSqlTable(tableSource.getAlias(), mergeColumns);
        } else if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) tableSource;
            SQLSelectQuery sqlSelectQuery = sqlSubqueryTableSource.getSelect().getQuery();
            Set<String> columns = new SelectLabelParser(logicDbConfig).parseSelectLabels(sqlSelectQuery);
            return new QueriedSqlTable(tableSource.getAlias(), columns);
        } else if (tableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource)tableSource;
            Set<String> columns = new SelectLabelParser(logicDbConfig).parseSelectLabels(sqlUnionQueryTableSource.getUnion());
            return new QueriedSqlTable(tableSource.getAlias(), columns);
        } else {
            //TODO
            throw new SqlParseException("无法识别的tableSource类型" + tableSource.getClass().getName());
        }
    }

    protected void accept0(SQLASTVisitor visitor) {

    }

    public SQLExpr getNewWhere() {
        return newWhere;
    }
}
