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
package org.druid.sql.dialect.postgresql.ast.stmt;

import org.druid.sql.ast.statement.SQLSelect;
import org.druid.sql.ast.statement.SQLSelectStatement;
import org.druid.sql.dialect.postgresql.ast.PGWithClause;
import org.druid.sql.dialect.postgresql.ast.stmt.PGSQLStatement;
import org.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import org.druid.sql.visitor.SQLASTVisitor;
import org.druid.util.JdbcConstants;

public class PGSelectStatement extends SQLSelectStatement implements PGSQLStatement {

    private PGWithClause with;

    public PGSelectStatement(){
        super(JdbcConstants.POSTGRESQL);
    }

    public PGSelectStatement(SQLSelect select){
        super(select, JdbcConstants.POSTGRESQL);
    }

    public PGWithClause getWith() {
        return with;
    }

    public void setWith(PGWithClause with) {
        this.with = with;
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((PGASTVisitor) visitor);
    }

    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.with);
            acceptChild(visitor, this.select);
        }
        visitor.endVisit(this);
    }
}
