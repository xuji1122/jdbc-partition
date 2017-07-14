package org.the.force.jdbc.partition.engine.parser.select;

import org.the.force.jdbc.partition.engine.executor.query.elements.Select;
import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/13.
 *
 */
public class BlockQuerySelectParser extends AbstractVisitor {

    private final LogicDbConfig logicDbConfig;

    private List<SqlRefer> groupByRefers;

    private List<SqlRefer> orderByRefers;

    private List<SQLSelectItem> sqlSelectItems;

    private Map<String,SqlTable> map = new HashMap<>();

    private Map<SqlRefer,Integer> sqlPropertyIntegerMap = new HashMap<>();

    private Select select;

    public BlockQuerySelectParser(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock) throws SQLException{
        this.logicDbConfig = logicDbConfig;
        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        boolean distinctAll = sqlSelectQueryBlock.getDistionOption()>0;
        select = new Select(distinctAll);
    }


    public boolean visit(SQLAggregateExpr astNode) {
        return isContinue();
    }



}
