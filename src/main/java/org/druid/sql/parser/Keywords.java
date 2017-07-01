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
package org.druid.sql.parser;

import org.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class Keywords {

    private final Map<String, org.druid.sql.parser.Token> keywords;

    public final static org.druid.sql.parser.Keywords DEFAULT_KEYWORDS;

    public final static org.druid.sql.parser.Keywords SQLITE_KEYWORDS;

    static {
        Map<String, org.druid.sql.parser.Token> map = new HashMap<String, org.druid.sql.parser.Token>();

        map.put("ALL", org.druid.sql.parser.Token.ALL);
        map.put("ALTER", org.druid.sql.parser.Token.ALTER);
        map.put("AND", org.druid.sql.parser.Token.AND);
        map.put("ANY", org.druid.sql.parser.Token.ANY);
        map.put("AS", org.druid.sql.parser.Token.AS);

        map.put("ENABLE", org.druid.sql.parser.Token.ENABLE);
        map.put("DISABLE", org.druid.sql.parser.Token.DISABLE);

        map.put("ASC", org.druid.sql.parser.Token.ASC);
        map.put("BETWEEN", org.druid.sql.parser.Token.BETWEEN);
        map.put("BY", org.druid.sql.parser.Token.BY);
        map.put("CASE", org.druid.sql.parser.Token.CASE);
        map.put("CAST", org.druid.sql.parser.Token.CAST);

        map.put("CHECK", org.druid.sql.parser.Token.CHECK);
        map.put("CONSTRAINT", org.druid.sql.parser.Token.CONSTRAINT);
        map.put("CREATE", org.druid.sql.parser.Token.CREATE);
        map.put("DATABASE", org.druid.sql.parser.Token.DATABASE);
        map.put("DEFAULT", org.druid.sql.parser.Token.DEFAULT);
        map.put("COLUMN", org.druid.sql.parser.Token.COLUMN);
        map.put("TABLESPACE", org.druid.sql.parser.Token.TABLESPACE);
        map.put("PROCEDURE", org.druid.sql.parser.Token.PROCEDURE);
        map.put("FUNCTION", org.druid.sql.parser.Token.FUNCTION);

        map.put("DELETE", org.druid.sql.parser.Token.DELETE);
        map.put("DESC", org.druid.sql.parser.Token.DESC);
        map.put("DISTINCT", org.druid.sql.parser.Token.DISTINCT);
        map.put("DROP", org.druid.sql.parser.Token.DROP);
        map.put("ELSE", org.druid.sql.parser.Token.ELSE);
        map.put("EXPLAIN", org.druid.sql.parser.Token.EXPLAIN);
        map.put("EXCEPT", org.druid.sql.parser.Token.EXCEPT);

        map.put("END", org.druid.sql.parser.Token.END);
        map.put("ESCAPE", org.druid.sql.parser.Token.ESCAPE);
        map.put("EXISTS", org.druid.sql.parser.Token.EXISTS);
        map.put("FOR", org.druid.sql.parser.Token.FOR);
        map.put("FOREIGN", org.druid.sql.parser.Token.FOREIGN);

        map.put("FROM", org.druid.sql.parser.Token.FROM);
        map.put("FULL", org.druid.sql.parser.Token.FULL);
        map.put("GROUP", org.druid.sql.parser.Token.GROUP);
        map.put("HAVING", org.druid.sql.parser.Token.HAVING);
        map.put("IN", org.druid.sql.parser.Token.IN);

        map.put("INDEX", org.druid.sql.parser.Token.INDEX);
        map.put("INNER", org.druid.sql.parser.Token.INNER);
        map.put("INSERT", org.druid.sql.parser.Token.INSERT);
        map.put("INTERSECT", org.druid.sql.parser.Token.INTERSECT);
        map.put("INTERVAL", org.druid.sql.parser.Token.INTERVAL);

        map.put("INTO", org.druid.sql.parser.Token.INTO);
        map.put("IS", org.druid.sql.parser.Token.IS);
        map.put("JOIN", org.druid.sql.parser.Token.JOIN);
        map.put("KEY", org.druid.sql.parser.Token.KEY);
        map.put("LEFT", org.druid.sql.parser.Token.LEFT);

        map.put("LIKE", org.druid.sql.parser.Token.LIKE);
        map.put("LOCK", org.druid.sql.parser.Token.LOCK);
        map.put("MINUS", org.druid.sql.parser.Token.MINUS);
        map.put("NOT", org.druid.sql.parser.Token.NOT);

        map.put("NULL", org.druid.sql.parser.Token.NULL);
        map.put("ON", org.druid.sql.parser.Token.ON);
        map.put("OR", org.druid.sql.parser.Token.OR);
        map.put("ORDER", org.druid.sql.parser.Token.ORDER);
        map.put("OUTER", org.druid.sql.parser.Token.OUTER);

        map.put("PRIMARY", org.druid.sql.parser.Token.PRIMARY);
        map.put("REFERENCES", org.druid.sql.parser.Token.REFERENCES);
        map.put("RIGHT", org.druid.sql.parser.Token.RIGHT);
        map.put("SCHEMA", org.druid.sql.parser.Token.SCHEMA);
        map.put("SELECT", org.druid.sql.parser.Token.SELECT);

        map.put("SET", org.druid.sql.parser.Token.SET);
        map.put("SOME", org.druid.sql.parser.Token.SOME);
        map.put("TABLE", org.druid.sql.parser.Token.TABLE);
        map.put("THEN", org.druid.sql.parser.Token.THEN);
        map.put("TRUNCATE", org.druid.sql.parser.Token.TRUNCATE);

        map.put("UNION", org.druid.sql.parser.Token.UNION);
        map.put("UNIQUE", org.druid.sql.parser.Token.UNIQUE);
        map.put("UPDATE", org.druid.sql.parser.Token.UPDATE);
        map.put("VALUES", org.druid.sql.parser.Token.VALUES);
        map.put("VIEW", org.druid.sql.parser.Token.VIEW);
        map.put("SEQUENCE", org.druid.sql.parser.Token.SEQUENCE);
        map.put("TRIGGER", org.druid.sql.parser.Token.TRIGGER);
        map.put("USER", org.druid.sql.parser.Token.USER);

        map.put("WHEN", org.druid.sql.parser.Token.WHEN);
        map.put("WHERE", org.druid.sql.parser.Token.WHERE);
        map.put("XOR", org.druid.sql.parser.Token.XOR);

        map.put("OVER", org.druid.sql.parser.Token.OVER);
        map.put("TO", org.druid.sql.parser.Token.TO);
        map.put("USE", org.druid.sql.parser.Token.USE);

        map.put("REPLACE", org.druid.sql.parser.Token.REPLACE);

        map.put("COMMENT", org.druid.sql.parser.Token.COMMENT);
        map.put("COMPUTE", org.druid.sql.parser.Token.COMPUTE);
        map.put("WITH", org.druid.sql.parser.Token.WITH);
        map.put("GRANT", org.druid.sql.parser.Token.GRANT);
        map.put("REVOKE", org.druid.sql.parser.Token.REVOKE);

        // MySql procedure: add by zz
        map.put("WHILE", org.druid.sql.parser.Token.WHILE);
        map.put("DO", org.druid.sql.parser.Token.DO);
        map.put("DECLARE", org.druid.sql.parser.Token.DECLARE);
        map.put("LOOP", org.druid.sql.parser.Token.LOOP);
        map.put("LEAVE", org.druid.sql.parser.Token.LEAVE);
        map.put("ITERATE", org.druid.sql.parser.Token.ITERATE);
        map.put("REPEAT", org.druid.sql.parser.Token.REPEAT);
        map.put("UNTIL", org.druid.sql.parser.Token.UNTIL);
        map.put("OPEN", org.druid.sql.parser.Token.OPEN);
        map.put("CLOSE", org.druid.sql.parser.Token.CLOSE);
        map.put("CURSOR", org.druid.sql.parser.Token.CURSOR);
        map.put("FETCH", org.druid.sql.parser.Token.FETCH);
        map.put("OUT", org.druid.sql.parser.Token.OUT);
        map.put("INOUT", org.druid.sql.parser.Token.INOUT);

        DEFAULT_KEYWORDS = new org.druid.sql.parser.Keywords(map);

        Map<String, org.druid.sql.parser.Token> sqlitemap = new HashMap<String, org.druid.sql.parser.Token>();

        sqlitemap.putAll(org.druid.sql.parser.Keywords.DEFAULT_KEYWORDS.getKeywords());

        sqlitemap.put("LIMIT", org.druid.sql.parser.Token.LIMIT);
        SQLITE_KEYWORDS = new org.druid.sql.parser.Keywords(sqlitemap);
    }

    public boolean containsValue(org.druid.sql.parser.Token token) {
        return this.keywords.containsValue(token);
    }

    public Keywords(Map<String, org.druid.sql.parser.Token> keywords){
        this.keywords = keywords;
    }

    public org.druid.sql.parser.Token getKeyword(String key) {
        key = key.toUpperCase();
        return keywords.get(key);
    }

    public Map<String, Token> getKeywords() {
        return keywords;
    }

}
