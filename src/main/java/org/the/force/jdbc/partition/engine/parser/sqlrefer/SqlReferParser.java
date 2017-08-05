package org.the.force.jdbc.partition.engine.parser.sqlrefer;

import org.the.force.jdbc.partition.engine.stmt.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.stmt.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xuji on 2017/5/23.
 * 从指定的sql表达式中获取属于某个sqlTable的引用的集合
 * 如果sqlTable为null则
 */
public class SqlReferParser extends AbstractVisitor {

    private final ConditionalSqlTable sqlTable;

    private Set<SqlRefer> sqlReferSet = new LinkedHashSet<>();

    public SqlReferParser(SQLObject sqlObject) {
        this(sqlObject, null);
    }

    public SqlReferParser(SQLObject sqlObject, ConditionalSqlTable sqlTable) {
        this.sqlTable = sqlTable;
        sqlObject.accept(this);
    }

    public boolean visit(SQLPropertyExpr sqlPropertyExpr) {
        SqlRefer sqlRefer = new SqlRefer(sqlPropertyExpr);
        if (sqlTable == null || SqlTableReferParser.checkOwner(sqlTable, sqlRefer)) {
            sqlReferSet.add(sqlRefer);
        }
        return false;
    }

    public boolean visit(SQLIdentifierExpr sqlIdentifierExpr) {
        SqlRefer sqlRefer = new SqlRefer(sqlIdentifierExpr);

        if (sqlTable == null || SqlTableReferParser.checkOwner(sqlTable, sqlRefer)) {
            sqlReferSet.add(sqlRefer);
        }
        return false;
    }

    public Set<SqlRefer> getSqlReferSet() {
        return sqlReferSet;
    }


}
