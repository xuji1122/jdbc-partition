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
import org.druid.sql.ast.SQLObject;
import org.druid.sql.ast.SQLStatementImpl;
import org.druid.sql.ast.expr.SQLIdentifierExpr;
import org.druid.sql.ast.expr.SQLPropertyExpr;
import org.druid.sql.ast.statement.SQLExprTableSource;
import org.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SQLAlterTableStatement extends SQLStatementImpl implements SQLDDLStatement {

    private org.druid.sql.ast.statement.SQLExprTableSource tableSource;
    private List<SQLAlterTableItem> items                   = new ArrayList<SQLAlterTableItem>();

    // for mysql
    private boolean                 ignore                  = false;

    private boolean                 updateGlobalIndexes     = false;
    private boolean                 invalidateGlobalIndexes = false;

    private boolean                 removePatiting          = false;
    private boolean                 upgradePatiting         = false;
    private Map<String, SQLObject>  tableOptions            = new LinkedHashMap<String, SQLObject>();

    // odps
    private boolean                 mergeSmallFiles         = false;

    public SQLAlterTableStatement(){

    }

    public SQLAlterTableStatement(String dbType){
        super(dbType);
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isRemovePatiting() {
        return removePatiting;
    }

    public void setRemovePatiting(boolean removePatiting) {
        this.removePatiting = removePatiting;
    }

    public boolean isUpgradePatiting() {
        return upgradePatiting;
    }

    public void setUpgradePatiting(boolean upgradePatiting) {
        this.upgradePatiting = upgradePatiting;
    }

    public boolean isUpdateGlobalIndexes() {
        return updateGlobalIndexes;
    }

    public void setUpdateGlobalIndexes(boolean updateGlobalIndexes) {
        this.updateGlobalIndexes = updateGlobalIndexes;
    }

    public boolean isInvalidateGlobalIndexes() {
        return invalidateGlobalIndexes;
    }

    public void setInvalidateGlobalIndexes(boolean invalidateGlobalIndexes) {
        this.invalidateGlobalIndexes = invalidateGlobalIndexes;
    }

    public boolean isMergeSmallFiles() {
        return mergeSmallFiles;
    }

    public void setMergeSmallFiles(boolean mergeSmallFiles) {
        this.mergeSmallFiles = mergeSmallFiles;
    }

    public List<SQLAlterTableItem> getItems() {
        return items;
    }

    public void addItem(SQLAlterTableItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public org.druid.sql.ast.statement.SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(org.druid.sql.ast.statement.SQLExprTableSource tableSource) {
        this.tableSource = tableSource;
    }

    public void setTableSource(SQLExpr table) {
        this.setTableSource(new org.druid.sql.ast.statement.SQLExprTableSource(table));
    }

    public SQLName getName() {
        if (getTableSource() == null) {
            return null;
        }
        return (SQLName) getTableSource().getExpr();
    }

    public void setName(SQLName name) {
        this.setTableSource(new org.druid.sql.ast.statement.SQLExprTableSource(name));
    }

    public Map<String, SQLObject> getTableOptions() {
        return tableOptions;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getTableSource());
            acceptChild(visitor, getItems());
        }
        visitor.endVisit(this);
    }

    public String getTableName() {
        if (tableSource == null) {
            return null;
        }
        SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
        if (expr instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) expr).getName();
        } else if (expr instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) expr).getName();
        }

        return null;
    }
}
