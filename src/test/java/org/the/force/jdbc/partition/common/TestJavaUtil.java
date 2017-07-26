package org.the.force.jdbc.partition.common;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.engine.sql.query.OrderByItem;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
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
        logger.info(initDbSet.size() + "");
        Iterator<String> ite = initDbSet.iterator();
        while (ite.hasNext()) {
            ite.next();
            ite.remove();
        }
        logger.info(initDbSet.size() + "");

    }

    @Test
    public void test3() {
        Object v = new String[] {"123"};
        if (v instanceof String[]) {
            String[] a = (String[]) v;
            logger.info(a.length + "");
        }
    }

    @Test
    public void test4() {
        TableConditionParser.StackArray stackArray = new TableConditionParser.StackArray(2);
        for (int i = 0; i < 5; i++) {
            stackArray.push(true);
        }
        for (int i = 0; i < 5; i++) {
            stackArray.pop();
        }

    }
    @Test
    public void test5() {
        List<Integer> list = new ArrayList<>();
        list.add(9);
        list.add(3);
        list.add(5);
        int[] order = new int[] {0, 0, 0};
        Collections.sort(list, (o1, o2) -> {
            int size = order.length;
            int o1P = size;
            int o2P = size;
            for (int i = 0; i < size; i++) {
                if (o1P == size && order[i] == o1.intValue()) {
                    o1P = i;
                }
                if (o2P == size && order[i] == o2.intValue()) {
                    o2P = i;
                }
            }
            return o1P - o2P;
        });
        logger.info("result:" + list);
    }

}
