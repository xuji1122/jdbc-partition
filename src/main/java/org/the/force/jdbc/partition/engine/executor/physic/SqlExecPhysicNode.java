package org.the.force.jdbc.partition.engine.executor.physic;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/28.
 */
public interface SqlExecPhysicNode {


    //获取child
    SqlExecPhysicNode get(String sqlKey);

    //put child 以sqlKey去重
    void put(String sqlKey, SqlExecPhysicNode sqlExecPhysicNode);

    /**
     * 打印 debug日志使用
     * @param preTabNumber
     * @param sb
     */
    void print(int preTabNumber, StringBuilder sb);

    int sqlSize();

    //执行
    void action(SqlExecCommand command) throws SQLException;


}
