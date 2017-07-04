package org.the.force.jdbc.partition.engine.parser;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.engine.plan.model.SqlExprTable;
import org.the.force.jdbc.partition.engine.plan.model.SqlTable;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;
import org.the.force.thirdparty.druid.util.JdbcConstants;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/2.
 */
@Test
public class TableConditionJsonParserTest extends AbstractVisitor {

    private Log logger = LogFactory.getLog(TableConditionJsonParserTest.class);

    private SQLExpr where = null;

    public boolean visit(MySqlSelectQueryBlock x) {
        where = x.getWhere();
        return false;
    }


    public void testTableConditionParser1() {
        String sql = "select id,name from t_order where user_id in (?,?,?) and name=?  and id>0  or (time>? or status=?) order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", null));
    }

    public void testTableConditionParser2() {
        String sql = "select id,name from t_order where  id>0  and ( (time>? or status=?) or (type=? and from_id=? )) order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", null));
    }

    public void testTableConditionParser3() {
        String sql = "select id,name from t_order o join t_order_item i on o.id=i.order_id where  o.id>0  and  o.test1=4+i.test2 and i.pid=?   order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", "o"), new SqlExprTable(null, "t_order_item", "i"));
    }

    public void testTableConditionParser4() {
        String sql =
            "select id,name from t_order o join t_order_item i on o.id=i.order_id where  o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=? order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", "o"), new SqlExprTable(null, "t_order_item", "i"));
    }

    public void testTableConditionParser5() {
        String sql = "select id,name from t_order o join t_order_item i on o.id=i.order_id where  o.id>0  order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", "o"), new SqlExprTable(null, "t_order_item", "i"));
    }

    public void testTableConditionParser6() {
        String sql =
            "select id,name from t_order o join t_order_item i on o.id=i.order_id where  o.id>0  and  o.f1 is null and  (i.time>? or i.status=?) and (o.status=o.type  and o.abd=3+o.bf  or o.status=2  )  and o.name in (1,2,3,o.id) and o.t in(4,5) and o.abc=?  and o.test1=4+i.test2  and o.f2 is not null order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", "o"), new SqlExprTable(null, "t_order_item", "i"));
    }

    public void testTableConditionParser7() {
        String sql =
            "select id,name from t_order o , t_order_item i  where o.id=i.order_id and o.id>0  and  o.f1 is null and  (i.time>? or i.status=?) and (o.status=o.type  and o.abd=3+o.bf  or o.status=2  )  and o.name in (1,2,3,o.id) and o.t in(4,5) and o.abc=?  and o.f2 is not null order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", "o"), new SqlExprTable(null, "t_order_item", "i"));
    }

    public void testTableConditionParser8() {
        String sql =
            "select id,name from t_order o , t_order_item i  where o.id=i.order_id and o.id>0  and  o.f1 is null and o.f2 is not null and  (i.time>? or i.status=?) and (o.status=o.type  and o.abd=3+o.bf  or o.status=2  )  and o.name in (1,2,3,o.id) and o.t in(4,5) and o.abc=? and o.user_id in (select id from user) and exits (select 1 from temp) and not exits (select 1 from temp_2 )  order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", "o"), new SqlExprTable(null, "t_order_item", "i"));
    }

    public void testTableConditionParser9() {
        String sql =
            "select id,name from t_order o , t_order_item i  where o.id=i.order_id  and o.user_id in (select id from user) and exits (select 1 from temp) and not exits (select 1 from temp_2 )  order by id limit 20 ";
        printResult(sql, new SqlExprTable(null, "t_order", "o"), new SqlExprTable(null, "t_order_item", "i"));
    }

    private void printResult(String sql, SqlTable... sqlTable) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
            sqlStatement.accept(this);
            //logger.info("原始sql\n:{}",SQLUtils.toMySqlString(sqlStatement));
            for (int k = 0; k < sqlTable.length; k++) {
                TableConditionParser conditionVisitor = new TableConditionParser(null, where, k, Lists.newArrayList(sqlTable));
                SQLExpr where = conditionVisitor.getOriginalWhere();
                SQLExpr tableOwnCondition = conditionVisitor.getCurrentTableCondition();
                SQLExpr newWhere = conditionVisitor.getNewWhere();

                Map<SQLBinaryOpExpr, Pair<Integer, Integer>> conditionTableMap = conditionVisitor.getConditionTableMap();
                logger.info(MessageFormat.format("{0}:======================", k));
                if (where == null) {
                    logger.info("where == null");
                } else {
                    logger.info("where=\n" + SQLUtils.toMySqlString(where));
                }
                if (tableOwnCondition == null) {
                    logger.info("tableOwnCondition == null");
                } else {
                    logger.info("tableOwnCondition=\n" + SQLUtils.toMySqlString(tableOwnCondition));
                    SubQueryConditionChecker conditionChecker = new SubQueryConditionChecker();
                    tableOwnCondition.accept(conditionChecker);
                    logger.info("tableOwnCondition 子查询\n" + conditionChecker.getSubQueryList());
                }
                if (newWhere == null) {
                    logger.info("newWhere == null");
                } else {
                    logger.info("newWhere=\n" + SQLUtils.toMySqlString(newWhere));
                    SubQueryConditionChecker conditionChecker = new SubQueryConditionChecker();
                    newWhere.accept(conditionChecker);
                    logger.info("newWhere 子查询\n" + conditionChecker.getSubQueryList());
                }
                if (conditionTableMap == null) {
                    logger.info("conditionTableMap == null");
                } else {
                    logger.info("conditionTableMap\n" + conditionTableMap);
                }
                if (newWhere == null) {
                    break;
                }
                this.where = newWhere;
            }

        }
    }
}
