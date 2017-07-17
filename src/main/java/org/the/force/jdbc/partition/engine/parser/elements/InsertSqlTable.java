package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/7/17.
 * insert中定义的SQLExprTableSource
 */
public class InsertSqlTable extends ExprSqlTable implements SqlTable {

    private Map<Integer, SqlRefer> columnMap = new LinkedHashMap<>();

    //第一行的values的每一列的取值函数
    private Map<Integer, SqlExprEvaluator> evaluatorMap = new LinkedHashMap<>();

    public InsertSqlTable(LogicDbConfig logicDbConfig, SQLExprTableSource sqlExprTableSource) {
        super(logicDbConfig, sqlExprTableSource);
    }

    public Map<Integer, SqlRefer> getColumnMap() {
        return columnMap;
    }

    public Map<Integer, SqlExprEvaluator> getEvaluatorMap() {
        return evaluatorMap;
    }
}
