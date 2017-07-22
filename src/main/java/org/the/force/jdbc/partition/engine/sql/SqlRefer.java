package org.the.force.jdbc.partition.engine.sql;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/27.
 * 在sql文中，对列的引用或者对表格的引用
 */
public class SqlRefer extends AbstractSqlExprEvaluator {

    private final String ownerName;

    private final String name;



    public SqlRefer(SQLName sqlExpr) {
        super(sqlExpr);
        if (sqlExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr sp = (SQLPropertyExpr) sqlExpr;
            //name可能为*
            name = sp.getName();
            if (sp.getOwner() != null) {
                ownerName = getSQLIdentifier(sp.getOwner());
            } else {
                ownerName = null;
            }
        } else {
            name = getSQLIdentifier(sqlExpr);
            ownerName = null;
        }
    }

    public SqlValue eval(SqlExprEvalContext sqlExprEvalContext, Object rows) throws SQLException {
        //TODO
        return null;
    }

    public static String getSQLIdentifier(SQLExpr sqlExpr) {
        if (sqlExpr == null) {
            throw new NullPointerException("sqlExpr==null");
        }
        if (sqlExpr instanceof SQLIdentifierExpr) {
            String name = ((SQLIdentifierExpr) sqlExpr).getName();
            return name;
        } else if (sqlExpr instanceof SQLName) {
            return ((SQLName) sqlExpr).getSimpleName();
        }
        return null;
    }


    public String getOwnerName() {
        return ownerName;
    }

    public String getName() {
        return name;
    }

}
