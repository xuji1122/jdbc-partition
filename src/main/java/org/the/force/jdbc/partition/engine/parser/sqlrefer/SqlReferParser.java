package org.the.force.jdbc.partition.engine.parser.sqlrefer;

import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/5/23.
 */
public class SqlReferParser extends AbstractVisitor {

    private final SqlTable sqlTable;

    private List<SqlRefer> sqlReferList = new ArrayList<>();

    public SqlReferParser(SQLObject sqlObject, SqlTable sqlTable) {
        this.sqlTable = sqlTable;
        sqlObject.accept(this);
    }

    public boolean visit(SQLPropertyExpr sqlPropertyExpr) {
        SqlRefer sqlRefer = getSqlRefer(sqlPropertyExpr);
        if (SqlTableReferParser.checkOwner(sqlTable, sqlRefer)) {
            sqlReferList.add(sqlRefer);
        }
        return false;
    }

    public boolean visit(SQLIdentifierExpr sqlIdentifierExpr) {
        SqlRefer sqlRefer = getSqlRefer(sqlIdentifierExpr);

        if (SqlTableReferParser.checkOwner(sqlTable, sqlRefer)) {
            sqlReferList.add(sqlRefer);
        }
        return false;
    }

    public List<SqlRefer> getSqlReferList() {
        return sqlReferList;
    }

    public static SqlRefer getSqlRefer(SQLExpr sqlExpr) {
        if (sqlExpr == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        if (sqlExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr sp = (SQLPropertyExpr) sqlExpr;
            //name可能为*
            String name = sp.getName();
            String owner = null;
            if (sp.getOwner() != null) {
                owner = getSQLIdentifier(sp.getOwner());
            }
            SqlRefer refer = new SqlRefer(owner, name);
            refer.setParent(sqlExpr.getParent());
            return refer;
        } else if (sqlExpr instanceof SQLAllColumnExpr) {
            SqlRefer refer = new SqlRefer(null, "*");
            refer.setParent(sqlExpr.getParent());
            return refer;
        } else {
            String name = getSQLIdentifier(sqlExpr);
            if (name == null) {
                return null;
            }
            SqlRefer refer = new SqlRefer(null, name);
            refer.setParent(sqlExpr.getParent());
            return refer;
        }
    }

    public static String getSQLIdentifier(SQLExpr sqlExpr) {
        if (sqlExpr == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        if (sqlExpr instanceof SQLIdentifierExpr) {
            String name = ((SQLIdentifierExpr) sqlExpr).getName();
            return name;
        } else if (sqlExpr instanceof SQLAllColumnExpr) {
            return "*";
        } else if (sqlExpr instanceof SQLName) {
            return ((SQLName) sqlExpr).getSimpleName();
        }
        return null;
    }
}
