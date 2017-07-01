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
package org.druid.sql.ast.expr;

import org.druid.sql.ast.SQLExprImpl;
import org.druid.sql.ast.SQLName;
import org.druid.sql.visitor.SQLASTVisitor;

public class SQLIdentifierExpr extends SQLExprImpl implements SQLName {

    private String           name;

    private transient String lowerName;
    private transient Boolean parameter;

    public SQLIdentifierExpr(){

    }

    public SQLIdentifierExpr(String name){
        this.name = name;
    }

    public String getSimpleName() {
        return name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.lowerName = null;
    }

    public String getLowerName() {
        if (lowerName == null && name != null) {
            lowerName = name.toLowerCase();
        }
        return lowerName;
    }

    public Boolean isParameter() {
        return parameter;
    }

    public void setParameter(Boolean parameter) {
        this.parameter = parameter;
    }

    public void output(StringBuffer buf) {
        buf.append(this.name);
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (!(obj instanceof org.druid.sql.ast.expr.SQLIdentifierExpr)) {
            return false;
        }
        org.druid.sql.ast.expr.SQLIdentifierExpr other = (org.druid.sql.ast.expr.SQLIdentifierExpr) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return this.name;
    }

    public org.druid.sql.ast.expr.SQLIdentifierExpr clone() {
        return new org.druid.sql.ast.expr.SQLIdentifierExpr(this.name);
    }
}
