package org.the.force.jdbc.partition.resource.statement;

import org.the.force.jdbc.partition.engine.value.AbstractSqlParameter;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.engine.value.types.ByteValue;
import org.the.force.jdbc.partition.engine.value.types.DateValue;
import org.the.force.jdbc.partition.engine.value.types.DecimalValue;
import org.the.force.jdbc.partition.engine.value.types.DoubleValue;
import org.the.force.jdbc.partition.engine.value.types.FloatValue;
import org.the.force.jdbc.partition.engine.value.types.IntValue;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.value.types.LongValue;
import org.the.force.jdbc.partition.engine.value.types.NullValue;
import org.the.force.jdbc.partition.engine.value.types.ObjectTypedValue;
import org.the.force.jdbc.partition.engine.value.types.ObjectValue;
import org.the.force.jdbc.partition.engine.value.types.ShortValue;
import org.the.force.jdbc.partition.engine.value.types.StringValue;
import org.the.force.jdbc.partition.engine.value.types.TimeValue;
import org.the.force.jdbc.partition.engine.value.types.TimestampValue;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

/**
 * Created by xuji on 2017/5/17.
 */
public abstract class AbstractPreparedStatement extends AbstractStatement implements PreparedStatement {

    protected final LogicSqlParameterHolder logicSqlParameterHolder;

    public AbstractPreparedStatement() {
        logicSqlParameterHolder = new LogicSqlParameterHolder();
    }


    public ResultSet executeQuery() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public int executeUpdate() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean execute() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void addBatch() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new NullValue(sqlType));
    }


    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new BooleanValue(x));
    }


    public void setByte(int parameterIndex, byte x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ByteValue(x));
    }


    public void setShort(int parameterIndex, short x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ShortValue(x));
    }


    public void setInt(int parameterIndex, int x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new IntValue(x));
    }


    public void setLong(int parameterIndex, long x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new LongValue(x));
    }


    public void setFloat(int parameterIndex, float x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new FloatValue(x));
    }


    public void setDouble(int parameterIndex, double x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new DoubleValue(x));
    }


    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new DecimalValue(x));
    }


    public void setString(int parameterIndex, String x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new StringValue(x));
    }


    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectTypedValue(x, Types.VARBINARY));
    }


    public void setDate(int parameterIndex, Date x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new DateValue(x));
    }


    public void setTime(int parameterIndex, Time x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new TimeValue(x));
    }


    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new TimestampValue(x));
    }


    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setAsciiStream(parameterIndex, x, length);
            }

            public int getSqlType() {
                return 0;
            }

            public Object getValue() {
                return x;
            }

        });
    }


    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setUnicodeStream(parameterIndex, x, length);
            }


            public Object getValue() {
                return x;
            }


            public int getSqlType() {
                return 0;
            }
        });
    }


    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBinaryStream(parameterIndex, x, length);
            }


            public Object getValue() {
                return x;
            }


            public int getSqlType() {
                return 0;
            }
        });
    }


    public void clearParameters() throws SQLException {
        logicSqlParameterHolder.clearParameters();
    }


    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectTypedValue(x, targetSqlType));
    }


    public void setObject(int parameterIndex, Object x) throws SQLException {

        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectValue(x));
    }


    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setCharacterStream(parameterIndex, reader, length);
            }

            public Object getValue() {
                return null;
            }

            public int getSqlType() {
                return 0;
            }
        });
    }


    public void setRef(int parameterIndex, Ref x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectTypedValue(x, Types.REF));
    }


    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectTypedValue(x, Types.BLOB));
    }


    public void setClob(int parameterIndex, Clob x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectTypedValue(x, Types.CLOB));
    }


    public void setArray(int parameterIndex, Array x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectTypedValue(x, Types.ARRAY));
    }


    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }


    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new DateValue(x, cal));
    }


    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new TimeValue(x, cal));
    }


    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new TimestampValue(x, cal));
    }


    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new NullValue(sqlType, typeName));
    }


    public void setURL(int parameterIndex, URL x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectTypedValue(x, Types.DATALINK));
    }


    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }


    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setRowId(parameterIndex, x);
            }

            public Object getValue() {
                return x;
            }

            public int getSqlType() {
                return Types.ROWID;
            }
        });
    }


    public void setNString(int parameterIndex, String value) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNString(parameterIndex, value);
            }

            public Object getValue() {
                return value;
            }

            public int getSqlType() {
                return Types.NVARCHAR;
            }
        });
    }


    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNCharacterStream(parameterIndex, value, length);
            }

            public Object getValue() {
                return value;
            }

            public int getSqlType() {
                return 0;
            }
        });
    }


    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNClob(parameterIndex, value);
            }

            public Object getValue() {
                return value;
            }

            public int getSqlType() {
                return Types.NCLOB;
            }
        });
    }


    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setClob(parameterIndex, reader, length);
            }

            public Object getValue() {
                return reader;
            }

            public int getSqlType() {
                return Types.CLOB;
            }
        });
    }


    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBlob(parameterIndex, inputStream, length);
            }

            public Object getValue() {
                return inputStream;
            }

            public int getSqlType() {
                return Types.BLOB;
            }
        });
    }


    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNClob(parameterIndex, reader, length);
            }

            public Object getValue() {
                return reader;
            }


            public int getSqlType() {
                return Types.NCLOB;
            }
        });
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setSQLXML(parameterIndex, xmlObject);
            }

            public Object getValue() {
                return xmlObject;
            }


            public int getSqlType() {
                return Types.SQLXML;
            }
        });
    }


    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
            }

            public Object getValue() {
                return x;
            }


            public int getSqlType() {
                return targetSqlType;
            }
        });
    }


    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setAsciiStream(parameterIndex, x, length);
            }

            public Object getValue() {
                return x;
            }

            public int getSqlType() {
                return Types.LONGVARCHAR;
            }
        });
    }


    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBinaryStream(parameterIndex, x, length);
            }

            public Object getValue() {
                return x;
            }


            public int getSqlType() {
                return Types.LONGVARBINARY;
            }
        });
    }


    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setCharacterStream(parameterIndex, reader, length);
            }

            public Object getValue() {
                return reader;
            }

            public int getSqlType() {
                return 0;
            }
        });
    }


    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setAsciiStream(parameterIndex, x);
            }

            public Object getValue() {
                return x;
            }

            public int getSqlType() {
                return Types.LONGVARCHAR;
            }
        });
    }


    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBinaryStream(parameterIndex, x);
            }

            public Object getValue() {
                return x;
            }

            public int getSqlType() {
                return Types.LONGVARBINARY;
            }
        });
    }


    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setCharacterStream(parameterIndex, reader);
            }

            public Object getValue() {
                return reader;
            }

            public int getSqlType() {
                return Types.LONGVARCHAR;
            }
        });
    }


    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNCharacterStream(parameterIndex, value);
            }

            public Object getValue() {
                return value;
            }


            public int getSqlType() {
                return 0;
            }
        });
    }


    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setClob(parameterIndex, reader);
            }

            public Object getValue() {
                return reader;
            }

            public int getSqlType() {
                return Types.CLOB;
            }
        });
    }


    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBlob(parameterIndex, inputStream);
            }

            public Object getValue() {
                return inputStream;
            }

            public int getSqlType() {
                return Types.BLOB;
            }
        });
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new AbstractSqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNClob(parameterIndex, reader);
            }

            public Object getValue() {
                return reader;
            }

            public int getSqlType() {
                return Types.NCLOB;
            }
        });
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
