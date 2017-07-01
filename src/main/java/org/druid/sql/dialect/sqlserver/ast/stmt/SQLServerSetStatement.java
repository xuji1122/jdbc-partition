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
package org.druid.sql.dialect.sqlserver.ast.stmt;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.statement.SQLAssignItem;
import org.druid.sql.dialect.sqlserver.ast.SQLServerStatement;
import org.druid.sql.dialect.sqlserver.ast.SQLServerStatementImpl;
import org.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerSetStatement extends SQLServerStatementImpl implements SQLServerStatement {

    private SQLAssignItem item = new SQLAssignItem();

    public SQLServerSetStatement(){
    }

    public SQLServerSetStatement(SQLExpr target, SQLExpr value){
        this.item = new SQLAssignItem(target, value);
    }

    public SQLAssignItem getItem() {
        return item;
    }

    public void setItem(SQLAssignItem item) {
        this.item = item;
    }

    public void output(StringBuffer buf) {
        buf.append("SET ");
        item.output(buf);
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.item);
        }
        visitor.endVisit(this);
    }
}
