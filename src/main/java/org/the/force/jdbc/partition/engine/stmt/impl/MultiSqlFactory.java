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
     * 通过 ; 分隔符 解析 多条sql语句
     * 计算每条sql语句 ? 出现的次数
     * @param sqlInput
     * @return
     */
    private ParametricStmt parseMultiSql(String sqlInput) {
        MultiParamLineStmt multiLogicSql = new MultiParamLineStmt();
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder sqlKeyBuilder = new StringBuilder();
        int paramSize = 0;
        boolean strStack1 = false;
        boolean strStack2 = false;
        int size = sqlInput.length();
        for (int i = 0; i < size; i++) {
            char ch = sqlInput.charAt(i);
            sqlKeyBuilder.append(Character.toLowerCase(ch));
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
                if (ch == '\\') {//转义后面一个字符
                    sqlBuilder.append(ch);
                    if (i + 1 < size) {
                        sqlBuilder.append(sqlInput.charAt(++i));
                    }
                } else if (strStack1 && strStack2) {//转义 任意长度字符串
                    sqlBuilder.append(ch);
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
                    ParamLineStmt atomicLogicSql = new ParamLineStmt(new SqlKey(sqlBuilder.toString(), sqlKeyBuilder.toString()), paramSize);
                    multiLogicSql.addChildSql(atomicLogicSql);
                    paramSize = 0;
                    sqlBuilder = new StringBuilder();
                    sqlKeyBuilder = new StringBuilder();
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
            ParamLineStmt atomicLogicSql = new ParamLineStmt(new SqlKey(lastSql, sqlKeyBuilder.toString()), paramSize);
            multiLogicSql.addChildSql(atomicLogicSql);
        }
        int sqlSize = multiLogicSql.childSqlSize();
        if (sqlSize < 1) {
            throw new RuntimeException("multiLogicSql.childSqlSize()<1");
        } else if (sqlSize == 1) {
            return multiLogicSql.getChildSql(0);
        } else {
            return multiLogicSql;
        }
    }

}
