package org.the.force.jdbc.partition.engine.router;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.sql.elements.SqlColumnValue;
import org.the.force.jdbc.partition.engine.sql.elements.SqlRefer;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.rule.PartitionColumnValue;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by xuji on 2017/7/17.
 */
class ColumnInValueListRouter {

    private final SqlExprEvalContext sqlExprEvalContext;

    private final List<List<SQLExpr>> tables;
    //parititionColumnsIndexes;
    private final List<List<Pair<SqlRefer, Integer>>> partitionColumnIndexes;

    private final List<SQLInListEvaluator> tableGetters;

    private final List<List<Object[]>> tableRows = new ArrayList<>();

    private final List<SqlColumnValue> columnValueList;

    private TreeSet<PartitionColumnValue> partitionColumnValueTreeSet = new TreeSet<>();

    private int[] cursors;

    private int[] limits;

    private boolean end;

    private Map<List<SQLExpr>, Pair<SQLInListExpr, Object[]>> currentRowColumnValues = new HashMap<>();

    public ColumnInValueListRouter(SqlExprEvalContext sqlExprEvalContext, Map<List<SQLExpr>, SQLInListEvaluator> partitionColumnInValueListMap,
        Map<SqlRefer, SqlExprEvaluator> partitionColumnValueMap) throws SQLException {
        this.sqlExprEvalContext = sqlExprEvalContext;
        int size = partitionColumnInValueListMap.size();
        cursors = new int[size];
        limits = new int[size];
        tables = new ArrayList<>(size);
        tableGetters = new ArrayList<>(size);
        partitionColumnIndexes = new ArrayList<>(size);
        try {
            partitionColumnInValueListMap.keySet().forEach(key -> {
                tables.add(key);
                SQLInListEvaluator sqlInListEvaluator = partitionColumnInValueListMap.get(key);
                tableGetters.add(sqlInListEvaluator);
                try {
                    List<Object[]> values = sqlInListEvaluator.getTargetListValue(sqlExprEvalContext, null);
                    if (values == null || values.isEmpty()) {//有一个没有值  则整体都不符合  and语义下
                        end = true;
                    }
                    tableRows.add(values);
                } catch (SQLException e) {
                    throw new SqlParseException(e);
                }
                int size2 = key.size();
                List<Pair<SqlRefer, Integer>> pairs = new ArrayList<>();
                for (int i = 0; i < size2; i++) {
                    SQLExpr sqlExpr = key.get(i);
                    if (sqlExpr instanceof SqlRefer) {
                        SqlRefer sqlRefer = (SqlRefer) sqlExpr;
                        pairs.add(new Pair<>(sqlRefer, i));

                    }
                }
                partitionColumnIndexes.add(pairs);
            });
        } catch (SqlParseException e) {
            if (e.getCause() instanceof SQLException) {
                throw ((SQLException) e.getCause());
            }
            throw e;
        }

        for (int i = 0; i < size; i++) {
            cursors[i] = 0;
            limits[i] = tableRows.get(i).size();
        }
        cursors[cursors.length - 1] = -1;

        columnValueList = new ArrayList<>();
        for (Map.Entry<SqlRefer, SqlExprEvaluator> entry2 : partitionColumnValueMap.entrySet()) {
            Object value = entry2.getValue().eval(sqlExprEvalContext, null);
            SqlColumnValue columnValueInner = new SqlColumnValue(entry2.getKey().getName(), value);
            columnValueList.add(columnValueInner);
        }
    }

    //将取值的游标加+1 如果低位的到达最大值则前一位往前进1
    private boolean add(int index) {
        if (index >= cursors.length || index < 0) {
            return false;
        }
        //+1
        cursors[index] = cursors[index] + 1;
        if (cursors[index] < limits[index]) {
            return true;
        }
        //到达最大值 进位
        if (add(index - 1)) {
            cursors[index] = 0;
            return true;
        } else {
            return false;
        }
    }

    public boolean next() {
        partitionColumnValueTreeSet.clear();
        currentRowColumnValues.clear();
        if (end) {
            return false;
        }
        if (add(cursors.length - 1)) {
            end = true;
            return false;
        }
        for (int i = 0; i < cursors.length; i++) {
            List<Object[]> list = tableRows.get(i);
            Object[] columnArray = list.get(cursors[i]);
            List<Pair<SqlRefer, Integer>> slqReferIndex = partitionColumnIndexes.get(i);
            for (Pair<SqlRefer, Integer> pair : slqReferIndex) {
                SqlColumnValue sqlColumnValue = new SqlColumnValue(pair.getLeft().getName().toLowerCase(), columnArray[pair.getRight().intValue()]);
                partitionColumnValueTreeSet.add(sqlColumnValue);
            }
            Pair<SQLInListExpr, Object[]> pair = new Pair<>(tableGetters.get(i).getOriginalSqlExpr(), columnArray);
            currentRowColumnValues.put(tables.get(i), pair);
        }

        partitionColumnValueTreeSet.addAll(columnValueList);
        return true;
    }


    public TreeSet<PartitionColumnValue> getCurrentPartitionColumnValues() {
        return partitionColumnValueTreeSet;
    }

    public Map<List<SQLExpr>, Pair<SQLInListExpr, Object[]>> getCurrentRowColumnValues() {
        return currentRowColumnValues;
    }

}
