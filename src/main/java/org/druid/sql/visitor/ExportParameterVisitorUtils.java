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
package org.druid.sql.visitor;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLObject;
import org.druid.sql.ast.expr.SQLBetweenExpr;
import org.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.druid.sql.ast.expr.SQLBooleanExpr;
import org.druid.sql.ast.expr.SQLCharExpr;
import org.druid.sql.ast.expr.SQLHexExpr;
import org.druid.sql.ast.expr.SQLLiteralExpr;
import org.druid.sql.ast.expr.SQLNumericLiteralExpr;
import org.druid.sql.ast.expr.SQLVariantRefExpr;
import org.druid.sql.dialect.db2.visitor.DB2ExportParameterVisitor;
import org.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import org.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import org.druid.sql.dialect.postgresql.visitor.PGExportParameterVisitor;
import org.druid.sql.dialect.sqlserver.visitor.MSSQLServerExportParameterVisitor;
import org.druid.sql.visitor.ExportParameterVisitor;
import org.druid.sql.visitor.ExportParameterizedOutputVisitor;
import org.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import org.druid.util.JdbcUtils;

import java.util.ArrayList;
import java.util.List;

public final class ExportParameterVisitorUtils {
    
    //private for util class not need new instance
    private ExportParameterVisitorUtils() {
        super();
    }

    public static ExportParameterVisitor createExportParameterVisitor(final  Appendable out ,final String dbType) {
        
        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlExportParameterVisitor(out);
        }
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleExportParameterVisitor(out);
        }
        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2ExportParameterVisitor(out);
        }
        
        if (JdbcUtils.MARIADB.equals(dbType)) {
            return new MySqlExportParameterVisitor(out);
        }
        
        if (JdbcUtils.H2.equals(dbType)) {
            return new MySqlExportParameterVisitor(out);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)
                || JdbcUtils.ENTERPRISEDB.equals(dbType)) {
            return new PGExportParameterVisitor(out);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new MSSQLServerExportParameterVisitor(out);
        }
       return new ExportParameterizedOutputVisitor(out);
    }

    

    public static boolean exportParamterAndAccept(final List<Object> parameters, List<SQLExpr> list) {
        for (int i = 0, size = list.size(); i < size; ++i) {
            SQLExpr param = list.get(i);

            SQLExpr result = exportParameter(parameters, param);
            if (result != param) {
                list.set(i, result);
            }
        }

        return false;
    }

    public static SQLExpr exportParameter(final List<Object> parameters, final SQLExpr param) {
        Object value = null;
        boolean replace = false;

        if (param instanceof SQLCharExpr) {
            value = ((SQLCharExpr) param).getText();
            replace = true;
        }

        if (param instanceof SQLBooleanExpr) {
            value = ((SQLBooleanExpr) param).getValue();
            replace = true;
        }

        if (param instanceof SQLNumericLiteralExpr) {
            value = ((SQLNumericLiteralExpr) param).getNumber();
            replace = true;
        }

        if (param instanceof SQLHexExpr) {
            value = ((SQLHexExpr) param).toBytes();
            replace = true;
        }

        if (replace) {
            SQLObject parent = param.getParent();
            if (parent != null) {
                List<SQLObject> mergedList = (List<SQLObject>) parent.getAttribute(ParameterizedOutputVisitorUtils.ATTR_MERGED);
                if (mergedList != null) {
                    List<Object> mergedListParams = new ArrayList<Object>(mergedList.size() + 1);
                    for (int i = 0; i < mergedList.size(); ++i) {
                        SQLObject item = mergedList.get(i);
                        if (item instanceof SQLBinaryOpExpr) {
                            SQLBinaryOpExpr binaryOpItem = (SQLBinaryOpExpr) item;
                            exportParameter(mergedListParams, binaryOpItem.getRight());
                        }
                    }
                    if (mergedListParams.size() > 0) {
                        mergedListParams.add(0, value);
                        value = mergedListParams;
                    }
                }
            }

            parameters.add(value);

            return new SQLVariantRefExpr("?");
        }

        return param;
    }

    public static void exportParameter(final List<Object> parameters, SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLLiteralExpr && x.getRight() instanceof SQLLiteralExpr && x.getOperator().isRelational()) {
            return;
        }

        {
            SQLExpr leftResult = org.druid.sql.visitor.ExportParameterVisitorUtils.exportParameter(parameters, x.getLeft());
            if (leftResult != x.getLeft()) {
                x.setLeft(leftResult);
            }
        }

        {
            SQLExpr rightResult = exportParameter(parameters, x.getRight());
            if (rightResult != x.getRight()) {
                x.setRight(rightResult);
            }
        }
    }

    public static void exportParameter(final List<Object> parameters, SQLBetweenExpr x) {
        {
            SQLExpr result = exportParameter(parameters, x.getBeginExpr());
            if (result != x.getBeginExpr()) {
                x.setBeginExpr(result);
            }
        }

        {
            SQLExpr result = exportParameter(parameters, x.getEndExpr());
            if (result != x.getBeginExpr()) {
                x.setEndExpr(result);
            }
        }

    }
}
