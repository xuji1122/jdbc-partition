package org.the.force.jdbc.partition.engine.evaluator.factory;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.method.AbstractMethodEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.method.ExitsEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/7/16.
 */
public class SqlMethodEvaluatorFactory {

    private final LogicDbConfig logicDbConfig;

    private Map<String, Class<? extends AbstractMethodEvaluator>> classMap = new HashMap<>();

    public SqlMethodEvaluatorFactory(LogicDbConfig logicDbConfig) {

        this.logicDbConfig = logicDbConfig;
        classMap.put("EXITS", ExitsEvaluator.class);
    }

    public SqlExprEvaluator matchSqlExprEvaluator(SQLMethodInvokeExpr sqlAggregateExpr) {
        String methodName = sqlAggregateExpr.getMethodName();
        Class<? extends AbstractMethodEvaluator> clazz = classMap.get(methodName.toUpperCase());
        try {
            Constructor<? extends AbstractMethodEvaluator> c = clazz.getConstructor(LogicDbConfig.class, SQLMethodInvokeExpr.class);
            return c.newInstance(logicDbConfig, sqlAggregateExpr);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return null;
    }
}
