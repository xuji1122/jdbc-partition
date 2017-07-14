package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.engine.executor.update.MySqlReplaceIntoExecution;
import org.the.force.jdbc.partition.engine.executor.update.UpdateExecution;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.engine.executor.definition.TableDdlExecution;
import org.the.force.jdbc.partition.engine.executor.update.DeleteExecution;
import org.the.force.jdbc.partition.engine.executor.update.InsertExecution;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.sql.SqlExecutionPlan;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDeleteStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropTableStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUpdateStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/5/17.
 * sql解析匹配器,获得适合的LogicSqlReWriter实例,快速定位，兼容不通的数据库类型，一般根据statement的root即可定位
 */
public class SqlExecutionFactory extends AbstractVisitor {

    private static Log logger = LogFactory.getLog(SqlExecutionFactory.class);

    protected final LogicDbConfig logicDbConfig;

    private SQLObject sqlStatement;

    private SQLExprTableSource tableSource;

    private SQLExpr condition;

    private Constructor constructor;


    public SqlExecutionFactory(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }


    protected boolean isContinue() {
        return constructor == null;
    }

    public boolean visit(SQLVariantRefExpr x) {
        return false;
    }

    public SqlExecutionPlan getSqlPlan() {
        List<Object> args = new ArrayList<>();
        args.add(logicDbConfig);
        args.add(sqlStatement);
        if (tableSource != null) {
            args.add(tableSource);
        }
        try {
            Object obj = constructor.newInstance(args.toArray());
            if (obj instanceof SqlExecutionPlan) {
                return (SqlExecutionPlan) obj;
            }
            if(obj instanceof QueryExecutionFactory){
                return ((QueryExecutionFactory)obj).getQueryExecution();
            }
            //TODO check null
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //==========================mysql=============================

    /**
     * @param x
     * @return
     */
    public boolean visit(MySqlSelectQueryBlock x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlSelectQueryBlock:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            try {
                constructor = BlockQueryExecutionFactory.class.getConstructor(LogicDbConfig.class, SQLSelectQueryBlock.class);
                sqlStatement = x;
            } catch (NoSuchMethodException e) {
                throw new ParserException("", e);
            }
        }
        return isContinue();
    }

    /**
     * @param mySqlUnionQuery
     * @return
     */
    public boolean visit(SQLUnionQuery mySqlUnionQuery) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlUnionQuery:{}", SQLUtils.toSQLString(mySqlUnionQuery, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            try {
                constructor = UnionQueryExecutionFactory.class.getConstructor(LogicDbConfig.class, SQLUnionQuery.class);
                sqlStatement = mySqlUnionQuery;
            } catch (NoSuchMethodException e) {
                throw new ParserException("", e);
            }
        }
        return isContinue();
    }

    /**
     * insert
     *
     * @param x
     * @return
     */
    public boolean visit(MySqlInsertStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlInsertStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        try {
            if (sqlStatement == null) {
                constructor = InsertExecution.class.getConstructor(LogicDbConfig.class, SQLInsertStatement.class);
                sqlStatement = x;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContinue();
    }

    public boolean visit(MySqlReplaceStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlReplaceStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        try {
            if (sqlStatement == null) {
                constructor = MySqlReplaceIntoExecution.class.getConstructor(LogicDbConfig.class, MySqlReplaceStatement.class);
                sqlStatement = x;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContinue();
    }

    /**
     * physic
     *
     * @param x
     * @return
     */
    public boolean visit(MySqlUpdateStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlUpdateStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        try {
            if (sqlStatement == null) {
                constructor = UpdateExecution.class.getConstructor(LogicDbConfig.class, SQLUpdateStatement.class);
                sqlStatement = x;
                sqlStatement = x;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContinue();
    }

    /**
     * delete
     *
     * @param x
     * @return
     */
    public boolean visit(MySqlDeleteStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlDeleteStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        try {
            if (sqlStatement == null) {
                constructor = DeleteExecution.class.getConstructor(LogicDbConfig.class, SQLDeleteStatement.class);
                sqlStatement = x;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContinue();
    }

    /**
     * create select
     *
     * @param x
     * @return
     */
    public boolean visit(MySqlCreateTableStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("MySqlCreateTableStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            sqlStatement = x;
            tableSource = x.getTableSource();
            try {
                constructor = TableDdlExecution.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return isContinue();
    }
    //==========================mysql=============================

    /**
     * drop select
     *
     * @param x
     * @return
     */
    public boolean visit(SQLDropTableStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("SQLDropTableStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            sqlStatement = x;
            tableSource = x.getTableSources().get(0);
            try {
                constructor = TableDdlExecution.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return isContinue();
    }

    /**
     * @param x
     * @return
     */
    public boolean visit(SQLAlterTableStatement x) {
        if (logger.isDebugEnabled()) {
            //logger.debug("SQLAlterTableStatement:{}", SQLUtils.toSQLString(x, JdbcConstants.MYSQL));
        }
        if (sqlStatement == null) {
            tableSource = x.getTableSource();
            sqlStatement = x;
            try {
                constructor = TableDdlExecution.class.getConstructor(LogicDbConfig.class, SQLStatement.class, SQLExprTableSource.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return isContinue();
    }
}
