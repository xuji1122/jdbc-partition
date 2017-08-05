package org.the.force.jdbc.partition.engine.parser.sqlrefer;

import org.the.force.jdbc.partition.engine.stmt.SqlRefer;
import org.the.force.jdbc.partition.engine.stmt.SqlTable;
import org.the.force.jdbc.partition.engine.stmt.SqlTableCitedLabels;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/7/8.
 * <p>
 * 1，检查某个sqlTable被引用到的所有列  输出 {@link SqlTableCitedLabels}
 * 不管是否rowQuery还是聚合还是row函数
 * 2，确保SqlTable的alias被正确设置 (当logicSql使用逻辑表名为前缀引用列时)
 */
public class SqlTableReferParser extends AbstractVisitor {

    private static Log logger = LogFactory.getLog(SqlTableReferParser.class);
    //输入

    private final LogicDbConfig logicDbConfig;

    private final SQLTableSource originTableSource;

    private final SQLObject parent;

    private final SqlTable sqlTable;

    //中间状态

    private boolean isInTableSource;

    private SQLTableSource visitingTableSource;

    private SQLSelectQuery subQuery;

    //输出
    private final SqlTableCitedLabels sqlTableCitedLabels;

    public SqlTableReferParser(LogicDbConfig logicDbConfig, SQLObject parent, SqlTable sqlTable) {
        this.logicDbConfig = logicDbConfig;
        this.sqlTable = sqlTable;
        this.sqlTableCitedLabels = new SqlTableCitedLabels();
        this.originTableSource = sqlTable.getSQLTableSource();
        this.parent = parent;
        if (this.parent instanceof SQLUnionQuery) {
            throw new SqlParseException("SQLUnionQuery 不支持SqlTableReferParser");
        }
        this.parent.accept(this);
    }

    public void preVisit(SQLObject sqlObject) {
        if (sqlObject instanceof SQLJoinTableSource) {
            return;
        }
        //判断是否有访问的必要
        if (sqlObject == originTableSource) {
            isInTableSource = true;
            return;
        }
        if (sqlObject instanceof SQLTableSource) {
            visitingTableSource = (SQLTableSource) sqlObject;
        }
        //判断是否处于子查询中  判断第一级子查询即可
        if (sqlObject instanceof SQLSelectQuery) {
            if (parent != sqlObject) {
                if (subQuery == null) {
                    subQuery = (SQLSelectQuery) sqlObject;
                }
            }
        }
    }

    public boolean visit(SQLJoinTableSource sqlJoinTableSource) {
        if (sqlJoinTableSource.getCondition() != null) {
            //判断join条件中的 sqlTable有没有被正确重置alias
            sqlJoinTableSource.getCondition().accept(this);
            sqlJoinTableSource.getLeft().accept(this);
            sqlJoinTableSource.getRight().accept(this);
            if (sqlJoinTableSource.getFlashback() != null) {
                sqlJoinTableSource.getFlashback().accept(this);
            }
            return false;
        } else {
            return true;
        }
    }

    public void postVisit(SQLObject sqlObject) {
        if (sqlObject == originTableSource) {
            isInTableSource = false;
        }
        if (sqlObject instanceof SQLSelectQuery) {
            if (parent != sqlObject) {
                if (subQuery != null && sqlObject == subQuery) {
                    subQuery = null;
                }
            }
        }
        if (visitingTableSource != null && sqlObject == visitingTableSource) {
            visitingTableSource = null;
        }
    }

    public boolean visit(SQLAllColumnExpr x) {
        if (isInTableSource || visitingTableSource != null || subQuery != null) {
            return false;
        }
        if (x.getParent() instanceof SQLSelectItem) {
            sqlTableCitedLabels.setBeCitedAll(true);
        }
        return false;
    }

    public boolean visit(SQLPropertyExpr propertyExpr) {
        //前缀匹配
        if (isInTableSource || visitingTableSource != null) {
            return false;
        }
        SqlRefer sqlRefer = new SqlRefer(propertyExpr);
        if (subQuery == null) {
            if (sqlRefer.getName().equals("*")) {
                if (sqlRefer.getOwnerName() == null) {
                    throw new ParserException("SQLPropertyExpr to SqlRefer is null");
                }
                Boolean b = checkByOwnerOnly(sqlTable, sqlRefer);
                if (b != null && b) {
                    sqlTableCitedLabels.setBeCitedAll(true);
                }

            } else {
                Boolean b = checkByOwnerOnly(sqlTable, sqlRefer);
                if (b != null && b) {
                    sqlTableCitedLabels.addReferLabel(sqlRefer.getName());
                }
            }
        } else {
            Boolean b = checkByOwnerOnly(sqlTable, sqlRefer);
            if (b != null && b) {
                throw new SqlParseException("不支持在子查询中引用父select的from子句定义的table");
            }
        }
        return false;
    }

    public boolean visit(SQLIdentifierExpr propertyExpr) {
        //前缀匹配
        if (isInTableSource || visitingTableSource != null) {
            return false;
        }
        SqlRefer sqlRefer = new SqlRefer(propertyExpr);
        if (subQuery == null) {
            boolean b = checkOwner(sqlTable, sqlRefer);
            if (b) {
                sqlTableCitedLabels.addReferLabel(sqlRefer.getName());
            }
        }
        return false;
    }

    public static boolean checkOwner(SqlTable sqlTable, SqlRefer sqlRefer) {
        if (sqlRefer.getOwnerName() != null) {
            Boolean b = checkByOwnerOnly(sqlTable, sqlRefer);
            if (b != null) {
                return b;
            }
        }
        List<String> columns = sqlTable.getAllReferAbleLabels();
        if (columns == null || columns.isEmpty()) {
            logger.warn("sqlTable columns can not init:" + sqlTable.toString());
            return false;
        }
        return !columns.stream().filter(column -> column.equalsIgnoreCase(sqlRefer.getName())).collect(Collectors.toSet()).isEmpty();
    }

    public static Boolean checkByOwnerOnly(SqlTable sqlTable, SqlRefer c) {
        String ownerName = c.getOwnerName();
        if (ownerName != null) {
            if (sqlTable.getAlias() != null) {
                return sqlTable.getAlias().equals(ownerName);
            }
            if (ownerName.equalsIgnoreCase(sqlTable.getTableName())) {
                sqlTable.setAlias(ownerName);
                return true;
            }
        }
        return null;
    }

    public String getTableAlias() {
        return this.sqlTable.getAlias();
    }

    public SqlTableCitedLabels getSqlTableCitedLabels() {
        return sqlTableCitedLabels;
    }
}
