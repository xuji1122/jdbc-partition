package org.the.force.thirdparty.druid;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;
import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.resource.executor.SqlKey;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;
import org.the.force.thirdparty.druid.util.JdbcConstants;

import java.util.List;

/**
 * Created by xuji on 2017/6/3.
 */
@Test
public class SelectStatementTest extends TestJdbcPartitionBase {

    private Log logger = LogFactory.getLog(UpdateStatementTest.class);

    public void testSQLSelect() {
        //SQLSelect  不带from，区分于SQLSelectQuery
        String sql = "executor 1 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testSelectQuery() {
        String sql = "executor order_id as id,name from t_order where user_id in (?,?,?) and id>0  order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testSelectQuery2() {
        String sql = "executor id,name from t_order where user_id in (?,?,?) and name=?  and id>0 and (time>? or status=?) order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testSelectQuery3() {
        String sql = "executor t.* from t_order t where user_id in (?,?,?) and name=?  and id>0 and (time>? or status=?) order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testCaseWhenQuery3() {
        String sql =
            "executor t.id,case  when t.type=1 then 1 else 0 end from t_order t where user_id in (?,?,?) and name=?  and id>0 and (time>? or status=?) order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testAggregateQuery1() {
        //SQLSelectItem  ---> SqlExpr <]-- SQLAggregateExpr
        String sql = "executor count(id),max(name) from t_order where user_id in (?,?,?) and id>0  order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testAggregateQuery2() {
        //SQLSelectItem  ---> SqlExpr <]-- SQLAggregateExpr
        //SQLSelectGroupByClause
        //SQLOrderBy
        String sql = "executor user_id,count(id) as id_count,max(name) from t_order where user_id in (?,?,?) and id>0  group by user_id  order by id_count limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testAggregateQuery3() {
        //SQLSelectItem  ---> SqlExpr <]-- SQLAggregateExpr
        //SQLSelectGroupByClause
        //SQLOrderBy
        String sql = "executor distinct user_id,user_name from t_order where user_id in(?,?,?) and id>0  order by user_id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }


    public void testAggregateQuery4() {
        //SQLSelectItem  ---> SqlExpr <]-- SQLAggregateExpr
        //SQLSelectGroupByClause
        //SQLOrderBy
        String sql = "executor distinct user_id,user_name from t_order where user_id in(?,?,?) and id>0  order by user_id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testAggregateQuery5() {
        //SQLSelectItem  ---> SqlExpr <]-- SQLAggregateExpr
        //SQLSelectGroupByClause
        //SQLOrderBy
        String sql = "executor count(distinct user_id) from t_order where user_id in(?,?,?) and id>0   limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testJoinQuery1() {
        String sql = "executor id,name from t_order t join t_order_item i on t.id=i.order_id where user_id in (?,?,?) and id>0  order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testJoinQuery2() {
        String sql =
            "executor id,name from t_order t join t_order_item i on t.id=i.order_id join product p on t.product_id=p.id where user_id in (?,?,?) and id>0  order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    /**
     *
     * SQLQueryExpr 可以出现在二元操作符中或者exits中或者函数中  between后的表达式不能是子查询
     * SQLInSubQueryExpr  in 中
     *
     */
    public void testwhere1() {
        //SQLInwhereExpr 桥接 SQLSelect
        String sql = "executor id,name from t_order where user_id in (executor id from user where id>10 ) order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testwhere2() {
        //SQLBinaryExpr  --> 桥接类 SQLQueryExpr  -->   SQLSelect  SQLSelectQuery
        String sql = "executor id,name from t_order where name=? and user_id = (executor id from user where id>10 ) order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testwhere3() {
        //SQLMethodInvokeExpr  表达式最为参数 --> 桥接类 SQLQueryExpr  -->   SQLSelect  SQLSelectQuery
        String sql = "executor id,name from t_order where name=? and not exits (executor id from user where id>10 ) order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }


    /**
     * SQLTableSource <---SQLTableSourceImpl
     * <]---  SQLExprTableSource  直接命名
     * <]---  SQLJoinTableSource  joinType on(可以无)
     * <]---  SQLSubqueryTableSource
     * <]---  SQLUnionQueryTableSource
     */
    public void testFromSubQuery1() {
        //SQLSubqueryTableSource  SQLTableSource

        String sql = "executor id,name from (executor id,name from user where id>10 ) t  where t.id>0 order by id limit 20 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    /**
     * SQLListExpr
     */
    public void testColumnsInValues() {
        //SQLSubqueryTableSource  SQLTableSource

        String sql = "executor id,name from  t  where (t.name,t.name) in (1,3) ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
            logger.info("\n:" + PartitionSqlUtils.toSql(sqlStatement, sqlDialect));
        }
    }

    /**
     * SQLListExpr
     */
    public void testColumnsInSubQuery() {
        //SQLSubqueryTableSource  SQLTableSource

        String sql = "executor id,name from  t  where (t.name,t.name) in (executor id,name from b where a>0) ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
            logger.info("\n:" + PartitionSqlUtils.toSql(sqlStatement, sqlDialect));
        }
    }

    public void testSqlKey() {
        String sql = "executor id,name from (executor id,name from user where id>10 ) t  where t.id>0 order by id limit 20 ";
        SqlKey sqlKey1 = new SqlKey(sql);
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
            String fsql = SQLUtils.toSQLString(sqlStatement, JdbcConstants.MYSQL);
            SqlKey sqlKey2 = new SqlKey(fsql);
            logger.info("sqlKey1:" + sqlKey1);
            logger.info("sqlKey2:" + sqlKey2);
            logger.info("equals:" + sqlKey2.equals(sqlKey1));
        }
    }
}
