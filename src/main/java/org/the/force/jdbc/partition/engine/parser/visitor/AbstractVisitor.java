package org.the.force.jdbc.partition.engine.parser.visitor;

import org.the.force.jdbc.partition.engine.executor.query.tablesource.JoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.query.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.query.tablesource.UnionQueriedTableSource;
import org.the.force.thirdparty.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.db2.ast.stmt.DB2ValuesStatement;
import org.the.force.thirdparty.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.MySqlKey;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.MySqlUnique;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlDeclareConditionStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlDeclareHandlerStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.expr.MySqlIntervalExpr;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterColumn;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableCharacter;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSetCharSetStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSetNamesStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSetPasswordStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateViewStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowIndexesStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowKeysStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByKey;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByList;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import org.the.force.jdbc.partition.engine.executor.query.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.jdbc.partition.engine.executor.query.subqueryexpr.SQLInSubQueriedExpr;

/**
 * Created by xuji on 2017/5/17.
 */
public abstract class AbstractVisitor extends GeneralVisitor implements MySqlASTVisitor, DB2ASTVisitor,PartitionSqlASTVisitor {


    protected boolean isContinue() {
        return true;
    }

    public boolean visit(ExitsSubQueriedExpr x) {
        return isContinue();
    }

    public boolean visit(SQLInSubQueriedExpr x) {
        return isContinue();
    }

    public boolean visit(JoinedTableSource joinedTableSource){
        return isContinue();
    }

    public boolean visit(SubQueriedTableSource subQueriedTableSource){
        return isContinue();
    }

    public boolean visit(UnionQueriedTableSource unionQueriedTableSource){
        return isContinue();
    }

    //=============mysql=======

    public boolean visit(MySqlTableIndex mySqlTableIndex) {
        return isContinue();
    }


    public void endVisit(MySqlTableIndex mySqlTableIndex) {

    }



    public boolean visit(MySqlKey mySqlKey) {
        return isContinue();
    }


    public void endVisit(MySqlKey mySqlKey) {

    }


    public boolean visit(MySqlPrimaryKey mySqlPrimaryKey) {
        return isContinue();
    }


    public void endVisit(MySqlPrimaryKey mySqlPrimaryKey) {

    }


    public boolean visit(MySqlUnique mySqlUnique) {
        return isContinue();
    }


    public void endVisit(MySqlUnique mySqlUnique) {

    }


    public boolean visit(MysqlForeignKey mysqlForeignKey) {
        return isContinue();
    }


    public void endVisit(MysqlForeignKey mysqlForeignKey) {

    }


    public void endVisit(MySqlIntervalExpr mySqlIntervalExpr) {

    }


    public boolean visit(MySqlIntervalExpr mySqlIntervalExpr) {
        return isContinue();
    }


    public void endVisit(MySqlExtractExpr mySqlExtractExpr) {

    }


    public boolean visit(MySqlExtractExpr mySqlExtractExpr) {
        return isContinue();
    }


    public void endVisit(MySqlMatchAgainstExpr mySqlMatchAgainstExpr) {

    }


    public boolean visit(MySqlMatchAgainstExpr mySqlMatchAgainstExpr) {
        return isContinue();
    }


    public void endVisit(MySqlPrepareStatement mySqlPrepareStatement) {

    }


    public boolean visit(MySqlPrepareStatement mySqlPrepareStatement) {
        return isContinue();
    }


    public void endVisit(MySqlExecuteStatement mySqlExecuteStatement) {

    }


    public boolean visit(MysqlDeallocatePrepareStatement mysqlDeallocatePrepareStatement) {
        return isContinue();
    }


    public void endVisit(MysqlDeallocatePrepareStatement mysqlDeallocatePrepareStatement) {

    }


    public boolean visit(MySqlExecuteStatement mySqlExecuteStatement) {
        return isContinue();
    }


    public void endVisit(MySqlDeleteStatement mySqlDeleteStatement) {

    }


    public boolean visit(MySqlDeleteStatement mySqlDeleteStatement) {
        return isContinue();
    }


    public void endVisit(MySqlInsertStatement mySqlInsertStatement) {

    }


    public boolean visit(MySqlInsertStatement mySqlInsertStatement) {
        return isContinue();
    }


    public void endVisit(MySqlLoadDataInFileStatement mySqlLoadDataInFileStatement) {

    }


    public boolean visit(MySqlLoadDataInFileStatement mySqlLoadDataInFileStatement) {
        return isContinue();
    }


    public void endVisit(MySqlLoadXmlStatement mySqlLoadXmlStatement) {

    }


    public boolean visit(MySqlLoadXmlStatement mySqlLoadXmlStatement) {
        return isContinue();
    }


    public void endVisit(MySqlReplaceStatement mySqlReplaceStatement) {

    }


    public boolean visit(MySqlReplaceStatement mySqlReplaceStatement) {
        return isContinue();
    }




    public void endVisit(MySqlShowColumnsStatement mySqlShowColumnsStatement) {

    }


    public boolean visit(MySqlShowColumnsStatement mySqlShowColumnsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowDatabasesStatement mySqlShowDatabasesStatement) {

    }


    public boolean visit(MySqlShowDatabasesStatement mySqlShowDatabasesStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowWarningsStatement mySqlShowWarningsStatement) {

    }


    public boolean visit(MySqlShowWarningsStatement mySqlShowWarningsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowStatusStatement mySqlShowStatusStatement) {

    }


    public boolean visit(MySqlShowStatusStatement mySqlShowStatusStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowAuthorsStatement mySqlShowAuthorsStatement) {

    }


    public boolean visit(MySqlShowAuthorsStatement mySqlShowAuthorsStatement) {
        return isContinue();
    }


    public void endVisit(CobarShowStatus cobarShowStatus) {

    }


    public boolean visit(CobarShowStatus cobarShowStatus) {
        return isContinue();
    }


    public void endVisit(MySqlKillStatement mySqlKillStatement) {

    }


    public boolean visit(MySqlKillStatement mySqlKillStatement) {
        return isContinue();
    }


    public void endVisit(MySqlBinlogStatement mySqlBinlogStatement) {

    }


    public boolean visit(MySqlBinlogStatement mySqlBinlogStatement) {
        return isContinue();
    }


    public void endVisit(MySqlResetStatement mySqlResetStatement) {

    }


    public boolean visit(MySqlResetStatement mySqlResetStatement) {
        return isContinue();
    }


    public void endVisit(MySqlCreateUserStatement mySqlCreateUserStatement) {

    }


    public boolean visit(MySqlCreateUserStatement mySqlCreateUserStatement) {
        return isContinue();
    }


    public void endVisit(MySqlCreateUserStatement.UserSpecification userSpecification) {

    }


    public boolean visit(MySqlCreateUserStatement.UserSpecification userSpecification) {
        return isContinue();
    }


    public void endVisit(MySqlPartitionByKey mySqlPartitionByKey) {

    }


    public boolean visit(MySqlPartitionByKey mySqlPartitionByKey) {
        return isContinue();
    }


    public boolean visit(MySqlSelectQueryBlock mySqlSelectQueryBlock) {
        return isContinue();
    }


    public void endVisit(MySqlSelectQueryBlock mySqlSelectQueryBlock) {

    }


    public boolean visit(MySqlOutFileExpr mySqlOutFileExpr) {
        return isContinue();
    }


    public void endVisit(MySqlOutFileExpr mySqlOutFileExpr) {

    }


    public boolean visit(MySqlExplainStatement mySqlExplainStatement) {
        return isContinue();
    }


    public void endVisit(MySqlExplainStatement mySqlExplainStatement) {

    }


    public boolean visit(MySqlUpdateStatement mySqlUpdateStatement) {
        return isContinue();
    }


    public void endVisit(MySqlUpdateStatement mySqlUpdateStatement) {

    }


    public boolean visit(MySqlSetTransactionStatement mySqlSetTransactionStatement) {
        return isContinue();
    }


    public void endVisit(MySqlSetTransactionStatement mySqlSetTransactionStatement) {

    }


    public boolean visit(MySqlSetNamesStatement mySqlSetNamesStatement) {
        return isContinue();
    }


    public void endVisit(MySqlSetNamesStatement mySqlSetNamesStatement) {

    }


    public boolean visit(MySqlSetCharSetStatement mySqlSetCharSetStatement) {
        return isContinue();
    }


    public void endVisit(MySqlSetCharSetStatement mySqlSetCharSetStatement) {

    }


    public boolean visit(MySqlShowBinaryLogsStatement mySqlShowBinaryLogsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowBinaryLogsStatement mySqlShowBinaryLogsStatement) {

    }


    public boolean visit(MySqlShowMasterLogsStatement mySqlShowMasterLogsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowMasterLogsStatement mySqlShowMasterLogsStatement) {

    }


    public boolean visit(MySqlShowCharacterSetStatement mySqlShowCharacterSetStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCharacterSetStatement mySqlShowCharacterSetStatement) {

    }


    public boolean visit(MySqlShowCollationStatement mySqlShowCollationStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCollationStatement mySqlShowCollationStatement) {

    }


    public boolean visit(MySqlShowBinLogEventsStatement mySqlShowBinLogEventsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowBinLogEventsStatement mySqlShowBinLogEventsStatement) {

    }


    public boolean visit(MySqlShowContributorsStatement mySqlShowContributorsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowContributorsStatement mySqlShowContributorsStatement) {

    }


    public boolean visit(MySqlShowCreateDatabaseStatement mySqlShowCreateDatabaseStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCreateDatabaseStatement mySqlShowCreateDatabaseStatement) {

    }


    public boolean visit(MySqlShowCreateEventStatement mySqlShowCreateEventStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCreateEventStatement mySqlShowCreateEventStatement) {

    }


    public boolean visit(MySqlShowCreateFunctionStatement mySqlShowCreateFunctionStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCreateFunctionStatement mySqlShowCreateFunctionStatement) {

    }


    public boolean visit(MySqlShowCreateProcedureStatement mySqlShowCreateProcedureStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCreateProcedureStatement mySqlShowCreateProcedureStatement) {

    }


    public boolean visit(MySqlShowCreateTableStatement mySqlShowCreateTableStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCreateTableStatement mySqlShowCreateTableStatement) {

    }


    public boolean visit(MySqlShowCreateTriggerStatement mySqlShowCreateTriggerStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCreateTriggerStatement mySqlShowCreateTriggerStatement) {

    }


    public boolean visit(MySqlShowCreateViewStatement mySqlShowCreateViewStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowCreateViewStatement mySqlShowCreateViewStatement) {

    }


    public boolean visit(MySqlShowEngineStatement mySqlShowEngineStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowEngineStatement mySqlShowEngineStatement) {

    }


    public boolean visit(MySqlShowEnginesStatement mySqlShowEnginesStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowEnginesStatement mySqlShowEnginesStatement) {

    }


    public boolean visit(MySqlShowErrorsStatement mySqlShowErrorsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowErrorsStatement mySqlShowErrorsStatement) {

    }


    public boolean visit(MySqlShowEventsStatement mySqlShowEventsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowEventsStatement mySqlShowEventsStatement) {

    }


    public boolean visit(MySqlShowFunctionCodeStatement mySqlShowFunctionCodeStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowFunctionCodeStatement mySqlShowFunctionCodeStatement) {

    }


    public boolean visit(MySqlShowFunctionStatusStatement mySqlShowFunctionStatusStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowFunctionStatusStatement mySqlShowFunctionStatusStatement) {

    }


    public boolean visit(MySqlShowGrantsStatement mySqlShowGrantsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowGrantsStatement mySqlShowGrantsStatement) {

    }


    public boolean visit(MySqlUserName mySqlUserName) {
        return isContinue();
    }


    public void endVisit(MySqlUserName mySqlUserName) {

    }


    public boolean visit(MySqlShowIndexesStatement mySqlShowIndexesStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowIndexesStatement mySqlShowIndexesStatement) {

    }


    public boolean visit(MySqlShowKeysStatement mySqlShowKeysStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowKeysStatement mySqlShowKeysStatement) {

    }


    public boolean visit(MySqlShowMasterStatusStatement mySqlShowMasterStatusStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowMasterStatusStatement mySqlShowMasterStatusStatement) {

    }


    public boolean visit(MySqlShowOpenTablesStatement mySqlShowOpenTablesStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowOpenTablesStatement mySqlShowOpenTablesStatement) {

    }


    public boolean visit(MySqlShowPluginsStatement mySqlShowPluginsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowPluginsStatement mySqlShowPluginsStatement) {

    }


    public boolean visit(MySqlShowPrivilegesStatement mySqlShowPrivilegesStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowPrivilegesStatement mySqlShowPrivilegesStatement) {

    }


    public boolean visit(MySqlShowProcedureCodeStatement mySqlShowProcedureCodeStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowProcedureCodeStatement mySqlShowProcedureCodeStatement) {

    }


    public boolean visit(MySqlShowProcedureStatusStatement mySqlShowProcedureStatusStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowProcedureStatusStatement mySqlShowProcedureStatusStatement) {

    }


    public boolean visit(MySqlShowProcessListStatement mySqlShowProcessListStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowProcessListStatement mySqlShowProcessListStatement) {

    }


    public boolean visit(MySqlShowProfileStatement mySqlShowProfileStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowProfileStatement mySqlShowProfileStatement) {

    }


    public boolean visit(MySqlShowProfilesStatement mySqlShowProfilesStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowProfilesStatement mySqlShowProfilesStatement) {

    }


    public boolean visit(MySqlShowRelayLogEventsStatement mySqlShowRelayLogEventsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowRelayLogEventsStatement mySqlShowRelayLogEventsStatement) {

    }


    public boolean visit(MySqlShowSlaveHostsStatement mySqlShowSlaveHostsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowSlaveHostsStatement mySqlShowSlaveHostsStatement) {

    }


    public boolean visit(MySqlShowSlaveStatusStatement mySqlShowSlaveStatusStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowSlaveStatusStatement mySqlShowSlaveStatusStatement) {

    }


    public boolean visit(MySqlShowTableStatusStatement mySqlShowTableStatusStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowTableStatusStatement mySqlShowTableStatusStatement) {

    }


    public boolean visit(MySqlShowTriggersStatement mySqlShowTriggersStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowTriggersStatement mySqlShowTriggersStatement) {

    }


    public boolean visit(MySqlShowVariantsStatement mySqlShowVariantsStatement) {
        return isContinue();
    }


    public void endVisit(MySqlShowVariantsStatement mySqlShowVariantsStatement) {

    }


    public boolean visit(MySqlRenameTableStatement.Item item) {
        return isContinue();
    }


    public void endVisit(MySqlRenameTableStatement.Item item) {

    }


    public boolean visit(MySqlRenameTableStatement mySqlRenameTableStatement) {
        return isContinue();
    }


    public void endVisit(MySqlRenameTableStatement mySqlRenameTableStatement) {

    }



    public boolean visit(MySqlUseIndexHint mySqlUseIndexHint) {
        return isContinue();
    }


    public void endVisit(MySqlUseIndexHint mySqlUseIndexHint) {

    }


    public boolean visit(MySqlIgnoreIndexHint mySqlIgnoreIndexHint) {
        return isContinue();
    }


    public void endVisit(MySqlIgnoreIndexHint mySqlIgnoreIndexHint) {

    }


    public boolean visit(MySqlLockTableStatement mySqlLockTableStatement) {
        return isContinue();
    }


    public void endVisit(MySqlLockTableStatement mySqlLockTableStatement) {

    }


    public boolean visit(MySqlUnlockTablesStatement mySqlUnlockTablesStatement) {
        return isContinue();
    }


    public void endVisit(MySqlUnlockTablesStatement mySqlUnlockTablesStatement) {

    }


    public boolean visit(MySqlForceIndexHint mySqlForceIndexHint) {
        return isContinue();
    }


    public void endVisit(MySqlForceIndexHint mySqlForceIndexHint) {

    }


    public boolean visit(MySqlAlterTableChangeColumn mySqlAlterTableChangeColumn) {
        return isContinue();
    }


    public void endVisit(MySqlAlterTableChangeColumn mySqlAlterTableChangeColumn) {

    }


    public boolean visit(MySqlAlterTableCharacter mySqlAlterTableCharacter) {
        return isContinue();
    }


    public void endVisit(MySqlAlterTableCharacter mySqlAlterTableCharacter) {

    }


    public boolean visit(MySqlAlterTableOption mySqlAlterTableOption) {
        return isContinue();
    }


    public void endVisit(MySqlAlterTableOption mySqlAlterTableOption) {

    }


    public boolean visit(MySqlCreateTableStatement mySqlCreateTableStatement) {
        return isContinue();
    }


    public void endVisit(MySqlCreateTableStatement mySqlCreateTableStatement) {

    }


    public boolean visit(MySqlHelpStatement mySqlHelpStatement) {
        return isContinue();
    }


    public void endVisit(MySqlHelpStatement mySqlHelpStatement) {

    }


    public boolean visit(MySqlCharExpr mySqlCharExpr) {
        return isContinue();
    }


    public void endVisit(MySqlCharExpr mySqlCharExpr) {

    }


    public boolean visit(MySqlAlterTableModifyColumn mySqlAlterTableModifyColumn) {
        return isContinue();
    }


    public void endVisit(MySqlAlterTableModifyColumn mySqlAlterTableModifyColumn) {

    }


    public boolean visit(MySqlAlterTableDiscardTablespace mySqlAlterTableDiscardTablespace) {
        return isContinue();
    }


    public void endVisit(MySqlAlterTableDiscardTablespace mySqlAlterTableDiscardTablespace) {

    }


    public boolean visit(MySqlAlterTableImportTablespace mySqlAlterTableImportTablespace) {
        return isContinue();
    }


    public void endVisit(MySqlAlterTableImportTablespace mySqlAlterTableImportTablespace) {

    }


    public boolean visit(MySqlCreateTableStatement.TableSpaceOption tableSpaceOption) {
        return isContinue();
    }


    public void endVisit(MySqlCreateTableStatement.TableSpaceOption tableSpaceOption) {

    }


    public boolean visit(MySqlAnalyzeStatement mySqlAnalyzeStatement) {
        return isContinue();
    }


    public void endVisit(MySqlAnalyzeStatement mySqlAnalyzeStatement) {

    }


    public boolean visit(MySqlAlterUserStatement mySqlAlterUserStatement) {
        return isContinue();
    }


    public void endVisit(MySqlAlterUserStatement mySqlAlterUserStatement) {

    }


    public boolean visit(MySqlOptimizeStatement mySqlOptimizeStatement) {
        return isContinue();
    }


    public void endVisit(MySqlOptimizeStatement mySqlOptimizeStatement) {

    }


    public boolean visit(MySqlSetPasswordStatement mySqlSetPasswordStatement) {
        return isContinue();
    }


    public void endVisit(MySqlSetPasswordStatement mySqlSetPasswordStatement) {

    }


    public boolean visit(MySqlHintStatement mySqlHintStatement) {
        return isContinue();
    }


    public void endVisit(MySqlHintStatement mySqlHintStatement) {

    }


    public boolean visit(MySqlOrderingExpr mySqlOrderingExpr) {
        return isContinue();
    }


    public void endVisit(MySqlOrderingExpr mySqlOrderingExpr) {

    }




    public boolean visit(MySqlCaseStatement mySqlCaseStatement) {
        return isContinue();
    }


    public void endVisit(MySqlCaseStatement mySqlCaseStatement) {

    }


    public boolean visit(MySqlDeclareStatement mySqlDeclareStatement) {
        return isContinue();
    }


    public void endVisit(MySqlDeclareStatement mySqlDeclareStatement) {

    }


    public boolean visit(MySqlSelectIntoStatement mySqlSelectIntoStatement) {
        return isContinue();
    }


    public void endVisit(MySqlSelectIntoStatement mySqlSelectIntoStatement) {

    }


    public boolean visit(MySqlCaseStatement.MySqlWhenStatement mySqlWhenStatement) {
        return isContinue();
    }


    public void endVisit(MySqlCaseStatement.MySqlWhenStatement mySqlWhenStatement) {

    }


    public boolean visit(MySqlLeaveStatement mySqlLeaveStatement) {
        return isContinue();
    }


    public void endVisit(MySqlLeaveStatement mySqlLeaveStatement) {

    }


    public boolean visit(MySqlIterateStatement mySqlIterateStatement) {
        return isContinue();
    }


    public void endVisit(MySqlIterateStatement mySqlIterateStatement) {

    }


    public boolean visit(MySqlRepeatStatement mySqlRepeatStatement) {
        return isContinue();
    }


    public void endVisit(MySqlRepeatStatement mySqlRepeatStatement) {

    }


    public boolean visit(MySqlCursorDeclareStatement mySqlCursorDeclareStatement) {
        return isContinue();
    }


    public void endVisit(MySqlCursorDeclareStatement mySqlCursorDeclareStatement) {

    }


    public boolean visit(MySqlUpdateTableSource mySqlUpdateTableSource) {
        return isContinue();
    }


    public void endVisit(MySqlUpdateTableSource mySqlUpdateTableSource) {

    }


    public boolean visit(MySqlAlterTableAlterColumn mySqlAlterTableAlterColumn) {
        return isContinue();
    }


    public void endVisit(MySqlAlterTableAlterColumn mySqlAlterTableAlterColumn) {

    }


    public boolean visit(MySqlSubPartitionByKey mySqlSubPartitionByKey) {
        return isContinue();
    }


    public void endVisit(MySqlSubPartitionByKey mySqlSubPartitionByKey) {

    }


    public boolean visit(MySqlSubPartitionByList mySqlSubPartitionByList) {
        return isContinue();
    }


    public void endVisit(MySqlSubPartitionByList mySqlSubPartitionByList) {

    }


    public boolean visit(MySqlDeclareHandlerStatement mySqlDeclareHandlerStatement) {
        return isContinue();
    }


    public void endVisit(MySqlDeclareHandlerStatement mySqlDeclareHandlerStatement) {

    }


    public boolean visit(MySqlDeclareConditionStatement mySqlDeclareConditionStatement) {
        return isContinue();
    }


    public void endVisit(MySqlDeclareConditionStatement mySqlDeclareConditionStatement) {

    }



    //=======================mysql end===================

    //========================DB2 start==========


    public boolean visit(DB2SelectQueryBlock x) {
        return isContinue();
    }


    public void endVisit(DB2SelectQueryBlock x) {

    }


    public boolean visit(DB2ValuesStatement x) {
        return isContinue();
    }


    public void endVisit(DB2ValuesStatement x) {

    }

    //========================DB2 end===============

}
