package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectTable {

    //被引用的立场
    private final ConditionalSqlTable sqlTable;

    private final boolean distinctAll;

    private int allColumnStartIndex = -1;//-1代表没有* 当有*存在时以为从数据库拉取列信息失败了，不能保证结果的正确性，只能尽可能满足

    private List<SqlExprEvaluator> selectValueNodes = new ArrayList<>();

    private List<String> selectLabels = new ArrayList<>();

    private int queryBound;

    private int extendBound;

    public SelectTable(ConditionalSqlTable sqlTable, boolean distinctAll) {
        this.sqlTable = sqlTable;
        this.distinctAll = distinctAll;
    }

    public List<SqlExprEvaluator> getSelectValueNodes() {
        return selectValueNodes;
    }

    public int getQueryBound() {
        return queryBound;
    }

    public void setQueryBound(int queryBound) {
        this.queryBound = queryBound;
    }

    public int getExtendBound() {
        return extendBound;
    }

    public void setExtendBound(int extendBound) {
        this.extendBound = extendBound;
    }

    public ConditionalSqlTable getSqlTable() {
        return sqlTable;
    }

    public boolean isDistinctAll() {
        return distinctAll;
    }

    public int getAllColumnStartIndex() {
        return allColumnStartIndex;
    }

    public void setAllColumnStartIndex(int allColumnStartIndex) {
        this.allColumnStartIndex = allColumnStartIndex;
    }

    public List<String> getSelectLabels() {
        return selectLabels;
    }
}
