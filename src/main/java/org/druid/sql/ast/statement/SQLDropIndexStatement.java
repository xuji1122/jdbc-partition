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
import org.druid.sql.ast.SQLStatementImpl;
import org.druid.sql.ast.statement.SQLExprTableSource;
import org.druid.sql.visitor.SQLASTVisitor;

public class SQLDropIndexStatement extends SQLStatementImpl implements SQLDDLStatement {

    private SQLExpr indexName;
    private org.druid.sql.ast.statement.SQLExprTableSource tableName;

    public SQLDropIndexStatement() {

    }

    public SQLDropIndexStatement(String dbType) {
        super (dbType);
    }

    public SQLExpr getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLExpr indexName) {
        this.indexName = indexName;
    }

    public org.druid.sql.ast.statement.SQLExprTableSource getTableName() {
        return tableName;
    }

    public void setTableName(SQLExpr tableName) {
        this.setTableName(new org.druid.sql.ast.statement.SQLExprTableSource(tableName));
    }

    public void setTableName(SQLExprTableSource tableName) {
        this.tableName = tableName;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, indexName);
            acceptChild(visitor, tableName);
        }
        visitor.endVisit(this);
    }
}
