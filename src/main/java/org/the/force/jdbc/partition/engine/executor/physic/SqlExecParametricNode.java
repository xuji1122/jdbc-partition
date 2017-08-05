package org.the.force.jdbc.partition.engine.executor.physic;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/5/28.
 */
public class SqlExecParametricNode implements SqlExecPhysicNode {

    protected final Map<String, SqlExecPhysicNode> map = new LinkedHashMap<>();

    public SqlExecParametricNode() {

    }

    public SqlExecPhysicNode get(String sqlKey) {
        return map.get(sqlKey);
    }

    public void put(String sqlKey, SqlExecPhysicNode sqlExecPhysicNode) {
        if (sqlExecPhysicNode == null) {
            return;
        }
        if (sqlKey != null) {
            if (map.containsKey(sqlKey)) {
                //TODO check
                return;
            }
        }
        if (sqlKey != null) {
            map.put(sqlKey, sqlExecPhysicNode);
        }
    }


    public int sqlSize() {
        int count = 0;
        for (SqlExecPhysicNode sqlExecPhysicNode : map.values()) {
            count += sqlExecPhysicNode.sqlSize();
        }
        return count;
    }

    public void action(SqlExecCommand sqlExecCommand) throws SQLException {
        for (SqlExecPhysicNode sqlExecPhysicNode : map.values()) {
            sqlExecPhysicNode.action(sqlExecCommand);
        }
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
        for (Map.Entry<String, SqlExecPhysicNode> entry : map.entrySet()) {
            sb.append(entry.getKey());
            entry.getValue().print(preTabNumber, sb);
        }
    }

}
