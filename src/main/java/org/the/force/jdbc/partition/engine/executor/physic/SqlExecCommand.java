package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.resource.SqlExecResource;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.value.SqlParameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuji on 2017/8/3.
 * 对物理db的sql执行命令
 * 加强版的命令模式
 * 1,命令提供资源参数和设置sql参数的功能
 * 2，receiver主要是准备statement，处理资源消耗以及sql方言等
 * 3，sql的执行通过回调SqlExecCommand的方式实现,目的是让具体的命令实现类获取命令执行的结果
 */
public interface SqlExecCommand {

    void execute();

    PResult getPResult();

    //======参数准备========
    void configStatement(Statement statement);

    SqlExecResource getSqlExecResource();

    LogicStmtConfig getLogicStmtConfig();

    void setParams(Integer lineNumber,PreparedStatement preparedStatement, List<SqlParameter> sqlParameters) throws SQLException;

    //============通过物理的jdbc statement执行sql,让命令的实现类自己搜集执行结果

    void execute(PreparedStatement preparedStatement, Integer lineNumber) throws SQLException;

    void execute(Statement statement, String sql, Integer lineNumber) throws SQLException;

    void executeBatch(PreparedStatement preparedStatement, List<Integer> lineOrder) throws SQLException;

    void executeBatch(Statement statement, List<Integer> lineOrder) throws SQLException;

}
