package org.the.force.jdbc.partition.engine.parser.visitor;

import org.druid.sql.ast.SQLArgument;
import org.druid.sql.ast.SQLCommentHint;
import org.druid.sql.ast.SQLDataType;
import org.druid.sql.ast.SQLDeclareItem;
import org.druid.sql.ast.SQLKeep;
import org.druid.sql.ast.SQLLimit;
import org.druid.sql.ast.SQLObject;
import org.druid.sql.ast.SQLOrderBy;
import org.druid.sql.ast.SQLOver;
import org.druid.sql.ast.SQLParameter;
import org.druid.sql.ast.SQLPartition;
import org.druid.sql.ast.SQLPartitionByHash;
import org.druid.sql.ast.SQLPartitionByList;
import org.druid.sql.ast.SQLPartitionByRange;
import org.druid.sql.ast.SQLPartitionValue;
import org.druid.sql.ast.SQLSubPartition;
import org.druid.sql.ast.SQLSubPartitionByHash;
import org.druid.sql.ast.SQLSubPartitionByList;
import org.druid.sql.ast.expr.SQLAggregateExpr;
import org.druid.sql.ast.expr.SQLAllColumnExpr;
import org.druid.sql.ast.expr.SQLAllExpr;
import org.druid.sql.ast.expr.SQLAnyExpr;
import org.druid.sql.ast.expr.SQLArrayExpr;
import org.druid.sql.ast.expr.SQLBetweenExpr;
import org.druid.sql.ast.expr.SQLBinaryExpr;
import org.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.druid.sql.ast.expr.SQLBooleanExpr;
import org.druid.sql.ast.expr.SQLCaseExpr;
import org.druid.sql.ast.expr.SQLCaseStatement;
import org.druid.sql.ast.expr.SQLCastExpr;
import org.druid.sql.ast.expr.SQLCharExpr;
import org.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import org.druid.sql.ast.expr.SQLDateExpr;
import org.druid.sql.ast.expr.SQLDefaultExpr;
import org.druid.sql.ast.expr.SQLExistsExpr;
import org.druid.sql.ast.expr.SQLFlashbackExpr;
import org.druid.sql.ast.expr.SQLGroupingSetExpr;
import org.druid.sql.ast.expr.SQLHexExpr;
import org.druid.sql.ast.expr.SQLIdentifierExpr;
import org.druid.sql.ast.expr.SQLInListExpr;
import org.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.druid.sql.ast.expr.SQLIntegerExpr;
import org.druid.sql.ast.expr.SQLListExpr;
import org.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.druid.sql.ast.expr.SQLNCharExpr;
import org.druid.sql.ast.expr.SQLNotExpr;
import org.druid.sql.ast.expr.SQLNullExpr;
import org.druid.sql.ast.expr.SQLNumberExpr;
import org.druid.sql.ast.expr.SQLPropertyExpr;
import org.druid.sql.ast.expr.SQLQueryExpr;
import org.druid.sql.ast.expr.SQLSequenceExpr;
import org.druid.sql.ast.expr.SQLSomeExpr;
import org.druid.sql.ast.expr.SQLTimestampExpr;
import org.druid.sql.ast.expr.SQLUnaryExpr;
import org.druid.sql.ast.expr.SQLVariantRefExpr;
import org.druid.sql.ast.statement.SQLAlterDatabaseStatement;
import org.druid.sql.ast.statement.SQLAlterTableAddColumn;
import org.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import org.druid.sql.ast.statement.SQLAlterTableAddIndex;
import org.druid.sql.ast.statement.SQLAlterTableAddPartition;
import org.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import org.druid.sql.ast.statement.SQLAlterTableAnalyzePartition;
import org.druid.sql.ast.statement.SQLAlterTableCheckPartition;
import org.druid.sql.ast.statement.SQLAlterTableCoalescePartition;
import org.druid.sql.ast.statement.SQLAlterTableConvertCharSet;
import org.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import org.druid.sql.ast.statement.SQLAlterTableDisableKeys;
import org.druid.sql.ast.statement.SQLAlterTableDisableLifecycle;
import org.druid.sql.ast.statement.SQLAlterTableDiscardPartition;
import org.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import org.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import org.druid.sql.ast.statement.SQLAlterTableDropForeignKey;
import org.druid.sql.ast.statement.SQLAlterTableDropIndex;
import org.druid.sql.ast.statement.SQLAlterTableDropKey;
import org.druid.sql.ast.statement.SQLAlterTableDropPartition;
import org.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import org.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import org.druid.sql.ast.statement.SQLAlterTableEnableKeys;
import org.druid.sql.ast.statement.SQLAlterTableEnableLifecycle;
import org.druid.sql.ast.statement.SQLAlterTableImportPartition;
import org.druid.sql.ast.statement.SQLAlterTableOptimizePartition;
import org.druid.sql.ast.statement.SQLAlterTableReOrganizePartition;
import org.druid.sql.ast.statement.SQLAlterTableRebuildPartition;
import org.druid.sql.ast.statement.SQLAlterTableRename;
import org.druid.sql.ast.statement.SQLAlterTableRenameColumn;
import org.druid.sql.ast.statement.SQLAlterTableRenamePartition;
import org.druid.sql.ast.statement.SQLAlterTableRepairPartition;
import org.druid.sql.ast.statement.SQLAlterTableSetComment;
import org.druid.sql.ast.statement.SQLAlterTableSetLifecycle;
import org.druid.sql.ast.statement.SQLAlterTableStatement;
import org.druid.sql.ast.statement.SQLAlterTableTouch;
import org.druid.sql.ast.statement.SQLAlterTableTruncatePartition;
import org.druid.sql.ast.statement.SQLAlterViewRenameStatement;
import org.druid.sql.ast.statement.SQLAssignItem;
import org.druid.sql.ast.statement.SQLBlockStatement;
import org.druid.sql.ast.statement.SQLCallStatement;
import org.druid.sql.ast.statement.SQLCharacterDataType;
import org.druid.sql.ast.statement.SQLCheck;
import org.druid.sql.ast.statement.SQLCloseStatement;
import org.druid.sql.ast.statement.SQLColumnCheck;
import org.druid.sql.ast.statement.SQLColumnDefinition;
import org.druid.sql.ast.statement.SQLColumnPrimaryKey;
import org.druid.sql.ast.statement.SQLColumnReference;
import org.druid.sql.ast.statement.SQLColumnUniqueKey;
import org.druid.sql.ast.statement.SQLCommentStatement;
import org.druid.sql.ast.statement.SQLCommitStatement;
import org.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import org.druid.sql.ast.statement.SQLCreateFunctionStatement;
import org.druid.sql.ast.statement.SQLCreateIndexStatement;
import org.druid.sql.ast.statement.SQLCreateProcedureStatement;
import org.druid.sql.ast.statement.SQLCreateSequenceStatement;
import org.druid.sql.ast.statement.SQLCreateTableStatement;
import org.druid.sql.ast.statement.SQLCreateTriggerStatement;
import org.druid.sql.ast.statement.SQLCreateViewStatement;
import org.druid.sql.ast.statement.SQLDeclareStatement;
import org.druid.sql.ast.statement.SQLDeleteStatement;
import org.druid.sql.ast.statement.SQLDescribeStatement;
import org.druid.sql.ast.statement.SQLDropDatabaseStatement;
import org.druid.sql.ast.statement.SQLDropFunctionStatement;
import org.druid.sql.ast.statement.SQLDropIndexStatement;
import org.druid.sql.ast.statement.SQLDropProcedureStatement;
import org.druid.sql.ast.statement.SQLDropSequenceStatement;
import org.druid.sql.ast.statement.SQLDropTableSpaceStatement;
import org.druid.sql.ast.statement.SQLDropTableStatement;
import org.druid.sql.ast.statement.SQLDropTriggerStatement;
import org.druid.sql.ast.statement.SQLDropUserStatement;
import org.druid.sql.ast.statement.SQLDropViewStatement;
import org.druid.sql.ast.statement.SQLErrorLoggingClause;
import org.druid.sql.ast.statement.SQLExplainStatement;
import org.druid.sql.ast.statement.SQLExprHint;
import org.druid.sql.ast.statement.SQLExprTableSource;
import org.druid.sql.ast.statement.SQLFetchStatement;
import org.druid.sql.ast.statement.SQLForeignKeyImpl;
import org.druid.sql.ast.statement.SQLGrantStatement;
import org.druid.sql.ast.statement.SQLIfStatement;
import org.druid.sql.ast.statement.SQLInsertStatement;
import org.druid.sql.ast.statement.SQLJoinTableSource;
import org.druid.sql.ast.statement.SQLLoopStatement;
import org.druid.sql.ast.statement.SQLMergeStatement;
import org.druid.sql.ast.statement.SQLNotNullConstraint;
import org.druid.sql.ast.statement.SQLNullConstraint;
import org.druid.sql.ast.statement.SQLOpenStatement;
import org.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import org.druid.sql.ast.statement.SQLReleaseSavePointStatement;
import org.druid.sql.ast.statement.SQLReturnStatement;
import org.druid.sql.ast.statement.SQLRevokeStatement;
import org.druid.sql.ast.statement.SQLRollbackStatement;
import org.druid.sql.ast.statement.SQLSavePointStatement;
import org.druid.sql.ast.statement.SQLSelect;
import org.druid.sql.ast.statement.SQLSelectGroupByClause;
import org.druid.sql.ast.statement.SQLSelectItem;
import org.druid.sql.ast.statement.SQLSelectOrderByItem;
import org.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.druid.sql.ast.statement.SQLSelectStatement;
import org.druid.sql.ast.statement.SQLSetStatement;
import org.druid.sql.ast.statement.SQLShowTablesStatement;
import org.druid.sql.ast.statement.SQLStartTransactionStatement;
import org.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.druid.sql.ast.statement.SQLTruncateStatement;
import org.druid.sql.ast.statement.SQLUnionQuery;
import org.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.druid.sql.ast.statement.SQLUnique;
import org.druid.sql.ast.statement.SQLUpdateSetItem;
import org.druid.sql.ast.statement.SQLUpdateStatement;
import org.druid.sql.ast.statement.SQLUseStatement;
import org.druid.sql.ast.statement.SQLWhileStatement;
import org.druid.sql.ast.statement.SQLWithSubqueryClause;
import org.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/5/21.
 */
public abstract class GeneralVisitor implements SQLASTVisitor {


    protected boolean isContinue() {
        return true;
    }


    public void endVisit(SQLAllColumnExpr x) {

    }


    public void endVisit(SQLBetweenExpr x) {

    }


    public void endVisit(SQLBinaryOpExpr x) {

    }


    public void endVisit(SQLCaseExpr x) {

    }


    public void endVisit(SQLCaseExpr.Item x) {

    }


    public void endVisit(SQLCaseStatement x) {

    }


    public void endVisit(SQLCaseStatement.Item x) {

    }


    public void endVisit(SQLCharExpr x) {

    }


    public void endVisit(SQLIdentifierExpr x) {

    }


    public void endVisit(SQLInListExpr x) {

    }


    public void endVisit(SQLIntegerExpr x) {

    }


    public void endVisit(SQLExistsExpr x) {

    }


    public void endVisit(SQLNCharExpr x) {

    }


    public void endVisit(SQLNotExpr x) {

    }


    public void endVisit(SQLNullExpr x) {

    }


    public void endVisit(SQLNumberExpr x) {

    }


    public void endVisit(SQLPropertyExpr x) {

    }


    public void endVisit(SQLSelectGroupByClause x) {

    }


    public void endVisit(SQLSelectItem x) {

    }


    public void endVisit(SQLSelectStatement selectStatement) {

    }


    public void postVisit(SQLObject astNode) {

    }


    public void preVisit(SQLObject astNode) {

    }


    public boolean visit(SQLAllColumnExpr x) {
        return isContinue();
    }


    public boolean visit(SQLBetweenExpr x) {
        return isContinue();
    }


    public boolean visit(SQLBinaryOpExpr x) {
        return isContinue();
    }


    public boolean visit(SQLCaseExpr x) {
        return isContinue();
    }


    public boolean visit(SQLCaseExpr.Item x) {
        return isContinue();
    }


    public boolean visit(SQLCaseStatement x) {
        return isContinue();
    }


    public boolean visit(SQLCaseStatement.Item x) {
        return isContinue();
    }


    public boolean visit(SQLCastExpr x) {
        return isContinue();
    }


    public boolean visit(SQLCharExpr x) {
        return isContinue();
    }


    public boolean visit(SQLExistsExpr x) {
        return isContinue();
    }


    public boolean visit(SQLIdentifierExpr x) {
        return isContinue();
    }


    public boolean visit(SQLInListExpr x) {
        return isContinue();
    }


    public boolean visit(SQLIntegerExpr x) {
        return isContinue();
    }


    public boolean visit(SQLNCharExpr x) {
        return isContinue();
    }


    public boolean visit(SQLNotExpr x) {
        return isContinue();
    }


    public boolean visit(SQLNullExpr x) {
        return isContinue();
    }


    public boolean visit(SQLNumberExpr x) {
        return isContinue();
    }


    public boolean visit(SQLPropertyExpr x) {
        return isContinue();
    }


    public boolean visit(SQLSelectGroupByClause x) {
        return isContinue();
    }


    public boolean visit(SQLSelectItem x) {
        return isContinue();
    }


    public void endVisit(SQLCastExpr x) {

    }


    public boolean visit(SQLSelectStatement astNode) {
        return isContinue();
    }


    public void endVisit(SQLAggregateExpr astNode) {

    }


    public boolean visit(SQLAggregateExpr astNode) {
        return isContinue();
    }


    public boolean visit(SQLVariantRefExpr x) {
        return isContinue();
    }


    public void endVisit(SQLVariantRefExpr x) {

    }


    public boolean visit(SQLQueryExpr x) {
        return isContinue();
    }


    public void endVisit(SQLQueryExpr x) {

    }


    public boolean visit(SQLUnaryExpr x) {
        return isContinue();
    }


    public void endVisit(SQLUnaryExpr x) {

    }


    public boolean visit(SQLHexExpr x) {
        return isContinue();
    }


    public void endVisit(SQLHexExpr x) {

    }


    public boolean visit(SQLSelect x) {
        return isContinue();
    }


    public void endVisit(SQLSelect select) {

    }


    public boolean visit(SQLSelectQueryBlock x) {
        return isContinue();
    }


    public void endVisit(SQLSelectQueryBlock x) {

    }


    public boolean visit(SQLExprTableSource x) {
        return isContinue();
    }


    public void endVisit(SQLExprTableSource x) {

    }


    public boolean visit(SQLOrderBy x) {
        return isContinue();
    }


    public void endVisit(SQLOrderBy x) {

    }


    public boolean visit(SQLSelectOrderByItem x) {
        return isContinue();
    }


    public void endVisit(SQLSelectOrderByItem x) {

    }


    public boolean visit(SQLDropTableStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropTableStatement x) {

    }


    public boolean visit(SQLCreateTableStatement x) {
        return isContinue();
    }


    public void endVisit(SQLCreateTableStatement x) {

    }


    public boolean visit(SQLColumnDefinition x) {
        return isContinue();
    }


    public void endVisit(SQLColumnDefinition x) {

    }


    public boolean visit(SQLColumnDefinition.Identity x) {
        return isContinue();
    }


    public void endVisit(SQLColumnDefinition.Identity x) {

    }


    public boolean visit(SQLDataType x) {
        return isContinue();
    }


    public void endVisit(SQLDataType x) {

    }


    public boolean visit(SQLCharacterDataType x) {
        return isContinue();
    }


    public void endVisit(SQLCharacterDataType x) {

    }


    public boolean visit(SQLDeleteStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDeleteStatement x) {

    }


    public boolean visit(SQLCurrentOfCursorExpr x) {
        return isContinue();
    }


    public void endVisit(SQLCurrentOfCursorExpr x) {

    }


    public boolean visit(SQLInsertStatement x) {
        return isContinue();
    }


    public void endVisit(SQLInsertStatement x) {

    }


    public boolean visit(SQLInsertStatement.ValuesClause x) {
        return isContinue();
    }


    public void endVisit(SQLInsertStatement.ValuesClause x) {

    }


    public boolean visit(SQLUpdateSetItem x) {
        return isContinue();
    }


    public void endVisit(SQLUpdateSetItem x) {

    }


    public boolean visit(SQLUpdateStatement x) {
        return isContinue();
    }


    public void endVisit(SQLUpdateStatement x) {

    }


    public boolean visit(SQLCreateViewStatement x) {
        return isContinue();
    }


    public void endVisit(SQLCreateViewStatement x) {

    }


    public boolean visit(SQLCreateViewStatement.Column x) {
        return isContinue();
    }


    public void endVisit(SQLCreateViewStatement.Column x) {

    }


    public boolean visit(SQLNotNullConstraint x) {
        return isContinue();
    }


    public void endVisit(SQLNotNullConstraint x) {

    }


    public void endVisit(SQLMethodInvokeExpr x) {

    }


    public boolean visit(SQLMethodInvokeExpr x) {
        return isContinue();
    }


    public void endVisit(SQLUnionQuery x) {

    }


    public boolean visit(SQLUnionQuery x) {
        return isContinue();
    }


    public void endVisit(SQLSetStatement x) {

    }


    public boolean visit(SQLSetStatement x) {
        return isContinue();
    }


    public void endVisit(SQLAssignItem x) {

    }


    public boolean visit(SQLAssignItem x) {
        return isContinue();
    }


    public void endVisit(SQLCallStatement x) {

    }


    public boolean visit(SQLCallStatement x) {
        return isContinue();
    }


    public void endVisit(SQLJoinTableSource x) {

    }


    public boolean visit(SQLJoinTableSource x) {
        return isContinue();
    }


    public void endVisit(SQLSomeExpr x) {

    }


    public boolean visit(SQLSomeExpr x) {
        return isContinue();
    }


    public void endVisit(SQLAnyExpr x) {

    }


    public boolean visit(SQLAnyExpr x) {
        return isContinue();
    }


    public void endVisit(SQLAllExpr x) {

    }


    public boolean visit(SQLAllExpr x) {
        return isContinue();
    }


    public void endVisit(SQLInSubQueryExpr x) {

    }


    public boolean visit(SQLInSubQueryExpr x) {
        return isContinue();
    }


    public void endVisit(SQLListExpr x) {

    }


    public boolean visit(SQLListExpr x) {
        return isContinue();
    }


    public void endVisit(SQLSubqueryTableSource x) {

    }


    public boolean visit(SQLSubqueryTableSource x) {
        return isContinue();
    }


    public void endVisit(SQLTruncateStatement x) {

    }


    public boolean visit(SQLTruncateStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDefaultExpr x) {

    }


    public boolean visit(SQLDefaultExpr x) {
        return isContinue();
    }


    public void endVisit(SQLCommentStatement x) {

    }


    public boolean visit(SQLCommentStatement x) {
        return isContinue();
    }


    public void endVisit(SQLUseStatement x) {

    }


    public boolean visit(SQLUseStatement x) {
        return isContinue();
    }


    public boolean visit(SQLAlterTableAddColumn x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableAddColumn x) {

    }


    public boolean visit(SQLAlterTableDropColumnItem x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDropColumnItem x) {

    }


    public boolean visit(SQLAlterTableDropIndex x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDropIndex x) {

    }


    public boolean visit(SQLDropIndexStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropIndexStatement x) {

    }


    public boolean visit(SQLDropViewStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropViewStatement x) {

    }


    public boolean visit(SQLSavePointStatement x) {
        return isContinue();
    }


    public void endVisit(SQLSavePointStatement x) {

    }


    public boolean visit(SQLRollbackStatement x) {
        return isContinue();
    }


    public void endVisit(SQLRollbackStatement x) {

    }


    public boolean visit(SQLReleaseSavePointStatement x) {
        return isContinue();
    }


    public void endVisit(SQLReleaseSavePointStatement x) {

    }


    public void endVisit(SQLCommentHint x) {

    }


    public boolean visit(SQLCommentHint x) {
        return isContinue();
    }


    public void endVisit(SQLCreateDatabaseStatement x) {

    }


    public boolean visit(SQLCreateDatabaseStatement x) {
        return isContinue();
    }


    public void endVisit(SQLOver x) {

    }


    public boolean visit(SQLOver x) {
        return isContinue();
    }


    public void endVisit(SQLKeep x) {

    }


    public boolean visit(SQLKeep x) {
        return isContinue();
    }


    public void endVisit(SQLColumnPrimaryKey x) {

    }


    public boolean visit(SQLColumnPrimaryKey x) {
        return isContinue();
    }


    public boolean visit(SQLColumnUniqueKey x) {
        return isContinue();
    }


    public void endVisit(SQLColumnUniqueKey x) {

    }


    public void endVisit(SQLWithSubqueryClause x) {

    }


    public boolean visit(SQLWithSubqueryClause x) {
        return isContinue();
    }


    public void endVisit(SQLWithSubqueryClause.Entry x) {

    }


    public boolean visit(SQLWithSubqueryClause.Entry x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableAlterColumn x) {

    }


    public boolean visit(SQLAlterTableAlterColumn x) {
        return isContinue();
    }


    public boolean visit(SQLCheck x) {
        return isContinue();
    }


    public void endVisit(SQLCheck x) {

    }


    public boolean visit(SQLAlterTableDropForeignKey x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDropForeignKey x) {

    }


    public boolean visit(SQLAlterTableDropPrimaryKey x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDropPrimaryKey x) {

    }


    public boolean visit(SQLAlterTableDisableKeys x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDisableKeys x) {

    }


    public boolean visit(SQLAlterTableEnableKeys x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableEnableKeys x) {

    }


    public boolean visit(SQLAlterTableStatement x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableStatement x) {

    }


    public boolean visit(SQLAlterTableDisableConstraint x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDisableConstraint x) {

    }


    public boolean visit(SQLAlterTableEnableConstraint x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableEnableConstraint x) {

    }


    public boolean visit(SQLColumnCheck x) {
        return isContinue();
    }


    public void endVisit(SQLColumnCheck x) {

    }


    public boolean visit(SQLExprHint x) {
        return isContinue();
    }


    public void endVisit(SQLExprHint x) {

    }


    public boolean visit(SQLAlterTableDropConstraint x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDropConstraint x) {

    }


    public boolean visit(SQLUnique x) {
        return isContinue();
    }


    public void endVisit(SQLUnique x) {

    }


    public boolean visit(SQLPrimaryKeyImpl x) {
        return isContinue();
    }


    public void endVisit(SQLPrimaryKeyImpl x) {

    }


    public boolean visit(SQLCreateIndexStatement x) {
        return isContinue();
    }


    public void endVisit(SQLCreateIndexStatement x) {

    }


    public boolean visit(SQLAlterTableRenameColumn x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableRenameColumn x) {

    }


    public boolean visit(SQLColumnReference x) {
        return isContinue();
    }


    public void endVisit(SQLColumnReference x) {

    }


    public boolean visit(SQLForeignKeyImpl x) {
        return isContinue();
    }


    public void endVisit(SQLForeignKeyImpl x) {

    }


    public boolean visit(SQLDropSequenceStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropSequenceStatement x) {

    }


    public boolean visit(SQLDropTriggerStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropTriggerStatement x) {

    }


    public void endVisit(SQLDropUserStatement x) {

    }


    public boolean visit(SQLDropUserStatement x) {
        return isContinue();
    }


    public void endVisit(SQLExplainStatement x) {

    }


    public boolean visit(SQLExplainStatement x) {
        return isContinue();
    }


    public void endVisit(SQLGrantStatement x) {

    }


    public boolean visit(SQLGrantStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropDatabaseStatement x) {

    }


    public boolean visit(SQLDropDatabaseStatement x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableAddIndex x) {

    }


    public boolean visit(SQLAlterTableAddIndex x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableAddConstraint x) {

    }


    public boolean visit(SQLAlterTableAddConstraint x) {
        return isContinue();
    }


    public void endVisit(SQLCreateTriggerStatement x) {

    }


    public boolean visit(SQLCreateTriggerStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropFunctionStatement x) {

    }


    public boolean visit(SQLDropFunctionStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropTableSpaceStatement x) {

    }


    public boolean visit(SQLDropTableSpaceStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDropProcedureStatement x) {

    }


    public boolean visit(SQLDropProcedureStatement x) {
        return isContinue();
    }


    public void endVisit(SQLBooleanExpr x) {

    }


    public boolean visit(SQLBooleanExpr x) {
        return isContinue();
    }


    public void endVisit(SQLUnionQueryTableSource x) {

    }


    public boolean visit(SQLUnionQueryTableSource x) {
        return isContinue();
    }


    public void endVisit(SQLTimestampExpr x) {

    }


    public boolean visit(SQLTimestampExpr x) {
        return isContinue();
    }


    public void endVisit(SQLRevokeStatement x) {

    }


    public boolean visit(SQLRevokeStatement x) {
        return isContinue();
    }


    public void endVisit(SQLBinaryExpr x) {

    }


    public boolean visit(SQLBinaryExpr x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableRename x) {

    }


    public boolean visit(SQLAlterTableRename x) {
        return isContinue();
    }


    public void endVisit(SQLAlterViewRenameStatement x) {

    }


    public boolean visit(SQLAlterViewRenameStatement x) {
        return isContinue();
    }


    public void endVisit(SQLShowTablesStatement x) {

    }


    public boolean visit(SQLShowTablesStatement x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableAddPartition x) {

    }


    public boolean visit(SQLAlterTableAddPartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDropPartition x) {

    }


    public boolean visit(SQLAlterTableDropPartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableRenamePartition x) {

    }


    public boolean visit(SQLAlterTableRenamePartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableSetComment x) {

    }


    public boolean visit(SQLAlterTableSetComment x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableSetLifecycle x) {

    }


    public boolean visit(SQLAlterTableSetLifecycle x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableEnableLifecycle x) {

    }


    public boolean visit(SQLAlterTableEnableLifecycle x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDisableLifecycle x) {

    }


    public boolean visit(SQLAlterTableDisableLifecycle x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableTouch x) {

    }


    public boolean visit(SQLAlterTableTouch x) {
        return isContinue();
    }


    public void endVisit(SQLArrayExpr x) {

    }


    public boolean visit(SQLArrayExpr x) {
        return isContinue();
    }


    public void endVisit(SQLOpenStatement x) {

    }


    public boolean visit(SQLOpenStatement x) {
        return isContinue();
    }


    public void endVisit(SQLFetchStatement x) {

    }


    public boolean visit(SQLFetchStatement x) {
        return isContinue();
    }


    public void endVisit(SQLCloseStatement x) {

    }


    public boolean visit(SQLCloseStatement x) {
        return isContinue();
    }


    public boolean visit(SQLGroupingSetExpr x) {
        return isContinue();
    }


    public void endVisit(SQLGroupingSetExpr x) {

    }


    public boolean visit(SQLIfStatement x) {
        return isContinue();
    }


    public void endVisit(SQLIfStatement x) {

    }


    public boolean visit(SQLIfStatement.ElseIf x) {
        return isContinue();
    }


    public void endVisit(SQLIfStatement.ElseIf x) {

    }


    public boolean visit(SQLIfStatement.Else x) {
        return isContinue();
    }


    public void endVisit(SQLIfStatement.Else x) {

    }


    public boolean visit(SQLLoopStatement x) {
        return isContinue();
    }


    public void endVisit(SQLLoopStatement x) {

    }


    public boolean visit(SQLParameter x) {
        return isContinue();
    }


    public void endVisit(SQLParameter x) {

    }


    public boolean visit(SQLCreateProcedureStatement x) {
        return isContinue();
    }


    public void endVisit(SQLCreateProcedureStatement x) {

    }


    public boolean visit(SQLBlockStatement x) {
        return isContinue();
    }


    public void endVisit(SQLBlockStatement x) {

    }


    public boolean visit(SQLAlterTableDropKey x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDropKey x) {

    }


    public boolean visit(SQLDeclareItem x) {
        return isContinue();
    }


    public void endVisit(SQLDeclareItem x) {

    }


    public boolean visit(SQLPartitionValue x) {
        return isContinue();
    }


    public void endVisit(SQLPartitionValue x) {

    }


    public boolean visit(SQLPartition x) {
        return isContinue();
    }


    public void endVisit(SQLPartition x) {

    }


    public boolean visit(SQLPartitionByRange x) {
        return isContinue();
    }


    public void endVisit(SQLPartitionByRange x) {

    }


    public boolean visit(SQLPartitionByHash x) {
        return isContinue();
    }


    public void endVisit(SQLPartitionByHash x) {

    }


    public boolean visit(SQLPartitionByList x) {
        return isContinue();
    }


    public void endVisit(SQLPartitionByList x) {

    }


    public boolean visit(SQLSubPartition x) {
        return isContinue();
    }


    public void endVisit(SQLSubPartition x) {

    }


    public boolean visit(SQLSubPartitionByHash x) {
        return isContinue();
    }


    public void endVisit(SQLSubPartitionByHash x) {

    }


    public boolean visit(SQLSubPartitionByList x) {
        return isContinue();
    }


    public void endVisit(SQLSubPartitionByList x) {

    }


    public boolean visit(SQLAlterDatabaseStatement x) {
        return isContinue();
    }


    public void endVisit(SQLAlterDatabaseStatement x) {

    }


    public boolean visit(SQLAlterTableConvertCharSet x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableConvertCharSet x) {

    }


    public boolean visit(SQLAlterTableReOrganizePartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableReOrganizePartition x) {

    }


    public boolean visit(SQLAlterTableCoalescePartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableCoalescePartition x) {

    }


    public boolean visit(SQLAlterTableTruncatePartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableTruncatePartition x) {

    }


    public boolean visit(SQLAlterTableDiscardPartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableDiscardPartition x) {

    }


    public boolean visit(SQLAlterTableImportPartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableImportPartition x) {

    }


    public boolean visit(SQLAlterTableAnalyzePartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableAnalyzePartition x) {

    }


    public boolean visit(SQLAlterTableCheckPartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableCheckPartition x) {

    }


    public boolean visit(SQLAlterTableOptimizePartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableOptimizePartition x) {

    }


    public boolean visit(SQLAlterTableRebuildPartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableRebuildPartition x) {

    }


    public boolean visit(SQLAlterTableRepairPartition x) {
        return isContinue();
    }


    public void endVisit(SQLAlterTableRepairPartition x) {

    }


    public boolean visit(SQLSequenceExpr x) {
        return isContinue();
    }


    public void endVisit(SQLSequenceExpr x) {

    }


    public boolean visit(SQLMergeStatement x) {
        return isContinue();
    }


    public void endVisit(SQLMergeStatement x) {

    }


    public boolean visit(SQLMergeStatement.MergeUpdateClause x) {
        return isContinue();
    }


    public void endVisit(SQLMergeStatement.MergeUpdateClause x) {

    }


    public boolean visit(SQLMergeStatement.MergeInsertClause x) {
        return isContinue();
    }


    public void endVisit(SQLMergeStatement.MergeInsertClause x) {

    }


    public boolean visit(SQLErrorLoggingClause x) {
        return isContinue();
    }


    public void endVisit(SQLErrorLoggingClause x) {

    }


    public boolean visit(SQLNullConstraint x) {
        return isContinue();
    }


    public void endVisit(SQLNullConstraint x) {

    }


    public boolean visit(SQLCreateSequenceStatement x) {
        return isContinue();
    }


    public void endVisit(SQLCreateSequenceStatement x) {

    }


    public boolean visit(SQLDateExpr x) {
        return isContinue();
    }


    public void endVisit(SQLDateExpr x) {

    }


    public boolean visit(SQLLimit x) {
        return isContinue();
    }


    public void endVisit(SQLLimit x) {

    }


    public void endVisit(SQLStartTransactionStatement x) {

    }


    public boolean visit(SQLStartTransactionStatement x) {
        return isContinue();
    }


    public void endVisit(SQLDescribeStatement x) {

    }


    public boolean visit(SQLDescribeStatement x) {
        return isContinue();
    }

    @Override
    public boolean visit(SQLCreateFunctionStatement x) {
         return isContinue();
    }

    @Override
    public void endVisit(SQLCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLWhileStatement x) {
        return isContinue();
    }

    @Override
    public void endVisit(SQLWhileStatement x) {

    }

    @Override
    public boolean visit(SQLDeclareStatement x) {
        return isContinue();
    }

    @Override
    public void endVisit(SQLDeclareStatement x) {

    }

    @Override
    public boolean visit(SQLReturnStatement x) {
        return isContinue();
    }

    @Override
    public void endVisit(SQLReturnStatement x) {

    }

    @Override
    public boolean visit(SQLArgument x) {
        return isContinue();
    }

    @Override
    public void endVisit(SQLArgument x) {

    }

    @Override
    public boolean visit(SQLCommitStatement x) {
        return isContinue();
    }

    @Override
    public void endVisit(SQLCommitStatement x) {

    }

    @Override
    public boolean visit(SQLFlashbackExpr x) {
        return isContinue();
    }

    @Override
    public void endVisit(SQLFlashbackExpr x) {

    }
}
