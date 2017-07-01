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
package org.druid.sql.dialect.oracle.ast.expr;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLExprImpl;
import org.druid.sql.dialect.oracle.ast.expr.OracleExpr;
import org.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.druid.sql.visitor.SQLASTVisitor;

public class OracleIsSetExpr extends SQLExprImpl implements OracleExpr {

    private SQLExpr nestedTable;

    public OracleIsSetExpr(){
    }

    public OracleIsSetExpr(SQLExpr nestedTable){
        this.nestedTable = nestedTable;
    }

    public SQLExpr getNestedTable() {
        return nestedTable;
    }

    public void setNestedTable(SQLExpr nestedTable) {
        this.nestedTable = nestedTable;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, nestedTable);
        }
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nestedTable == null) ? 0 : nestedTable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        org.druid.sql.dialect.oracle.ast.expr.OracleIsSetExpr other = (org.druid.sql.dialect.oracle.ast.expr.OracleIsSetExpr) obj;
        if (nestedTable == null) {
            if (other.nestedTable != null) {
                return false;
            }
        } else if (!nestedTable.equals(other.nestedTable)) {
            return false;
        }
        return true;
    }

}
