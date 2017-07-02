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
package org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt;

import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.PGWithClause;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDeleteStatement;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;
import org.the.force.thirdparty.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class PGDeleteStatement extends SQLDeleteStatement implements PGSQLStatement {

    private PGWithClause with;
    private boolean       only  = false;
    private List<SQLName> using = new ArrayList<SQLName>(2);
    private boolean       returning;
    private String        alias;
    
    public PGDeleteStatement() {
        super (JdbcConstants.POSTGRESQL);
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<SQLName> getUsing() {
        return using;
    }

    public void setUsing(List<SQLName> using) {
        this.using = using;
    }

    public boolean isOnly() {
        return only;
    }

    public void setOnly(boolean only) {
        this.only = only;
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

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, with);
            acceptChild(visitor, tableSource);
            acceptChild(visitor, using);
            acceptChild(visitor, where);
        }

        visitor.endVisit(this);
    }

}
