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

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.statement.SQLSetStatement;
import org.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import org.druid.sql.visitor.SQLASTVisitor;
import org.druid.util.JdbcConstants;

import java.util.List;

public class PGSetStatement extends SQLSetStatement implements PGSQLStatement {
    public final String range;
    public final String param;
    public final List<SQLExpr> values;

    // SET [ SESSION | LOCAL ] TIME ZONE { timezone | LOCAL | DEFAULT }
    // SET [ SESSION | LOCAL ] configuration_parameter { TO | = } { value | 'value' | DEFAULT }
    public PGSetStatement(String range, String param, List<SQLExpr> values) {
        super(JdbcConstants.POSTGRESQL);
        this.range = range;
        this.param = param;
        this.values = values;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            accept0((PGASTVisitor) visitor);
        }
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
