package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public class GroupBy {

    private SqlExprEvaluator having;//返回true false

    private final List<SqlExprEvaluator> sqlExprEvaluatorList = new ArrayList<>();

    public SqlExprEvaluator getHaving() {
        return having;
    }

    public void setHaving(SqlExprEvaluator having) {
        this.having = having;
    }

    public void addItem(SqlExprEvaluator sqlExprEvaluator) {
        sqlExprEvaluatorList.add(sqlExprEvaluator);
    }

    public void updateSqlExprEvaluator(int index, SqlExprEvaluator sqlExprEvaluator) {
        if (!sqlExprEvaluator.getOriginalSqlExpr().equals(sqlExprEvaluatorList.get(index).getOriginalSqlExpr())) {
            throw new RuntimeException("!sqlExprEvaluator.getOriginalSqlExpr().equals(sqlExprEvaluatorList.get(index))");
        }
        sqlExprEvaluatorList.set(index, sqlExprEvaluator);
    }

    public SqlExprEvaluator getSqlExprEvaluator(int index) {
        return sqlExprEvaluatorList.get(index);
    }

    public int getItemSize() {
        return sqlExprEvaluatorList.size();
    }


    /**
     * 当使用group by重新排序时，先把group by的列尽量按照order by的列的顺序排序，让order by最大限度被兼容
     *
     * @param orderBy
     */
    public void sortFromOrderBy(SelectTable selectTable, OrderBy orderBy) {
        if (orderBy == null) {
            return;
        }
        List<OrderByItem> orderByItemList = orderBy.getOrderByItems();
        Collections.sort(sqlExprEvaluatorList, new Comparator<SqlExprEvaluator>() {
            public int compare(SqlExprEvaluator o1, SqlExprEvaluator o2) {
                int size = orderByItemList.size();
                int o1P = size;
                int o2P = size;
                for (int i = 0; i < size; i++) {
                    OrderByItem orderByItem = orderByItemList.get(i);
                    if (o1P == size && selectTable.checkEquals(o1, orderByItem.getRsIndexEvaluator())) {
                        o1P = i;
                    }
                    if (o2P == size && selectTable.checkEquals(o2, orderByItem.getRsIndexEvaluator())) {
                        o2P = i;
                    }
                }
                return o1P - o2P;
            }
        });
    }

}
