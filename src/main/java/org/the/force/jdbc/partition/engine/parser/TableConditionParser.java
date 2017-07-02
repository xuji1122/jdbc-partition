package org.the.force.jdbc.partition.engine.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNotExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLQueryExpr;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlNameParser;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlProperty;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.plan.dql.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.plan.model.SqlColumn;
import org.the.force.jdbc.partition.engine.plan.model.SqlTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/21.
 * 对where子句的column value条件进行访问、筛选，以单表的路由判断为目的，是sql改写核心实现类之一，主要功能如下
 * 1，判断指定的sqlTable的分库分表列的条件（通过partitionColumnStack实现）
 * 2，重置子查询的条件  目前只支持  exits 子查询和 in 子查询，用子查询对象替换掉原始的where条件中的子查询条件
 * 3，对表格join的场景，归集sqlTable的查询条件（通过tableOwnColumnStack实现）,currentTableCondition
 * 4, 对表格join的场景，搜集可能出现在where条件中的join条件  conditionTableMap
 * 5，对表格join的场景，从原始的where条件中 去除已经归集到tableSource的条件和join的条件，拼装新的where条件 newWhere
 * <p>
 */
public class TableConditionParser extends AbstractVisitor {

    private final LogicDbConfig logicDbConfig;

    /**
     * TODO 条件中含有动态值（含有 列，只能支持数据库的查询?）
     */
    private final SQLExpr originalWhere;//重置过子查询 where被替换掉子查询类型时  sql输出的支持

    private final SqlTable[] orderedSqlTables;//有序的sqlTable数组

    private final boolean inSelectJoinMode;

    private boolean originalWhereHasSubQuery;//原始的where表达式是否有子查询，子查询可能被归集到某个tableSource发生转移，因此调用方需要二次check

    private final StackArray partitionColumnStack;

    private final StackArray tableOwnColumnStack;//不为null这说明需要判断表格 join 的归集条件

    //相等的条件  分区字段
    private final Map<SqlColumn, SQLExpr> partitionColumnValueMap = new HashMap<>();

    //分区字段 in表达式分库分表
    private final Map<SqlColumn, SQLInListExpr> partitionColumnInValuesMap = new HashMap<>();

    private final SqlTable currentSqlTable;//传入的

    //两个sqlName关系的sql表达式  分析where条件中的join表达式 Pair是orderedSqlTables的索引
    private final Map<SQLBinaryOpExpr, Pair<Integer, Integer>> conditionTableMap = new HashMap<>();

    //按照sqlTable归集的字段条件（and语义下）
    private SQLExpr currentTableCondition;//归集到某个tableSource的sql条件



    //重置之后的where条件
    private SQLExpr newWhere;//对象并且去除了tableOwnCondition

    private boolean hasSqlNameInValue;// 取值表达式中含有sqlName

    //join的条件

    public TableConditionParser(LogicDbConfig logicDbConfig, SqlTable sqlTable, SQLExpr originalWhere) {
        this(logicDbConfig, originalWhere, 0, sqlTable);
    }

    public TableConditionParser(LogicDbConfig logicDbConfig, SQLExpr originalWhere, int currentIndex, SqlTable... orderedSqlTables) {
        this.currentSqlTable = orderedSqlTables[currentIndex];
        this.logicDbConfig = logicDbConfig;
        this.inSelectJoinMode = orderedSqlTables.length > 1;
        if (inSelectJoinMode) {
            partitionColumnStack = new StackArray(16);
            tableOwnColumnStack = new StackArray(16);
            //TODO check 表格不重复
            this.orderedSqlTables = orderedSqlTables;
        } else {
            partitionColumnStack = new StackArray(8);
            tableOwnColumnStack = null;
            this.orderedSqlTables = new SqlTable[] {currentSqlTable};
        }

        boolean singleRelation = true;//where条件是单一的关系型表达式

        if (originalWhere instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) originalWhere;
            SQLBinaryOperator operator = sqlBinaryOpExpr.getOperator();
            if (operator.isLogical()) {
                singleRelation = false;
            }
        }
        if (singleRelation) {
            //确保原始的where表达式 能够重置子查询条件
            SQLExpr newExpr = checkExpr(originalWhere);//转换SQLExpr的类型
            if (newExpr != null) {
                originalWhereHasSubQuery = true;
                this.originalWhere = newExpr;
            } else {
                this.originalWhere = originalWhere;
            }
            this.originalWhere.accept(this);
            if (currentTableCondition != null && inSelectJoinMode) {
                this.newWhere = null;
            }
        } else {
            this.originalWhere = originalWhere;
            this.originalWhere.accept(this);
        }
    }


    public Map<SqlColumn, SQLExpr> getPartitionColumnValueMap() {
        return partitionColumnValueMap;
    }

    public Map<SqlColumn, SQLInListExpr> getPartitionColumnInValuesMap() {
        return partitionColumnInValuesMap;
    }

    public Map<SQLBinaryOpExpr, Pair<Integer, Integer>> getConditionTableMap() {
        return conditionTableMap;
    }

    public boolean isOriginalWhereHasSubQuery() {
        return originalWhereHasSubQuery;
    }

    public SQLExpr getOriginalWhere() {
        return this.originalWhere;
    }

    public SQLExpr getNewWhere() {
        return newWhere;
    }

    public SQLExpr getCurrentTableCondition() {
        return currentTableCondition;
    }

    //替换子查询的类型
    public void preVisit(SQLObject x) {
        if (x instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) x;
            if (sqlBinaryOpExpr.getOperator().isLogical()) {
                SQLExpr left = sqlBinaryOpExpr.getLeft();
                SQLExpr right = sqlBinaryOpExpr.getRight();
                SQLExpr newExpr = checkExpr(left);
                if (newExpr != null) {
                    sqlBinaryOpExpr.setLeft(newExpr);
                    originalWhereHasSubQuery = true;
                }
                newExpr = checkExpr(right);
                if (newExpr != null) {
                    sqlBinaryOpExpr.setRight(newExpr);
                    originalWhereHasSubQuery = true;
                }
            }
        }
    }

    /**
     * 逻辑关系表达式
     *
     * @param x
     * @return
     */
    public boolean visit(SQLBinaryOpExpr x) {
        SQLBinaryOperator operator = x.getOperator();
        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();
        if (operator == SQLBinaryOperator.BooleanOr || operator == SQLBinaryOperator.BooleanXor) {
            if (tableOwnColumnStack == null || !tableOwnColumnStack.isAllTrue()) {
                partitionColumnStack.push(false);//当前设置为or语义下
                left.accept(this);
                right.accept(this);
                partitionColumnStack.pop();
            } else {
                SQLExpr parent = this.currentTableCondition;//保留parent节点的引用
                SQLExpr newWhereParent = this.newWhere;//保留parent节点的引用
                this.currentTableCondition = null;//当前parent
                this.newWhere = null;
                partitionColumnStack.push(false);//当前设置为or语义下
                left.accept(this);
                SQLExpr leftGuiJi = this.currentTableCondition;
                SQLExpr leftNew = this.newWhere;
                if (leftGuiJi == null) {
                    tableOwnColumnStack.push(false);//推入false
                    this.newWhere = null;
                    right.accept(this);
                    SQLExpr rightNew = this.newWhere;
                    partitionColumnStack.pop();
                    tableOwnColumnStack.pop();
                    this.newWhere = mergeLogicalSqlExpr(newWhereParent, leftNew, rightNew, x);
                    return false;
                } else {
                    this.currentTableCondition = null;
                    this.newWhere = null;
                    right.accept(this);
                    partitionColumnStack.pop();
                    tableOwnColumnStack.pop();
                    SQLExpr rightGuiJi = this.currentTableCondition;
                    SQLExpr rightNew = this.newWhere;
                    if (rightGuiJi != null) {
                        this.currentTableCondition = mergeLogicalSqlExpr(parent, leftGuiJi, rightGuiJi, x);
                        this.newWhere = mergeLogicalSqlExpr(newWhereParent, leftNew, rightNew, x);
                    } else {
                        this.newWhere = mergeLogicalSqlExpr(newWhereParent, left, rightNew, x);
                    }
                    return false;
                }
            }
            return false;
        } else if (operator == SQLBinaryOperator.BooleanAnd) {
            if (tableOwnColumnStack == null) {
                partitionColumnStack.push(true);
                left.accept(this);
                right.accept(this);
                partitionColumnStack.pop();
                return false;
            } else {
                SQLExpr parent = this.currentTableCondition;//保留parent节点的引用
                SQLExpr newWhereParent = this.newWhere;//保留parent节点的引用
                this.currentTableCondition = null;//当前parent
                this.newWhere = null;
                partitionColumnStack.push(true);
                tableOwnColumnStack.push(true);

                left.accept(this);
                SQLExpr leftGuiJi = this.currentTableCondition;
                SQLExpr leftNew = this.newWhere;
                this.currentTableCondition = null;
                this.newWhere = null;
                right.accept(this);
                partitionColumnStack.pop();
                tableOwnColumnStack.pop();
                SQLExpr rightGuiJi = this.currentTableCondition;
                SQLExpr rightNew = this.newWhere;
                this.currentTableCondition = mergeLogicalSqlExpr(parent, leftGuiJi, rightGuiJi, x);
                this.newWhere = mergeLogicalSqlExpr(newWhereParent, leftNew, rightNew, x);
                return false;
            }

        } else {
            return visitRelationalExpr(x, left, right);
        }
    }

    private SQLExpr mergeLogicalSqlExpr(SQLExpr parent, SQLExpr left, SQLExpr right, SQLBinaryOpExpr originalOpExpr) {
        SQLExpr mergeExpr = null;
        if (left != null && right != null) {
            mergeExpr = new SQLBinaryOpExpr(left, originalOpExpr.getOperator(), right, originalOpExpr.getDbType());
            mergeExpr.getAttributes().putAll(originalOpExpr.getAttributes());

        } else if (left != null) {
            mergeExpr = left;

        } else if (right != null) {
            mergeExpr = right;
        }
        if (mergeExpr == null) {
            return null;
        }
        if (parent == null) {
            return mergeExpr;
        }
        if (parent instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) parent;
            if (ParserUtils.isLogical(sqlBinaryOpExpr)) {
                sqlBinaryOpExpr.setLeft(mergeExpr);
                return parent;
            }
        }
        return parent;
    }

    private boolean visitRelationalExpr(SQLBinaryOpExpr x, SQLExpr left, SQLExpr right) {
        //保留当前条件 确保newWhere尽量包含所有
        this.newWhere = x;
        //判断是否是RelationalCondition 决定是否访问

        if (!ParserUtils.isRelational(x)) {
            return true;
        }
        boolean l = left instanceof SQLName;
        boolean r = right instanceof SQLName;
        // > != 等二元操作符
        if (!l && !r) {
            return true;
        }
        if (l && r) {
            //TODO 如果是join表达式则 newWhere不需要保留此条件，因此需要确定是否是join 条件 表达式
            //TODO join的条件
            if (!partitionColumnStack.isAllTrue()) {//不在and语义下
                return false;
            }
            SqlProperty c1 = SqlNameParser.getSqlProperty(left);
            SqlProperty c2 = SqlNameParser.getSqlProperty(right);
            int index1 = getOwnerFromTables(c1);
            int index2 = getOwnerFromTables(c2);
            if (index1 == index2) {
                throw new ParserException("表格匹配重复");
            }
            boolean flag = index1 > index2;
            conditionTableMap.put(x, new Pair<>(flag ? index2 : index1, flag ? index1 : index2));
            if (tableOwnColumnStack != null && tableOwnColumnStack.isAllTrue()) {
                this.newWhere = null;
            }
            return false;
        }
        if (r) {
            SQLExpr lc = left;
            left = right;
            right = lc;
        }

        SqlProperty c = SqlNameParser.getSqlProperty(left);
        if (c == null) {
            //TODO check 异常
            return true;
        }
        if (!checkOwner(c)) {
            right.accept(this);
            return false;
        }
        boolean copyStatus = hasSqlNameInValue;
        hasSqlNameInValue = false;
        right.accept(this);
        boolean b = this.hasSqlNameInValue;
        hasSqlNameInValue = copyStatus;
        if (b) {
            return false;
        }
        if (tableOwnColumnStack != null && tableOwnColumnStack.isAllTrue()) {
            //按照表名 归集sql条件，只归集明确有表格归属的sql条件
            this.currentTableCondition = x;
            this.newWhere = null;
        }
        SQLBinaryOperator operator = x.getOperator();
        if (operator != SQLBinaryOperator.Equality) {
            return false;
        }
        if (logicDbConfig == null) {
            return false;
        }
        if (!partitionColumnStack.isAllTrue()) {//不在and语义下
            return false;
        }

        if (!logicDbConfig.getLogicTableManager(currentSqlTable.getTableName()).getLogicTableConfig()[0].getPartitionColumnNames().contains(c.getName().toLowerCase())) {
            return false;
        }
        SqlColumn sqlColumn = new SqlColumn(currentSqlTable, c.getName());
        partitionColumnValueMap.put(sqlColumn, right);
        return false;
    }

    /**
     * plan between 表达式
     *
     * @param x
     * @return
     */
    public boolean visit(SQLBetweenExpr x) {
        SQLExpr sqlExpr = x.getTestExpr();
        this.newWhere = x;
        if (!(sqlExpr instanceof SQLName)) {
            return true;
        }
        SqlProperty c = SqlNameParser.getSqlProperty(sqlExpr);
        if (c == null) {
            //TODO check 异常
            return true;
        }
        if (!checkOwner(c)) {
            x.getBeginExpr().accept(this);
            x.getEndExpr().accept(this);
            return false;
        }
        boolean copyStatus = hasSqlNameInValue;
        hasSqlNameInValue = false;
        x.getBeginExpr().accept(this);
        x.getEndExpr().accept(this);
        boolean b = this.hasSqlNameInValue;
        hasSqlNameInValue = copyStatus;
        if (b) {
            return false;
        }
        if (tableOwnColumnStack != null && tableOwnColumnStack.isAllTrue()) {
            this.currentTableCondition = x;
            this.newWhere = null;
        }
        return false;
    }

    /**
     * plan in 条件表达式
     *
     * @param x
     * @return
     */
    public boolean visit(SQLInListExpr x) {
        SQLExpr sqlExpr = x.getExpr();
        this.newWhere = x;
        if (!(sqlExpr instanceof SQLName)) {
            return true;
        }
        SqlProperty c = SqlNameParser.getSqlProperty(sqlExpr);
        if (c == null) {
            //TODO check 异常
            return true;
        }
        if (!checkOwner(c)) {
            List<SQLExpr> list = x.getTargetList();
            for (SQLExpr l : list) {
                l.accept(this);
            }
            return false;
        }
        boolean copyStatus = hasSqlNameInValue;
        hasSqlNameInValue = false;
        List<SQLExpr> list = x.getTargetList();
        for (SQLExpr l : list) {
            l.accept(this);
        }
        boolean b = this.hasSqlNameInValue;
        hasSqlNameInValue = copyStatus;
        if (b) {
            return false;
        }
        if (tableOwnColumnStack != null && tableOwnColumnStack.isAllTrue()) {
            this.currentTableCondition = x;
            this.newWhere = null;
        }
        //partition column判断
        if (logicDbConfig == null) {
            return false;
        }
        if (x.isNot()) {
            return false;
        }
        if (!partitionColumnStack.isAllTrue()) {//不在and语义下
            return false;
        }
        if (!logicDbConfig.getLogicTableManager(currentSqlTable.getTableName()).getLogicTableConfig()[0].getPartitionColumnNames().contains(c.getName().toLowerCase())) {
            return false;
        }
        try {
            SqlColumn sqlColumn = new SqlColumn(currentSqlTable, c.getName());
            if (partitionColumnInValuesMap.containsKey(sqlColumn)) {
                //TODO 不必要的重复sql 不支持
                return false;
            }
            partitionColumnInValuesMap.put(sqlColumn, x);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * plan in 子查询表达式  in not in
     * TODO 归属的column是分库分表位的情况，延迟分库分表
     *
     * @param x
     * @return
     */
    public boolean visit(SQLInSubQueriedExpr x) {
        SQLInListExpr sqlInListExpr = x;
        return visit(sqlInListExpr);
    }

    public boolean visit(ExitsSubQueriedExpr x) {
        this.newWhere = x;
        return false;
    }

    //=========sqlName check相关======

    public boolean visit(SQLIdentifierExpr x) {
        hasSqlNameInValue = true;
        return false;
    }

    public boolean visit(SQLPropertyExpr x) {
        SqlProperty c = SqlNameParser.getSqlProperty(x);
        checkOwner(c);
        hasSqlNameInValue = true;
        return false;
    }

    /**
     * 检查某一个SqlProperty是否归属于某一个表，并判断sql改写时是否需要增加tableSource的简称
     *
     * @param c
     * @return
     */
    private boolean checkOwner(SqlProperty c) {
        //前缀匹配
        String ownerName = c.getOwnerName();
        if (ownerName != null) {
            if (ownerName.equals(currentSqlTable.getAlias())) {
                return true;
            }
            if (ownerName.equalsIgnoreCase(currentSqlTable.getTableName())) {
                if (currentSqlTable.getAlias() == null) {
                    currentSqlTable.setAlias(ownerName);
                }
                return true;
            }
            if (currentSqlTable.getAlias() != null && !ownerName.equals(currentSqlTable.getAlias())) {
                return false;
            }
        }
        //表格含有此列
        //表格配置信息
        return !inSelectJoinMode;
    }

    /**
     * 检查某一个SqlProperty是否归属于某一个表，并判断sql改写时是否需要增加tableSource的简称
     *
     * @param c
     * @return
     */
    private int getOwnerFromTables(SqlProperty c) {
        //前缀匹配
        String ownerName = c.getOwnerName();
        for (int i = 0; i < orderedSqlTables.length; i++) {
            SqlTable sqlTable = orderedSqlTables[i];
            if (ownerName != null) {
                if (ownerName.equals(sqlTable.getAlias())) {
                    return i;
                }
                if (ownerName.equalsIgnoreCase(sqlTable.getTableName())) {
                    if (sqlTable.getAlias() == null) {
                        sqlTable.setAlias(ownerName);
                    }
                    return i;
                }

            } else {
                //TODO 表格含有此列 表格配置信息
            }

        }
        throw new SqlParseException("无法匹配table source");
    }

    // ======子查询 check相关====

    private  SQLExpr checkExpr(SQLExpr x) {
        if (x instanceof SQLInSubQueryExpr) {
            return new SQLInSubQueriedExpr(logicDbConfig,(SQLInSubQueryExpr) x);
        } else if (x instanceof SQLNotExpr) {
            SQLNotExpr sqlNotExpr = (SQLNotExpr) x;
            SQLExpr sqlExpr = sqlNotExpr.getExpr();
            if (sqlExpr instanceof SQLMethodInvokeExpr) {
                return checkExitsQuery((SQLMethodInvokeExpr) sqlExpr, true);
                //将exitsSubQueriedExpr 设置到x的parent下
            }
        } else if (x instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) x;
            return checkExitsQuery(methodInvokeExpr, false);
        }
        return null;
    }

    private  ExitsSubQueriedExpr checkExitsQuery(SQLMethodInvokeExpr methodInvokeExpr, boolean not) {
        if (methodInvokeExpr.getMethodName().equalsIgnoreCase("exits")) {
            List<SQLExpr> parameters = methodInvokeExpr.getParameters();
            if (!parameters.isEmpty() && parameters.size() == 1) {
                SQLExpr pExpr = parameters.get(0);
                if (pExpr instanceof SQLQueryExpr) {
                    SQLQueryExpr sqlQueryExpr = (SQLQueryExpr) pExpr;
                    ExitsSubQueriedExpr r = new ExitsSubQueriedExpr(logicDbConfig,sqlQueryExpr,methodInvokeExpr, not);
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * and or等column value条件 必须在and的语境下才能生效，为了在遍历sql条件时判断当前是否在and的语义下
     * 使用栈保存and条件 从栈底部到栈顶全部是and的语义范围才能成立
     */
    public static class StackArray {
        private boolean[] array;//用数组实现
        private int top; //栈顶指针  数组的角标
        private final int size;

        public StackArray() {
            size = 10;
            array = new boolean[size];
            top = -1; //栈空的时候
        }

        public StackArray(int size) {
            this.size = size;
            array = new boolean[size];
            top = -1; //栈空的时候
        }

        //压栈
        public void push(boolean element) {
            if (top == size - 1) {
                boolean[] n = new boolean[array.length + size];
                System.arraycopy(array, 0, n, 0, array.length);
                this.array = n;
            }
            array[++top] = element;
        }

        //弹栈 不关心返回值
        public void pop() {
            if (top == -1) {
                return;
            }
            top--;
        }

        //判断是否全部为true
        public boolean isAllTrue() {//从栈底部到栈顶全部是true才能成立
            for (int i = 0; i <= top; i++) {
                if (!array[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}
