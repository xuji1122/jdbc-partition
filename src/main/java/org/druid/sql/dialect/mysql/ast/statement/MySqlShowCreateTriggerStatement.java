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
package org.druid.sql.dialect.mysql.ast.statement;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.dialect.mysql.ast.statement.MySqlShowStatement;
import org.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import org.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowCreateTriggerStatement extends MySqlStatementImpl implements MySqlShowStatement {

    private SQLExpr name;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr functionName) {
        this.name = functionName;
    }

}
