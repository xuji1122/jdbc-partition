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
package org.druid.sql.dialect.db2.visitor;

import org.druid.sql.ast.expr.SQLBinaryOperator;
import org.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import org.druid.sql.dialect.db2.ast.stmt.DB2ValuesStatement;
import org.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import org.druid.sql.visitor.SQLASTOutputVisitor;
import org.druid.util.JdbcConstants;

public class DB2OutputVisitor extends SQLASTOutputVisitor implements DB2ASTVisitor {

    public DB2OutputVisitor(Appendable appender){
        super(appender, JdbcConstants.DB2);
    }

    public DB2OutputVisitor(Appendable appender, boolean parameterized){
        super(appender, parameterized);
        this.dbType = JdbcConstants.DB2;
    }

    @Override
    public boolean visit(DB2SelectQueryBlock x) {
        this.visit((SQLSelectQueryBlock) x);

        if (x.isForReadOnly()) {
            println();
            print0(ucase ? "FOR READ ONLY" : "for read only");
        } else if (x.isForUpdate()) {
            println();
            print0(ucase ? "FOR UPDATE" : "for update");
        }

        if (x.getIsolation() != null) {
            println();
            print0(ucase ? "WITH " : "with ");
            print0(x.getIsolation().name());
        }

        if (x.getOptimizeFor() != null) {
            println();
            print0(ucase ? "OPTIMIZE FOR " : "optimize for ");
            x.getOptimizeFor().accept(this);
        }

        return false;
    }


    @Override
    public void endVisit(DB2SelectQueryBlock x) {

    }

    @Override
    public boolean visit(DB2ValuesStatement x) {
        print0(ucase ? "VALUES " : "values ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(DB2ValuesStatement x) {

    }
    
    protected void printOperator(SQLBinaryOperator operator) {
        if (operator == SQLBinaryOperator.Concat) {
            print0(ucase ? "CONCAT" : "concat");
        } else {
            print0(ucase ? operator.name : operator.name_lcase);
        }
    }
}
