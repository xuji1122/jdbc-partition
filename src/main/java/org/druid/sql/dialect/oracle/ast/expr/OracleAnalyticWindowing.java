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
import org.druid.sql.ast.SQLObjectImpl;
import org.druid.sql.dialect.oracle.ast.expr.OracleExpr;
import org.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.druid.sql.visitor.SQLASTVisitor;

public class OracleAnalyticWindowing extends SQLObjectImpl implements OracleExpr {

    private Type    type;
    private SQLExpr expr;

    public OracleAnalyticWindowing(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public org.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing clone() {
        org.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing x = new org.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing();
        x.type = type;
        if (expr != null) {
            this.setExpr(expr.clone());
        }
        return x;
    }

    public static enum Type {
        ROWS, RANGE;
    }
}
