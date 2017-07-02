/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.the.force.thirdparty.druid.sql.visitor;

import org.the.force.thirdparty.druid.sql.ast.SQLArgument;
import org.the.force.thirdparty.druid.sql.ast.SQLDataType;
import org.the.force.thirdparty.druid.sql.ast.SQLDeclareItem;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLKeep;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.SQLOver;
import org.the.force.thirdparty.druid.sql.ast.SQLParameter;
import org.the.force.thirdparty.druid.sql.ast.SQLPartition;
import org.the.force.thirdparty.druid.sql.ast.SQLPartitionByHash;
import org.the.force.thirdparty.druid.sql.ast.SQLPartitionByList;
import org.the.force.thirdparty.druid.sql.ast.SQLPartitionByRange;
import org.the.force.thirdparty.druid.sql.ast.SQLSubPartitionByHash;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAnyExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLArrayExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBooleanExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCaseExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCaseStatement;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCastExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCharExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLDateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLDefaultExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLExistsExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLFlashbackExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLGroupingSetExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNotExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNullExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNumberExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLSequenceExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLSomeExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLUnaryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterDatabaseStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableAddColumn;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableAddIndex;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableAddPartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableAnalyzePartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableCheckPartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableCoalescePartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableConvertCharSet;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDisableKeys;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDisableLifecycle;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDiscardPartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDropForeignKey;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDropIndex;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDropKey;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDropPartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableEnableKeys;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableEnableLifecycle;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableImportPartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableOptimizePartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableRenameColumn;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableRenamePartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableRepairPartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableSetLifecycle;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableTouch;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableTruncatePartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterViewRenameStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAssignItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLBlockStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCallStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCharacterDataType;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCheck;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCloseStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLColumnCheck;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLColumnPrimaryKey;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLColumnReference;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCommentStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCommitStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateFunctionStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateIndexStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateProcedureStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateTableStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateTriggerStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateViewStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDeclareStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDeleteStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDescribeStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropDatabaseStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropFunctionStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropIndexStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropSequenceStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropTableSpaceStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropTableStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropUserStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropViewStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExplainStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLFetchStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLForeignKeyImpl;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLGrantStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLIfStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLLoopStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLMergeStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLNotNullConstraint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLReleaseSavePointStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLReturnStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLRevokeStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLRollbackStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectGroupByClause;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectOrderByItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSetStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLShowTablesStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLStartTransactionStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTruncateStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnique;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUpdateSetItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUseStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLWhileStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLWithSubqueryClause;
import org.the.force.thirdparty.druid.sql.ast.SQLCommentHint;
import org.the.force.thirdparty.druid.sql.ast.SQLPartitionValue;
import org.the.force.thirdparty.druid.sql.ast.SQLSubPartition;
import org.the.force.thirdparty.druid.sql.ast.SQLSubPartitionByList;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLHexExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIntegerExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNCharExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLTimestampExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableReOrganizePartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableRebuildPartition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableRename;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLAlterTableSetComment;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLColumnDefinition;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLColumnUniqueKey;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLCreateSequenceStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropProcedureStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDropTriggerStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLErrorLoggingClause;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLNullConstraint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLOpenStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSavePointStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUpdateStatement;

public class SQLASTVisitorAdapter implements SQLASTVisitor {

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
        return true;
    }

    public boolean visit(SQLBetweenExpr x) {
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        return true;
    }

    public boolean visit(SQLCaseExpr x) {
        return true;
    }

    public boolean visit(SQLCaseExpr.Item x) {
        return true;
    }

    public boolean visit(SQLCaseStatement x) {
        return true;
    }

    public boolean visit(SQLCaseStatement.Item x) {
        return true;
    }

    public boolean visit(SQLCastExpr x) {
        return true;
    }

    public boolean visit(SQLCharExpr x) {
        return true;
    }

    public boolean visit(SQLExistsExpr x) {
        return true;
    }

    public boolean visit(SQLIdentifierExpr x) {
        return true;
    }

    public boolean visit(SQLInListExpr x) {
        return true;
    }

    public boolean visit(SQLIntegerExpr x) {
        return true;
    }

    public boolean visit(SQLNCharExpr x) {
        return true;
    }

    public boolean visit(SQLNotExpr x) {
        return true;
    }

    public boolean visit(SQLNullExpr x) {
        return true;
    }

    public boolean visit(SQLNumberExpr x) {
        return true;
    }

    public boolean visit(SQLPropertyExpr x) {
        return true;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        return true;
    }

    public boolean visit(SQLSelectItem x) {
        return true;
    }

    public void endVisit(SQLCastExpr x) {
    }

    public boolean visit(SQLSelectStatement astNode) {
        return true;
    }

    public void endVisit(SQLAggregateExpr x) {
    }

    public boolean visit(SQLAggregateExpr x) {
        return true;
    }

    public boolean visit(SQLVariantRefExpr x) {
        return true;
    }

    public void endVisit(SQLVariantRefExpr x) {
    }

    public boolean visit(SQLQueryExpr x) {
        return true;
    }

    public void endVisit(SQLQueryExpr x) {
    }

    public boolean visit(SQLSelect x) {
        return true;
    }

    public void endVisit(SQLSelect select) {
    }

    public boolean visit(SQLSelectQueryBlock x) {
        return true;
    }

    public void endVisit(SQLSelectQueryBlock x) {
    }

    public boolean visit(SQLExprTableSource x) {
        return true;
    }

    public void endVisit(SQLExprTableSource x) {
    }

    public boolean visit(SQLOrderBy x) {
        return true;
    }

    public void endVisit(SQLOrderBy x) {
    }

    public boolean visit(SQLSelectOrderByItem x) {
        return true;
    }

    public void endVisit(SQLSelectOrderByItem x) {
    }

    public boolean visit(SQLDropTableStatement x) {
        return true;
    }

    public void endVisit(SQLDropTableStatement x) {
    }

    public boolean visit(SQLCreateTableStatement x) {
        return true;
    }

    public void endVisit(SQLCreateTableStatement x) {
    }

    public boolean visit(SQLColumnDefinition x) {
        return true;
    }

    public void endVisit(SQLColumnDefinition x) {
    }

    public boolean visit(SQLColumnDefinition.Identity x) {
        return true;
    }

    public void endVisit(SQLColumnDefinition.Identity x) {
    }

    public boolean visit(SQLDataType x) {
        return true;
    }

    public void endVisit(SQLDataType x) {
    }

    public boolean visit(SQLDeleteStatement x) {
        return true;
    }

    public void endVisit(SQLDeleteStatement x) {
    }

    public boolean visit(SQLCurrentOfCursorExpr x) {
        return true;
    }

    public void endVisit(SQLCurrentOfCursorExpr x) {
    }

    public boolean visit(SQLInsertStatement x) {
        return true;
    }

    public void endVisit(SQLInsertStatement x) {
    }

    public boolean visit(SQLUpdateSetItem x) {
        return true;
    }

    public void endVisit(SQLUpdateSetItem x) {
    }

    public boolean visit(SQLUpdateStatement x) {
        return true;
    }

    public void endVisit(SQLUpdateStatement x) {
    }

    public boolean visit(SQLCreateViewStatement x) {
        return true;
    }

    public void endVisit(SQLCreateViewStatement x) {
    }

    public boolean visit(SQLCreateViewStatement.Column x) {
        return true;
    }

    public void endVisit(SQLCreateViewStatement.Column x) {
    }

    public boolean visit(SQLNotNullConstraint x) {
        return true;
    }

    public void endVisit(SQLNotNullConstraint x) {
    }

    @Override
    public void endVisit(SQLMethodInvokeExpr x) {

    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnionQuery x) {

    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        return true;
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnaryExpr x) {

    }

    @Override
    public boolean visit(SQLHexExpr x) {
        return false;
    }

    @Override
    public void endVisit(SQLHexExpr x) {

    }

    @Override
    public void endVisit(SQLSetStatement x) {

    }

    @Override
    public boolean visit(SQLSetStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAssignItem x) {

    }

    @Override
    public boolean visit(SQLAssignItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLCallStatement x) {

    }

    @Override
    public boolean visit(SQLCallStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLJoinTableSource x) {

    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        return true;
    }

    @Override
    public boolean visit(SQLInsertStatement.ValuesClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLInsertStatement.ValuesClause x) {

    }

    @Override
    public void endVisit(SQLSomeExpr x) {

    }

    @Override
    public boolean visit(SQLSomeExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAnyExpr x) {

    }

    @Override
    public boolean visit(SQLAnyExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAllExpr x) {

    }

    @Override
    public boolean visit(SQLAllExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLInSubQueryExpr x) {

    }

    @Override
    public boolean visit(SQLInSubQueryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLListExpr x) {

    }

    @Override
    public boolean visit(SQLListExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubqueryTableSource x) {

    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLTruncateStatement x) {

    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDefaultExpr x) {

    }

    @Override
    public boolean visit(SQLDefaultExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommentStatement x) {

    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLUseStatement x) {

    }

    @Override
    public boolean visit(SQLUseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddColumn x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropColumnItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropColumnItem x) {

    }

    @Override
    public boolean visit(SQLDropIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropIndexStatement x) {

    }

    @Override
    public boolean visit(SQLDropViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropViewStatement x) {

    }

    @Override
    public boolean visit(SQLSavePointStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLSavePointStatement x) {

    }

    @Override
    public boolean visit(SQLRollbackStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLRollbackStatement x) {

    }

    @Override
    public boolean visit(SQLReleaseSavePointStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLReleaseSavePointStatement x) {
    }

    @Override
    public boolean visit(SQLCommentHint x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommentHint x) {

    }

    @Override
    public void endVisit(SQLCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLCreateDatabaseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLAlterTableDropIndex x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropIndex x) {

    }

    @Override
    public void endVisit(SQLOver x) {
    }

    @Override
    public boolean visit(SQLOver x) {
        return true;
    }
    
    @Override
    public void endVisit(SQLKeep x) {
    }
    
    @Override
    public boolean visit(SQLKeep x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnPrimaryKey x) {

    }

    @Override
    public boolean visit(SQLColumnPrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnUniqueKey x) {

    }

    @Override
    public boolean visit(SQLColumnUniqueKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLWithSubqueryClause x) {
    }

    @Override
    public boolean visit(SQLWithSubqueryClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLWithSubqueryClause.Entry x) {
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        return true;
    }

    @Override
    public boolean visit(SQLCharacterDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLCharacterDataType x) {

    }

    @Override
    public void endVisit(SQLAlterTableAlterColumn x) {

    }

    @Override
    public boolean visit(SQLAlterTableAlterColumn x) {
        return true;
    }

    @Override
    public boolean visit(SQLCheck x) {
        return true;
    }

    @Override
    public void endVisit(SQLCheck x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropForeignKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropForeignKey x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropPrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropPrimaryKey x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableKeys x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableKeys x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableKeys x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableKeys x) {

    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableConstraint x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableConstraint x) {

    }

    @Override
    public boolean visit(SQLColumnCheck x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnCheck x) {

    }

    @Override
    public boolean visit(SQLExprHint x) {
        return true;
    }

    @Override
    public void endVisit(SQLExprHint x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropConstraint x) {

    }

    @Override
    public boolean visit(SQLUnique x) {
        for (SQLExpr column : x.getColumns()) {
            column.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(SQLUnique x) {

    }

    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateIndexStatement x) {

    }

    @Override
    public boolean visit(SQLPrimaryKeyImpl x) {
        return true;
    }

    @Override
    public void endVisit(SQLPrimaryKeyImpl x) {

    }

    @Override
    public boolean visit(SQLAlterTableRenameColumn x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRenameColumn x) {

    }

    @Override
    public boolean visit(SQLColumnReference x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnReference x) {

    }

    @Override
    public boolean visit(SQLForeignKeyImpl x) {
        return true;
    }

    @Override
    public void endVisit(SQLForeignKeyImpl x) {

    }

    @Override
    public boolean visit(SQLDropSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropSequenceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTriggerStatement x) {

    }

    @Override
    public void endVisit(SQLDropUserStatement x) {

    }

    @Override
    public boolean visit(SQLDropUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLExplainStatement x) {

    }

    @Override
    public boolean visit(SQLExplainStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLGrantStatement x) {

    }

    @Override
    public boolean visit(SQLGrantStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLDropDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddIndex x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddIndex x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddConstraint x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(SQLCreateTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLDropFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTableSpaceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTableSpaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropProcedureStatement x) {

    }

    @Override
    public boolean visit(SQLDropProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBooleanExpr x) {

    }

    @Override
    public boolean visit(SQLBooleanExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnionQueryTableSource x) {

    }

    @Override
    public boolean visit(SQLUnionQueryTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLTimestampExpr x) {

    }

    @Override
    public boolean visit(SQLTimestampExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLRevokeStatement x) {

    }

    @Override
    public boolean visit(SQLRevokeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBinaryExpr x) {

    }

    @Override
    public boolean visit(SQLBinaryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRename x) {

    }

    @Override
    public boolean visit(SQLAlterTableRename x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterViewRenameStatement x) {

    }

    @Override
    public boolean visit(SQLAlterViewRenameStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowTablesStatement x) {

    }

    @Override
    public boolean visit(SQLShowTablesStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRenamePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRenamePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableSetComment x) {

    }

    @Override
    public boolean visit(SQLAlterTableSetComment x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableSetLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableSetLifecycle x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableLifecycle x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableLifecycle x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableTouch x) {

    }

    @Override
    public boolean visit(SQLAlterTableTouch x) {
        return true;
    }

    @Override
    public void endVisit(SQLArrayExpr x) {

    }

    @Override
    public boolean visit(SQLArrayExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLOpenStatement x) {

    }

    @Override
    public boolean visit(SQLOpenStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLFetchStatement x) {

    }

    @Override
    public boolean visit(SQLFetchStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCloseStatement x) {

    }

    @Override
    public boolean visit(SQLCloseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLGroupingSetExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLGroupingSetExpr x) {

    }

    @Override
    public boolean visit(SQLIfStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement x) {

    }

    @Override
    public boolean visit(SQLIfStatement.Else x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement.Else x) {

    }

    @Override
    public boolean visit(SQLIfStatement.ElseIf x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement.ElseIf x) {

    }

    @Override
    public boolean visit(SQLLoopStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLLoopStatement x) {

    }

    @Override
    public boolean visit(SQLParameter x) {
        return true;
    }

    @Override
    public void endVisit(SQLParameter x) {

    }

    @Override
    public boolean visit(SQLCreateProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(SQLCreateFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLBlockStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBlockStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropKey x) {

    }

    @Override
    public boolean visit(SQLDeclareItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLDeclareItem x) {
    }

    @Override
    public boolean visit(SQLPartitionValue x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionValue x) {

    }

    @Override
    public boolean visit(SQLPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartition x) {

    }

    @Override
    public boolean visit(SQLPartitionByRange x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionByRange x) {

    }

    @Override
    public boolean visit(SQLPartitionByHash x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionByHash x) {

    }

    @Override
    public boolean visit(SQLPartitionByList x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionByList x) {

    }

    @Override
    public boolean visit(SQLSubPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartition x) {

    }

    @Override
    public boolean visit(SQLSubPartitionByHash x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartitionByHash x) {

    }

    @Override
    public boolean visit(SQLSubPartitionByList x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartitionByList x) {

    }

    @Override
    public boolean visit(SQLAlterDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableConvertCharSet x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableConvertCharSet x) {

    }

    @Override
    public boolean visit(SQLAlterTableReOrganizePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableReOrganizePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableCoalescePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableCoalescePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableTruncatePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableTruncatePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDiscardPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDiscardPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableImportPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableImportPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableAnalyzePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAnalyzePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableCheckPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableCheckPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableOptimizePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableOptimizePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRebuildPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRebuildPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRepairPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRepairPartition x) {

    }
    
    @Override
    public boolean visit(SQLSequenceExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(SQLSequenceExpr x) {
        
    }

    @Override
    public boolean visit(SQLMergeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLMergeStatement x) {
        
    }

    @Override
    public boolean visit(SQLMergeStatement.MergeUpdateClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLMergeStatement.MergeUpdateClause x) {
        
    }

    @Override
    public boolean visit(SQLMergeStatement.MergeInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLMergeStatement.MergeInsertClause x) {
        
    }

    @Override
    public boolean visit(SQLErrorLoggingClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLErrorLoggingClause x) {

    }

    @Override
    public boolean visit(SQLNullConstraint x) {
	return true;
    }

    @Override
    public void endVisit(SQLNullConstraint x) {
    }

    @Override
    public boolean visit(SQLCreateSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateSequenceStatement x) {
    }

    @Override
    public boolean visit(SQLDateExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLDateExpr x) {

    }

    @Override
    public boolean visit(SQLLimit x) {
        return true;
    }

    @Override
    public void endVisit(SQLLimit x) {

    }

    @Override
    public void endVisit(SQLStartTransactionStatement x) {

    }

    @Override
    public boolean visit(SQLStartTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDescribeStatement x) {

    }

    @Override
    public boolean visit(SQLDescribeStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLWhileStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLWhileStatement x) {

    }


    @Override
    public boolean visit(SQLDeclareStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDeclareStatement x) {

    }

    @Override
    public boolean visit(SQLReturnStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLReturnStatement x) {

    }

    @Override
    public boolean visit(SQLArgument x) {
        return true;
    }

    @Override
    public void endVisit(SQLArgument x) {

    }

    @Override
    public boolean visit(SQLCommitStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommitStatement x) {

    }

    @Override
    public boolean visit(SQLFlashbackExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLFlashbackExpr x) {

    }
}
