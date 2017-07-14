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

import org.the.force.thirdparty.druid.sql.ast.SQLSetQuantifier;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTruncateStatement;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.PGWithClause;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGBoxExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGCidrExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGExtractExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGInetExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGIntervalExpr;
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
import org.the.force.thirdparty.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import org.the.force.thirdparty.druid.sql.parser.Token;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTOutputVisitor;
import org.the.force.thirdparty.druid.util.StringUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectStatement;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.PGWithQuery;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGCircleExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGLineSegmentsExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.expr.PGMacAddrExpr;
import org.the.force.thirdparty.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;

public class PGOutputVisitor extends SQLASTOutputVisitor implements PGASTVisitor {

    public PGOutputVisitor(Appendable appender){
        super(appender);
    }

    public PGOutputVisitor(Appendable appender, boolean parameterized){
        super(appender, parameterized);
    }

    @Override
    public void endVisit(PGSelectQueryBlock.WindowClause x) {

    }

    @Override
    public boolean visit(PGSelectQueryBlock.WindowClause x) {
        print0(ucase ? "WINDOW " : "window ");
        x.getName().accept(this);
        print0(ucase ? " AS " : " as ");
        for (int i = 0; i < x.getDefinition().size(); ++i) {
            if (i != 0) {
                println(", ");
            }
            print('(');
            x.getDefinition().get(i).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(PGSelectQueryBlock.FetchClause x) {

    }

    @Override
    public boolean visit(PGSelectQueryBlock.FetchClause x) {
        print0(ucase ? "FETCH " : "fetch ");
        if (PGSelectQueryBlock.FetchClause.Option.FIRST.equals(x.getOption())) {
            print0(ucase ? "FIRST " : "first ");
        } else if (PGSelectQueryBlock.FetchClause.Option.NEXT.equals(x.getOption())) {
            print0(ucase ? "NEXT " : "next ");
        }
        x.getCount().accept(this);
        print0(ucase ? " ROWS ONLY" : " rows only");
        return false;
    }

    @Override
    public void endVisit(PGSelectQueryBlock.ForClause x) {

    }

    @Override
    public boolean visit(PGSelectQueryBlock.ForClause x) {
        print0(ucase ? "FOR " : "for ");
        if (PGSelectQueryBlock.ForClause.Option.UPDATE.equals(x.getOption())) {
            print0(ucase ? "UPDATE " : "dml ");
        } else if (PGSelectQueryBlock.ForClause.Option.SHARE.equals(x.getOption())) {
            print0(ucase ? "SHARE " : "share ");
        }

        if (x.getOf().size() > 0) {
            for (int i = 0; i < x.getOf().size(); ++i) {
                if (i != 0) {
                    println(", ");
                }
                x.getOf().get(i).accept(this);
            }
        }

        if (x.isNoWait()) {
            print0(ucase ? " NOWAIT" : " nowait");
        }

        return false;
    }

    @Override
    public void endVisit(PGWithQuery x) {

    }

    @Override
    public boolean visit(PGWithQuery x) {
        x.getName().accept(this);

        if (x.getColumns().size() > 0) {
            print0(" (");
            printAndAccept(x.getColumns(), ", ");
            print(')');
        }
        println();
        print0(ucase ? "AS" : "as");
        println();
        print('(');
        incrementIndent();
        println();
        x.getQuery().accept(this);
        decrementIndent();
        println();
        print(')');

        return false;
    }

    @Override
    public void endVisit(PGWithClause x) {

    }

    @Override
    public boolean visit(PGWithClause x) {
        print0(ucase ? "WITH" : "with");
        if (x.isRecursive()) {
            print0(ucase ? " RECURSIVE " : " recursive ");
        }
        incrementIndent();
        println();
        printlnAndAccept(x.getWithQuery(), ", ");
        decrementIndent();
        return false;
    }

    public boolean visit(PGSelectQueryBlock x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        print0(ucase ? "SELECT " : "select ");

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");

            if (x.getDistinctOn() != null && x.getDistinctOn().size() > 0) {
                print0(ucase ? "ON " : "on ");
                printAndAccept(x.getDistinctOn(), ", ");
            }
        }

        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            println();
            if (x.getIntoOption() != null) {
                print0(x.getIntoOption().name());
                print(' ');
            }

            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);
        }

        if (x.getFrom() != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        if (x.getGroupBy() != null) {
            println();
            x.getGroupBy().accept(this);
        }

        if (x.getWindow() != null) {
            println();
            x.getWindow().accept(this);
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            println();
            x.getLimit().accept(this);
        }

        if (x.getFetch() != null) {
            println();
            x.getFetch().accept(this);
        }

        if (x.getForClause() != null) {
            println();
            x.getForClause().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        print0(ucase ? "TRUNCATE TABLE " : "truncate select ");
        if (x.isOnly()) {
            print0(ucase ? "ONLY " : "only ");
        }

        printlnAndAccept(x.getTableSources(), ", ");

        if (x.getRestartIdentity() != null) {
            if (x.getRestartIdentity().booleanValue()) {
                print0(ucase ? " RESTART IDENTITY" : " restart identity");
            } else {
                print0(ucase ? " CONTINUE IDENTITY" : " continue identity");
            }
        }

        if (x.getCascade() != null) {
            if (x.getCascade().booleanValue()) {
                print0(ucase ? " CASCADE" : " cascade");
            } else {
                print0(ucase ? " RESTRICT"  : " restrict");
            }
        }
        return false;
    }

    @Override
    public void endVisit(PGDeleteStatement x) {

    }

    @Override
    public boolean visit(PGDeleteStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        print0(ucase ? "DELETE FROM " : "delete from ");

        if (x.isOnly()) {
            print0(ucase ? "ONLY " : "only ");
        }

        printTableSourceExpr(x.getTableName());

        if (x.getAlias() != null) {
            print0(ucase ? " AS " : " as ");
            print0(x.getAlias());
        }

        if (x.getUsing().size() > 0) {
            println();
            print0(ucase ? "USING " : "using ");
            printAndAccept(x.getUsing(), ", ");
        }

        if (x.getWhere() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            incrementIndent();
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
        }

        if (x.isReturning()) {
            println();
            print0(ucase ? "RETURNING *" : "returning *");
        }

        return false;
    }

    @Override
    public void endVisit(PGInsertStatement x) {

    }

    @Override
    public boolean visit(PGInsertStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        print0(ucase ? "INSERT INTO " : "insert into ");

        x.getTableSource().accept(this);

        printInsertColumns(x.getColumns());

        if (x.getValues() != null) {
            println();
            print0(ucase ? "VALUES " : "values ");
            printlnAndAccept(x.getValuesList(), ", ");
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().accept(this);
            }
        }

        if (x.getReturning() != null) {
            println();
            print0(ucase ? "RETURNING " : "returning ");
            x.getReturning().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(PGSelectStatement x) {

    }

    @Override
    public boolean visit(PGSelectStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        return visit((SQLSelectStatement) x);
    }

    @Override
    public void endVisit(PGUpdateStatement x) {

    }

    @Override
    public boolean visit(PGUpdateStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        print0(ucase ? "UPDATE " : "dml ");

        if (x.isOnly()) {
            print0(ucase ? "ONLY " : "only ");
        }

        x.getTableSource().accept(this);

        println();
        print0(ucase ? "SET " : "set ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getItems().get(i).accept(this);
        }

        if (x.getFrom() != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            incrementIndent();
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
        }

        if (x.getReturning().size() > 0) {
            println();
            print0(ucase ? "RETURNING " : "returning ");
            printAndAccept(x.getReturning(), ", ");
        }

        return false;
    }

    @Override
    public void endVisit(PGSelectQueryBlock x) {

    }

    @Override
    public boolean visit(PGFunctionTableSource x) {
        x.getExpr().accept(this);

        if (x.getAlias() != null) {
            print0(ucase ? " AS " : " as ");
            print0(x.getAlias());
        }

        if (x.getParameters().size() > 0) {
            print('(');
            printAndAccept(x.getParameters(), ", ");
            print(')');
        }

        return false;
    }

    @Override
    public void endVisit(PGFunctionTableSource x) {

    }

    @Override
    public void endVisit(PGTypeCastExpr x) {
        
    }

    @Override
    public boolean visit(PGTypeCastExpr x) {
        x.getExpr().accept(this);
        print0("::");
        x.getDataType().accept(this);
        return false;
    }

    @Override
    public void endVisit(PGValuesQuery x) {
        
    }

    @Override
    public boolean visit(PGValuesQuery x) {
        print0(ucase ? "VALUES(" : "values(");
        printAndAccept(x.getValues(), ", ");
        print(')');
        return false;
    }
    
    @Override
    public void endVisit(PGExtractExpr x) {
        
    }
    
    @Override
    public boolean visit(PGExtractExpr x) {
        print0(ucase ? "EXTRACT (" : "extract (");
        print0(x.getField().name());
        print0(ucase ? " FROM " : " from ");
        x.getSource().accept(this);
        print(')');
        return false;
    }
    
    @Override
    public boolean visit(PGBoxExpr x) {
        print0(ucase ? "BOX " : "box ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public void endVisit(PGBoxExpr x) {
        
    }
    
    @Override
    public boolean visit(PGPointExpr x) {
        print0(ucase ? "POINT " : "point ");
        x.getValue().accept(this);
        return false;
    }
    
    @Override
    public void endVisit(PGPointExpr x) {
        
    }
    
    @Override
    public boolean visit(PGMacAddrExpr x) {
        print0("macaddr ");
        x.getValue().accept(this);
        return false;
    }
    
    @Override
    public void endVisit(PGMacAddrExpr x) {
        
    }
    
    @Override
    public boolean visit(PGInetExpr x) {
        print0("inet ");
        x.getValue().accept(this);
        return false;
    }
    
    @Override
    public void endVisit(PGInetExpr x) {
        
    }
    
    @Override
    public boolean visit(PGCidrExpr x) {
        print0("cidr ");
        x.getValue().accept(this);
        return false;
    }
    
    @Override
    public void endVisit(PGCidrExpr x) {
        
    }
    
    @Override
    public boolean visit(PGPolygonExpr x) {
        print0("polygon ");
        x.getValue().accept(this);
        return false;
    }
    
    @Override
    public void endVisit(PGPolygonExpr x) {
        
    }
    
    @Override
    public boolean visit(PGCircleExpr x) {
        print0("circle ");
        x.getValue().accept(this);
        return false;
    }
    
    @Override
    public void endVisit(PGCircleExpr x) {
        
    }
    
    @Override
    public boolean visit(PGLineSegmentsExpr x) {
        print0("lseg ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public void endVisit(PGIntervalExpr x) {

    }

    @Override
    public boolean visit(PGIntervalExpr x) {
        print0(ucase ? "INTERVAL " : "interval ");
        x.getValue().accept(this);
        return true;
    }

    @Override
    public void endVisit(PGLineSegmentsExpr x) {
        
    }
    
    @Override
    public boolean visit(SQLBinaryExpr x) {
        print0(ucase ? "B'" : "b'");
        print0(x.getValue());
        print('\'');

        return false;
    }
    
    @Override
    public void endVisit(PGShowStatement x) {
        
    }
    
    @Override
    public boolean visit(PGShowStatement x) {
        print0(ucase ? "SHOW " : "show ");
        x.getExpr().accept(this);
        return false;
    }

    public boolean visit(SQLLimit x) {
        print0(ucase ? "LIMIT " : "limit ");

        x.getRowCount().accept(this);

        if (x.getOffset() != null) {
            print0(ucase ? " OFFSET " : " offset ");
            x.getOffset().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(PGStartTransactionStatement x) {
        
    }

    @Override
    public boolean visit(PGStartTransactionStatement x) {
        print0(ucase ? "START TRANSACTION" : "start transaction");
        return false;
    }

    @Override
    public void endVisit(PGSetStatement x) {
        
    }

    @Override
    public boolean visit(PGSetStatement x) {
        print0(ucase ? "SET " : "set ");
        if (!StringUtils.isEmpty(x.range)) {
            print0(x.range);
            print0(" ");
        }
        if (PGSQLStatementParser.TIME_ZONE.equalsIgnoreCase(x.param)) {
            print0(PGSQLStatementParser.TIME_ZONE);
            print0(" ");
            x.values.get(0).accept(this);
            return false;
        }
        print0(x.param);
        print0(" ");
        print0(Token.TO.name());
        print0(" ");
        for (int i = 0; i < x.values.size(); i++) {
            if (i != 0) {
                print0(", ");
            }
            x.values.get(i).accept(this);
        }
        return false;
    }
}
