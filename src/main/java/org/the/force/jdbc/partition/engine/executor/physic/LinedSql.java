package org.the.force.jdbc.partition.engine.executor.physic;

/**
 * Created by xuji on 2017/5/29.
 */
public class LinedSql{
    private final int lineNum;
    private final String sql;

    public LinedSql(int lineNum, String sql) {
        this.lineNum = lineNum;
        this.sql = sql;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getSql() {
        return sql;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(lineNum).append(":").append(sql);
        return sb.toString();
    }

    public void print(int preTabNumber, StringBuilder sb) {
        sb.append("\n");
        for (int i = 0; i < preTabNumber; i++) {
            sb.append("\t");
        }
        sb.append(lineNum).append(":").append(sql);
    }
}
