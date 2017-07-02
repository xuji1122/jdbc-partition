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
package org.the.force.thirdparty.druid.sql.dialect.oracle.ast.clause;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.the.force.thirdparty.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;

import java.util.ArrayList;
import java.util.List;

public class CycleClause extends OracleSQLObjectImpl {

    private final List<SQLExpr> aliases = new ArrayList<SQLExpr>();
    private SQLExpr mark;
    private SQLExpr value;
    private SQLExpr defaultValue;

    public SQLExpr getMark() {
        return mark;
    }

    public void setMark(SQLExpr mark) {
        this.mark = mark;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public SQLExpr getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(SQLExpr defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<SQLExpr> getAliases() {
        return aliases;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, aliases);
            acceptChild(visitor, mark);
            acceptChild(visitor, value);
            acceptChild(visitor, defaultValue);
        }
        visitor.endVisit(this);
    }

}
