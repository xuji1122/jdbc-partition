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

import org.druid.sql.ast.SQLCommentHint;
import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLName;
import org.druid.sql.ast.SQLStatementImpl;
import org.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.druid.sql.ast.expr.SQLBinaryOperator;
import org.druid.sql.ast.expr.SQLIntegerExpr;
import org.druid.sql.ast.statement.SQLAssignItem;
import org.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLSetStatement extends SQLStatementImpl {

    private List<org.druid.sql.ast.statement.SQLAssignItem> items = new ArrayList<org.druid.sql.ast.statement.SQLAssignItem>();

    private List<SQLCommentHint> hints;

    public SQLSetStatement(){
    }

    public SQLSetStatement(String dbType){
        super (dbType);
    }

    public SQLSetStatement(SQLExpr target, SQLExpr value){
        this(target, value, null);
    }

    public SQLSetStatement(SQLExpr target, SQLExpr value, String dbType){
        super (dbType);
        this.items.add(new org.druid.sql.ast.statement.SQLAssignItem(target, value));
    }

    public static org.druid.sql.ast.statement.SQLSetStatement plus(SQLName target) {
        SQLExpr value = new SQLBinaryOpExpr(target.clone(), SQLBinaryOperator.Add, new SQLIntegerExpr(1));
        return new org.druid.sql.ast.statement.SQLSetStatement(target, value);
    }

    public List<org.druid.sql.ast.statement.SQLAssignItem> getItems() {
        return items;
    }

    public void setItems(List<org.druid.sql.ast.statement.SQLAssignItem> items) {
        this.items = items;
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.hints);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("SET ");

        for (int i = 0; i < items.size(); ++i) {
            if (i != 0) {
                buf.append(", ");
            }

            SQLAssignItem item = items.get(i);
            item.output(buf);
        }
    }
}
