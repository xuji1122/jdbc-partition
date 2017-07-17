package org.the.force.jdbc.partition.engine.parser.sqlrefer;

import org.the.force.jdbc.partition.engine.parser.elements.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.elements.ConditionPartitionSqlTable;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/5/23.
 */
public class SqlReferParser extends AbstractVisitor {

    private final ConditionalSqlTable sqlTable;

    private List<SqlRefer> sqlReferList = new ArrayList<>();

    public SqlReferParser(SQLObject sqlObject, ConditionalSqlTable sqlTable) {
        this.sqlTable = sqlTable;
        sqlObject.accept(this);
    }

    public boolean visit(SQLPropertyExpr sqlPropertyExpr) {
        SqlRefer sqlRefer = new SqlRefer(sqlPropertyExpr);
        if (SqlTableReferParser.checkOwner(sqlTable, sqlRefer)) {
            sqlReferList.add(sqlRefer);
        }
        return false;
    }

    public boolean visit(SQLIdentifierExpr sqlIdentifierExpr) {
        SqlRefer sqlRefer = new SqlRefer(sqlIdentifierExpr);

        if (SqlTableReferParser.checkOwner(sqlTable, sqlRefer)) {
            sqlReferList.add(sqlRefer);
        }
        return false;
    }

    public List<SqlRefer> getSqlReferList() {
        return sqlReferList;
    }



}
