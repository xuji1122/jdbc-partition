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
package org.the.force.thirdparty.druid.sql.dialect.oracle.visitor;

import org.the.force.thirdparty.druid.sql.ast.statement.SQLMergeStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.OracleDataTypeTimestamp;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.CycleClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.ModelClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.SampleClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.SearchClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleArgumentExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleIsSetExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleRangeExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterIndexStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterProcedureStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterSynonymStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceAddDataFile;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterTriggerStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleAlterViewStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleContinueStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCreateDatabaseDbLinkStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCreateIndexStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCreatePackageStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleDropDbLinkStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExecuteImmediateStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExitStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExprStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleFileSpecification;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleLabelStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleRaiseStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSupplementalIdKey;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSupplementalLogGrp;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitorAdapter;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleSizeExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleForStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleUnique;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;

public class OracleASTVisitorAdapter extends SQLASTVisitorAdapter implements OracleASTVisitor {

    @Override
    public void endVisit(OraclePLSQLCommitStatement astNode) {

    }

    @Override
    public void endVisit(OracleAnalytic x) {

    }

    @Override
    public void endVisit(OracleAnalyticWindowing x) {

    }

    @Override
    public void endVisit(OracleDbLinkExpr x) {

    }

    @Override
    public void endVisit(OracleDeleteStatement x) {

    }

    @Override
    public void endVisit(OracleIntervalExpr x) {

    }

    @Override
    public void endVisit(OracleOuterExpr x) {

    }

    @Override
    public void endVisit(OracleSelectJoin x) {

    }

    @Override
    public void endVisit(OracleSelectPivot x) {

    }

    @Override
    public void endVisit(OracleSelectPivot.Item x) {

    }

    @Override
    public void endVisit(OracleSelectRestriction.CheckOption x) {

    }

    @Override
    public void endVisit(OracleSelectRestriction.ReadOnly x) {

    }

    @Override
    public void endVisit(OracleSelectSubqueryTableSource x) {

    }

    @Override
    public void endVisit(OracleSelectUnPivot x) {

    }

    @Override
    public void endVisit(OracleUpdateStatement x) {

    }

    @Override
    public boolean visit(OraclePLSQLCommitStatement astNode) {

        return true;
    }

    @Override
    public boolean visit(OracleAnalytic x) {

        return true;
    }

    @Override
    public boolean visit(OracleAnalyticWindowing x) {

        return true;
    }

    @Override
    public boolean visit(OracleDbLinkExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {

        return true;
    }

    @Override
    public boolean visit(OracleIntervalExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleOuterExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectJoin x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectPivot x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectPivot.Item x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectRestriction.CheckOption x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectRestriction.ReadOnly x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectSubqueryTableSource x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectUnPivot x) {

        return true;
    }

    @Override
    public boolean visit(OracleUpdateStatement x) {

        return true;
    }

    @Override
    public boolean visit(SampleClause x) {

        return true;
    }

    @Override
    public void endVisit(SampleClause x) {

    }

    @Override
    public boolean visit(OracleSelectTableReference x) {

        return true;
    }

    @Override
    public void endVisit(OracleSelectTableReference x) {

    }

    @Override
    public boolean visit(PartitionExtensionClause x) {

        return true;
    }

    @Override
    public void endVisit(PartitionExtensionClause x) {

    }

    @Override
    public boolean visit(OracleWithSubqueryEntry x) {

        return true;
    }

    @Override
    public void endVisit(OracleWithSubqueryEntry x) {

    }

    @Override
    public boolean visit(SearchClause x) {

        return true;
    }

    @Override
    public void endVisit(SearchClause x) {

    }

    @Override
    public boolean visit(CycleClause x) {

        return true;
    }

    @Override
    public void endVisit(CycleClause x) {

    }

    @Override
    public boolean visit(OracleBinaryFloatExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleBinaryFloatExpr x) {

    }

    @Override
    public boolean visit(OracleBinaryDoubleExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleBinaryDoubleExpr x) {

    }

    @Override
    public boolean visit(OracleCursorExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleCursorExpr x) {

    }

    @Override
    public boolean visit(OracleIsSetExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleIsSetExpr x) {

    }

    @Override
    public boolean visit(ModelClause.ReturnRowsClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause.ReturnRowsClause x) {

    }

    @Override
    public boolean visit(ModelClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause x) {

    }

    @Override
    public boolean visit(ModelClause.MainModelClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause.MainModelClause x) {

    }

    @Override
    public boolean visit(ModelClause.ModelColumnClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause.ModelColumnClause x) {

    }

    @Override
    public boolean visit(ModelClause.QueryPartitionClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause.QueryPartitionClause x) {

    }

    @Override
    public boolean visit(ModelClause.ModelColumn x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause.ModelColumn x) {

    }

    @Override
    public boolean visit(ModelClause.ModelRulesClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause.ModelRulesClause x) {

    }

    @Override
    public boolean visit(ModelClause.CellAssignmentItem x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause.CellAssignmentItem x) {

    }

    @Override
    public boolean visit(ModelClause.CellAssignment x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause.CellAssignment x) {

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
    public boolean visit(OracleReturningClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleReturningClause x) {

    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleInsertStatement x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement.InsertIntoClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement.InsertIntoClause x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement.ConditionalInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement.ConditionalInsertClause x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement.ConditionalInsertClauseItem x) {
        return true;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement.ConditionalInsertClauseItem x) {

    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        return true;
    }

    @Override
    public void endVisit(OracleSelectQueryBlock x) {

    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleLockTableStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterSessionStatement x) {

    }

    @Override
    public boolean visit(OracleExprStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExprStatement x) {

    }

    @Override
    public boolean visit(OracleDatetimeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleDatetimeExpr x) {

    }

    @Override
    public boolean visit(OracleSysdateExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleSysdateExpr x) {

    }

    @Override
    public boolean visit(OracleExceptionStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExceptionStatement x) {

    }

    @Override
    public boolean visit(OracleExceptionStatement.Item x) {
        return true;
    }

    @Override
    public void endVisit(OracleExceptionStatement.Item x) {

    }

    @Override
    public boolean visit(OracleArgumentExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleArgumentExpr x) {

    }

    @Override
    public boolean visit(OracleSetTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleSetTransactionStatement x) {

    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExplainStatement x) {

    }

    @Override
    public boolean visit(OracleAlterProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterProcedureStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableDropPartition x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableDropPartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableTruncatePartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.TableSpaceItem x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableModify x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableModify x) {

    }

    @Override
    public boolean visit(OracleCreateIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreateIndexStatement x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement x) {

    }

    @Override
    public boolean visit(OracleForStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleForStatement x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement.Rebuild x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement.Rebuild x) {

    }

    @Override
    public boolean visit(OracleRangeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleRangeExpr x) {

    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(OraclePrimaryKey x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreateTableStatement x) {

    }

    @Override
    public boolean visit(OracleStorageClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleStorageClause x) {

    }

    @Override
    public boolean visit(OracleGotoStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleGotoStatement x) {

    }

    @Override
    public boolean visit(OracleLabelStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleLabelStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTriggerStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSynonymStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterSynonymStatement x) {

    }

    @Override
    public boolean visit(OracleAlterViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterViewStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableMoveTablespace x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTableMoveTablespace x) {

    }

    @Override
    public boolean visit(OracleSizeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleSizeExpr x) {

    }

    @Override
    public boolean visit(OracleFileSpecification x) {
        return true;
    }

    @Override
    public void endVisit(OracleFileSpecification x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceAddDataFile x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTablespaceAddDataFile x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(OracleExitStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExitStatement x) {

    }

    @Override
    public boolean visit(OracleContinueStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleContinueStatement x) {

    }

    @Override
    public boolean visit(OracleRaiseStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleRaiseStatement x) {

    }

    @Override
    public boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreateDatabaseDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDropDbLinkStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleDropDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDataTypeTimestamp x) {
        return true;
    }

    @Override
    public void endVisit(OracleDataTypeTimestamp x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalYear x) {
        return true;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalYear x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalDay x) {
        return true;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalDay x) {

    }

    @Override
    public boolean visit(OracleUsingIndexClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleUsingIndexClause x) {

    }

    @Override
    public boolean visit(OracleLobStorageClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleLobStorageClause x) {

    }

    @Override
    public boolean visit(OracleUnique x) {
        return true;
    }

    @Override
    public void endVisit(OracleUnique x) {

    }

    @Override
    public boolean visit(OracleForeignKey x) {
        return true;
    }

    @Override
    public void endVisit(OracleForeignKey x) {

    }

    @Override
    public boolean visit(OracleCheck x) {
        return true;
    }

    @Override
    public void endVisit(OracleCheck x) {

    }

    @Override
    public boolean visit(OracleSupplementalIdKey x) {
        return true;
    }

    @Override
    public void endVisit(OracleSupplementalIdKey x) {

    }

    @Override
    public boolean visit(OracleSupplementalLogGrp x) {
        return true;
    }

    @Override
    public void endVisit(OracleSupplementalLogGrp x) {

    }

    public boolean visit(OracleCreateTableStatement.Organization x) {
        return true;
    }

    public void endVisit(OracleCreateTableStatement.Organization x) {

    }

    public boolean visit(OracleCreateTableStatement.OracleExternalRecordFormat x) {
        return true;
    }

    public void endVisit(OracleCreateTableStatement.OracleExternalRecordFormat x) {

    }
    public boolean visit(OracleCreateTableStatement.OIDIndex x) {
        return true;
    }

    public void endVisit(OracleCreateTableStatement.OIDIndex x) {

    }

    @Override
    public boolean visit(OracleCreatePackageStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleCreatePackageStatement x) {

    }

    @Override
    public boolean visit(OracleExecuteImmediateStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExecuteImmediateStatement x) {

    }
}
