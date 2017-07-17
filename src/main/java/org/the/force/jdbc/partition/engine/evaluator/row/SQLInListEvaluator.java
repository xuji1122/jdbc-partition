package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
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

    public Boolean eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {

        List<Object[]> targetListValue = getTargetListValue(sqlExprEvalContext, data);
        Object object = exprEvaluator.eval(sqlExprEvalContext, data);
        if (targetListValue.size() > 1) {
            throw new PartitionSystemException("targetListValue.size() > 1,只能返回一行");
        }
        if (targetListValue.isEmpty()) {
            return false;
        }
        if (object == null) {
            return false;
        }
        if (object instanceof Object[]) {
            return Arrays.equals((Object[]) object, targetListValue.get(0));
        } else {
            if (targetListValue.get(0).length != 1) {
                throw new PartitionSystemException("targetListValue.get(0).length != 1,只能返回一行一列");
            }
            return object.equals(targetListValue.get(0)[0]);
        }
    }

    public List<Object[]> getTargetListValue(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        List<Object[]> targetValues = new ArrayList<>(1);
        Object[] array = new Object[targetListEvaluator.size()];
        for (int i = 0; i < array.length; i++) {
            SqlExprEvaluator function = targetListEvaluator.get(i);
            Object value = function.eval(sqlExprEvalContext, data);
            array[i] = value;
        }
        targetValues.add(array);
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
