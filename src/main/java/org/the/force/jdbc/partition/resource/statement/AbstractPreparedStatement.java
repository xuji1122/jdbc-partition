package org.the.force.jdbc.partition.resource.statement;

import org.the.force.jdbc.partition.engine.parameter.ByteSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.DecimalSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.DoubleSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.FloatSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.IntegerSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.LongSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.ShortSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.StringSqlParameter;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;

import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.parameter.DateSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.NullSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.ObjectSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.parameter.TimeSqlParameter;
import org.the.force.jdbc.partition.engine.parameter.TimestampSqlParameter;

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


    @Override
    public ResultSet executeQuery() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public int executeUpdate() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public boolean execute() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    @Override
    public void addBatch() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new NullSqlParameter(sqlType));
    }


    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, Types.BOOLEAN));
    }


    public void setByte(int parameterIndex, byte x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ByteSqlParameter(x));
    }


    public void setShort(int parameterIndex, short x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ShortSqlParameter(x));
    }


    public void setInt(int parameterIndex, int x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new IntegerSqlParameter(x));
    }


    public void setLong(int parameterIndex, long x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new LongSqlParameter(x));
    }


    public void setFloat(int parameterIndex, float x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new FloatSqlParameter(x));
    }


    public void setDouble(int parameterIndex, double x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new DoubleSqlParameter(x));
    }


    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new DecimalSqlParameter(x));
    }


    public void setString(int parameterIndex, String x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new StringSqlParameter(x));
    }


    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, Types.VARBINARY));
    }


    public void setDate(int parameterIndex, Date x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new DateSqlParameter(x));
    }


    public void setTime(int parameterIndex, Time x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new TimeSqlParameter(x));
    }


    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new TimestampSqlParameter(x));
    }


    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setAsciiStream(parameterIndex, x, length);
            }


            public Object getValue() {
                return x;
            }

        });
    }


    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

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
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

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
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, targetSqlType));
    }


    public void setObject(int parameterIndex, Object x) throws SQLException {

        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, null));
    }



    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

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
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, Types.REF));
    }


    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, Types.BLOB));
    }


    public void setClob(int parameterIndex, Clob x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, Types.CLOB));
    }


    public void setArray(int parameterIndex, Array x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, Types.ARRAY));
    }


    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }


    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new DateSqlParameter(x, cal));
    }


    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new TimeSqlParameter(x, cal));
    }


    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new TimestampSqlParameter(x, cal));
    }


    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new NullSqlParameter(sqlType, typeName));
    }


    public void setURL(int parameterIndex, URL x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new ObjectSqlParameter(x, Types.DATALINK));
    }


    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }


    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setRowId(parameterIndex, x);
            }

            public Object getValue() {
                return x;
            }

        });
    }


    public void setNString(int parameterIndex, String value) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNString(parameterIndex, value);
            }

            public Object getValue() {
                return value;
            }

        });
    }


    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNCharacterStream(parameterIndex, value, length);
            }

            public Object getValue() {
                return value;
            }

        });
    }


    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNClob(parameterIndex, value);
            }

            public Object getValue() {
                return value;
            }

        });
    }


    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setClob(parameterIndex, reader, length);
            }

            public Object getValue() {
                return reader;
            }

        });
    }


    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBlob(parameterIndex, inputStream, length);
            }

            public Object getValue() {
                return inputStream;
            }

        });
    }


    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNClob(parameterIndex, reader, length);
            }

            public Object getValue() {
                return reader;
            }

        });
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setSQLXML(parameterIndex, xmlObject);
            }

            public Object getValue() {
                return xmlObject;
            }

        });
    }


    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
            }

            public Object getValue() {
                return x;
            }

        });
    }


    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setAsciiStream(parameterIndex, x, length);
            }

            public Object getValue() {
                return x;
            }

        });
    }


    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBinaryStream(parameterIndex, x, length);
            }

            public Object getValue() {
                return x;
            }

        });
    }


    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setCharacterStream(parameterIndex, reader, length);
            }

            public Object getValue() {
                return reader;
            }

        });
    }


    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setAsciiStream(parameterIndex, x);
            }

            public Object getValue() {
                return x;
            }

        });
    }


    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBinaryStream(parameterIndex, x);
            }

            public Object getValue() {
                return x;
            }

        });
    }


    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setCharacterStream(parameterIndex, reader);
            }

            public Object getValue() {
                return reader;
            }

        });
    }


    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNCharacterStream(parameterIndex, value);
            }

            public Object getValue() {
                return value;
            }

        });
    }


    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setClob(parameterIndex, reader);
            }

            public Object getValue() {
                return reader;
            }

        });
    }


    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBlob(parameterIndex, inputStream);
            }

            public Object getValue() {
                return inputStream;
            }

        });
    }


    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        logicSqlParameterHolder.setParameter(parameterIndex, new SqlParameter() {

            public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setNClob(parameterIndex, reader);
            }

            public Object getValue() {
                return reader;
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
