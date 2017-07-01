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

import org.druid.sql.SQLUtils;
import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLObjectImpl;
import org.druid.sql.ast.SQLReplaceable;
import org.druid.sql.ast.expr.SQLIdentifierExpr;
import org.druid.sql.ast.expr.SQLPropertyExpr;
import org.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectItem extends SQLObjectImpl implements SQLReplaceable {

    protected SQLExpr expr;
    protected String  alias;
    protected boolean connectByRoot = false;

    public SQLSelectItem(){

    }

    public SQLSelectItem(SQLExpr expr){
        this(expr, null);
    }

    public SQLSelectItem(SQLExpr expr, String alias){
        this.expr = expr;
        this.alias = alias;

        if (expr != null) {
            expr.setParent(this);
        }
    }
    
    public SQLSelectItem(SQLExpr expr, String alias, boolean connectByRoot){
        this.connectByRoot = connectByRoot;
        this.expr = expr;
        this.alias = alias;
        
        if (expr != null) {
            expr.setParent(this);
        }
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
        if (expr != null) {
            expr.setParent(this);
        }
    }

    public String computeAlias() {
        String alias = this.getAlias();
        if (alias == null) {
            if (expr instanceof SQLIdentifierExpr) {
                alias = ((SQLIdentifierExpr) expr).getName();
            } else if (expr instanceof SQLPropertyExpr) {
                alias = ((SQLPropertyExpr) expr).getName();
            }
        }

        return SQLUtils.normalize(alias);
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void output(StringBuffer buf) {
        if(this.connectByRoot) {
            buf.append(" CONNECT_BY_ROOT ");
        }
        this.expr.output(buf);
        if ((this.alias != null) && (this.alias.length() != 0)) {
            buf.append(" AS ");
            buf.append(this.alias);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        org.druid.sql.ast.statement.SQLSelectItem other = (org.druid.sql.ast.statement.SQLSelectItem) obj;
        if (alias == null) {
            if (other.alias != null) return false;
        } else if (!alias.equals(other.alias)) return false;
        if (expr == null) {
            if (other.expr != null) return false;
        } else if (!expr.equals(other.expr)) return false;
        return true;
    }

    public boolean isConnectByRoot() {
        return connectByRoot;
    }

    public void setConnectByRoot(boolean connectByRoot) {
        this.connectByRoot = connectByRoot;
    }

    public org.druid.sql.ast.statement.SQLSelectItem clone() {
        org.druid.sql.ast.statement.SQLSelectItem x = new org.druid.sql.ast.statement.SQLSelectItem();
        x.alias = alias;
        if (expr != null) {
            x.expr = expr.clone();
        }
        x.connectByRoot = connectByRoot;
        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == expr) {
            setExpr(target);
            return true;
        }

        return false;
    }

    public boolean match(String alias) {
        if (alias == null) {
            return false;
        }

        String alias_normalized = SQLUtils.normalize(alias);

        if (alias_normalized.equalsIgnoreCase(this.alias)) {
            return true;
        }

        if (expr instanceof SQLIdentifierExpr) {
            String ident = ((SQLIdentifierExpr) expr).getName();
            return alias_normalized.equalsIgnoreCase(SQLUtils.normalize(ident));
        }

        if (expr instanceof SQLPropertyExpr) {
            String ident = ((SQLPropertyExpr) expr).getName();
            return alias_normalized.equalsIgnoreCase(SQLUtils.normalize(ident));
        }

        return false;
    }
}
