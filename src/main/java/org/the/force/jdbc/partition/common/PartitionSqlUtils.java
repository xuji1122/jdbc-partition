package org.the.force.jdbc.partition.common;

import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.dialect.db2.visitor.DB2OutputVisitor;
import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import org.the.force.thirdparty.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTOutputVisitor;
import org.the.force.thirdparty.druid.util.JdbcConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by xuji on 2017/5/20.
 */
public class PartitionSqlUtils {


    /**
     * 参考接口ExportParameterVisitor
     * 导出参数
     * 参数化 将直接量转为？ 导出参数列表
     * 将字符串参数转为 ？
     *
     * @param sqlStatement
     * @param sqlDialect
     */

    public static String toSql(SQLObject sqlStatement, SqlDialect sqlDialect) {
        StringBuilder sb = new StringBuilder(System.getProperty("line.separator"));
        SQLASTOutputVisitor sqlastOutputVisitor;
        if (sqlDialect.getDruidSqlDialect().equalsIgnoreCase(JdbcConstants.MYSQL)) {
            MySqlOutputVisitor mySqlOutputVisitor = new MySqlOutputVisitor(sb, false);//是否将直接量转为sql参数
            mySqlOutputVisitor.setShardingSupport(false);
            sqlastOutputVisitor = mySqlOutputVisitor;
        } else if (sqlDialect.getDruidSqlDialect().equalsIgnoreCase(JdbcConstants.ORACLE)) {
            sqlastOutputVisitor = new OracleParameterizedOutputVisitor(sb, false);
        } else if (sqlDialect.getDruidSqlDialect().equalsIgnoreCase(JdbcConstants.DB2)) {
            sqlastOutputVisitor = new DB2OutputVisitor(sb, false);
        } else {
            sqlastOutputVisitor = new SQLASTOutputVisitor(sb, false);
        }
        sqlastOutputVisitor.setPrettyFormat(true);
        sqlStatement.accept(sqlastOutputVisitor);
        return sb.toString();
    }

    /**
     * 加载sql文件，返回sql数组
     *
     * @param filePath
     * @return
     */
    public static String[] loadSqlFromFile(String filePath, SqlDialect sqlDialect) {
        BufferedReader bufferedReader = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            InputStreamReader reader = new InputStreamReader(fileInputStream, "utf-8");
            bufferedReader = new BufferedReader(reader);
            StringBuilder sqlSb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (sqlSb.length() > 0) {
                    sqlSb.append(System.getProperty("line.separator"));
                }
                sqlSb.append(line);
            }
            List<SQLStatement> stmts = SQLUtils.parseStatements(sqlSb.toString(), sqlDialect.getDruidSqlDialect());
            String[] sqls = new String[stmts.size()];
            for (int i = 0; i < stmts.size(); i++) {
                SQLStatement sqlStatement = stmts.get(i);
                String sql = toSql(sqlStatement, sqlDialect);
                sqls[i] = sql;
            }
            return sqls;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String toSqlKey(String sql) {
        StringBuilder sb = new StringBuilder(sql.length() - 2);
        for (int i = 0, size = sql.length(); i < size; i++) {
            char ch = sql.charAt(i);
            if (ch > ' ') {
                sb.append(Character.toLowerCase(ch));
            }
        }
        return sb.toString();
    }

    public static boolean sqlEquals(String sql1, String sql2) {
        if (sql1 == null || sql2 == null) {
            return false;
        }
        sql1 = toSqlKey(sql1);
        sql2 = toSqlKey(sql2);
        return sql1.equals(sql2);
    }

    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        Class<?> clazz = null;
        try {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                clazz = contextLoader.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
        }
        if (clazz == null) {
            clazz = Class.forName(className);
        }
        return clazz;
    }


}
