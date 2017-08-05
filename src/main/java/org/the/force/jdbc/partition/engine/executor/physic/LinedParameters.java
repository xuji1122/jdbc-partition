package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.engine.value.SqlParameter;

import java.util.List;

/**
 * Created by xuji on 2017/5/29.
 */
public class LinedParameters {

    private final int lineNum;

    private final List<SqlParameter> sqlParameters;

    public LinedParameters(int lineNum, List<SqlParameter> sqlParameters) {
        this.lineNum = lineNum;
        this.sqlParameters = sqlParameters;
    }

    public int getLineNum() {
        return lineNum;
    }

    public List<SqlParameter> getSqlParameters() {
        return sqlParameters;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        print(0, sb);
        return sb.toString();
    }

    public void print(int preTabNumber, StringBuilder sb) {
        sb.append("\n");
        for (int i = 0; i < preTabNumber; i++) {
            sb.append("\t");
        }
        sb.append(lineNum).append(":[");
        for (int i = 0; i < sqlParameters.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            SqlParameter sqlParameter = sqlParameters.get(i);
            sb.append(sqlParameter.getValue());
        }
        sb.append("]");
    }

}
