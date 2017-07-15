package org.the.force.jdbc.partition.engine.parser.function;

import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionAbstractVisitor;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCaseExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;

/**
 * Created by xuji on 2017/7/14.
 */
public class SqlFunctionParser extends PartitionAbstractVisitor {

    public SQLExpr parse(SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLAggregateExpr) {
            return parse((SQLAggregateExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLMethodInvokeExpr) {
            return parse((SQLMethodInvokeExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLName) {
            return parse((SQLName) sqlExpr);
        }
        if (SQLUtils.isValue(sqlExpr)) {//取值节点

        }
        return null;
    }

    public SQLExpr parse(SQLAggregateExpr astNode) {
        return null;
    }

    public SQLExpr parse(SQLMethodInvokeExpr astNode) {
        return null;
    }

    public SQLExpr parse(SQLBinaryExpr astNode) {

        return null;
    }

    public SQLExpr parse(SQLName astNode) {
        SqlRefer sqlRefer = SqlReferParser.getSqlRefer(astNode);
        return sqlRefer;
    }

    public SQLExpr parse(SQLCaseExpr astNode) {
        SqlRefer sqlRefer = SqlReferParser.getSqlRefer(astNode);
        return sqlRefer;
    }

}
