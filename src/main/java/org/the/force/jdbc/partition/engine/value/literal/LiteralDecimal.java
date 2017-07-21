package org.the.force.jdbc.partition.engine.value.literal;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlDecimal;
import org.the.force.jdbc.partition.engine.value.SqlLiteral;
import org.the.force.jdbc.partition.engine.value.SqlValue;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/21.
 */
public class LiteralDecimal extends AbstractSqlValue implements SqlLiteral, SqlDecimal {

    private final BigDecimal value;

    public LiteralDecimal(BigDecimal value) {
        this.value = value;
    }

    public LiteralDecimal(String inputString) {
        StringBuilder sb = new StringBuilder();
        boolean ten = false;
        for (int i = 0; i < inputString.length(); i++) {
            char ch = inputString.charAt(i);
            if (ch == '-') {
                if (i == 0) {
                    sb.append(ch);
                } else {
                    break;
                }
            } else if (ch == '.') {
                if (!ten) {
                    sb.append(ch);
                    ten = true;
                } else {
                    break;
                }
            } else if (ch >= '0' && ch <= '9') {
                sb.append(ch);
            } else {
                break;
            }
        }
        this.value = new BigDecimal(sb.toString());
    }

    public BigDecimal getValue() {
        return value;
    }

    public String toSql() {
        return " " + value.toString() + " ";
    }

    public BigDecimal getNumber() {
        return getValue();
    }

    public LiteralDecimal add(SqlValue sqlValue) throws SQLException {
        return new LiteralDecimal(this.value.add(new BigDecimal(sqlValue.toString())));
    }

    public LiteralDecimal subtract(SqlValue sqlValue) throws SQLException {
        return new LiteralDecimal(this.value.subtract(new LiteralDecimal(sqlValue.toString()).getNumber()));
    }

    public LiteralDecimal multiply(SqlValue sqlValue) throws SQLException {
        return new LiteralDecimal(this.value.multiply(new LiteralDecimal(sqlValue.toString()).getNumber()));
    }

    public LiteralDecimal divide(SqlValue sqlValue) throws SQLException {
        return new LiteralDecimal(this.value.divide(new LiteralDecimal(sqlValue.toString()).getNumber(), 16, BigDecimal.ROUND_HALF_UP));
    }

    public LiteralDecimal mod(SqlValue sqlValue) throws SQLException {
        throw new RuntimeException("can not mod");
    }


}
