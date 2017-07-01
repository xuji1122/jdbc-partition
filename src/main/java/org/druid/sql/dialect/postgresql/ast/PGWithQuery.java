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
package org.druid.sql.dialect.postgresql.ast;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLStatement;
import org.druid.sql.dialect.postgresql.ast.PGSQLObjectImpl;
import org.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class PGWithQuery extends PGSQLObjectImpl {

    private SQLExpr name;
    private final List<SQLExpr> columns = new ArrayList<SQLExpr>();
    private SQLStatement query;

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        this.name = name;
    }

    public SQLStatement getQuery() {
        return query;
    }

    public void setQuery(SQLStatement query) {
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
    
    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, columns);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }
}
