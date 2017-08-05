package org.the.force.jdbc.partition.engine.stmt.impl;

import org.the.force.jdbc.partition.resource.executor.SqlKey;

/**
 * Created by xuji on 2017/7/29.
 */
public class MultiSqlFactory {

    private static MultiSqlFactory multiSqlFactory = new MultiSqlFactory();

    public static ParametricStmt getLogicSql(String sql) {
        return multiSqlFactory.parseMultiSql(sql);
    }

    /**
     * 通过 ; 分隔符 解析 多条sql语句和 ? 出现的次数
     * @param sqlInput
     * @return
     */
    private ParametricStmt parseMultiSql(String sqlInput) {
        MultiParamLineStmt multiLogicSql = new MultiParamLineStmt();
        StringBuilder sqlBuilder = new StringBuilder();
        int paramSize = 0;
        boolean strStack1 = false;
        boolean strStack2 = false;
        int size = sqlInput.length();
        for (int i = 0; i < size; i++) {
            char ch = sqlInput.charAt(i);
            if (ch == '\'') {
                if (!strStack1) {
                    strStack1 = true;
                } else {
                    strStack1 = false;
                }
                sqlBuilder.append(ch);
                continue;
            } else if (ch == '\"') {
                if (!strStack2) {
                    strStack2 = true;
                } else {
                    strStack2 = false;
                }
                sqlBuilder.append(ch);
                continue;
            }
            if (strStack1 || strStack2) {//字符串的含义范围内
                if (ch == '\\') {//转义
                    sqlBuilder.append(ch);
                    if (i + 1 < size) {
                        sqlBuilder.append(sqlInput.charAt(++i));
                    }
                } else {
                    if (ch == '?') {
                        paramSize++;
                        sqlBuilder.append(ch);
                    } else {
                        sqlBuilder.append(ch);
                    }
                }
            } else {//不在字符串含义范围内
                if (ch == '?') {
                    paramSize++;
                    sqlBuilder.append(ch);
                } else if (ch == ';') {
                    ParamLineStmt atomicLogicSql = new ParamLineStmt(new SqlKey(sqlBuilder.toString()), paramSize);
                    multiLogicSql.addLogicSql(atomicLogicSql);
                    paramSize = 0;
                    sqlBuilder = new StringBuilder();
                } else {
                    sqlBuilder.append(ch);
                }
            }
        }
        if (strStack1 || strStack2) {
            throw new RuntimeException("sql text is not end");
        }
        String lastSql = sqlBuilder.toString().trim();
        if (lastSql.length() > 0) {
            ParamLineStmt atomicLogicSql = new ParamLineStmt(new SqlKey(lastSql), paramSize);
            multiLogicSql.addLogicSql(atomicLogicSql);
        }
        int sqlSize = multiLogicSql.logicSqlSize();
        if (sqlSize < 1) {
            throw new RuntimeException("multiLogicSql.logicSqlSize()<1");
        } else if (sqlSize == 1) {
            return multiLogicSql.getLogicSql(0);
        } else {
            return multiLogicSql;
        }
    }

}
