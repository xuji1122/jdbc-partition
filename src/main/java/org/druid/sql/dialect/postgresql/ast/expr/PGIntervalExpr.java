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
package org.druid.sql.dialect.postgresql.ast.expr;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLExprImpl;
import org.druid.sql.ast.expr.SQLLiteralExpr;
import org.druid.sql.dialect.postgresql.ast.expr.PGExpr;
import org.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import org.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by tianzhen.wtz on 2014/12/29 0029 16:10.
 * 类说明：
 */
public class PGIntervalExpr extends SQLExprImpl implements SQLLiteralExpr,PGExpr {

    private SQLExpr value;


    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        org.druid.sql.dialect.postgresql.ast.expr.PGIntervalExpr that = (org.druid.sql.dialect.postgresql.ast.expr.PGIntervalExpr) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((PGASTVisitor) visitor);
    }
}
