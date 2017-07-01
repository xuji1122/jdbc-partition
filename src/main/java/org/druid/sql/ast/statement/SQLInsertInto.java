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
package org.druid.sql.ast.statement;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLName;
import org.druid.sql.ast.SQLObjectImpl;
import org.druid.sql.ast.statement.SQLExprTableSource;
import org.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import org.druid.sql.ast.statement.SQLSelect;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLInsertInto extends SQLObjectImpl {

    protected org.druid.sql.ast.statement.SQLExprTableSource tableSource;

    protected final List<SQLExpr> columns = new ArrayList<SQLExpr>();
    protected SQLSelect query;

    protected final List<ValuesClause>  valuesList = new ArrayList<ValuesClause>();

    public SQLInsertInto(){

    }

    public String getAlias() {
        return tableSource.getAlias();
    }

    public void setAlias(String alias) {
        this.tableSource.setAlias(alias);
    }

    public org.druid.sql.ast.statement.SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(org.druid.sql.ast.statement.SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public SQLName getTableName() {
        return (SQLName) tableSource.getExpr();
    }

    public void setTableName(SQLName tableName) {
        this.setTableSource(new org.druid.sql.ast.statement.SQLExprTableSource(tableName));
    }

    public void setTableSource(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelect query) {
        this.query = query;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }
    
    public void addColumn(SQLExpr column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }

    public ValuesClause getValues() {
        if (valuesList.size() == 0) {
            return null;
        }
        return valuesList.get(0);
    }

    public void setValues(ValuesClause values) {
        if (valuesList.size() == 0) {
            valuesList.add(values);
        } else {
            valuesList.set(0, values);
        }
    }
    
    public List<ValuesClause> getValuesList() {
        return valuesList;
    }

    public void addValueCause(ValuesClause valueClause) {
        if (valueClause != null) {
            valueClause.setParent(this);
        }
        valuesList.add(valueClause);
    }
}
