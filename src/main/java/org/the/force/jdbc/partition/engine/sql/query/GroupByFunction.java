package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public class GroupByFunction {

    private SqlExprEvaluator having;//返回true false

    private List<SqlExprEvaluator> itemList = new ArrayList<>();

    public SqlExprEvaluator getHaving() {
        return having;
    }

    public void setHaving(SqlExprEvaluator having) {
        this.having = having;
    }

    public List<SqlExprEvaluator> getItemList() {
        return itemList;
    }

}
