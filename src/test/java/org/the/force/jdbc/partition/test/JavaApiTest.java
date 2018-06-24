package org.the.force.jdbc.partition.test;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuji on 2018/3/17.
 */
public class JavaApiTest {

    @Test
    public void test1() {

        StringBuilder sb = new StringBuilder(4);
        sb.append("xxxxxxxx");
        sb.toString();

        StringBuffer sb2 = new StringBuffer(4);
        sb2.append("xxxxxxxx");
        sb2.toString();
    }

    @Test
    public void test2() {
        Map<String, Integer> map = new HashMap<>();
        map.put("123", 1);
        map.put("123", 2);
    }

    @Test
    public void test3() {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put("123", 1);
        map.put("123", 2);
    }
}
