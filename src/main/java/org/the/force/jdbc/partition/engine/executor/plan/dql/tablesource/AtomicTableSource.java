package org.the.force.jdbc.partition.engine.executor.plan.dql.tablesource;

import org.the.force.jdbc.partition.engine.executor.plan.dql.PlanedTableSource;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTableColumns;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableColumnsParser;
import org.the.force.jdbc.partition.engine.parser.table.SubQueryConditionChecker;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/6.
 */
public class AtomicTableSource extends PlanedTableSource {

    private final SQLExprTableSource sqlExprTableSource;
    private final SqlTable sqlTable;
    private final SQLExpr condition;
    private final SqlTableColumns sqlTableColumns;
    private final Map<SqlColumn, SQLExpr> columnValueMap;
    private final Map<SqlColumn, SQLInListExpr> columnInValuesMap;
    private final List<SQLExpr> subQuerys;

    public AtomicTableSource(LogicDbConfig logicDbConfig, SQLExprTableSource sqlExprTableSource, SqlTable sqlTable, SQLExpr condition, Map<SqlColumn, SQLExpr> columnValueMap,
        Map<SqlColumn, SQLInListExpr> columnInValuesMap) {
        super(logicDbConfig);
        this.sqlExprTableSource = sqlExprTableSource;
        this.sqlTable = sqlTable;
        this.condition = condition;
        this.columnValueMap = columnValueMap;
        this.columnInValuesMap = columnInValuesMap;

        SqlTableColumnsParser parser = new SqlTableColumnsParser(logicDbConfig, sqlExprTableSource, sqlTable);
        sqlTableColumns = parser.getSqlTableColumns();

        if (condition != null) {
            SubQueryConditionChecker conditionChecker = new SubQueryConditionChecker(logicDbConfig);
            condition.accept(conditionChecker);
            subQuerys = conditionChecker.getSubQueryList();
        } else {
            subQuerys = null;
        }
    }

    public SQLExprTableSource getSqlExprTableSource() {
        return sqlExprTableSource;
    }

    public SqlTable getSqlTable() {
        return sqlTable;
    }

    public SQLExpr getCondition() {
        return condition;
    }


    public SqlTableColumns getSqlTableColumns() {
        return sqlTableColumns;
    }

    public Map<SqlColumn, SQLExpr> getColumnValueMap() {
        return columnValueMap;
    }

    public Map<SqlColumn, SQLInListExpr> getColumnInValuesMap() {
        return columnInValuesMap;
    }
}
