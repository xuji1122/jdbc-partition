package org.the.force.jdbc.partition.resource.db.mysql;

import org.the.force.jdbc.partition.common.ObjectedValue;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;
import org.the.force.jdbc.partition.resource.connection.ConnectionAdapter;
import org.the.force.jdbc.partition.resource.db.AbstractDatabaseMetaData;
import org.the.force.jdbc.partition.resource.db.DbMetaRsAdapter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.resultset.AbstractResultSet;
import org.the.force.jdbc.partition.rule.Partition;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xuji on 2017/7/2.
 */
public class MySqlDdMetaDataImpl extends AbstractDatabaseMetaData {

    private final LogicDbConfig logicDbConfig;
    private final ConnectionAdapter connectionAdapter;

    public MySqlDdMetaDataImpl(LogicDbConfig logicDbConfig, ConnectionAdapter connectionAdapter) {
        this.logicDbConfig = logicDbConfig;
        this.connectionAdapter = connectionAdapter;
    }

    public ResultSet getCatalogs() throws SQLException {
        final String logicDbName = logicDbConfig.getLogicDbName();
        List<String> logicDbNames = new ArrayList<>();
        logicDbNames.add(logicDbName);
        return simpleResultSet(logicDbNames, CATALOG_HEADER[0]);
    }

    public ResultSet getSchemas() throws SQLException {
        return getSchemas(null, null);
    }

    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return emptyResultSet(new MySqlDbMetaRsMeta(SCHAMA_HEADER));
    }

    public ResultSet getTableTypes() throws SQLException {
        List<String> list = new ArrayList<>();
        list.add("TABLE");
        return simpleResultSet(list, TABLE_TYPE_HEADER[0]);
    }

    private ResultSet simpleResultSet(List<String> list, String label) {
        Iterator<String> iterator = list.iterator();
        return new AbstractResultSet() {
            public ResultSetMetaData getMetaData() throws SQLException {
                return new MySqlDbMetaRsMeta(new String[] {label});
            }

            public boolean next() throws SQLException {
                return iterator.hasNext();
            }

            public String getString(int columnIndex) throws SQLException {
                if (columnIndex == 1) {
                    return iterator.next();
                } else {
                    return null;
                }
            }

            public String getString(String columnLabel) throws SQLException {
                if (columnLabel.equalsIgnoreCase(label)) {
                    return getString(1);
                } else {
                    return null;
                }
            }

            public String getObject(String columnLabel) throws SQLException {
                return getString(columnLabel);
            }

            public String getObject(int columnIndex) throws SQLException {
                return getString(columnIndex);
            }
        };
    }

    private ResultSet emptyResultSet(ResultSetMetaData rsd) {
        return new AbstractResultSet() {
            public ResultSetMetaData getMetaData() throws SQLException {
                return rsd;
            }

            public boolean next() throws SQLException {
                return false;
            }
        };
    }

    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String types[]) throws SQLException {
        final String logicDbName = logicDbConfig.getLogicDbName();
        final List<String> result = matchTables(catalog, schemaPattern, tableNamePattern, types);
        final AtomicInteger index = new AtomicInteger(-1);
        return new AbstractResultSet() {
            public ResultSetMetaData getMetaData() throws SQLException {
                return new MySqlDbMetaRsMeta(TABLE_HEADER);
            }

            public boolean next() throws SQLException {
                index.addAndGet(1);
                return index.get() < result.size();
            }

            public String getString(int columnIndex) throws SQLException {
                if (columnIndex == 1) {
                    return logicDbName;
                }
                if (columnIndex == 3) {
                    return result.get(index.get());
                }
                if (columnIndex == 4) {
                    return "TABLE";
                }
                return null;
            }

            public String getString(String columnLabel) throws SQLException {
                if (columnLabel.equalsIgnoreCase(TABLE_TYPE_HEADER[0])) {
                    return getString(1);
                } else if (columnLabel.equalsIgnoreCase(TABLE_TYPE_HEADER[2])) {
                    return getString(3);
                } else if (columnLabel.equalsIgnoreCase(TABLE_TYPE_HEADER[3])) {
                    return getString(4);
                } else {
                    return null;
                }
            }

            public String getObject(String columnLabel) throws SQLException {
                return getString(columnLabel);
            }

            public String getObject(int columnIndex) throws SQLException {
                return getString(columnIndex);
            }
        };
    }

    private List<String> matchTables(String catalog, String schemaPattern, String tableNamePattern, String types[]) {
        final String logicDbName = logicDbConfig.getLogicDbName();
        final List<String> result = new ArrayList<>();
        if (catalog == null || catalog.length() < 1 || catalog.equalsIgnoreCase(logicDbName)) {
            Iterator<String> iterator = logicDbConfig.getLogicTables().iterator();
            while (iterator.hasNext()) {
                String tableName = iterator.next();
                if (tableNamePattern == null || tableNamePattern.length() < 1 || tableName.equalsIgnoreCase(tableNamePattern)) {
                    boolean match = false;
                    if (types == null || types.length < 1) {
                        match = true;
                    } else {
                        for (String type : types) {
                            if (type.equalsIgnoreCase("TABLE")) {
                                match = true;
                                break;
                            }
                        }
                    }
                    if (match) {
                        result.add(tableName);
                    }
                }
            }
        }
        return result;
    }

    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        final String logicDbName = logicDbConfig.getLogicDbName();
        List<String> tables = matchTables(catalog, schemaPattern, tableNamePattern, null);
        if (tables.isEmpty()) {
            return emptyResultSet(new MySqlDbMetaRsMeta(COLUMN_HEADER, COLUMN_HEADER_TYPES));
        }
        final Iterator<String> tableIterator = tables.iterator();
        final ObjectedValue<String> objectedValue = new ObjectedValue(null);
        DbMetaRsAdapter dbMetaRsAdapter = new DbMetaRsAdapter(null) {
            protected ResultSet checkResultSet(ResultSet rs, boolean checkNext) throws SQLException {
                if (rs == null || (checkNext && !rs.next())) {
                    if (!tableIterator.hasNext()) {
                        return null;
                    }
                    String tableName = tableIterator.next();
                    objectedValue.setValue(tableName);
                    Partition partition = logicDbConfig.getLogicTableManager(tableName).getLogicTableConfig()[0].getPartitions().first();
                    Connection connection = connectionAdapter.getConnection(partition.getPhysicDbName());
                    DatabaseMetaData dbmd = connection.getMetaData();
                    ResultSet newResultSet = dbmd.getColumns(partition.getPhysicDbName(), null, partition.getPhysicTableName(), null);
                    return newResultSet;
                }
                return rs;
            }
        };
        dbMetaRsAdapter.put(1, new ObjectedValue<>(logicDbName));
        dbMetaRsAdapter.put(3, objectedValue);
        dbMetaRsAdapter.put(INDEX_HEADER[0], 1);
        dbMetaRsAdapter.put(INDEX_HEADER[2], 3);
        return dbMetaRsAdapter;
    }

    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        final String logicDbName = logicDbConfig.getLogicDbName();
        List<String> tables = matchTables(catalog, schema, table, null);
        if (tables.isEmpty()) {
            return emptyResultSet(new MySqlDbMetaRsMeta(INDEX_HEADER, INDEX_HEADER_TYPES));
        }
        final Iterator<String> tableIterator = tables.iterator();
        final ObjectedValue<String> objectedValue = new ObjectedValue(null);
        DbMetaRsAdapter dbMetaRsAdapter = new DbMetaRsAdapter(null) {
            protected ResultSet checkResultSet(ResultSet rs, boolean checkNext) throws SQLException {
                if (rs == null || (checkNext && !rs.next())) {
                    if (!tableIterator.hasNext()) {
                        return null;
                    }
                    String tableName = tableIterator.next();
                    objectedValue.setValue(tableName);
                    Partition partition = logicDbConfig.getLogicTableManager(tableName).getLogicTableConfig()[0].getPartitions().first();
                    Connection connection = connectionAdapter.getConnection(partition.getPhysicDbName());
                    DatabaseMetaData dbmd = connection.getMetaData();
                    ResultSet newResultSet = dbmd.getIndexInfo(partition.getPhysicDbName(), null, partition.getPhysicTableName(), unique, approximate);
                    return newResultSet;
                }
                return rs;
            }
        };
        dbMetaRsAdapter.put(1, new ObjectedValue<>(logicDbName));
        dbMetaRsAdapter.put(3, objectedValue);
        dbMetaRsAdapter.put(INDEX_HEADER[0], 1);
        dbMetaRsAdapter.put(INDEX_HEADER[2], 3);
        return dbMetaRsAdapter;
    }

    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        final String logicDbName = logicDbConfig.getLogicDbName();
        List<String> tables = matchTables(catalog, schema, table, null);
        if (tables.isEmpty()) {
            return emptyResultSet(new MySqlDbMetaRsMeta(PK_HEADER, PK_HEADER_TYPES));
        }
        final Iterator<String> tableIterator = tables.iterator();
        final ObjectedValue<String> objectedValue = new ObjectedValue(null);
        DbMetaRsAdapter dbMetaRsAdapter = new DbMetaRsAdapter(null) {
            protected ResultSet checkResultSet(ResultSet rs, boolean checkNext) throws SQLException {
                if (rs == null || (checkNext && !rs.next())) {
                    if (!tableIterator.hasNext()) {
                        return null;
                    }
                    String tableName = tableIterator.next();
                    objectedValue.setValue(tableName);
                    Partition partition = logicDbConfig.getLogicTableManager(tableName).getLogicTableConfig()[0].getPartitions().first();
                    Connection connection = connectionAdapter.getConnection(partition.getPhysicDbName());
                    DatabaseMetaData dbmd = connection.getMetaData();
                    ResultSet newResultSet = dbmd.getPrimaryKeys(partition.getPhysicDbName(), null, partition.getPhysicTableName());
                    return newResultSet;
                }
                return rs;
            }
        };
        dbMetaRsAdapter.put(1, new ObjectedValue<>(logicDbName));
        dbMetaRsAdapter.put(3, objectedValue);
        dbMetaRsAdapter.put(INDEX_HEADER[0], 1);
        dbMetaRsAdapter.put(INDEX_HEADER[2], 3);
        return dbMetaRsAdapter;
    }


    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }



}
