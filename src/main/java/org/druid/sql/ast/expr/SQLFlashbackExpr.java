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
import org.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by wenshao on 14/06/2017.
 */
public class SQLFlashbackExpr extends SQLExprImpl {
    private Type type;
    private SQLExpr expr;

    public SQLFlashbackExpr() {

    }

    public SQLFlashbackExpr(Type type, SQLExpr expr) {
        this.type = type;
        this.setExpr(expr);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }
        visitor.endVisit(this);
    }

    public SQLFlashbackExpr clone() {
        SQLFlashbackExpr x = new SQLFlashbackExpr();
        x.type = this.type;
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLFlashbackExpr that = (SQLFlashbackExpr) o;

        if (type != that.type) return false;
        return expr != null ? expr.equals(that.expr) : that.expr == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (expr != null ? expr.hashCode() : 0);
        return result;
    }

    public static enum Type {
        SCN, TIMESTAMP
    }
}
