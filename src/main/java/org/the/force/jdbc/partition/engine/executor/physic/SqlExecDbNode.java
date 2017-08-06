package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.rule.comparator.NameComparator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by xuji on 2017/8/5.
 */
public class SqlExecDbNode implements SqlExecPhysicNode {
    //physicDbName为key
    private TreeMap<String, SqlExecPhysicNode> dbExecuteRouterMap = new TreeMap<>(NameComparator.getSingleton());

    public void put(String sqlKey, SqlExecPhysicNode sqlExecPhysicNode) {
        if (sqlExecPhysicNode == null) {
            return;
        }
        if (sqlKey != null) {
            if (dbExecuteRouterMap.containsKey(sqlKey)) {
                //TODO check
                return;
            }
        }
        if (sqlKey != null) {
            dbExecuteRouterMap.put(sqlKey, sqlExecPhysicNode);
        }
    }

    public SqlExecPhysicNode get(String sqlKey) {
        return dbExecuteRouterMap.get(sqlKey);
    }

    public void action(final SqlExecCommand template) throws SQLException {
        int i = dbExecuteRouterMap.size();
        List<Future<Boolean>> ts = new ArrayList<>();
        for (Map.Entry<String, SqlExecPhysicNode> entry : dbExecuteRouterMap.entrySet()) {
            final String physicDbName = entry.getKey();
            final SqlExecPhysicNode sqlExecuteRouter = entry.getValue();
            i--;
            if (sqlExecuteRouter.sqlSize() < 1) {
                continue;
            }
            if (i == 0) {
                //last
                template.getSqlExecResource().getConnectionAdapter().initConnection(physicDbName);
                sqlExecuteRouter.action(template);
            } else {
                Future<Boolean> t = template.getSqlExecResource().getThreadPool().submit(() -> {
                    template.getSqlExecResource().getConnectionAdapter().initConnection(physicDbName);
                    sqlExecuteRouter.action(template);
                    return true;
                });
                ts.add(t);
            }
        }
        for (Future<Boolean> t : ts) {
            try {
                t.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.getCause().printStackTrace();
            }
        }
    }

    public int sqlSize() {
        int count = 0;
        for (Map.Entry<String, SqlExecPhysicNode> entry : dbExecuteRouterMap.entrySet()) {
            count += entry.getValue().sqlSize();
        }
        return count;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        print(0, sb);
        return sb.toString();
    }

    public void print(int preTabNumber, StringBuilder sb) {

        for (Map.Entry<String, SqlExecPhysicNode> entry : dbExecuteRouterMap.entrySet()) {
            sb.append("\n");
            for (int i = 0; i < preTabNumber; i++) {
                sb.append("\t");
            }
            sb.append(entry.getKey());
            entry.getValue().print(preTabNumber + 1, sb);
        }
    }
}
