package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.exception.PartitionSystemException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLInListEvaluator extends AbstractSqlExprEvaluator {

    protected final LogicDbConfig logicDbConfig;

    protected final SqlExprEvaluator exprEvaluator;

    protected final List<SqlExprEvaluator> targetListEvaluator = new ArrayList<>();

    public SQLInListEvaluator(LogicDbConfig logicDbConfig, SQLInListExpr originalSqlExpr) {
        super(originalSqlExpr);
        this.logicDbConfig = logicDbConfig;
        exprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getExpr());
        List<SQLExpr> sqlExprList = originalSqlExpr.getTargetList();
        if (sqlExprList != null && !sqlExprList.isEmpty()) {
            targetListEvaluator.addAll(sqlExprList.stream().map(sqlExpr -> logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(sqlExpr)).collect(Collectors.toList()));
        }
    }

    public BooleanValue eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {

        List<Object[]> targetListValue = getTargetListValue(sqlExprEvalContext, data);
        Object object = exprEvaluator.eval(sqlExprEvalContext, data);
        if (targetListValue.size() > 1) {
            throw new PartitionSystemException("targetListValue.size() > 1,只能返回一行");
        }
        if (targetListValue.isEmpty()) {
            return new BooleanValue(false);
        }
        if (object == null) {
            return new BooleanValue(false);
        }
        if (object instanceof Object[]) {
            return new BooleanValue(Arrays.equals((Object[]) object, targetListValue.get(0)));
        } else {
            if (targetListValue.get(0).length != 1) {
                throw new PartitionSystemException("targetListValue.get(0).length != 1,只能返回一行一列");
            }
            return new BooleanValue(object.equals(targetListValue.get(0)[0]));
        }
    }

    public List<Object[]> getTargetListValue(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        List<Object[]> targetValues = new ArrayList<>();
        int size = targetListEvaluator.size();
        for (int i = 0; i < size; i++) {
            SqlExprEvaluator evaluator = targetListEvaluator.get(i);
            Object value = evaluator.eval(sqlExprEvalContext, data);
            if (value instanceof Object[]) {
                targetValues.add((Object[]) value);
            } else {
                targetValues.add(new Object[] {value});
            }
        }
        return targetValues;
    }

    public SqlExprEvaluator getExprEvaluator() {
        return exprEvaluator;
    }

    public List<SqlExprEvaluator> getTargetListEvaluator() {
        return targetListEvaluator;
    }

    //    public List<Object> getValues(SqlRefer sqlRefer, SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
    //        List<Object> values = new AttributeList();
    //        if (exprEvaluator instanceof SqlRefer) {
    //            if (exprEvaluator.equals(sqlRefer)) {
    //                return getTargetListValue(sqlExprEvalContext, data);
    //            }
    //        } else if (exprEvaluator instanceof SQLListEvaluator) {
    //
    //        } else {
    //            return values;
    //        }
    //
    //    }

    public SQLInListExpr getOriginalSqlExpr() {
        return (SQLInListExpr) super.getOriginalSqlExpr();
    }

}
