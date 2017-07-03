package org.the.force.jdbc.partition.resource.resultset;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/2.
 */
public abstract class WrappedResultSet implements ResultSet {


    private ResultSetMetaData resultSetMetaData;

    private List<ResultSet> rsList = new ArrayList<>();

    private int rsIndex = -1;

    public WrappedResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            rsList.add(resultSet);
            rsIndex = 0;
        }
    }

    public WrappedResultSet(ResultSet... resultSets) {
        for (ResultSet rs : resultSets) {
            rsIndex = 0;
            rsList.add(rs);
        }
    }

    protected final ResultSet getCurrentResultSet() throws SQLException {
        if (rsIndex > -1 && rsIndex < rsList.size()) {
            return rsList.get(rsIndex);
        }
        return null;
    }

    public final ResultSetMetaData getMetaData() throws SQLException {
        if (resultSetMetaData == null) {
            ResultSet currentResultSet = getCurrentResultSet();
            if (currentResultSet != null) {
                resultSetMetaData = checkMetaData(currentResultSet);
            } else {
                ResultSet rs = checkResultSet(null, false);
                if (rs != null) {
                    rsList.add(rs);
                    rsIndex++;
                    resultSetMetaData = checkMetaData(rs);
                }
            }
        }
        return resultSetMetaData;
    }

    /**
     * 不能覆盖
     *
     * @return
     * @throws SQLException
     */
    public final boolean next() throws SQLException {
        ResultSet currentResultSet = getCurrentResultSet();
        ResultSet rs = checkResultSet(currentResultSet, true);
        if (rs == null) {
            rsIndex = rsList.size();
            return false;
        } else {
            if (rs != currentResultSet) {
                //new
                rsList.add(rs);
                rsIndex++;
                return next();
            }
            return true;
        }
    }

    /**
     * 默认的实现，可以覆盖
     *
     * @param rs
     * @param checkNext
     * @return
     * @throws SQLException
     */
    protected ResultSet checkResultSet(ResultSet rs, boolean checkNext) throws SQLException {
        if (checkNext) {
            if (rs.next()) {
                return rs;
            } else {
                return null;
            }
        } else {
            return rs;
        }
    }

    /**
     * 默认的实现，可以覆盖
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    protected ResultSetMetaData checkMetaData(ResultSet rs) throws SQLException {
        return rs.getMetaData();
    }

    public boolean isBeforeFirst() throws SQLException {
        return rsIndex == 0 && rsList.get(0).isBeforeFirst();
    }


    public boolean isAfterLast() throws SQLException {
        if (rsList.isEmpty()) {
            return false;
        }
        return rsIndex >= rsList.size() && rsList.get(rsList.size() - 1).isAfterLast();
    }


    public boolean isFirst() throws SQLException {
        if (rsList.isEmpty()) {
            return false;
        }
        return rsIndex == 0 && rsList.get(0).isFirst();
    }


    public boolean isLast() throws SQLException {
        if (rsList.isEmpty()) {
            return false;
        }
        return (rsIndex == rsList.size() - 1) && getCurrentResultSet().isLast();
    }


    public void beforeFirst() throws SQLException {
        rsIndex = -1;
        for (ResultSet rs : rsList) {
            rs.beforeFirst();
        }
    }


    public void afterLast() throws SQLException {
        rsIndex = rsList.size();
        for (ResultSet rs : rsList) {
            rs.afterLast();
        }
    }


    public boolean first() throws SQLException {
        if (rsList.isEmpty()) {
            return false;
        }
        rsIndex = 0;
        for (int i = 1; i < rsList.size(); i++) {
            rsList.get(i).beforeFirst();
        }
        return rsList.get(0).first();
    }


    public boolean last() throws SQLException {
        if (rsList.isEmpty()) {
            return false;
        }
        rsIndex = rsList.size() - 1;
        for (int i = 0; i < rsList.size() - 1; i++) {
            rsList.get(i).afterLast();
        }
        return rsList.get(rsIndex).last();
    }


    public void close() throws SQLException {
        if (getCurrentResultSet() != null) {
            getCurrentResultSet().close();
        }
    }

    public boolean wasNull() throws SQLException {
        return getCurrentResultSet().wasNull();
    }


    public String getString(int columnIndex) throws SQLException {
        return getCurrentResultSet().getString(columnIndex);
    }


    public boolean getBoolean(int columnIndex) throws SQLException {
        return getCurrentResultSet().getBoolean(columnIndex);
    }


    public byte getByte(int columnIndex) throws SQLException {
        return getCurrentResultSet().getByte(columnIndex);
    }


    public short getShort(int columnIndex) throws SQLException {
        return getCurrentResultSet().getShort(columnIndex);
    }


    public int getInt(int columnIndex) throws SQLException {
        return getCurrentResultSet().getInt(columnIndex);
    }


    public long getLong(int columnIndex) throws SQLException {
        return getCurrentResultSet().getLong(columnIndex);
    }


    public float getFloat(int columnIndex) throws SQLException {
        return getCurrentResultSet().getFloat(columnIndex);
    }


    public double getDouble(int columnIndex) throws SQLException {
        return 0;
    }


    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return getBigDecimal(columnIndex, scale);
    }


    public byte[] getBytes(int columnIndex) throws SQLException {
        return getCurrentResultSet().getBytes(columnIndex);
    }


    public Date getDate(int columnIndex) throws SQLException {
        return getCurrentResultSet().getDate(columnIndex);
    }


    public Time getTime(int columnIndex) throws SQLException {
        return getCurrentResultSet().getTime(columnIndex);
    }


    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getCurrentResultSet().getTimestamp(columnIndex);
    }


    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return getCurrentResultSet().getAsciiStream(columnIndex);
    }


    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return getCurrentResultSet().getUnicodeStream(columnIndex);
    }


    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return getCurrentResultSet().getBinaryStream(columnIndex);
    }


    public String getString(String columnLabel) throws SQLException {
        return getCurrentResultSet().getString(columnLabel);
    }


    public boolean getBoolean(String columnLabel) throws SQLException {
        return getCurrentResultSet().getBoolean(columnLabel);
    }


    public byte getByte(String columnLabel) throws SQLException {
        return getCurrentResultSet().getByte(columnLabel);
    }


    public short getShort(String columnLabel) throws SQLException {
        return getCurrentResultSet().getShort(columnLabel);
    }


    public int getInt(String columnLabel) throws SQLException {
        return getCurrentResultSet().getInt(columnLabel);
    }


    public long getLong(String columnLabel) throws SQLException {
        return getCurrentResultSet().getLong(columnLabel);
    }


    public float getFloat(String columnLabel) throws SQLException {
        return getCurrentResultSet().getFloat(columnLabel);
    }


    public double getDouble(String columnLabel) throws SQLException {
        return getCurrentResultSet().getDouble(columnLabel);
    }


    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getCurrentResultSet().getBigDecimal(columnLabel, scale);
    }


    public byte[] getBytes(String columnLabel) throws SQLException {
        return getCurrentResultSet().getBytes(columnLabel);
    }


    public Date getDate(String columnLabel) throws SQLException {
        return getCurrentResultSet().getDate(columnLabel);
    }


    public Time getTime(String columnLabel) throws SQLException {
        return null;
    }


    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getCurrentResultSet().getTimestamp(columnLabel);
    }


    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getCurrentResultSet().getAsciiStream(columnLabel);
    }


    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getCurrentResultSet().getUnicodeStream(columnLabel);
    }


    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getCurrentResultSet().getBinaryStream(columnLabel);
    }


    public SQLWarning getWarnings() throws SQLException {
        return getCurrentResultSet().getWarnings();
    }


    public void clearWarnings() throws SQLException {
        getCurrentResultSet().clearWarnings();
    }


    public String getCursorName() throws SQLException {
        return getCurrentResultSet().getCursorName();
    }



    public Object getObject(int columnIndex) throws SQLException {
        return getCurrentResultSet().getObject(columnIndex);
    }


    public Object getObject(String columnLabel) throws SQLException {
        return getCurrentResultSet().getObject(columnLabel);
    }


    public int findColumn(String columnLabel) throws SQLException {
        return 0;
    }


    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return getCurrentResultSet().getCharacterStream(columnIndex);
    }


    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCurrentResultSet().getCharacterStream(columnLabel);
    }


    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return getCurrentResultSet().getBigDecimal(columnIndex);
    }


    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getCurrentResultSet().getBigDecimal(columnLabel);
    }



    public int getRow() throws SQLException {
        return getCurrentResultSet().getRow();
    }


    public boolean absolute(int row) throws SQLException {
        return getCurrentResultSet().absolute(row);
    }


    public boolean relative(int rows) throws SQLException {
        return getCurrentResultSet().relative(rows);
    }


    public boolean previous() throws SQLException {
        return getCurrentResultSet().previous();
    }


    public void setFetchDirection(int direction) throws SQLException {
        getCurrentResultSet().setFetchDirection(direction);
    }


    public int getFetchDirection() throws SQLException {
        return getCurrentResultSet().getFetchDirection();
    }


    public void setFetchSize(int rows) throws SQLException {
        getCurrentResultSet().setFetchDirection(rows);
    }


    public int getFetchSize() throws SQLException {
        return getCurrentResultSet().getFetchSize();
    }


    public int getType() throws SQLException {
        return getCurrentResultSet().getType();
    }


    public int getConcurrency() throws SQLException {
        return getCurrentResultSet().getConcurrency();
    }


    public boolean rowUpdated() throws SQLException {
        return getCurrentResultSet().rowUpdated();
    }


    public boolean rowInserted() throws SQLException {
        return getCurrentResultSet().rowInserted();
    }


    public boolean rowDeleted() throws SQLException {
        return getCurrentResultSet().rowDeleted();
    }


    public void updateNull(int columnIndex) throws SQLException {
        getCurrentResultSet().updateNull(columnIndex);
    }


    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        getCurrentResultSet().updateBoolean(columnIndex, x);
    }


    public void updateByte(int columnIndex, byte x) throws SQLException {
        getCurrentResultSet().updateByte(columnIndex, x);
    }


    public void updateShort(int columnIndex, short x) throws SQLException {
        getCurrentResultSet().updateShort(columnIndex, x);
    }


    public void updateInt(int columnIndex, int x) throws SQLException {
        getCurrentResultSet().updateInt(columnIndex, x);
    }


    public void updateLong(int columnIndex, long x) throws SQLException {
        getCurrentResultSet().updateLong(columnIndex, x);
    }


    public void updateFloat(int columnIndex, float x) throws SQLException {
        getCurrentResultSet().updateFloat(columnIndex, x);
    }


    public void updateDouble(int columnIndex, double x) throws SQLException {
        getCurrentResultSet().updateDouble(columnIndex, x);
    }


    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        getCurrentResultSet().updateBigDecimal(columnIndex, x);
    }


    public void updateString(int columnIndex, String x) throws SQLException {
        getCurrentResultSet().updateString(columnIndex, x);
    }


    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        getCurrentResultSet().updateBytes(columnIndex, x);
    }


    public void updateDate(int columnIndex, Date x) throws SQLException {
        getCurrentResultSet().updateDate(columnIndex, x);
    }


    public void updateTime(int columnIndex, Time x) throws SQLException {
        getCurrentResultSet().updateTime(columnIndex, x);
    }


    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        getCurrentResultSet().updateTimestamp(columnIndex, x);
    }


    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        getCurrentResultSet().updateAsciiStream(columnIndex, x, length);
    }


    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        getCurrentResultSet().updateBinaryStream(columnIndex, x, length);
    }


    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        getCurrentResultSet().updateCharacterStream(columnIndex, x, length);
    }


    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        getCurrentResultSet().updateObject(columnIndex, x, scaleOrLength);
    }


    public void updateObject(int columnIndex, Object x) throws SQLException {
        getCurrentResultSet().updateObject(columnIndex, x);
    }


    public void updateNull(String columnLabel) throws SQLException {
        getCurrentResultSet().updateNull(columnLabel);
    }


    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        getCurrentResultSet().updateBoolean(columnLabel, x);
    }


    public void updateByte(String columnLabel, byte x) throws SQLException {
        getCurrentResultSet().updateByte(columnLabel, x);
    }


    public void updateShort(String columnLabel, short x) throws SQLException {
        getCurrentResultSet().updateShort(columnLabel, x);
    }


    public void updateInt(String columnLabel, int x) throws SQLException {
        getCurrentResultSet().updateInt(columnLabel, x);
    }


    public void updateLong(String columnLabel, long x) throws SQLException {
        getCurrentResultSet().updateLong(columnLabel, x);
    }


    public void updateFloat(String columnLabel, float x) throws SQLException {
        getCurrentResultSet().updateFloat(columnLabel, x);
    }


    public void updateDouble(String columnLabel, double x) throws SQLException {
        getCurrentResultSet().updateDouble(columnLabel, x);
    }


    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        getCurrentResultSet().updateBigDecimal(columnLabel, x);
    }


    public void updateString(String columnLabel, String x) throws SQLException {
        getCurrentResultSet().updateString(columnLabel, x);
    }


    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        getCurrentResultSet().updateBytes(columnLabel, x);
    }


    public void updateDate(String columnLabel, Date x) throws SQLException {
        getCurrentResultSet().updateDate(columnLabel, x);
    }


    public void updateTime(String columnLabel, Time x) throws SQLException {
        getCurrentResultSet().updateTime(columnLabel, x);
    }


    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        getCurrentResultSet().updateTimestamp(columnLabel, x);
    }


    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        getCurrentResultSet().updateAsciiStream(columnLabel, x, length);
    }


    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        getCurrentResultSet().updateBinaryStream(columnLabel, x, length);
    }


    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        getCurrentResultSet().updateCharacterStream(columnLabel, reader, length);
    }


    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        getCurrentResultSet().updateObject(columnLabel, x, scaleOrLength);
    }


    public void updateObject(String columnLabel, Object x) throws SQLException {
        getCurrentResultSet().updateObject(columnLabel, x);
    }


    public void insertRow() throws SQLException {
        getCurrentResultSet().insertRow();
    }


    public void updateRow() throws SQLException {
        getCurrentResultSet().updateRow();
    }


    public void deleteRow() throws SQLException {
        getCurrentResultSet().deleteRow();
    }


    public void refreshRow() throws SQLException {
        getCurrentResultSet().refreshRow();
    }


    public void cancelRowUpdates() throws SQLException {
        getCurrentResultSet().cancelRowUpdates();
    }


    public void moveToInsertRow() throws SQLException {
        getCurrentResultSet().moveToInsertRow();
    }


    public void moveToCurrentRow() throws SQLException {
        getCurrentResultSet().moveToCurrentRow();
    }


    public Statement getStatement() throws SQLException {
        return getCurrentResultSet().getStatement();
    }


    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return getCurrentResultSet().getObject(columnIndex, map);
    }


    public Ref getRef(int columnIndex) throws SQLException {
        return getCurrentResultSet().getRef(columnIndex);
    }


    public Blob getBlob(int columnIndex) throws SQLException {
        return getCurrentResultSet().getBlob(columnIndex);
    }


    public Clob getClob(int columnIndex) throws SQLException {
        return getCurrentResultSet().getClob(columnIndex);
    }


    public Array getArray(int columnIndex) throws SQLException {
        return getCurrentResultSet().getArray(columnIndex);
    }


    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getCurrentResultSet().getObject(columnLabel, map);
    }


    public Ref getRef(String columnLabel) throws SQLException {
        return getCurrentResultSet().getRef(columnLabel);
    }


    public Blob getBlob(String columnLabel) throws SQLException {
        return getCurrentResultSet().getBlob(columnLabel);
    }


    public Clob getClob(String columnLabel) throws SQLException {
        return getCurrentResultSet().getClob(columnLabel);
    }


    public Array getArray(String columnLabel) throws SQLException {
        return getCurrentResultSet().getArray(columnLabel);
    }


    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return getCurrentResultSet().getDate(columnIndex, cal);
    }


    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getCurrentResultSet().getDate(columnLabel, cal);
    }


    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getCurrentResultSet().getTime(columnIndex, cal);
    }


    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getCurrentResultSet().getTime(columnLabel, cal);
    }


    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getCurrentResultSet().getTimestamp(columnIndex, cal);
    }


    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getCurrentResultSet().getTimestamp(columnLabel, cal);
    }


    public URL getURL(int columnIndex) throws SQLException {
        return getCurrentResultSet().getURL(columnIndex);
    }


    public URL getURL(String columnLabel) throws SQLException {
        return getCurrentResultSet().getURL(columnLabel);
    }


    public void updateRef(int columnIndex, Ref x) throws SQLException {
        getCurrentResultSet().updateRef(columnIndex, x);
    }


    public void updateRef(String columnLabel, Ref x) throws SQLException {
        getCurrentResultSet().updateRef(columnLabel, x);
    }


    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        getCurrentResultSet().updateBlob(columnIndex, x);
    }


    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        getCurrentResultSet().updateBlob(columnLabel, x);
    }


    public void updateClob(int columnIndex, Clob x) throws SQLException {
        getCurrentResultSet().updateClob(columnIndex, x);
    }


    public void updateClob(String columnLabel, Clob x) throws SQLException {
        getCurrentResultSet().updateClob(columnLabel, x);
    }


    public void updateArray(int columnIndex, Array x) throws SQLException {
        getCurrentResultSet().updateArray(columnIndex, x);
    }


    public void updateArray(String columnLabel, Array x) throws SQLException {
        getCurrentResultSet().updateArray(columnLabel, x);
    }


    public RowId getRowId(int columnIndex) throws SQLException {
        return getCurrentResultSet().getRowId(columnIndex);
    }


    public RowId getRowId(String columnLabel) throws SQLException {
        return getCurrentResultSet().getRowId(columnLabel);
    }


    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        getCurrentResultSet().updateRowId(columnIndex, x);
    }


    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        getCurrentResultSet().updateRowId(columnLabel, x);
    }


    public int getHoldability() throws SQLException {
        return getCurrentResultSet().getHoldability();
    }


    public boolean isClosed() throws SQLException {
        return getCurrentResultSet().isClosed();
    }


    public void updateNString(int columnIndex, String nString) throws SQLException {
        getCurrentResultSet().updateNString(columnIndex, nString);
    }


    public void updateNString(String columnLabel, String nString) throws SQLException {
        getCurrentResultSet().updateNString(columnLabel, nString);
    }


    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        getCurrentResultSet().updateNClob(columnIndex, nClob);
    }


    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        getCurrentResultSet().updateNClob(columnLabel, nClob);
    }


    public NClob getNClob(int columnIndex) throws SQLException {
        return getCurrentResultSet().getNClob(columnIndex);
    }


    public NClob getNClob(String columnLabel) throws SQLException {
        return getCurrentResultSet().getNClob(columnLabel);
    }


    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return getCurrentResultSet().getSQLXML(columnIndex);
    }


    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return getCurrentResultSet().getSQLXML(columnLabel);
    }


    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        getCurrentResultSet().updateSQLXML(columnIndex, xmlObject);
    }


    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        getCurrentResultSet().updateSQLXML(columnLabel, xmlObject);
    }


    public String getNString(int columnIndex) throws SQLException {
        return getCurrentResultSet().getNString(columnIndex);
    }


    public String getNString(String columnLabel) throws SQLException {
        return getCurrentResultSet().getNString(columnLabel);
    }


    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return getCurrentResultSet().getNCharacterStream(columnIndex);
    }


    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getCurrentResultSet().getNCharacterStream(columnLabel);
    }


    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        getCurrentResultSet().updateNCharacterStream(columnIndex, x, length);
    }


    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        getCurrentResultSet().updateNCharacterStream(columnLabel, reader, length);
    }


    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        getCurrentResultSet().updateAsciiStream(columnIndex, x, length);
    }


    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        getCurrentResultSet().updateBinaryStream(columnIndex, x, length);
    }


    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        getCurrentResultSet().updateCharacterStream(columnIndex, x, length);
    }


    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        getCurrentResultSet().updateAsciiStream(columnLabel, x, length);
    }


    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        getCurrentResultSet().updateBinaryStream(columnLabel, x, length);
    }


    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        getCurrentResultSet().updateCharacterStream(columnLabel, reader, length);
    }


    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        getCurrentResultSet().updateBlob(columnIndex, inputStream, length);
    }


    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        getCurrentResultSet().updateBlob(columnLabel, inputStream, length);
    }


    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        getCurrentResultSet().updateClob(columnIndex, reader, length);
    }


    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        getCurrentResultSet().updateClob(columnLabel, reader, length);
    }


    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        getCurrentResultSet().updateNClob(columnIndex, reader, length);
    }


    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        getCurrentResultSet().updateNClob(columnLabel, reader, length);
    }


    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        getCurrentResultSet().updateNCharacterStream(columnIndex, x);
    }


    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        getCurrentResultSet().updateNCharacterStream(columnLabel, reader);
    }


    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        getCurrentResultSet().updateAsciiStream(columnIndex, x);
    }


    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        getCurrentResultSet().updateBinaryStream(columnIndex, x);
    }


    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        getCurrentResultSet().updateCharacterStream(columnIndex, x);
    }


    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        getCurrentResultSet().updateAsciiStream(columnLabel, x);
    }


    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        getCurrentResultSet().updateBinaryStream(columnLabel, x);
    }


    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        getCurrentResultSet().updateCharacterStream(columnLabel, reader);
    }


    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        getCurrentResultSet().updateBlob(columnIndex, inputStream);
    }


    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        getCurrentResultSet().updateBlob(columnLabel, inputStream);
    }


    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        getCurrentResultSet().updateClob(columnIndex, reader);
    }


    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        getCurrentResultSet().updateClob(columnLabel, reader);
    }


    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        getCurrentResultSet().updateNClob(columnIndex, reader);
    }


    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        getCurrentResultSet().updateNClob(columnLabel, reader);
    }


    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return getCurrentResultSet().getObject(columnIndex, type);
    }


    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return getCurrentResultSet().getObject(columnLabel, type);
    }


    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
