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
package org.the.force.thirdparty.druid.sql.ast.statement;

import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.SQLObjectImpl;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

public class SQLUnionQuery extends SQLObjectImpl implements SQLSelectQuery {

    private boolean          bracket  = false;

    private SQLSelectQuery left;
    private SQLSelectQuery right;
    private SQLUnionOperator operator = SQLUnionOperator.UNION;
    private SQLOrderBy orderBy;

    private SQLLimit limit;

    public SQLUnionOperator getOperator() {
        return operator;
    }

    public void setOperator(SQLUnionOperator operator) {
        this.operator = operator;
    }

    public SQLUnionQuery(){

    }

    public SQLSelectQuery getLeft() {
        return left;
    }

    public void setLeft(SQLSelectQuery left) {
        if (left != null) {
            left.setParent(this);
        }
        this.left = left;
    }

    public SQLSelectQuery getRight() {
        return right;
    }

    public void setRight(SQLSelectQuery right) {
        if (right != null) {
            right.setParent(this);
        }
        this.right = right;
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        this.orderBy = orderBy;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, left);
            acceptChild(visitor, right);
            acceptChild(visitor, orderBy);
            acceptChild(visitor, limit);
        }
        visitor.endVisit(this);
    }


    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit limit) {
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

    public boolean isBracket() {
        return bracket;
    }

    public void setBracket(boolean bracket) {
        this.bracket = bracket;
    }
}