package org.the.force.jdbc.partition.engine.executor.dql.filter;

import org.the.force.jdbc.partition.engine.sql.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xuji on 2017/7/14.
 * 在对查询条件按照tableSource过滤分组之后
 * 对子查询类型的tableSource 所具有的条件的封装
 * 条件过滤的是子查询的tableSource的select labels(where和排序) 不涉及group by问题
 */
public class QueryReferFilter {

    private final LogicDbConfig logicDbConfig;

    //引用的表
    private final ConditionalSqlTable referTable;

    /**
     * referTable 如果需要和其他表join,则referTable的结果集 最好依据作为join条件的列排序，提高join merge效率
     */
    private Set<SqlRefer> orderBySqlRefersForJoin;


    public QueryReferFilter(LogicDbConfig logicDbConfig, ConditionalSqlTable referTable) {
        this.logicDbConfig = logicDbConfig;
        this.referTable = referTable;
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

    public Set<SqlRefer> getOrderBySqlRefersForJoin() {
        if (orderBySqlRefersForJoin == null) {
            orderBySqlRefersForJoin = new LinkedHashSet<>();
        }
        return orderBySqlRefersForJoin;
    }

}
