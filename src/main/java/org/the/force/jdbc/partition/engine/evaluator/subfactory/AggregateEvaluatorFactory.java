package org.the.force.jdbc.partition.engine.evaluator.subfactory;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.AggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.AvgAggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.CountAggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.MaxAggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.MinAggregateEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.aggregate.SumAggregateEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/7/16.
 */
public class AggregateEvaluatorFactory {

    private final LogicDbConfig logicDbConfig;


    private Map<String, Class<? extends AggregateEvaluator>> classMap = new HashMap<>();

    public AggregateEvaluatorFactory(LogicDbConfig logicDbConfig) {

        this.logicDbConfig = logicDbConfig;
        classMap.put("AVG", AvgAggregateEvaluator.class);
        classMap.put("COUNT", CountAggregateEvaluator.class);
        classMap.put("MAX", MaxAggregateEvaluator.class);
        classMap.put("MIN", MinAggregateEvaluator.class);
        classMap.put("SUM", SumAggregateEvaluator.class);
    }

    public SqlExprEvaluator matchSqlExprEvalFunction(SQLAggregateExpr sqlAggregateExpr) {
        String methodName = sqlAggregateExpr.getMethodName();
        Class<? extends AggregateEvaluator> clazz = classMap.get(methodName.toUpperCase());
        try {
            Constructor<? extends AggregateEvaluator> c = clazz.getConstructor(LogicDbConfig.class, SQLAggregateExpr.class);
            return c.newInstance(logicDbConfig, sqlAggregateExpr);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
