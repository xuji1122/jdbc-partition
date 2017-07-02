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
package org.the.force.thirdparty.druid.sql.ast;

import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

public abstract class SQLStatementImpl extends SQLObjectImpl implements SQLStatement {

    private String dbType;

    private boolean afterSemi;

    public SQLStatementImpl(){

    }
    
    public SQLStatementImpl(String dbType){
        this.dbType = dbType;
    }
    
    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String toString() {
        return SQLUtils.toSQLString(this, dbType);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public boolean isAfterSemi() {
        return afterSemi;
    }

    public void setAfterSemi(boolean afterSemi) {
        this.afterSemi = afterSemi;
    }
}