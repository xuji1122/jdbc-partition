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
package org.the.force.thirdparty.druid.sql.dialect.postgresql.visitor;

import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.PGWithClause;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGBoxExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGCidrExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGCircleExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGExtractExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGInetExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGIntervalExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGMacAddrExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGPointExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGPolygonExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGTypeCastExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGFunctionTableSource;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGSetStatement;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGShowStatement;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGStartTransactionStatement;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGValuesQuery;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitorAdapter;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.PGWithQuery;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGLineSegmentsExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;

public class PGASTVisitorAdapter extends SQLASTVisitorAdapter implements PGASTVisitor {

    @Override
    public void endVisit(PGSelectQueryBlock.WindowClause x) {

    }

    @Override
    public boolean visit(PGSelectQueryBlock.WindowClause x) {

        return true;
    }

    @Override
    public void endVisit(PGSelectQueryBlock.FetchClause x) {

    }

    @Override
    public boolean visit(PGSelectQueryBlock.FetchClause x) {

        return true;
    }

    @Override
    public void endVisit(PGSelectQueryBlock.ForClause x) {

    }

    @Override
    public boolean visit(PGSelectQueryBlock.ForClause x) {

        return true;
    }

    @Override
    public void endVisit(PGWithQuery x) {

    }

    @Override
    public boolean visit(PGWithQuery x) {

        return true;
    }

    @Override
    public void endVisit(PGWithClause x) {

    }

    @Override
    public boolean visit(PGWithClause x) {
        return true;
    }

    @Override
    public void endVisit(PGDeleteStatement x) {

    }

    @Override
    public boolean visit(PGDeleteStatement x) {

        return true;
    }

    @Override
    public void endVisit(PGInsertStatement x) {

    }

    @Override
    public boolean visit(PGInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGSelectStatement x) {

    }

    @Override
    public boolean visit(PGSelectStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGUpdateStatement x) {

    }

    @Override
    public boolean visit(PGUpdateStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGSelectQueryBlock x) {

    }

    @Override
    public boolean visit(PGSelectQueryBlock x) {
        return true;
    }

    @Override
    public void endVisit(PGFunctionTableSource x) {

    }

    @Override
    public boolean visit(PGFunctionTableSource x) {
        return true;
    }
	
	@Override
	public boolean visit(PGTypeCastExpr x) {
	    return true;
	}
	
	@Override
	public void endVisit(PGTypeCastExpr x) {
	    
	}

    @Override
    public void endVisit(PGValuesQuery x) {
        
    }

    @Override
    public boolean visit(PGValuesQuery x) {
        return true;
    }
    
    @Override
    public void endVisit(PGExtractExpr x) {
        
    }
    
    @Override
    public boolean visit(PGExtractExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGBoxExpr x) {
        
    }
    
    @Override
    public boolean visit(PGBoxExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGPointExpr x) {
        
    }
    
    @Override
    public boolean visit(PGPointExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGMacAddrExpr x) {
        
    }
    
    @Override
    public boolean visit(PGMacAddrExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGInetExpr x) {
        
    }
    
    @Override
    public boolean visit(PGInetExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGCidrExpr x) {
        
    }
    
    @Override
    public boolean visit(PGCidrExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGPolygonExpr x) {
        
    }
    
    @Override
    public boolean visit(PGPolygonExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGCircleExpr x) {
        
    }
    
    @Override
    public boolean visit(PGCircleExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGLineSegmentsExpr x) {
        
    }
    
    @Override
    public boolean visit(PGLineSegmentsExpr x) {
        return true;
    }

    @Override
    public void endVisit(PGIntervalExpr x) {

    }

    @Override
    public boolean visit(PGIntervalExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGShowStatement x) {
        
    }
    
    @Override
    public boolean visit(PGShowStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGStartTransactionStatement x) {
        
    }

    @Override
    public boolean visit(PGStartTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGSetStatement x) {
        
    }

    @Override
    public boolean visit(PGSetStatement x) {
        return true;
    }

}
