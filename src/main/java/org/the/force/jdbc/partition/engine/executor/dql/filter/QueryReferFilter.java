package org.the.force.jdbc.partition.engine.executor.dql.filter;

import org.the.force.jdbc.partition.engine.sqlelements.sqltable.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sqlelements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.table.SubQueryResetParser;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xuji on 2017/7/14.
 * 在对查询条件按照tableSource过滤分组之后
 * 对子查询类型的tableSource 所具有的条件的封装
 * 条件过滤的是子查询的tableSource的select labels(where和排序) 不涉及group by问题，不涉及排序问题
 */
public class QueryReferFilter {

    private final LogicDbConfig logicDbConfig;

    //引用的表
    private final ConditionalSqlTable referTable;

    //当 selectReferFilterCondition包含子查询时
    private final SubQueryFilter subQueryFilter;

    //join的时候最后依据join的列排序，提高join效率用
    private Set<SqlRefer> orderBySqlRefers;


    public QueryReferFilter(LogicDbConfig logicDbConfig, ConditionalSqlTable referTable) {
        this.logicDbConfig = logicDbConfig;
        this.referTable = referTable;
        if (referTable.getTableOwnCondition() == null) {
            subQueryFilter = null;
        } else {
            SubQueryResetParser conditionChecker = new SubQueryResetParser(logicDbConfig,referTable.getTableOwnCondition());
            List<SQLExpr> subQueries = conditionChecker.getSubQueryList();
            if (subQueries != null && !subQueries.isEmpty()) {
                subQueryFilter = new SubQueryFilter(logicDbConfig, subQueries);
            } else {
                subQueryFilter = null;
            }
        }
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public ConditionalSqlTable getReferTable() {
        return referTable;
    }

    public SQLExpr getSelectReferFilterCondition() {
        return referTable.getTableOwnCondition();
    }

    public SubQueryFilter getSubQueryFilter() {
        return subQueryFilter;
    }

    public Set<SqlRefer> getOrderBySqlRefers() {
        if (orderBySqlRefers == null) {
            orderBySqlRefers = new LinkedHashSet<>();
        }
        return orderBySqlRefers;
    }

}
