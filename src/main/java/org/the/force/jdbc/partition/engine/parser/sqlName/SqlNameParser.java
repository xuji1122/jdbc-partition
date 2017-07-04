package org.the.force.jdbc.partition.engine.parser.sqlName;

import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.jdbc.partition.engine.plan.model.SqlExprTable;

/**
 * Created by xuji on 2017/5/23.
 */
public class SqlNameParser {

    private SqlNameParser() {

    }

    public static SqlExprTable getSQLExprTable(SQLExprTableSource sqlExprTableSource) {
        if (sqlExprTableSource == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        String alias = sqlExprTableSource.getAlias();//大小写敏感
        SqlProperty sqlProperty = getSqlProperty(sqlExprTableSource.getExpr());
        if (sqlProperty == null) {
            throw new SqlParseException("sqlProperty == null");
        }
        return new SqlExprTable(sqlProperty.getOwnerName(), sqlProperty.getName(), alias);
    }


    public static String getSQLIdentifier(SQLExpr sqlExpr) {
        if (sqlExpr == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        if (sqlExpr instanceof SQLIdentifierExpr) {
            String name = ((SQLIdentifierExpr) sqlExpr).getName().toLowerCase();
            return name;
        } else if (sqlExpr instanceof SQLName) {
            return ((SQLName) sqlExpr).getSimpleName().toLowerCase();
        }
        return null;
    }

    public static SqlProperty getSqlProperty(SQLExpr sqlExpr) {
        if (sqlExpr == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        if (sqlExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr sp = (SQLPropertyExpr) sqlExpr;
            String name = sp.getName();
            String owner = null;
            if (sp.getOwner() != null) {
                owner = getSQLIdentifier(sp.getOwner());
                if (owner == null) {
                    return null;
                }
            }
            return new SqlProperty(owner, name);
        } else {
            String name = getSQLIdentifier(sqlExpr);
            if (name == null) {
                return null;
            }
            return new SqlProperty(null, name);
        }
    }

    public static SQLExprTableSource copySQLExprTableSource(SQLExprTableSource sqlExprTableSource) {
        SQLExprTableSource newS = new SQLExprTableSource();
        newS.setParent(sqlExprTableSource.getParent());
        newS.setAlias(sqlExprTableSource.getAlias());
        SQLExpr n = copySqlProperty(sqlExprTableSource.getExpr());
        newS.setExpr(n);
        return newS;
    }

    public static SQLExpr copySqlProperty(SQLExpr sqlExpr) {
        if (sqlExpr == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        if (sqlExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr sp = (SQLPropertyExpr) sqlExpr;
            SQLPropertyExpr news = new SQLPropertyExpr();
            news.setParent(sqlExpr.getParent());
            news.setName(sp.getName());
            news.setOwner(copyIdentifier(sp.getOwner()));
            return news;
        } else {
            return copyIdentifier(sqlExpr);
        }
    }

    public static SQLExpr copyIdentifier(SQLExpr sqlExpr) {
        if (sqlExpr == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        if (sqlExpr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlExpr;
            SQLIdentifierExpr news = new SQLIdentifierExpr(sqlIdentifierExpr.getName());
            news.setParent(sqlExpr.getParent());
            return news;
        } else {
            //TODO 异常check
        }
        return null;
    }

}
