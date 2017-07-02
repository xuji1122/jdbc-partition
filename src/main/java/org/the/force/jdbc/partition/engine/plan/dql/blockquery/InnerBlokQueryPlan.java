package org.the.force.jdbc.partition.engine.plan.dql.blockquery;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.jdbc.partition.engine.plan.dql.BlockQueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/30.
 */
public class InnerBlokQueryPlan extends BlockQueryPlan {

    private final List<SQLSelectItem> selectList = new ArrayList<>();
    private final SQLExprTableSource sqlExprTableSource;
    private final SQLExpr where;

    public InnerBlokQueryPlan(LogicDbConfig logicDbConfig, List<SQLSelectItem> selectList, SQLExprTableSource sqlExprTableSource, SQLExpr where) {
        super(logicDbConfig);
        this.selectList.addAll(selectList);
        this.sqlExprTableSource = sqlExprTableSource;
        this.where = where;
    }


    public List<SQLSelectItem> getSelectList() {
        return selectList;
    }

    public SQLExprTableSource getSqlExprTableSource() {
        return sqlExprTableSource;
    }

    public SQLExpr getWhere() {
        return where;
    }
}
