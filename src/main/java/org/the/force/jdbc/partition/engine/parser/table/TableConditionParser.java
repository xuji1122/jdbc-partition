package org.the.force.jdbc.partition.engine.parser.table;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.engine.parser.ParserUtils;
import org.the.force.jdbc.partition.engine.parser.elements.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionAbstractVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.parser.ParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/5/21.
 * 对where子句的column value条件进行访问、筛选，以单表的路由判断为目的，是sql改写核心实现类之一，主要功能如下
 * 1，判断指定的sqlTable的分库分表列的条件（通过currentTableColumnValueConditionStack实现）输出currentTableColumnValueMap  currentTableColumnsInValuesMap
 * 2，归集sqlTable的查询条件（通过tableOwnConditionStack实现）,输出currentTableCondition
 * 3, 搜集可能出现在where条件中的join条件  输出joinConditionMap
 * 4，从原始的where条件中 去除已经归集到tableSource的条件和join的条件，拼装新的where条件 输出otherCondition
 * 5，借助SubQueryResetParser 重置where表达式中的子查询为可以执行的子查询表达式
 * <p>
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
    private final StackArray currentTableColumnValueConditionStack;//表格列的取值是否是全局有效的

    private final StackArray tableOwnConditionStack;//条件归集到当前表的条件

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
        subQueryResetWhere = (SQLExpr) subQueryResetParser.getSubQueryResetExprObj();
        currentTableColumnValueConditionStack = new StackArray(16);
        tableOwnConditionStack = new StackArray(16);
        this.subQueryResetWhere.accept(this);
        currentSqlTable.setCurrentTableOwnCondition(currentTableOwnCondition);

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
            SqlRefer c1 = new SqlRefer((SQLName) left);
            SqlRefer c2 = new SqlRefer((SQLName) right);
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
            if (!currentSqlTable.getJoinConditionMap().containsKey(pair)) {
                currentSqlTable.getJoinConditionMap().put(pair, new ArrayList<>());
            }
            currentSqlTable.getJoinConditionMap().get(pair).add(x);
            this.otherCondition = null;
            return false;
        }
        if (r) {
            SQLExpr lc = left;
            left = right;
            right = lc;
        }

        SqlRefer c = new SqlRefer((SQLName) left);
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
        currentSqlTable.getColumnValueMap().put(c, logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(right));
        return false;
    }

    /**
     * factory between 表达式
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
        SqlRefer c = new SqlRefer((SQLName) sqlExpr);
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
     * factory in 条件表达式
     * @param x
     * @return
     */
    public boolean visit(SQLInListExpr x) {
        SQLExpr sqlExpr = x.getExpr();
        backupOtherCondition(x);
        //TODO 表达式是多列的情况
        List<SQLExpr> listKey = new ArrayList<>();

        if (sqlExpr instanceof SQLListExpr) {
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
                return false;
            }
        } else {
            if (!(sqlExpr instanceof SQLName)) {
                return false;
            }
            listKey.add(new SqlRefer((SQLName) sqlExpr));
        }
        boolean tableOwnMatch = true;
        boolean match = false;
        for (SQLExpr expr : listKey) {
            if (expr instanceof SqlRefer) {
                SqlRefer c = (SqlRefer) expr;
                if (checkOwner(c)) {
                    match = true;
                }else{
                    tableOwnMatch = false;
                }
            } else {
                tableOwnMatch = false;
            }
        }
        if (!match) {
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

        if(tableOwnMatch){
            resetTableOwnCondition(x);
        }
        if (x.isNot()) {
            return false;
        }
        if (!currentTableColumnValueConditionStack.isAllTrue()) {//不在and语义下
            return false;
        }

        SQLInListEvaluator sqlExprEvaluator = (SQLInListEvaluator)logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(x);
        currentSqlTable.getColumnInValueListMap().put(listKey, sqlExprEvaluator);
        return false;
    }

    /**
     * factory in 子查询表达式  in not in
     * TODO 归属的column是分库分表位的情况，延迟分库分表
     *
     * @param x
     * @return
     */
    public boolean visit(SQLInSubQueriedExpr x) {
        SQLInListExpr sqlInListExpr = x;
        return visit(sqlInListExpr);
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

    //=========sqlrefer check相关======

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
    private boolean checkOwner(SqlRefer c) {
        //前缀匹配
        //表格配置信息
        if (orderedSqlTables.size() == 1) {
            return true;
        }
        return SqlTableReferParser.checkOwner(currentSqlTable, c);
    }

    /**
     * 检查某一个SqlProperty是否归属于某一个表，并判断sql改写时是否需要增加tableSource的简称
     *
     * @param c
     * @return
     */
    private int getOwnerFromTables(SqlRefer c) {
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
