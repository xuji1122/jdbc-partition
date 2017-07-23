package org.the.force.jdbc.partition.engine.parser.table;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.engine.parser.ParserUtils;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionAbstractVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNotExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.parser.ParserException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/21.
 * 对where子句的column value条件进行访问、筛选，主要功能如下
 * 1，归集sqlTable的列的条件（通过{@linkplain columnConditionStack}实现) 写入currentSqlTable 为分库分表条件分析做准备
 * 2，归集sqlTable的条件（可能包括多个不同的列）（通过{@linkplain tableOwnConditionStack}实现）,写入{@linkplain currentSqlTable}
 * 3, 搜集可能出现在where条件中的join条件  写入{@linkplain joinConditionMap}，并判断join的table之间的列的条件等价关系
 * 4，从原始的条件中 去除已经归集到tableSource的条件和join的条件，拼装新的条件 输出{@linkplain otherCondition}
 * 5，借助SubQueryResetParser 重置条件表达式中的子查询为可以执行的子查询表达式
 */
public class TableConditionParser extends PartitionAbstractVisitor {
    /**
     * 输入项
     */
    private final LogicDbConfig logicDbConfig;

    private final List<ConditionalSqlTable> orderedSqlTables;//有序的sqlTable数组

    /**
     * 仅仅作为中间状态的
     */
    private final StackArray columnConditionStack;//表格列的取值是否是全局有效的

    private final StackArray tableOwnConditionStack;//归集到当前表的条件

    private boolean hasSqlNameInValue = false;
    /**
     * 输出项
     */
    private final ConditionalSqlTable currentSqlTable;//传入的

    private final SubQueryResetParser subQueryResetParser;

    private final SQLExpr subQueryResetWhere;//重置过子查询的，状态只会因为子查询而改变

    //按照sqlTable归集的字段条件
    private SQLExpr currentTableOwnCondition;//归集到currentSqlTable的sql条件

    private SQLExpr otherCondition;//originalWhere对象去除了currentTableCondition剩余的条件

    private Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> joinConditionMap = new LinkedHashMap<>();

    public TableConditionParser(LogicDbConfig logicDbConfig, ConditionalSqlTable sqlTable, SQLExpr originalWhere) {
        this(logicDbConfig, originalWhere, 0, Lists.newArrayList(sqlTable));
    }

    public TableConditionParser(LogicDbConfig logicDbConfig, SQLExpr originalWhere, int currentIndex, List<ConditionalSqlTable> orderedSqlTables) {
        if (currentIndex < 0 || currentIndex >= orderedSqlTables.size()) {
            throw new SqlParseException("currentIndex<0||currentIndex>=orderedSqlTables.size()");
        }
        this.currentSqlTable = orderedSqlTables.get(currentIndex);
        this.logicDbConfig = logicDbConfig;
        this.orderedSqlTables = new ArrayList<>(orderedSqlTables);
        //先重置子查询
        subQueryResetParser = new SubQueryResetParser(logicDbConfig, originalWhere);
        subQueryResetWhere = (SQLExpr) subQueryResetParser.getSubQueryResetSqlObject();
        columnConditionStack = new StackArray(16);
        tableOwnConditionStack = new StackArray(16);
        this.subQueryResetWhere.accept(this);
        currentSqlTable.setTableOwnCondition(currentTableOwnCondition);

    }

    public SQLExpr getSubQueryResetWhere() {
        return this.subQueryResetWhere;
    }

    public List<SQLExpr> getSubQueryList() {
        return subQueryResetParser.getSubQueryList();
    }

    public SQLExpr getOtherCondition() {
        return otherCondition;
    }

    public Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> getJoinConditionMap() {
        return joinConditionMap;
    }


    public void preVisit(SQLObject sqlObject) {
        if (sqlObject instanceof SQLBinaryOpExpr) {
            return;
        }
        if (sqlObject instanceof SQLExpr) {
            backupOtherCondition((SQLExpr) sqlObject);
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
            columnConditionStack.push(false);//当前设置为or语义下

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
                columnConditionStack.pop();

                SQLExpr rightOther = this.otherCondition;
                tableOwnConditionStack.pop();
                //非tableOwn的条件
                this.otherCondition = mergeLogicalCondition(otherConditionParent, leftOther, rightOther, x);
                return false;
            } else {
                this.currentTableOwnCondition = null;
                this.otherCondition = null;
                right.accept(this);
                columnConditionStack.pop();
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
            columnConditionStack.push(true);

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
            columnConditionStack.pop();
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

    public boolean visit(SQLNotExpr x) {
        columnConditionStack.push(false);
        tableOwnConditionStack.push(false);
        x.getExpr().accept(this);
        columnConditionStack.pop();
        tableOwnConditionStack.pop();
        return false;
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
            return false;
        }
        if (l && r) {//可能join的条件
            //join的条件必须是两个都满足
            if (!columnConditionStack.isAllTrue()) {//不在and语义下的join条件不算数
                return false;
            }
            SqlRefer c1 = new SqlRefer((SQLName) left);
            SqlRefer c2 = new SqlRefer((SQLName) right);
            int index1 = getOwnerTableIndex(c1);
            int index2 = getOwnerTableIndex(c2);
            if (index1 == index2) {
                //同一个表
                if (currentSqlTable == orderedSqlTables.get(index1)) {
                    if (columnConditionStack.isAllTrue()) {
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
            if (x.getOperator() == SQLBinaryOperator.Equality) {//等价条件
                //c1的条件等价于c2的条件，设置到c1对应的sqlTable中
                equalReferMap(c1, index1, c2, index2);
                //c2的条件等价于c1的条件，设置到c2对应的sqlTable中
                equalReferMap(c2, index2, c1, index1);
            }
            boolean flag = index1 > index2;
            Pair<Integer, Integer> pair = new Pair<>(flag ? index2 : index1, flag ? index1 : index2);
            if (!joinConditionMap.containsKey(pair)) {
                joinConditionMap.put(pair, new ArrayList<>());
            }
            List<SQLBinaryOpExpr> list = joinConditionMap.get(pair);
            if (!list.contains(x)) {
                list.add(x);
            }
            this.otherCondition = null;
            return false;
        }
        if (r) {
            SQLExpr lc = left;
            left = right;
            right = lc;
        }

        SqlRefer sqlRefer = new SqlRefer((SQLName) left);
        if (!checkCurrentSqlTableOwn(sqlRefer)) {
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
        if (!columnConditionStack.isAllTrue()) {//不在and语义下
            return false;
        }
        addColumnCondition(currentSqlTable, sqlRefer, x, logicDbConfig);
        return false;
    }

    public static void addColumnCondition(ConditionalSqlTable currentSqlTable, SqlRefer sqlRefer, SQLExpr sqlExpr, LogicDbConfig logicDbConfig) {
        Map<SqlRefer, List<SqlExprEvaluator>> map = currentSqlTable.getColumnConditionsMap();
        if (!map.containsKey(sqlRefer)) {
            map.put(sqlRefer, new ArrayList<>());
        }
        List<SqlExprEvaluator> list = map.get(sqlRefer);
        SqlExprEvaluator sqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(sqlExpr);
        if (!list.contains(sqlExprEvaluator)) {
            list.add(sqlExprEvaluator);
        }
    }


    /**
     * between 表达式
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
        SqlRefer sqlRefer = new SqlRefer((SQLName) sqlExpr);
        if (!checkCurrentSqlTableOwn(sqlRefer)) {
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
        if (!columnConditionStack.isAllTrue()) {//不在and语义下
            return false;
        }
        addColumnCondition(currentSqlTable, sqlRefer, x, logicDbConfig);
        return false;
    }


    /**
     * in 条件表达式 支持的类别
     * c1 in (1,2,3)
     * (c1,c2) in ((1,2),(3,4))
     * c1 in (select id from xxx)
     * (c1,c2) in (select id,type from xxx)
     * 子查询的处理借助where条件重置{@link SubQueryResetParser}和{@link SQLInSubQueriedExpr}实现
     *
     * @param x
     * @return
     */
    public boolean visit(SQLInListExpr x) {
        SQLExpr sqlExpr = x.getExpr();
        backupOtherCondition(x);
        List<SQLExpr> listKey = matchListKey(sqlExpr);
        if (listKey == null || listKey.isEmpty()) {
            return false;
        }
        //检查取值表达式中有无sqlName变量
        boolean copyStatus = hasSqlNameInValue;
        hasSqlNameInValue = false;
        List<SQLExpr> targetList = x.getTargetList();
        for (SQLExpr l : targetList) {
            l.accept(this);
        }
        boolean b = this.hasSqlNameInValue;
        hasSqlNameInValue = copyStatus;
        if (b) {
            return false;
        }
        resetTableOwnCondition(x);
        if (!columnConditionStack.isAllTrue()) {//不在and语义下
            return false;
        }
        SQLInListEvaluator sqlExprEvaluator = (SQLInListEvaluator) logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(x);
        currentSqlTable.getColumnInListConditionMap().put(listKey, sqlExprEvaluator);
        return false;
    }

    private List<SQLExpr> matchListKey(SQLExpr sqlExpr) {
        List<SQLExpr> listKey = new ArrayList<>();
        if (sqlExpr instanceof SQLListExpr) {
            //表达式是多列的情况
            SQLListExpr sqlListExpr = (SQLListExpr) sqlExpr;
            boolean match = false;
            for (SQLExpr expr : sqlListExpr.getItems()) {
                if (expr instanceof SQLName) {
                    listKey.add(new SqlRefer((SQLName) expr));
                    match = true;
                } else {
                    listKey.add(expr);
                }
            }
            if (!match) {
                return null;
            }
        } else {
            if (!(sqlExpr instanceof SQLName)) {
                return null;
            }
            listKey.add(new SqlRefer((SQLName) sqlExpr));
        }
        for (SQLExpr expr : listKey) {
            if (expr instanceof SqlRefer) {
                SqlRefer c = (SqlRefer) expr;
                if (!checkCurrentSqlTableOwn(c)) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return listKey;
    }

    /**
     * in 子查询表达式  in not in
     *
     * @param x
     * @return
     */
    public boolean visit(SQLInSubQueriedExpr x) {
        SQLExpr sqlExpr = x.getExpr();
        backupOtherCondition(x);
        List<SQLExpr> listKey = matchListKey(sqlExpr);
        if (listKey == null || listKey.isEmpty()) {
            return false;
        }
        resetTableOwnCondition(x);
        if (!columnConditionStack.isAllTrue()) {//不在and语义下
            return false;
        }
        SQLInListEvaluator sqlExprEvaluator = (SQLInListEvaluator) logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(x);
        currentSqlTable.getColumnInListConditionMap().put(listKey, sqlExprEvaluator);
        return false;
    }

    public boolean visit(SubQueriedExpr x) {
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


    private void equalReferMap(SqlRefer sqlRefer, int index, SqlRefer target, int targetIndex) {
        Map<SqlRefer, List<Pair<ConditionalSqlTable, SqlRefer>>> equalReferMap = orderedSqlTables.get(index).getEqualReferMap();
        if (!equalReferMap.containsKey(sqlRefer)) {
            equalReferMap.put(sqlRefer, new ArrayList<>());
        }
        List<Pair<ConditionalSqlTable, SqlRefer>> list = equalReferMap.get(sqlRefer);
        Pair<ConditionalSqlTable, SqlRefer> newPair = new Pair<>(orderedSqlTables.get(targetIndex), target);
        if (!list.contains(newPair)) {
            list.add(newPair);
        }
    }

    //=========sqlRefer check相关======

    public boolean visit(SQLIdentifierExpr x) {
        hasSqlNameInValue = true;
        return false;
    }

    public boolean visit(SQLPropertyExpr x) {
        hasSqlNameInValue = true;
        return false;
    }


    /**
     * 检查某一个SqlProperty是否归属于某一个表，并判断sql改写时是否需要增加tableSource的简称
     *
     * @param c
     * @return
     */
    private boolean checkCurrentSqlTableOwn(SqlRefer c) {
        //前缀匹配
        //表格配置信息
        if (orderedSqlTables.size() == 1) {
            return true;
        }
        return SqlTableReferParser.checkOwner(currentSqlTable, c);
    }

    /**
     * 检查某一个SqlRefer是否归属于某一个表，并判断sql改写时是否需要增加tableSource的简称
     *
     * @param c
     * @return
     */
    private int getOwnerTableIndex(SqlRefer c) {
        //前缀匹配
        if (orderedSqlTables.size() == 1) {
            return 0;
        }
        for (int i = 0; i < orderedSqlTables.size(); i++) {
            ConditionalSqlTable sqlTable = orderedSqlTables.get(i);
            if (SqlTableReferParser.checkOwner(sqlTable, c)) {
                return i;
            }
        }
        throw new SqlParseException("无法匹配table source");
    }


    /**
     * 保留and语义的布尔类型的栈
     * 语法树从从左往右，从叶子节点到往上的顺序递归遍历的，
     * 因此只要碰见and节点就是往栈顶推入一个true,and节点结束就从栈顶推出一个true，碰见or就推入false,or节点结束就从栈顶推出一个false，
     * 那么当遍历到分库分表的条件或者任何你想提取的有效的条件时，你只要判断当前栈是否全部为true就可以了
     */
    public static class StackArray {
        private boolean[] array;//用数组实现
        private int top; //栈顶指针  数组的角标,栈顶的数据在数组的尾部
        private final int extendSize;//扩容的size

        public StackArray() {
            extendSize = 10;
            array = new boolean[extendSize];
            top = -1; //栈空的时候
        }

        public StackArray(int extendSize) {
            this.extendSize = extendSize;
            array = new boolean[extendSize];
            top = -1; //栈空的时候
        }

        //压栈
        public void push(boolean element) {
            if (top >= array.length - 1) {//扩容 + extendSize
                boolean[] n = new boolean[array.length + extendSize];
                //栈顶以内的数据复制过来
                System.arraycopy(array, 0, n, 0, top + 1);
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
            if (array.length - top > 2 * extendSize) {//尺寸太大了舍弃
                boolean[] n = new boolean[array.length - extendSize];
                //栈顶以内的数据复制过来
                System.arraycopy(array, 0, n, 0, top + 1);
                this.array = n;
            }
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
