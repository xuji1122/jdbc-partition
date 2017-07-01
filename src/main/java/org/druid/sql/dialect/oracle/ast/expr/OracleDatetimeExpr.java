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
import org.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import org.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleDatetimeExpr extends OracleSQLObjectImpl implements SQLExpr {

    private SQLExpr expr;
    private SQLExpr timeZone;

    public OracleDatetimeExpr(){

    }

    public OracleDatetimeExpr(SQLExpr expr, SQLExpr timeZone){
        this.expr = expr;
        this.timeZone = timeZone;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
            acceptChild(visitor, timeZone);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public SQLExpr getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(SQLExpr timeZone) {
        this.timeZone = timeZone;
    }

    public org.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr clone() {
        org.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr x = new org.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr();

        if (expr != null) {
            x.setExpr(expr.clone());
        }

        if (timeZone != null) {
            x.setTimeZone(timeZone.clone());
        }

        return x;
    }
}
