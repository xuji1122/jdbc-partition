package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.engine.executor.WriteCommand;
import org.the.force.jdbc.partition.rule.PartitionComparator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by xuji on 2017/5/28.
 * 一个sql分区到多个物理db上执行
 */
public class PhysicDbExecutor {

    //physicDbName为key
    private TreeMap<String, PhysicTableExecutor> dbExecuteRouterMap = new TreeMap<>(PartitionComparator.getSingleton());

    public final PhysicTableExecutor get(String physicDbName) {
        PhysicTableExecutor sqlExecuteRouter = dbExecuteRouterMap.get(physicDbName.toLowerCase());
        if (sqlExecuteRouter == null) {
            sqlExecuteRouter = new PhysicTableExecutor(physicDbName.toLowerCase());
            dbExecuteRouterMap.put(physicDbName.toLowerCase(), sqlExecuteRouter);
        }
        return sqlExecuteRouter;
    }

    public void executeWrite(final WriteCommand template) throws SQLException {
        int i = dbExecuteRouterMap.size();
        List<Future<Boolean>> ts = new ArrayList<>();
        for (Map.Entry<String, PhysicTableExecutor> entry : dbExecuteRouterMap.entrySet()) {
            final String physicDbName = entry.getKey();
            final PhysicTableExecutor sqlExecuteRouter = entry.getValue();
            i--;
            if (sqlExecuteRouter.sqlSize() < 1) {
                continue;
            }
            if (i == 0) {
                //last
                template.initConnection(physicDbName);
                sqlExecuteRouter.executeUpdate(template);
            } else {
                Future<Boolean> t = template.getExecutorService().submit(() -> {
                    template.initConnection(physicDbName);
                    sqlExecuteRouter.executeUpdate(template);
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
        for (Map.Entry<String, PhysicTableExecutor> entry : dbExecuteRouterMap.entrySet()) {
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
        sb.append("\n");
        for (int i = 0; i < preTabNumber; i++) {
            sb.append("\t");
        }
        for (Map.Entry<String, PhysicTableExecutor> entry : dbExecuteRouterMap.entrySet()) {
            entry.getValue().print(preTabNumber + 1, sb);
        }
    }

    public void clearParameters(int lineNum) {
        for (Map.Entry<String, PhysicTableExecutor> entry : dbExecuteRouterMap.entrySet()) {
            PhysicTableExecutor sqlExecuteRouter = entry.getValue();
            sqlExecuteRouter.clearParameters(lineNum);
        }
    }

    public void clearBatch() {
        for (Map.Entry<String, PhysicTableExecutor> entry : dbExecuteRouterMap.entrySet()) {
            PhysicTableExecutor sqlExecuteRouter = entry.getValue();
            sqlExecuteRouter.clearBatch();
        }
    }

    public void close() {
        for (Map.Entry<String, PhysicTableExecutor> entry : dbExecuteRouterMap.entrySet()) {
            PhysicTableExecutor sqlExecuteRouter = entry.getValue();
            sqlExecuteRouter.close();
        }
        dbExecuteRouterMap.clear();
    }



}
