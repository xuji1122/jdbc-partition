package org.the.force.jdbc.partition.engine.stmt.impl;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.resource.SqlExecResource;

import java.sql.SQLException;
import java.util.ArrayList;
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
     * @see #binarySearchSqlAndParamIndex
     */
    private List<Integer> logicSqlIndexes = new ArrayList<>();
    /**
     * 代表 ; 分割的每条sql
     */
    private List<ParamLineStmt> logicSqlList = new ArrayList<>();

    private int lineNumber;

    public MultiParamLineStmt() {
        this.setLineNumber(-1);
    }

    public PResult execute(SqlExecResource sqlExecResource,LogicStmtConfig logicStmtConfig) throws SQLException {
        return null;
    }


    public void addLogicSql(ParamLineStmt logicSql) {
        if (logicSql == null) {
            return;
        }
        logicSql.setLineNumber(logicSqlList.size());
        logicSqlList.add(logicSql);
        int paramSize = logicSqlIndexes.isEmpty() ? logicSql.getParamSize() : logicSqlIndexes.get(logicSqlIndexes.size() - 1) + logicSql.getParamSize();
        logicSqlIndexes.add(paramSize);
    }

    public int getParamSize() {
        return logicSqlIndexes.isEmpty() ? 0 : logicSqlIndexes.get(logicSqlIndexes.size() - 1);
    }

    /**
     * client端的api，从1开始计数
     *
     * @param parameterIndex
     * @param parameter
     */
    public void setParameter(int parameterIndex, SqlParameter parameter) {
        Pair<ParamLineStmt, Integer> pair = binarySearchSqlAndParamIndex(parameterIndex - 1);
        if (pair == null) {
            throw new IndexOutOfBoundsException("expect " + parameterIndex + ",actual:" + (logicSqlIndexes.get(logicSqlIndexes.size())));
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
        Pair<ParamLineStmt, Integer> pair = binarySearchSqlAndParamIndex(parameterIndex);
        if (pair == null) {
            return null;
        }
        return pair.getLeft().getSqlParameter(pair.getRight());
    }

    public void clearParameters() throws SQLException {
        for (ParamLineStmt logicSql : logicSqlList) {
            try {
                logicSql.clearParameters();
            } catch (Exception e) {

            }
        }
    }

    /**
     * targetIndex代表MultiLogicSql的第几个参数，从0开始计数的
     *
     * @param targetIndex
     * @return
     */
    public Pair<ParamLineStmt, Integer> binarySearchSqlAndParamIndex(int targetIndex) {
        int size = logicSqlIndexes.size();
        if (size < 1) {
            return null;
        }
        int left = 0;
        int right = size;
        int middle;
        do {
            if (right - left <= 1) {
                int number = logicSqlIndexes.get(left);
                if (number < 1) {
                    return null;
                }
                if (left > 0) {
                    if (number == logicSqlIndexes.get(left - 1)) {
                        return null;
                    }
                }
                return new Pair<>(logicSqlList.get(left), targetIndex - number);
            }
            middle = (left + right) >> 1;
            if (targetIndex >= logicSqlIndexes.get(middle)) {
                left = middle;
            } else {
                right = middle;
            }
        } while (middle >= 0 && middle < size);
        return null;
    }

    public int logicSqlSize() {
        return logicSqlList.size();
    }

    public ParamLineStmt getLogicSql(int index) {
        return logicSqlList.get(index);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }




    public void clearBatch() throws SQLException {
        for (ParamLineStmt paramLineLogicSql : logicSqlList) {
            paramLineLogicSql.clearBatch();
        }
    }

    public void addBatch() throws SQLException {
        for (ParamLineStmt paramLineLogicSql : logicSqlList) {
            paramLineLogicSql.addBatch();
        }
    }

}
