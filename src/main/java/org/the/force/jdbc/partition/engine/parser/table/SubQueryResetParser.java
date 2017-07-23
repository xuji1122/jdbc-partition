package org.the.force.jdbc.partition.engine.parser.table;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionAbstractVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCaseExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNotExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectGroupByClause;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xuji on 2017/6/14.
 * 将subQuery的表达式重置掉
 */
public class SubQueryResetParser extends PartitionAbstractVisitor {

    private final LogicDbConfig logicDbConfig;

    private List<SQLExpr> subQueryList = new ArrayList<>();

    private SQLObject subQueryResetSqlObject;

    private final List<SQLObject> excludes;


    private static Log log = LogFactory.getLog(SubQueryResetParser.class);

    public SubQueryResetParser(LogicDbConfig logicDbConfig, SQLObject sqlExpr) {
        this(logicDbConfig, sqlExpr, new SQLObject[] {});
    }

    public SubQueryResetParser(LogicDbConfig logicDbConfig, SQLObject sqlExpr, SQLObject... excludes) {
        this.logicDbConfig = logicDbConfig;
        if (excludes == null) {
            excludes = new SQLObject[] {};
        }
        this.excludes = new ArrayList<>(Arrays.asList(excludes));
        SQLExpr newExpr = checkSubExpr(sqlExpr);
        if (newExpr != null) {
            subQueryResetSqlObject = newExpr;
        } else {
            sqlExpr.accept(this);
            subQueryResetSqlObject = sqlExpr;
        }
    }

    //替换子查询的类型

    /**
     * 需要穷举所有可能出现子查询的SQLExpr实例
     *
     * @param x
     */
    public void preVisit(SQLObject x) {
        if (!excludes.isEmpty()) {
            Iterator<SQLObject> sqlObjectIterator = excludes.iterator();
            while (sqlObjectIterator.hasNext()) {
                SQLObject sqlObject = sqlObjectIterator.next();
                if (x == sqlObject) {
                    sqlObjectIterator.remove();
                    return;
                }
            }
        }
        if (x instanceof SQLSelectItem) {
            SQLSelectItem sqlSelectItem = (SQLSelectItem) x;
            SQLExpr newExpr = checkSubExpr(sqlSelectItem.getExpr());
            if (newExpr != null) {
                sqlSelectItem.setExpr(newExpr);
            }
        }
        if (!(x instanceof SQLExpr)) {
            return;
        }
        if (x instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) x;
            SQLExpr left = sqlBinaryOpExpr.getLeft();
            SQLExpr right = sqlBinaryOpExpr.getRight();
            SQLExpr newExpr = checkSubExpr(left);
            if (newExpr != null) {
                sqlBinaryOpExpr.setLeft(newExpr);
            }
            newExpr = checkSubExpr(right);
            if (newExpr != null) {
                sqlBinaryOpExpr.setRight(newExpr);
            }
        } else if (x instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr sqlMethodInvokeExpr = (SQLMethodInvokeExpr) x;
            List<SQLExpr> list = sqlMethodInvokeExpr.getParameters();
            for (int i = 0; i < list.size(); i++) {
                SQLExpr newExpr = checkSubExpr(list.get(i));
                if (newExpr != null) {
                    list.set(i, newExpr);
                }
            }
            if (sqlMethodInvokeExpr.getFrom() != null) {
                log.warn("SQLMethodInvokeExpr from有值" + PartitionSqlUtils.toSql(x, logicDbConfig.getSqlDialect()));
            }
        } else if (x instanceof SQLListExpr) {
            SQLListExpr sqlListExpr = (SQLListExpr) x;
            List<SQLExpr> list = sqlListExpr.getItems();
            for (int i = 0; i < list.size(); i++) {
                SQLExpr newExpr = checkSubExpr(list.get(i));
                if (newExpr != null) {
                    list.set(i, newExpr);
                }
            }
        } else if (x instanceof SQLInListExpr) {
            SQLInListExpr sqlInListExpr = (SQLInListExpr) x;
            SQLExpr newExpr = checkSubExpr(sqlInListExpr.getExpr());
            if (newExpr != null) {
                sqlInListExpr.setExpr(newExpr);
            }
            List<SQLExpr> list = sqlInListExpr.getTargetList();
            for (int i = 0; i < list.size(); i++) {
                newExpr = checkSubExpr(list.get(i));
                if (newExpr != null) {
                    list.set(i, newExpr);
                }
            }
        } else if (x instanceof SQLBetweenExpr) {
            SQLBetweenExpr sqlBetweenExpr = (SQLBetweenExpr) x;
            SQLExpr newExpr = checkSubExpr(sqlBetweenExpr.getTestExpr());
            if (newExpr != null) {
                sqlBetweenExpr.setTestExpr(newExpr);
            }
            newExpr = checkSubExpr(sqlBetweenExpr.getBeginExpr());
            if (newExpr != null) {
                sqlBetweenExpr.setBeginExpr(newExpr);
            }
            newExpr = checkSubExpr(sqlBetweenExpr.getEndExpr());
            if (newExpr != null) {
                sqlBetweenExpr.setEndExpr(newExpr);
            }
        } else if (x instanceof SQLCaseExpr) {
            SQLCaseExpr sqlCaseExpr = (SQLCaseExpr) x;
            SQLExpr newExpr = checkSubExpr(sqlCaseExpr.getValueExpr());
            if (newExpr != null) {
                sqlCaseExpr.setValueExpr(newExpr);
            }
            newExpr = checkSubExpr(sqlCaseExpr.getElseExpr());
            if (newExpr != null) {
                sqlCaseExpr.setElseExpr(newExpr);
            }
            List<SQLCaseExpr.Item> items = sqlCaseExpr.getItems();
            for (int i = 0; i < items.size(); i++) {
                SQLCaseExpr.Item item = items.get(i);
                newExpr = checkSubExpr(item.getConditionExpr());
                if (newExpr != null) {
                    item.setConditionExpr(newExpr);
                }
                newExpr = checkSubExpr(item.getValueExpr());
                if (newExpr != null) {
                    item.setValueExpr(newExpr);
                }
            }
        } else if (x instanceof SQLAggregateExpr) {
            SQLAggregateExpr aggregateExpr = (SQLAggregateExpr) x;
            List<SQLExpr> list = aggregateExpr.getArguments();
            for (int i = 0; i < list.size(); i++) {
                SQLExpr newExpr = checkSubExpr(list.get(i));
                if (newExpr != null) {
                    list.set(i, newExpr);
                }
            }
        } else if (x instanceof SQLNotExpr) {
            SQLNotExpr sqlNotExpr = (SQLNotExpr) x;
            SQLExpr newExpr = checkSubExpr(sqlNotExpr.getExpr());
            if (newExpr != null) {
                sqlNotExpr.setExpr(newExpr);
            }
        }
    }
    // ======子查询 check相关====

    public SQLExpr checkSubExpr(SQLObject x) {
        //保证幂等操作，多个tableSource可能会重复调用
        if (x == null) {
            return null;
        }
        if (x instanceof SubQueriedExpr) {
            return null;
        }
        if (x instanceof SQLInSubQueriedExpr) {
            return null;
        }
        if (x instanceof SQLInSubQueryExpr) {
            SQLInSubQueryExpr sqlInSubQueryExpr = (SQLInSubQueryExpr) x;
            SQLExpr newExpr = checkSubExpr(sqlInSubQueryExpr.getExpr());
            if (newExpr != null) {
                sqlInSubQueryExpr.setExpr(newExpr);
            } else {
                sqlInSubQueryExpr.getExpr().accept(this);
            }
            return new SQLInSubQueriedExpr(logicDbConfig, (SQLInSubQueryExpr) x);
        } else if (x instanceof SQLQueryExpr) {

            SQLQueryExpr sqlQueryExpr = (SQLQueryExpr) x;
            return new SubQueriedExpr(logicDbConfig, sqlQueryExpr);
        }
        return null;
    }

    public boolean visit(SubQueriedExpr x) {
        subQueryList.add(x);
        return false;
    }

    public boolean visit(SQLInSubQueriedExpr x) {
        subQueryList.add(x);
        return false;
    }

    public boolean visit(SQLInListExpr x) {
        return true;
    }

    public boolean isHasSubQuery() {
        return !subQueryList.isEmpty();
    }

    public List<SQLExpr> getSubQueryList() {
        return subQueryList;
    }

    public SQLObject getSubQueryResetSqlObject() {
        return subQueryResetSqlObject;
    }
}
