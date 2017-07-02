package org.the.force.jdbc.partition.driver;

import org.the.force.thirdparty.druid.util.JdbcConstants;
import org.the.force.jdbc.partition.rule.PartitionFactory;
import org.the.force.jdbc.partition.rule.mysql.MySqlPartitionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/5/15.
 */
public enum SqlDialect {

    MySql(JdbcConstants.MYSQL,new MySqlPartitionFactory());

    private final String druidSqlDialect;

    private final PartitionFactory partitionFactory;

    SqlDialect(String druidSqlDialect, PartitionFactory partitionFactory) {
        this.druidSqlDialect = druidSqlDialect;
        this.partitionFactory = partitionFactory;
    }

    public String getDruidSqlDialect() {
        return druidSqlDialect;
    }


    public PartitionFactory getPartitionFactory() {
        return partitionFactory;
    }

    private static Map<String, SqlDialect> map = new HashMap<>();

    static {
        SqlDialect[] values = SqlDialect.values();
        for (SqlDialect sqlDialect : values) {
            map.put(sqlDialect.name().toUpperCase(), sqlDialect);
        }
    }

    public static SqlDialect getByName(String name) {
        if (name == null) {
            return null;
        }
        return map.get(name.toUpperCase());
    }



}
