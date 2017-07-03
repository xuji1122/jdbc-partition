package org.the.force.jdbc.partition.common;

import org.testng.annotations.Test;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by xuji on 2017/6/2.
 */
public class TestJavaUtil {

    private Log logger = LogFactory.getLog(TestJavaUtil.class);

    @Test
    public void test1() {
        Map<String, String> map = new Hashtable<>();
        map.put("1", "1-v");
        map.put("2", "2_v");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            for (Map.Entry<String, String> entry1 : map.entrySet()) {
                if (entry.getKey() == entry1.getKey()) {
                    continue;
                }
                logger.info(MessageFormat.format("{0},{1}", entry.getValue(), entry1.getValue()));
            }
        }
    }

    @Test
    public void test2() {
        Set<String> initDbSet = new ConcurrentSkipListSet<>();
        initDbSet.add("1");
        initDbSet.add("2");
        logger.info(initDbSet.size()+"");
        Iterator<String> ite = initDbSet.iterator();
        while (ite.hasNext()) {
            ite.next();
            ite.remove();
        }
        logger.info(initDbSet.size()+"");

    }

    @Test
    public void test3() {
        Object v = new String[] {"123"};
        if (v instanceof String[]) {
            String[] a = (String[]) v;
            logger.info(a.length + "");
        }
    }

}
