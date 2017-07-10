package org.the.force.jdbc.partition.engine.parser.table;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.executor.plan.dql.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.plan.dql.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.parser.ParserUtils;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlProperty;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlNameParser;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.parser.ParserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/21.
 * 对where子句的column value条件进行访问、筛选，以单表的路由判断为目的，是sql改写核心实现类之一，主要功能如下
 * 1，判断指定的sqlTable的分库分表列的条件（通过currentTableColumnValueConditionStack实现）输出currentTableColumnValueMap  currentTableColumnInValuesMap
 * 2，重置子查询的条件  目前只支持  exits 子查询和 in 子查询，用子查询对象替换掉原始的where条件中的子查询条件，输出subQueryResetWhere
 * 3，归集sqlTable的查询条件（通过tableOwnConditionStack实现）,输出currentTableCondition
 * 4, 搜集可能出现在where条件中的join条件  输出joinConditionMap
 * 5，从原始的where条件中 去除已经归集到tableSource的条件和join的条件，拼装新的where条件 输出otherCondition
 * 6, 判断sql重写时是否需要强制指定tableSource的alias(不保证ok，除了where条件还有其他sql子句影响)
 * <p>
 */
public class TableConditionParser extends AbstractVisitor {

    /**
     * 输入项
     */

    private final LogicDbConfig logicDbConfig;

    private final List<SqlTable> orderedSqlTables;//有序的sqlTable数组

    private final SqlTable currentSqlTable;//传入的

    /**
     * 仅仅作为中间状态的
     */
    private final StackArray currentTableColumnValueConditionStack;//表格列的取值是否是全局有效的

    private final StackArray tableOwnConditionStack;//条件归集到当前表的条件

    private boolean hasSqlNameInValue = false;
    /**
     * 输出项
     */
    private final SQLExpr subQueryResetWhere;//重置过子查询的，状态只会因为子查询而改变


    private final SubQueryConditionChecker subQueryConditionChecker;
    //and语义下currentTable的字段取值条件 =
    private final Map<SqlColumn, SQLExpr> currentTableColumnValueMap = new HashMap<>();
    //and语义下currentTable的字段取值条件 in
    private final Map<SqlColumn, SQLInListExpr> currentTableColumnInValuesMap = new HashMap<>();

    //两个sqlName关系的sql表达式
    private final Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> joinConditionMap = new HashMap<>();

    //按照sqlTable归集的字段条件
    private SQLExpr currentTableOwnCondition;//归集到currentSqlTable的sql条件

    private SQLExpr otherCondition;//originalWhere对象去除了currentTableCondition剩余的条件

    public TableConditionParser(LogicDbConfig logicDbConfig, SqlTable sqlTable, SQLExpr originalWhere) {
        this(logicDbConfig, originalWhere, 0, Lists.newArrayList(sqlTable));
    }

    public TableConditionParser(LogicDbConfig logicDbConfig, SQLExpr originalWhere, int currentIndex, List<SqlTable> orderedSqlTables) {
        if (currentIndex < 0 || currentIndex >= orderedSqlTables.size()) {
            throw new SqlParseException("currentIndex<0||currentIndex>=orderedSqlTables.size()");
        }
        this.currentSqlTable = orderedSqlTables.get(currentIndex);
        this.logicDbConfig = logicDbConfig;
        this.orderedSqlTables = new ArrayList<>(orderedSqlTables);
        subQueryConditionChecker = new SubQueryConditionChecker(logicDbConfig);
        currentTableColumnValueConditionStack = new StackArray(16);
        tableOwnConditionStack = new StackArray(16);
        boolean singleRelation = true;//where条件是单一的关系型表达式

        if (originalWhere == null) {
            subQueryResetWhere = null;
        } else {
            if (originalWhere instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) originalWhere;
                SQLBinaryOperator operator = sqlBinaryOpExpr.getOperator();
                if (operator.isLogical()) {
                    singleRelation = false;
                }
            }
            if (singleRelation) {
                //确保原始的where表达式 能够重置子查询条件
                SQLExpr newExpr = subQueryConditionChecker.checkSubExpr(originalWhere);//转换SQLExpr的类型
                if (newExpr != null) {
                    this.subQueryResetWhere = newExpr;
                    this.subQueryResetWhere.accept(subQueryConditionChecker);
                    hasSqlNameInValue = true;
                } else {
                    this.subQueryResetWhere = originalWhere;
                }
                this.subQueryResetWhere.accept(this);
                if (currentTableOwnCondition != null) {
                    this.otherCondition = null;
                }
            } else {
                this.subQueryResetWhere = originalWhere;
                this.subQueryResetWhere.accept(this);
            }
        }

    }

    public SQLExpr getSubQueryResetWhere() {
        return this.subQueryResetWhere;
    }

    public List<SQLExpr> getSubQueryList() {
        return subQueryConditionChecker.getSubQueryList();
    }

    public SQLExpr getCurrentTableOwnCondition() {
        return currentTableOwnCondition;
    }

    public SQLExpr getOtherCondition() {
        return otherCondition;
    }


    public Map<SqlColumn, SQLExpr> getCurrentTableColumnValueMap() {
        return currentTableColumnValueMap;
    }

    public Map<SqlColumn, SQLInListExpr> getCurrentTableColumnInValuesMap() {
        return currentTableColumnInValuesMap;
    }

    public Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> getJoinConditionMap() {
        return joinConditionMap;
    }


    //处理子查询 替换子查询的类型
    public void preVisit(SQLObject x) {
        if (x instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) x;
            if (sqlBinaryOpExpr.getOperator().isLogical()) {
                SQLExpr left = sqlBinaryOpExpr.getLeft();
                SQLExpr right = sqlBinaryOpExpr.getRight();
                SQLExpr newExpr = subQueryConditionChecker.checkSubExpr(left);
                if (newExpr != null) {
                    sqlBinaryOpExpr.setLeft(newExpr);
                    newExpr.accept(subQueryConditionChecker);
                    hasSqlNameInValue = true;
                }
                newExpr = subQueryConditionChecker.checkSubExpr(right);
                if (newExpr != null) {
                    sqlBinaryOpExpr.setRight(newExpr);
                    newExpr.accept(subQueryConditionChecker);
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
            currentTableColumnValueConditionStack.push(false);//当前设置为or语义下

            SQLExpr parent = this.currentTableOwnCondition;//保留parent节点的引用
            SQLExpr otherConditionParent = this.otherCondition;//保留parent节点的引用
            this.currentTableOwnCondition = null;//当前parent
            this.otherCondition = null;
            left.accept(this);
            SQLExpr leftGuiJi = this.currentTableOwnCondition;
            SQLExpr leftOther = this.otherCondition;
            if (leftGuiJi == null) {//不归属于currentTable newWhere复制了条件
                tableOwnConditionStack.push(false);//推入false
                this.otherCondition = null;
                right.accept(this);
                currentTableColumnValueConditionStack.pop();

                SQLExpr rightOther = this.otherCondition;
                tableOwnConditionStack.pop();
                //非tableOwn的条件
                this.otherCondition = mergeLogicalCondition(otherConditionParent, leftOther, rightOther, x);
                return false;
            } else {
                this.currentTableOwnCondition = null;
                this.otherCondition = null;
                right.accept(this);
                currentTableColumnValueConditionStack.pop();
                tableOwnConditionStack.pop();
                SQLExpr rightGuiJi = this.currentTableOwnCondition;
                SQLExpr rightOther = this.otherCondition;
                if (rightGuiJi != null) {
                    this.currentTableOwnCondition = mergeLogicalCondition(parent, leftGuiJi, rightGuiJi, x);
                    this.otherCondition = mergeLogicalCondition(otherConditionParent, leftOther, rightOther, x);
                } else {
                    this.otherCondition = mergeLogicalCondition(otherConditionParent, left, rightOther, x);
                }
                return false;
            }
        } else if (operator == SQLBinaryOperator.BooleanAnd) {
            currentTableColumnValueConditionStack.push(true);

            SQLExpr parent = this.currentTableOwnCondition;//保留parent节点的引用
            SQLExpr otherConditionParent = this.otherCondition;//保留parent节点的引用
            this.currentTableOwnCondition = null;//当前parent
            this.otherCondition = null;
            tableOwnConditionStack.push(true);

            left.accept(this);
            SQLExpr leftGuiJi = this.currentTableOwnCondition;
            SQLExpr leftOther = this.otherCondition;
            this.currentTableOwnCondition = null;
            this.otherCondition = null;
            right.accept(this);
            currentTableColumnValueConditionStack.pop();
            tableOwnConditionStack.pop();
            SQLExpr rightGuiJi = this.currentTableOwnCondition;
            SQLExpr rightOther = this.otherCondition;
            this.currentTableOwnCondition = mergeLogicalCondition(parent, leftGuiJi, rightGuiJi, x);
            this.otherCondition = mergeLogicalCondition(otherConditionParent, leftOther, rightOther, x);
            return false;
        } else {
            return visitRelationalExpr(x, left, right);
        }
    }

    private boolean visitRelationalExpr(SQLBinaryOpExpr x, SQLExpr left, SQLExpr right) {
        //保留当前条件 确保otherCondition尽量包含所有
        backupOtherCondition(x);

        if (!ParserUtils.isRelational(x)) {
            return true;
        }

        //判断是否是RelationalCondition 决定是否访问
        boolean l = left instanceof SQLName;
        boolean r = right instanceof SQLName;
        // > != 等二元操作符
        if (!l && !r) {
            return true;
        }
        if (l && r) {
            //TODO 如果是join表达式则 newWhere不需要保留此条件，因此需要确定是否是join 条件 表达式
            //TODO join的条件
            //join的条件必须是两个都满足
            if (!currentTableColumnValueConditionStack.isAllTrue()) {//不在and语义下
                return false;
            }
            SqlProperty c1 = SqlNameParser.getSqlProperty(left);
            SqlProperty c2 = SqlNameParser.getSqlProperty(right);
            int index1 = getOwnerFromTables(c1);
            int index2 = getOwnerFromTables(c2);
            if (index1 == index2) {
                //同一个表
                if (currentSqlTable == orderedSqlTables.get(index1)) {
                    if (currentTableColumnValueConditionStack.isAllTrue()) {
                        //按照表名 归集sql条件，只归集明确有表格归属的sql条件
                        this.currentTableOwnCondition = x;
                        this.otherCondition = null;
                    }
                }
                return false;
            }
            if (orderedSqlTables.size() < 2 || !tableOwnConditionStack.isAllTrue()) {
                return false;
            }
            boolean flag = index1 > index2;
            Pair<Integer, Integer> pair = new Pair<>(flag ? index2 : index1, flag ? index1 : index2);
            if (!joinConditionMap.containsKey(pair)) {
                joinConditionMap.put(pair, new ArrayList<>());
            }
            joinConditionMap.get(pair).add(x);
            this.otherCondition = null;
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

        resetTableOwnCondition(x);

        SQLBinaryOperator operator = x.getOperator();
        if (operator != SQLBinaryOperator.Equality) {
            return false;
        }
        if (!currentTableColumnValueConditionStack.isAllTrue()) {//不在and语义下
            return false;
        }
        SqlColumn sqlColumn = new SqlColumn(currentSqlTable, c.getName());
        currentTableColumnValueMap.put(sqlColumn, right);
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

        backupOtherCondition(x);

        if (!(sqlExpr instanceof SQLName)) {
            return true;
        }
        SqlProperty c = SqlNameParser.getSqlProperty(sqlExpr);
        if (c == null) {
            //TODO check 异常
            return true;
        }
        if (!checkOwner(c)) {
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
        resetTableOwnCondition(x);
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
        backupOtherCondition(x);
        if (!(sqlExpr instanceof SQLName)) {
            return false;
        }
        SqlProperty c = SqlNameParser.getSqlProperty(sqlExpr);
        if (c == null) {
            //TODO check 异常
            throw new ParserException("parse SqlProperty return null:" + sqlExpr.getClass().getName());
        }
        if (!checkOwner(c)) {
            return false;
        }
        //检查取值表达式中有无sqlName变量
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

        resetTableOwnCondition(x);

        if (x.isNot()) {
            return false;
        }
        if (!currentTableColumnValueConditionStack.isAllTrue()) {//不在and语义下
            return false;
        }
        SqlColumn sqlColumn = new SqlColumn(currentSqlTable, c.getName());
        if (currentTableColumnInValuesMap.containsKey(sqlColumn)) {
            //TODO 不必要的重复sql 不支持
            return false;
        }
        currentTableColumnInValuesMap.put(sqlColumn, x);
        return false;
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
        backupOtherCondition(x);
        return false;
    }



    protected void backupOtherCondition(SQLExpr x) {
        if (this.otherCondition == null) {
            this.otherCondition = x;
        }
    }

    protected void resetTableOwnCondition(SQLExpr x) {
        if (tableOwnConditionStack.isAllTrue()) {
            if (currentTableOwnCondition != null) {
                throw new ParserException("currentTableOwnCondition != null");
            }
            this.currentTableOwnCondition = x;
            this.otherCondition = null;
        }
    }

    /**
     * merge sql条件
     *
     * @param parent
     * @param left
     * @param right
     * @param originalOpExpr
     * @return
     */
    private SQLExpr mergeLogicalCondition(SQLExpr parent, SQLExpr left, SQLExpr right, SQLBinaryOpExpr originalOpExpr) {
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
            return parent;
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

    //=========sqlName check相关======

    public boolean visit(SQLIdentifierExpr x) {
        hasSqlNameInValue = true;
        return false;
    }

    public boolean visit(SQLPropertyExpr x) {
        hasSqlNameInValue = true;
        return false;
    }

    public boolean visit(SQLAllColumnExpr x) {
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
        //表格配置信息
        if (orderedSqlTables.size() == 1) {
            return true;
        }
        return SqlTableColumnsParser.checkOwner(currentSqlTable, c);
    }

    /**
     * 检查某一个SqlProperty是否归属于某一个表，并判断sql改写时是否需要增加tableSource的简称
     *
     * @param c
     * @return
     */
    private int getOwnerFromTables(SqlProperty c) {
        //前缀匹配
        if (orderedSqlTables.size() == 1) {
            return 0;
        }
        String ownerName = c.getOwnerName();
        for (int i = 0; i < orderedSqlTables.size(); i++) {
            SqlTable sqlTable = orderedSqlTables.get(i);
            if (SqlTableColumnsParser.checkOwner(sqlTable, c)) {
                return i;
            }
        }
        throw new SqlParseException("无法匹配table source");
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
