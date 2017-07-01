package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.WriteCommand;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by xuji on 2017/5/28.
 */
public class PhysicTableExecutor {

    private final String physicDbName;

    protected final LinkedList<PhysicSqlExecutor> deque = new LinkedList<>();

    protected final Map<String, PhysicSqlExecutor> map = new HashMap<>();

    public String getPhysicDbName() {
        return physicDbName;
    }

    public PhysicTableExecutor(String physicDbName) {
        this.physicDbName = physicDbName;
    }

    public void add(PhysicSqlExecutor physicSqlExecutor) {
        if (physicSqlExecutor == null) {
            return;
        }
        String sqlKey = physicSqlExecutor.getSqlKey();
        if (sqlKey != null) {
            if (map.containsKey(sqlKey)) {
                //TODO check
                return;
            }
        }
        deque.addLast(physicSqlExecutor);
        if (sqlKey != null) {
            map.put(sqlKey, physicSqlExecutor);
        }
    }

    public <T extends PhysicSqlExecutor> T get(String sqlKey) {
        return (T) map.get(sqlKey);
    }

    public int sqlSize() {
        int count = 0;
        for (PhysicSqlExecutor physicSqlExecutor : deque) {
            count += physicSqlExecutor.sqlSize();
        }
        return count;
    }

    public void executeUpdate(WriteCommand template) throws SQLException {
        for (PhysicSqlExecutor physicSqlExecutor : deque) {
            physicSqlExecutor.executeUpdate(template);
        }
    }

    public ResultSet executeQuery(QueryCommand executeQueryTemplate) throws SQLException {
        return null;
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
        sb.append(physicDbName).append(":");
        for (PhysicSqlExecutor sql : deque) {
            sql.print(preTabNumber + 1, sb);
        }
    }

    public void clearParameters(int lineNum) {
        for (PhysicSqlExecutor physicSqlExecutor : deque) {
            physicSqlExecutor.clearParameters(lineNum);
        }
    }

    public void clearBatch() {
        for (PhysicSqlExecutor physicSqlExecutor : deque) {
            physicSqlExecutor.clearBatch();
        }
    }

    public void close() {
        for (PhysicSqlExecutor physicSqlExecutor : deque) {
            physicSqlExecutor.close();
        }
        deque.clear();
    }

}
