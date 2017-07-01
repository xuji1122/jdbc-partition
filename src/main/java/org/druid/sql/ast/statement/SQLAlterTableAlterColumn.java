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
import org.druid.sql.ast.statement.SQLColumnDefinition;
import org.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableAlterColumn extends SQLObjectImpl implements SQLAlterTableItem {
    private SQLName originColumn;

    private org.druid.sql.ast.statement.SQLColumnDefinition column;

    private boolean             setNotNull;

    private boolean             dropNotNull;

    private SQLExpr setDefault;

    private boolean             dropDefault;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, column);
            acceptChild(visitor, setDefault);
        }
        visitor.endVisit(this);
    }

    public org.druid.sql.ast.statement.SQLColumnDefinition getColumn() {
        return column;
    }

    public void setColumn(SQLColumnDefinition column) {
        this.column = column;
    }

    public boolean isSetNotNull() {
        return setNotNull;
    }

    public void setSetNotNull(boolean setNotNull) {
        this.setNotNull = setNotNull;
    }

    public boolean isDropNotNull() {
        return dropNotNull;
    }

    public void setDropNotNull(boolean dropNotNull) {
        this.dropNotNull = dropNotNull;
    }

    public SQLExpr getSetDefault() {
        return setDefault;
    }

    public void setSetDefault(SQLExpr setDefault) {
        this.setDefault = setDefault;
    }

    public boolean isDropDefault() {
        return dropDefault;
    }

    public void setDropDefault(boolean dropDefault) {
        this.dropDefault = dropDefault;
    }

    public SQLName getOriginColumn() {
        return originColumn;
    }

    public void setOriginColumn(SQLName originColumn) {
        if (originColumn != null) {
            originColumn.setParent(this);
        }
        this.originColumn = originColumn;
    }
}
