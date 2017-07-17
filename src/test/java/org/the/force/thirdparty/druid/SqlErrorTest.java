package org.the.force.thirdparty.druid;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcBase;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.parser.OracleStatementParser;
import org.the.force.thirdparty.druid.util.JdbcConstants;

import java.util.List;

/**
 * Created by xuji on 2017/7/16.
 */
@Test
public class SqlErrorTest extends TestJdbcBase {

    public void testSQLSelect1() {
        String sql = "select * from t_order elder by name";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
            String sql2 = SQLUtils.toSQLString(sqlStatement, sqlDialect.getDruidSqlDialect());
            logger.info("sql2:\n" + sql2);
        }
    }

    public void testSQLSelect2() {
        String sql = "select * from t_order \n 1=1";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
            String sql2 = SQLUtils.toSQLString(sqlStatement, sqlDialect.getDruidSqlDialect());
            logger.info("sql2:\n" + sql2);
        }
    }

    public void testSQLSelect3() {
        String sql = "select * from t1 where b in (select b from t2) and a = 1";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
            String sql2 = SQLUtils.toSQLString(sqlStatement, sqlDialect.getDruidSqlDialect());
            logger.info("sql2:\n" + sql2);
        }
    }

    public void testDDL1() {
        String sql = "create table month_part (c1 number,c3 date) partition by range(c3) interval(numtoyminterval (1,'month')) "
            + "(partition part1 values less than (to_date('2010-01-01','YYYY-MM-DD')),partition part2 values less than (to_date('2010-02-01','YYYY-MM-DD')))";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        for (SQLStatement stmt : stmtList) {
            System.out.println("druid parse sql is:" + SQLUtils.toOracleString(stmt));
        }
    }
}
