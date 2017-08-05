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


    public boolean nullsAreSortedHigh() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean nullsAreSortedLow() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean nullsAreSortedAtStart() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean nullsAreSortedAtEnd() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getDatabaseProductName() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getDatabaseProductVersion() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getDriverName() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getDriverVersion() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public int getDriverMajorVersion() {
        return 0;
    }


    public int getDriverMinorVersion() {
        return 0;
    }


    public boolean usesLocalFiles() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean usesLocalFilePerTable() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean storesUpperCaseIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean storesLowerCaseIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean storesMixedCaseIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getIdentifierQuoteString() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getSQLKeywords() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getNumericFunctions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getStringFunctions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getSystemFunctions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getTimeDateFunctions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getSearchStringEscape() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getExtraNameCharacters() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsColumnAliasing() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean nullPlusNonNullIsNull() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsConvert() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsTableCorrelationNames() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsExpressionsInOrderBy() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsOrderByUnrelated() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsGroupBy() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsGroupByUnrelated() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsGroupByBeyondSelect() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsLikeEscapeClause() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsMultipleResultSets() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsMultipleTransactions() throws SQLException {
       return false;
    }


    public boolean supportsNonNullableColumns() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsMinimumSQLGrammar() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsCoreSQLGrammar() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsExtendedSQLGrammar() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsANSI92FullSQL() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsOuterJoins() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsFullOuterJoins() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsLimitedOuterJoins() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getSchemaTerm() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getProcedureTerm() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getCatalogTerm() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean isCatalogAtStart() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getCatalogSeparator() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSchemasInDataManipulation() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsPositionedDelete() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsPositionedUpdate() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSelectForUpdate() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsStoredProcedures() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSubqueriesInComparisons() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSubqueriesInExists() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSubqueriesInIns() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsCorrelatedSubqueries() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsUnion() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsUnionAll() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }


    public int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }


    public int getMaxColumnNameLength() throws SQLException {
        return 0;
    }


    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }


    public int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }


    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }


    public int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }


    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }


    public int getMaxConnections() throws SQLException {
        return 0;
    }


    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }


    public int getMaxIndexLength() throws SQLException {
        return 0;
    }


    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }


    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }


    public int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }


    public int getMaxRowSize() throws SQLException {
        return 0;
    }


    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public int getMaxStatementLength() throws SQLException {
        return 0;
    }


    public int getMaxStatements() throws SQLException {
        return 0;
    }


    public int getMaxTableNameLength() throws SQLException {
        return 0;
    }


    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }


    public int getMaxUserNameLength() throws SQLException {
        return 0;
    }


    public int getDefaultTransactionIsolation() throws SQLException {
        return 0;
    }


    public boolean supportsTransactions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getSchemas() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getCatalogs() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getTableTypes() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable)
        throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getTypeInfo() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsResultSetType(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean ownDeletesAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean ownInsertsAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean othersDeletesAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean othersInsertsAreVisible(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean updatesAreDetected(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean deletesAreDetected(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean insertsAreDetected(int type) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsBatchUpdates() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Connection getConnection() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsSavepoints() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsNamedParameters() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }


    public boolean supportsGetGeneratedKeys() throws SQLException {
       return false;
    }


    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public int getResultSetHoldability() throws SQLException {
        return 0;
    }


    public int getDatabaseMajorVersion() throws SQLException {
        return 0;
    }


    public int getDatabaseMinorVersion() throws SQLException {
        return 0;
    }


    public int getJDBCMajorVersion() throws SQLException {
        return 0;
    }


    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }


    public int getSQLStateType() throws SQLException {
        return 0;
    }


    public boolean locatorsUpdateCopy() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsStatementPooling() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getClientInfoProperties() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }
}
