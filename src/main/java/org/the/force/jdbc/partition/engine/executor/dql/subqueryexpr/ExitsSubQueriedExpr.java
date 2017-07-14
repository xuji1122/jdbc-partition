package org.the.force.jdbc.partition.engine.executor.dql.subqueryexpr;

import org.the.force.jdbc.partition.engine.executor.factory.BlockQueryExecutionFactory;
import org.the.force.jdbc.partition.engine.executor.factory.QueryExecutionFactory;
import org.the.force.jdbc.partition.engine.executor.factory.UnionQueryExecutionFactory;
import org.the.force.jdbc.partition.engine.executor.dql.ExecutableSqlExpr;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLExprImpl;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNotExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/6/3.
 * 作为叶子节点
 */
public class ExitsSubQueriedExpr extends SQLExprImpl implements ExecutableSqlExpr {

    private final LogicDbConfig logicDbConfig;

    private final SQLQueryExpr sqlQueryExpr;

    private boolean not;

    private final SQLMethodInvokeExpr sqlMethodInvokeExpr;

    private final QueryExecutionFactory queryExecutionFactory;

    public ExitsSubQueriedExpr(LogicDbConfig logicDbConfig, SQLQueryExpr sqlQueryExpr, SQLMethodInvokeExpr sqlMethodInvokeExpr, boolean not) {
        this.logicDbConfig = logicDbConfig;
        this.sqlQueryExpr = sqlQueryExpr;
        super.setParent(sqlQueryExpr.getParent());
        attributes = sqlQueryExpr.getAttributesDirect();
        this.sqlMethodInvokeExpr = sqlMethodInvokeExpr;
        this.not = not;
        SQLSelect sqlSelect = sqlQueryExpr.getSubQuery();
        SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
        if (sqlSelectQuery == null) {
            throw new ParserException("sqlSelect.getQuery()==null");
        }
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            this.queryExecutionFactory = new BlockQueryExecutionFactory(logicDbConfig, (SQLSelectQueryBlock) sqlSelectQuery);
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            this.queryExecutionFactory = new UnionQueryExecutionFactory(logicDbConfig, (SQLUnionQuery) sqlSelectQuery);
        } else {
            throw new ParserException("不受支持的sqlSelectQuery类型" + sqlSelectQuery.getClass());
        }

    }

    public boolean isNot() {
        return not;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            PartitionSqlASTVisitor partitionSqlASTVisitor = (PartitionSqlASTVisitor) visitor;
            partitionSqlASTVisitor.visit(this);
        } else {
            if (not) {
                SQLNotExpr sqlNotExpr = new SQLNotExpr();
                sqlNotExpr.setExpr(sqlMethodInvokeExpr);
                sqlNotExpr.setParent(this.getParent());
                visitor.visit(sqlNotExpr);
            } else {
                visitor.visit(sqlMethodInvokeExpr);
            }

        }
    }

    public SQLQueryExpr getSqlQueryExpr() {
        return sqlQueryExpr;
    }


    public boolean equals(Object o) {
        if ((o instanceof ExitsSubQueriedExpr)) {
            return false;
        }
        if (not) {
            SQLNotExpr sqlNotExpr = new SQLNotExpr();
            sqlNotExpr.setExpr(sqlMethodInvokeExpr);
            sqlNotExpr.setParent(this.getParent());
            return sqlNotExpr.equals(o);
        } else {
            return sqlMethodInvokeExpr.equals(o);
        }
    }

    public int hashCode() {
        if (not) {
            SQLNotExpr sqlNotExpr = new SQLNotExpr();
            sqlNotExpr.setExpr(sqlMethodInvokeExpr);
            sqlNotExpr.setParent(this.getParent());
            return sqlNotExpr.hashCode();
        } else {
            return sqlMethodInvokeExpr.hashCode();
        }

    }

    public String toString() {
        return not + ":" + SQLUtils.toMySqlString(sqlQueryExpr);
    }
}
