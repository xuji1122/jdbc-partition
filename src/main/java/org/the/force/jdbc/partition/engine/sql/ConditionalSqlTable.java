package org.the.force.jdbc.partition.engine.sql;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/17.
 */
public interface ConditionalSqlTable extends SqlTable {

    /**
     * 按照表格归集的单表单列的集合
     * @return
     */
    Map<SqlRefer, List<SqlExprEvaluator>> getColumnConditionsMap();

    /**
     * 按照表格归集的in的条件的集合
     * key支持有多列，每一列都属于SqlTable对象
     * (name,type) in (('name1',2),('name2',2))
     * @return
     */
    Map<List<SQLExpr>, SQLInListEvaluator> getColumnInListConditionMap();

    /**
     * 在join的条件中指明的与其他的sqlTable的某个字段相等
     * 两个表具有此种关联时，可以共用彼此的条件
     * @return
     */
    Map<SqlRefer, List<Pair<ConditionalSqlTable,SqlRefer>>> getEqualReferMap();

    /**
     * 属于sqlTable但是又涉及多列的条件，
     * 主要是针对多列的条件在or的语义下但是or的每个子表达式又都属于同一个sqlTable
     * @return
     */
    SQLExpr getTableOwnCondition();

    void setTableOwnCondition(SQLExpr tableOwnCondition);

}
