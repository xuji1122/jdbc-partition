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
package org.druid.sql.dialect.oracle.visitor;

import org.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.druid.sql.ast.expr.SQLNumberExpr;
import org.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import org.druid.sql.visitor.ExportParameterVisitor;
import org.druid.sql.visitor.ExportParameterVisitorUtils;
import org.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import org.druid.sql.visitor.ParameterizedVisitor;

public class OracleParameterizedOutputVisitor extends OracleOutputVisitor implements ParameterizedVisitor {

    public OracleParameterizedOutputVisitor(){
        this(new StringBuilder());
        this.parameterized = true;
    }

    public OracleParameterizedOutputVisitor(Appendable appender){
        super(appender);
        this.parameterized = true;
    }

    public OracleParameterizedOutputVisitor(Appendable appender, boolean printPostSemi){
        super(appender, printPostSemi);
        this.parameterized = true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        x = ParameterizedOutputVisitorUtils.merge(this, x);

        return super.visit(x);
    }

    public boolean visit(SQLNumberExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        print('?');
        incrementReplaceCunt();
        
        if(this instanceof ExportParameterVisitor || this.parameters != null){
            ExportParameterVisitorUtils.exportParameter((this).getParameters(), x);
        }
        return false;
    }

}
