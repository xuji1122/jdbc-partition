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

import org.druid.sql.ast.SQLName;
import org.druid.sql.ast.SQLStatementImpl;
import org.druid.sql.ast.statement.SQLExprTableSource;
import org.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLDropViewStatement extends SQLStatementImpl implements SQLDDLStatement {

    protected List<org.druid.sql.ast.statement.SQLExprTableSource> tableSources = new ArrayList<org.druid.sql.ast.statement.SQLExprTableSource>();

    protected boolean                  cascade      = false;
    protected boolean                  restrict     = false;
    protected boolean                  ifExists     = false;

    public SQLDropViewStatement(){

    }

    public SQLDropViewStatement(String dbType){
        super (dbType);
    }

    public SQLDropViewStatement(SQLName name){
        this(new org.druid.sql.ast.statement.SQLExprTableSource(name));
    }

    public SQLDropViewStatement(org.druid.sql.ast.statement.SQLExprTableSource tableSource){
        this.tableSources.add(tableSource);
    }

    public List<org.druid.sql.ast.statement.SQLExprTableSource> getTableSources() {
        return tableSources;
    }

    public void addPartition(org.druid.sql.ast.statement.SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSources.add(tableSource);
    }

    public void setName(SQLName name) {
        this.addTableSource(new org.druid.sql.ast.statement.SQLExprTableSource(name));
    }

    public void addTableSource(SQLName name) {
        this.addTableSource(new org.druid.sql.ast.statement.SQLExprTableSource(name));
    }

    public void addTableSource(SQLExprTableSource tableSource) {
        tableSources.add(tableSource);
    }

    public boolean isCascade() {
        return cascade;
    }

    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSources);
        }
        visitor.endVisit(this);
    }

    public boolean isRestrict() {
        return restrict;
    }

    public void setRestrict(boolean restrict) {
        this.restrict = restrict;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

}
