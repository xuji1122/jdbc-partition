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

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLExprImpl;
import org.druid.sql.ast.SQLName;
import org.druid.sql.ast.expr.SQLIdentifierExpr;
import org.druid.sql.visitor.SQLASTVisitor;

public class SQLPropertyExpr extends SQLExprImpl implements SQLName {

    private SQLExpr owner;
    private String  name;

    public SQLPropertyExpr(String owner, String name){
        this(new org.druid.sql.ast.expr.SQLIdentifierExpr(owner), name);
    }

    public SQLPropertyExpr(SQLExpr owner, String name){
        setOwner(owner);
        this.name = name;
    }

    public SQLPropertyExpr(){

    }

    public String getSimpleName() {
        return name;
    }

    public SQLExpr getOwner() {
        return this.owner;
    }

    public String getOwnernName() {
        if (owner instanceof org.druid.sql.ast.expr.SQLIdentifierExpr) {
            return ((org.druid.sql.ast.expr.SQLIdentifierExpr) owner).getName();
        }

        return null;
    }

    public void setOwner(SQLExpr owner) {
        if (owner != null) {
            owner.setParent(this);
        }
        this.owner = owner;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void output(StringBuffer buf) {
        this.owner.output(buf);
        buf.append(".");
        buf.append(this.name);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.owner);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
        if (!(obj instanceof org.druid.sql.ast.expr.SQLPropertyExpr)) {
            return false;
        }
        org.druid.sql.ast.expr.SQLPropertyExpr other = (org.druid.sql.ast.expr.SQLPropertyExpr) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        return true;
    }

    public org.druid.sql.ast.expr.SQLPropertyExpr clone() {
        org.druid.sql.ast.expr.SQLPropertyExpr propertyExpr = new org.druid.sql.ast.expr.SQLPropertyExpr();
        propertyExpr.name = this.name;
        if (owner != null) {
            propertyExpr.setOwner(owner.clone());
        }
        return propertyExpr;
    }

    public boolean matchOwner(String alias) {
        if (owner instanceof org.druid.sql.ast.expr.SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) owner).getName().equalsIgnoreCase(alias);
        }

        return false;
    }
}
