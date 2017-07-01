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

import org.druid.sql.ast.statement.SQLUnionQuery;
import org.druid.sql.visitor.SQLASTVisitor;

public class SQLUnionQueryTableSource extends SQLTableSourceImpl {

    private org.druid.sql.ast.statement.SQLUnionQuery union;

    public SQLUnionQueryTableSource(){

    }

    public SQLUnionQueryTableSource(String alias){
        super(alias);
    }

    public SQLUnionQueryTableSource(org.druid.sql.ast.statement.SQLUnionQuery union, String alias){
        super(alias);
        this.setUnion(union);
    }

    public SQLUnionQueryTableSource(org.druid.sql.ast.statement.SQLUnionQuery union){
        this.setUnion(union);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, union);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("(");
        this.union.output(buf);
        buf.append(")");
    }

    public org.druid.sql.ast.statement.SQLUnionQuery getUnion() {
        return union;
    }

    public void setUnion(SQLUnionQuery union) {
        if (union != null) {
            union.setParent(this);
        }
        this.union = union;
    }
}
