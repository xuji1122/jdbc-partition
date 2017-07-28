package org.the.force.jdbc.partition.test;


import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameterFactory;
import org.the.force.jdbc.partition.engine.value.types.DateValue;
import org.the.force.jdbc.partition.engine.value.types.DecimalValue;
import org.the.force.jdbc.partition.engine.value.types.DoubleValue;
import org.the.force.jdbc.partition.engine.value.types.FloatValue;
import org.the.force.jdbc.partition.engine.value.types.IntValue;
import org.the.force.jdbc.partition.engine.value.types.LongValue;
import org.the.force.jdbc.partition.engine.value.types.StringValue;
import org.the.force.jdbc.partition.engine.value.types.TimestampValue;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/7/3.
 */
public class SqlCVSFileReader {

    private Log logger = LogFactory.getLog(SqlCVSFileReader.class);

    private final BufferedReader bufferedReader;

    private boolean end = false;

    private long lineCount = 0;

    private String sql;
    private String sqlType;
    private String[] types;
    private Map<String, SqlParameterFactory> parameterFactoryMap = new HashMap<>();

    public SqlCVSFileReader(String filePath) throws Exception {
        this(filePath, "UTF-8");
    }

    public SqlCVSFileReader(String filePath, String charset) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, charset));
        parameterFactoryMap.put("long", new LongValue(0));
        parameterFactoryMap.put("int", new IntValue(0));
        parameterFactoryMap.put("double", new DoubleValue(0));
        parameterFactoryMap.put("float", new FloatValue(0.0F));
        parameterFactoryMap.put("decimal", new DecimalValue(new BigDecimal("0")));
        parameterFactoryMap.put("string", new StringValue(""));
        parameterFactoryMap.put("date", new DateValue(new java.sql.Date(new java.util.Date().getTime())));
        parameterFactoryMap.put("timestamp", new TimestampValue(new java.sql.Timestamp(new java.util.Date().getTime())));
        String first = bufferedReader.readLine();
        int index = first.indexOf(":");
        if (index > -1) {
            sqlType = first.substring(0, index).trim();
        }
        sql = first.substring(index + 1).trim();
    }

    public List<SqlParameter> nextSqlLine() {
        String line = nextLine();
        if (line == null) {
            return null;
        }
        if (line.startsWith("types:")) {
            line = line.substring("types:".length()).trim();
            List<String> types = parseLine(line);
            types = types.stream().map(String::toLowerCase).collect(Collectors.toList());
            this.types = new String[types.size()];
            types.toArray(this.types);
            line = nextLine();
        }
        List<String> values = parseLine(line);
        List<SqlParameter> parameters = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            String input = values.get(i);
            SqlParameter sqlParameter = parameterFactoryMap.get(this.types[i]).parse(input);
            parameters.add(sqlParameter);
        }
        return parameters;

    }

    public String nextLine() {
        if (end) {
            return null;
        }
        try {
            String line = bufferedReader.readLine();
            if (line == null) {
                bufferedReader.close();
                end = true;
                return null;
            }
            line = line.trim();
            lineCount++;
            return line;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> parseLine(String line) {
        List<String> list = new ArrayList<>();
        boolean stringToken = false;//是否被双引号引用起来标识字符串，不区分分隔符和空格
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {//明确标识为字符串类型
                if (!stringToken) {
                    stringToken = true;
                    continue;
                }
                list.add(stringBuffer.toString());
                stringBuffer = new StringBuilder();
                stringToken = false;
                continue;
            }
            if (stringToken) {
                stringBuffer.append(ch);
                continue;
            }
            if (ch <= ' ') {//空格跳过
                if (stringBuffer.length() < 1) {
                    continue;
                }
                list.add(stringBuffer.toString());
                stringBuffer = new StringBuilder();
            } else {
                stringBuffer.append(ch);
            }
        }
        if (stringToken) {
            throw new RuntimeException("文件格式不正确,字符串不完整lineCount=" + lineCount + ",line=" + line);
        }
        //最后一分隔符之后
        if (stringBuffer.length() > 0) {
            list.add(stringBuffer.toString());
        }
        logger.info(MessageFormat.format("\n\r读取文件记录行 {0}:{1}", lineCount, list.toString()));
        return list;
    }

    public String getSql() {
        return sql;
    }

    public String getSqlType() {
        return sqlType;
    }
}
