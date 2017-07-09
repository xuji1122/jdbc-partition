package org.the.force.jdbc.partition.engine.parser.sqlName;

import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.QueriedSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlProperty;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;

import java.sql.SQLException;
import java.util.Set;

/**
 * Created by xuji on 2017/7/5.
 */
public class SqlTableParser {

    private final LogicDbConfig logicDbConfig;

    public SqlTableParser(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    public SqlTableParser() {
        this.logicDbConfig = null;
    }

    //主要目的是获取tableSource包括哪些列，从而为sql条件归集提供必要的依据
    public SqlTable getSqlTable(SQLTableSource tableSource){
        if (tableSource instanceof SQLExprTableSource) {
            ExprSqlTable sqlTable = getSQLExprTable((SQLExprTableSource) tableSource, logicDbConfig);
            return sqlTable;
        } else if (tableSource instanceof SQLJoinTableSource) {
            throw new SqlParseException("SQLJoinTableSource 不能用于获取SqlTable");
        }
        if (tableSource.getAlias() == null) {
            throw new SqlParseException("tableSource.getAlias()==null)");
        }
        if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) tableSource;
            SQLSelectQuery sqlSelectQuery = sqlSubqueryTableSource.getSelect().getQuery();
            return new QueriedSqlTable(tableSource.getAlias()) {
                public Set<String> getColumns() {
                    Set<String> columns;
                    try {
                        columns = new SelectLabelParser(logicDbConfig).parseSelectLabels(sqlSelectQuery);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return columns;
                }
            };
        } else if (tableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource) tableSource;
            return new QueriedSqlTable(tableSource.getAlias()) {
                public Set<String> getColumns() {
                    Set<String> columns;
                    try {
                        columns = new SelectLabelParser(logicDbConfig).parseSelectLabels(sqlUnionQueryTableSource.getUnion());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return columns;
                }
            };
        } else {
            //TODO
            throw new SqlParseException("无法识别的tableSource类型" + tableSource.getClass().getName());
        }
    }

    public static ExprSqlTable getSQLExprTable(SQLExprTableSource sqlExprTableSource,LogicDbConfig logicDbConfig) {
        if (sqlExprTableSource == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        String alias = sqlExprTableSource.getAlias();//大小写敏感
        SqlProperty sqlProperty = SqlNameParser.getSqlProperty(sqlExprTableSource.getExpr());
        if (sqlProperty == null) {
            throw new SqlParseException("sqlProperty == null");
        }
        return new ExprSqlTable(logicDbConfig,sqlProperty.getOwnerName(), sqlProperty.getName(), alias);
    }
}
