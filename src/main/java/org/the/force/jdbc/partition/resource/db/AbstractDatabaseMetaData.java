package org.the.force.jdbc.partition.resource.db;

import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/7/2.
 */
public abstract class AbstractDatabaseMetaData implements DatabaseMetaData {

    protected static final String[] CATALOG_HEADER = new String[] {"TABLE_CAT"};

    protected static final String[] TABLE_TYPE_HEADER = new String[] {"TABLE_TYPE"};

    protected static final String[] SCHAMA_HEADER = new String[] {"TABLE_SCHEM", "TABLE_CATALOG"};

    protected static final String[] TABLE_HEADER =
        new String[] {"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS", "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION"};

    protected static final String[] COLUMN_HEADER =
        new String[] {"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "NUM_PREC_RADIX",
            "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATALOG", "SCOPE_SCHEMA",
            "SCOPE_TABLE", "SOURCE_DATA_TYPE", "IS_AUTOINCREMENT", "IS_GENERATEDCOLUMN"};

    protected static final int[] COLUMN_HEADER_TYPES =
        new int[] {Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR, Types.INTEGER, Types.CHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.CHAR,
            Types.CHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR, Types.SMALLINT, Types.CHAR, Types.CHAR};

    protected static final String[] PK_HEADER = new String[] {"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "KEY_SEQ", "PK_NAME"};

    protected static final int[] PK_HEADER_TYPES = new int[] {Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR, Types.SMALLINT, Types.CHAR};

    protected static final String[] INDEX_HEADER =
        new String[] {"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "INDEX_QUALIFIER", "INDEX_NAME", "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY", "PAGES",
            "FILTER_CONDITION"};
    protected static final int[] INDEX_HEADER_TYPES =
        new int[] {Types.CHAR, Types.CHAR, Types.CHAR, Types.BOOLEAN, Types.CHAR, Types.CHAR, Types.SMALLINT, Types.SMALLINT, Types.CHAR, Types.CHAR, Types.INTEGER, Types.INTEGER,
            Types.CHAR};

    public boolean allProceduresAreCallable() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public boolean allTablesAreSelectable() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public String getURL() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public String getUserName() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public boolean isReadOnly() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getDriverName() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getDriverVersion() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public int getDriverMajorVersion() {
        return 0;
    }

    @Override
    public int getDriverMinorVersion() {
        return 0;
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getStringFunctions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return 0;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable)
        throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return 0;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }
}
