package org.the.force.jdbc.partition.engine.parser.sqlName;

import org.the.force.jdbc.partition.engine.parser.elements.SqlProperty;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;

/**
 * Created by xuji on 2017/5/23.
 */
public class SqlNameParser extends AbstractVisitor {


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

    public static SqlProperty getSqlProperty(SQLExpr sqlExpr) {
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
            return new SqlProperty(owner, name);
        } else if (sqlExpr instanceof SQLAllColumnExpr) {
            return new SqlProperty(null, "*");
        } else {
            String name = getSQLIdentifier(sqlExpr);
            if (name == null) {
                return null;
            }
            return new SqlProperty(null, name);
        }
    }
}
