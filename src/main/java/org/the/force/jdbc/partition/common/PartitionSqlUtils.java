package org.the.force.jdbc.partition.common;

import org.druid.sql.SQLUtils;
import org.druid.sql.ast.SQLObject;
import org.druid.sql.ast.SQLStatement;
import org.druid.sql.dialect.db2.visitor.DB2OutputVisitor;
import org.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import org.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;
import org.druid.sql.visitor.SQLASTOutputVisitor;
import org.druid.util.JdbcConstants;
import org.the.force.jdbc.partition.driver.SqlDialect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
        StringBuilder sb = new StringBuilder();
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
        sqlastOutputVisitor.setParameters(null);
        sqlastOutputVisitor.setPrettyFormat(false);
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

}
