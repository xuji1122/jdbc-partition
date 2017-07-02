package org.the.force.jdbc.partition.engine.plan.dql.tablesource;

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
import java.util.List;

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
        //目标是聚合查询则不必归集where条件
    }

    private void parseTableSource(SQLJoinTableSource sqlJoinTableSource) throws Exception {
        SQLTableSource left = sqlJoinTableSource.getLeft();
        SQLTableSource right = sqlJoinTableSource.getRight();
        if (left instanceof SQLJoinTableSource) {
            parseTableSource((SQLJoinTableSource) left);
        } else {
            addAtomicSource(left);
        }
        addAtomicSource(right);
        JoinConnector joinConnector = new JoinConnector(sqlJoinTableSource.getJoinType(), sqlJoinTableSource.getCondition());
        joinConnectors.add(joinConnector);
    }

    private void addAtomicSource(SQLTableSource tableSource) throws Exception {
        if (tableSource instanceof SQLExprTableSource) {
            SqlTable sqlTable = SqlNameParser.getSQLExprTable((SQLExprTableSource) tableSource);
            sqlTables.add(sqlTable);
            tableSources.add(tableSource);
        } else if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) tableSource;
            String alias = sqlSubqueryTableSource.getAlias();
            if (alias == null) {
                //TODO 不支持
                throw new UnsupportedSqlOperatorException("alias == null");
            }
            SQLSelectQuery sqlSelectQuery = sqlSubqueryTableSource.getSelect().getQuery();
            if (sqlSelectQuery == null) {
                throw new UnsupportedSqlOperatorException("sqlSelectQuery == null");
            }
            if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
                List<SQLSelectItem> itemList =  sqlSelectQueryBlock.getSelectList();
                //获取表结构配置

            } else if (sqlSelectQuery instanceof SQLUnionQuery) {
                SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;

            } else {
                throw new UnsupportedSqlOperatorException("sqlSelectQuery not block and not union" + PartitionSqlUtils.toSql(tableSource, logicDbConfig.getSqlDialect()));
            }
            tableSources.add(sqlSubqueryTableSource);
        } else if (tableSource instanceof SQLUnionQueryTableSource) {
            tableSources.add(tableSource);
        } else {
            //TODO
        }
    }

    protected void accept0(SQLASTVisitor visitor) {

    }

    public SQLExpr getNewWhere() {
        return newWhere;
    }
}
