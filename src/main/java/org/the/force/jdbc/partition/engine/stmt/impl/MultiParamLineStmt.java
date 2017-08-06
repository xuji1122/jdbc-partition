package org.the.force.jdbc.partition.engine.stmt.impl;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.driver.result.UpdateResult;
import org.the.force.jdbc.partition.engine.executor.result.UpdateMerger;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.resource.SqlExecResource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xuji on 2017/7/29.
 * 多sql语句
 */

public class MultiParamLineStmt implements ParametricStmt, SqlLine {

    /**
     * 每个单独的sql的的参数个数构成的有序数组，有序的，累计的，代表了sql的顺序
     * 1,3,4 代表3条sql,第一个sql一个参数，第二个sql是3-1个参数，第三个sql是4—3个参数，总共4个参数
     * 1,1,5 代表的是第二个sql没有参数
     * 如此设计的目的是当参数个数非常多的时候可以有效的二分查找
     *
     * @see #setParameter
     * @see #binarySearchChildSqlAndParamIndex
     */
    private List<Integer> childSqlParamIndexes = new ArrayList<>();
    /**
     * 代表 ; 分割的每条sql
     */
    private List<ParamLineStmt> childSqlList = new ArrayList<>();

    private int lineNumber;

    public MultiParamLineStmt() {
        childSqlParamIndexes.add(0);
        this.setLineNumber(-1);
    }

    public PResult execute(SqlExecResource sqlExecResource, LogicStmtConfig logicStmtConfig) throws SQLException {
        //TODO check是否是返回最后一个sql的执行结果
        int total = 0;
        try {
            Iterator<ParamLineStmt> iterator = childSqlList.iterator();
            while (iterator.hasNext()) {
                ParamLineStmt paramLineStmt = iterator.next();
                PResult pResult = paramLineStmt.execute(sqlExecResource, logicStmtConfig);
                int updateCount = pResult.getUpdateCount();
                if (updateCount > -1) {
                    total += updateCount;
                }
            }

        } finally {
            clearParameters();
        }
        UpdateMerger updateMerger = new UpdateMerger(1);
        updateMerger.addSuccess(0, total);
        return new UpdateResult(sqlExecResource.getLogicDbConfig(), updateMerger);
    }


    public void addChildSql(ParamLineStmt logicSql) {
        if (logicSql == null) {
            return;
        }
        logicSql.setLineNumber(childSqlList.size());
        childSqlList.add(logicSql);
        int paramSize = childSqlParamIndexes.get(childSqlParamIndexes.size() - 1) + logicSql.getParamSize();
        childSqlParamIndexes.add(paramSize);
    }

    public int getParamSize() {
        return childSqlParamIndexes.get(childSqlParamIndexes.size() - 1);
    }

    /**
     * client端的api，从1开始计数
     *
     * @param parameterIndex
     * @param parameter
     */
    public void setParameter(int parameterIndex, SqlParameter parameter) {
        Pair<ParamLineStmt, Integer> pair = binarySearchChildSqlAndParamIndex(parameterIndex - 1);
        if (pair == null) {
            throw new IndexOutOfBoundsException("expect " + parameterIndex + ",actual:" + (childSqlParamIndexes.get(childSqlParamIndexes.size()-1)));
        }
        pair.getLeft().setParameter(pair.getRight() + 1, parameter);
    }

    /**
     * 按照角标0开始计数
     *
     * @param parameterIndex
     * @return
     */
    public SqlParameter getSqlParameter(int parameterIndex) {
        Pair<ParamLineStmt, Integer> pair = binarySearchChildSqlAndParamIndex(parameterIndex);
        if (pair == null) {
            throw new IndexOutOfBoundsException("expect " + parameterIndex + ",actual:" + (childSqlParamIndexes.get(childSqlParamIndexes.size()-1)));
        }
        return pair.getLeft().getSqlParameter(pair.getRight());
    }

    public void clearParameters() throws SQLException {
        for (ParamLineStmt logicSql : childSqlList) {
            try {
                logicSql.clearParameters();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 二分查找参数角标和childSql的对应关系
     * targetIndex代表MultiLogicSql的第几个参数，从0开始计数的
     * childSqlParamIndexes的存储总是第一个是0，并且size比childSqlList的size大1
     * 此二分查找的特点是
     * 1，childSqlParamIndexes是待查找的数组，但是相等的判断逻辑不仅包括了target和数组的元素相等，而且target在相邻的两个数组元素范围内也是符合条件的
     * 2，即两个相邻元素之间可能相等 即业务上需要处理childSql参数为0个的情况
     *
     * @param targetIndex 需要查找的角标
     * @return left:childSql
     * right:以childSql自己的参数列表为起始的index角标（从0开始计数）
     */
    public Pair<ParamLineStmt, Integer> binarySearchChildSqlAndParamIndex(int targetIndex) {
        int size = childSqlParamIndexes.size();//最大值
        int left = 0;
        int right = size;
        //判断参数是否越界
        if (targetIndex < 0 || targetIndex >= childSqlParamIndexes.get(right - 1)) {
            return null;
        }
        int middle;
        do {
            middle = (left + right) >> 1;
            int index = childSqlParamIndexes.get(middle);
            if (targetIndex == index) {
                /**
                 * 需要跳过没有参数的sql，判断的方法就是判断下一个（left + 1）logicSqlParamIndex是否继续等于targetIndex
                 */
                left = middle;
                while (left + 1 < right) {
                    index = childSqlParamIndexes.get(left + 1);
                    if (index == targetIndex) {
                        left++;
                    } else {
                        //和下一个不相等则说明left对应的childSql有参数
                        right = left + 1;
                    }
                }
            } else if (targetIndex > index) {
                left = middle;
            } else {
                right = middle;
            }
        } while (right - left > 1);

        int number = childSqlParamIndexes.get(left);
        /**
         * 当left小于right时，所取的childSql非临界值，就是第left个
         * 当left和right相等时，由于left已经跳过了没有参数的sql,所取的childSql应该是第left-1个
         */
        int sqlIndex = left == right ? left - 1 : left;
        return new Pair<>(childSqlList.get(sqlIndex), targetIndex - number);
    }

    public int childSqlSize() {
        return childSqlList.size();
    }

    public ParamLineStmt getChildSql(int index) {
        return childSqlList.get(index);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


    public void clearBatch() throws SQLException {
        for (ParamLineStmt paramLineLogicSql : childSqlList) {
            paramLineLogicSql.clearBatch();
        }
    }

    public void addBatch() throws SQLException {
        for (ParamLineStmt paramLineLogicSql : childSqlList) {
            paramLineLogicSql.addBatch();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        print(0, sb);
        return sb.toString();
    }

    public void print(int preTabNumber, StringBuilder sb) {
        sb.append("\n");
        for (int i = 0; i < preTabNumber; i++) {
            sb.append("\t");
        }
        sb.append("(");
        for (int i = 0; i < childSqlParamIndexes.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(childSqlParamIndexes.get(i));
        }
        sb.append("):[");
        for (ParamLineStmt paramLineStmt : childSqlList) {
            paramLineStmt.print(preTabNumber + 1, sb);
        }
        sb.append("\n");
        for (int i = 0; i < preTabNumber; i++) {
            sb.append("\t");
        }
        sb.append("]");
    }

}
