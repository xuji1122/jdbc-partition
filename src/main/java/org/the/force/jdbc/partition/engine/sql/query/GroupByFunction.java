package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public class GroupByFunction {

    private SqlExprEvaluator having;//返回true false

    private List<SqlRefer> itemList = new ArrayList<>();

    public SqlExprEvaluator getHaving() {
        return having;
    }

    public void setHaving(SqlExprEvaluator having) {
        this.having = having;
    }

    public List<SqlRefer> getItemList() {
        return itemList;
    }

}
