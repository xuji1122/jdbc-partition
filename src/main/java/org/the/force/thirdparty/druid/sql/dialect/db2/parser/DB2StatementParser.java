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
package org.the.force.thirdparty.druid.sql.dialect.db2.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.parser.Lexer;
import org.the.force.thirdparty.druid.sql.parser.SQLStatementParser;
import org.the.force.thirdparty.druid.sql.parser.Token;
import org.the.force.thirdparty.druid.sql.dialect.db2.ast.stmt.DB2ValuesStatement;

import java.util.List;


public class DB2StatementParser extends SQLStatementParser {
    public DB2StatementParser(String sql) {
        super (new DB2ExprParser(sql));
    }

    public DB2StatementParser(Lexer lexer){
        super(new DB2ExprParser(lexer));
    }
    
    public DB2SelectParser createSQLSelectParser() {
        return new DB2SelectParser(this.exprParser);
    }
    
    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();
            DB2ValuesStatement stmt = new DB2ValuesStatement();
            stmt.setExpr(this.exprParser.expr());
            statementList.add(stmt);
            return true;
        }
        
        return false;
    }
}
