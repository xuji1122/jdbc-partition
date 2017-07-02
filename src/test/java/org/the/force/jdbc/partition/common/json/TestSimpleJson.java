package org.the.force.jdbc.partition.common.json;

import org.testng.annotations.Test;

/**
 * Created by xuji on 2017/7/2.
 */
@Test
public class TestSimpleJson {

    public void test1() {
        String json = "{ \"k\"  : [{\"1\":1},2,3],\"k1\":{\"tt\":1}  }";
        JsonParser jsonParser = new JsonParser(json);
        Object obj = jsonParser.parse();
        System.out.println(obj);
    }
    public void test2() {
        String json = "{\"k\":\"1\"}";
        JsonParser jsonParser = new JsonParser(json);
        Object obj = jsonParser.parse();
        System.out.println(obj);
    }
    public void test3() {
        String json = "{  \"k\"  : [1,2,3]  }";
        JsonParser jsonParser = new JsonParser(json);
        Object obj = jsonParser.parse();
        System.out.println(obj);
    }
}
