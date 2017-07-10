package org.the.force.jdbc.partition.engine.parser.table;

import org.the.force.jdbc.partition.engine.parser.elements.SqlProperty;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTableColumns;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlNameParser;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.parser.ParserException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/7/8.
 */
public class SqlTableColumnsParser extends AbstractVisitor {

    //输入

    private final LogicDbConfig logicDbConfig;

    private final SQLTableSource originTableSource;

    private final SQLObject parent;

    private final SqlTable sqlTable;

    //中间状态

    private boolean isInTableSource;

    private SQLTableSource visitingTableSource;

    private SQLSelectQuery subQuery;

    private boolean isContinue = true;
    //输出
    private final SqlTableColumns sqlTableColumns;


    protected boolean isContinue() {
        return isContinue;
    }

    public SqlTableColumnsParser(LogicDbConfig logicDbConfig, SQLTableSource originTableSource) {
        this(logicDbConfig, originTableSource, new SqlTableParser(logicDbConfig).getSqlTable(originTableSource));
    }

    public SqlTableColumnsParser(LogicDbConfig logicDbConfig, SQLTableSource originTableSource, SqlTable sqlTable) {
        this.logicDbConfig = logicDbConfig;
        this.sqlTable = sqlTable;
        this.sqlTableColumns = new SqlTableColumns();
        this.originTableSource = originTableSource;
        //        if (!(originTableSource.getParent() instanceof SQLSelectQuery)) {
        //            throw new ParserException("originTableSource.getParent() is not a SQLSelectQuery instance");
        //        }
        this.parent = originTableSource.getParent();
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
            SQLTableSource sqlTableSource = (SQLTableSource) sqlObject;
            if (sqlTableSource.getAlias() != null && sqlTableSource.getAlias().equals(sqlTable.getAlias())) {
                throw new ParserException("tableSource 别称重复");
            }
            SqlTable otherSqlTable = new SqlTableParser(logicDbConfig).getSqlTable(sqlTableSource);
            if (sqlTableSource.getAlias() == null && (otherSqlTable.getTableName().equals(sqlTable.getTableName()) || otherSqlTable.getTableName().equals(sqlTable.getAlias()))) {
                throw new ParserException("tableSource 名称重复复重复");
            }
            visitingTableSource = sqlTableSource;
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

    public boolean visit(SQLJoinTableSource sqlJoinTableSource) {
        sqlJoinTableSource.getLeft().accept(this);
        if (sqlJoinTableSource.getCondition() != null) {
            sqlJoinTableSource.getCondition().accept(this);
        }
        sqlJoinTableSource.getRight().accept(this);
        return false;
    }

    public boolean visit(SQLAllColumnExpr x) {
        if (isInTableSource || visitingTableSource != null || subQuery != null) {
            return false;
        }
        if (x.getParent() instanceof SQLSelectItem) {
            sqlTableColumns.setQueryAll(true);
            isContinue = false;
            return false;
        }
        return false;
    }

    public boolean visit(SQLPropertyExpr propertyExpr) {
        //前缀匹配
        if (isInTableSource || visitingTableSource != null) {
            return false;
        }
        SqlProperty sqlProperty = SqlNameParser.getSqlProperty(propertyExpr);
        if (sqlProperty == null) {
            throw new ParserException("SQLPropertyExpr to SqlProperty is null");
        }
        if (subQuery == null) {
            if (sqlProperty.getName().equals("*")) {
                if (sqlProperty.getOwnerName() == null) {
                    throw new ParserException("SQLPropertyExpr to SqlProperty is null");
                }
                Boolean b = checkByOwnerOnly(sqlTable, sqlProperty);
                if (b != null && b) {
                    sqlTableColumns.setQueryAll(true);
                    isContinue = false;
                    return false;
                }
            } else {
                Boolean b = checkByOwnerOnly(sqlTable, sqlProperty);
                if (b != null && b) {
                    sqlTableColumns.addQueriedColumn(sqlProperty.getName());
                }
            }
        } else {
            Boolean b = checkByOwnerOnly(sqlTable, sqlProperty);
            if (b != null && b) {
                throw new ParserException("不支持在子查询中引用父select的from子句定义的table");
            }
        }
        return false;
    }

    public boolean visit(SQLIdentifierExpr propertyExpr) {
        //前缀匹配
        if (isInTableSource || visitingTableSource != null) {
            return false;
        }
        SqlProperty sqlProperty = SqlNameParser.getSqlProperty(propertyExpr);
        if (sqlProperty == null) {
            throw new ParserException("SQLPropertyExpr to SqlProperty is null");
        }
        if (subQuery == null) {
            boolean b = checkOwner(sqlTable, sqlProperty);
            if (b) {
                sqlTableColumns.addQueriedColumn(sqlProperty.getName());
            }
        }
        return false;
    }

    public static boolean checkOwner(SqlTable sqlTable, SqlProperty sqlProperty) {
        if (sqlProperty.getOwnerName() != null) {
            Boolean b = checkByOwnerOnly(sqlTable, sqlProperty);
            if (b != null) {
                return b;
            }
        }
        Set<String> columns = sqlTable.getColumns();
        if (columns == null || columns.isEmpty()) {
            throw new ParserException("sqlTable columns can not init:" + sqlTable.toString());
        }
        return !columns.stream().filter(column -> column.equalsIgnoreCase(sqlProperty.getName())).collect(Collectors.toSet()).isEmpty();
    }

    public static Boolean checkByOwnerOnly(SqlTable sqlTable, SqlProperty c) {
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

    public SqlTableColumns getSqlTableColumns() {
        return sqlTableColumns;
    }
}
