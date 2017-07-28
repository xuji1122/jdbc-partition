package org.the.force.jdbc.partition.test;

import org.the.force.jdbc.partition.TestSupport;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by xuji on 2017/7/27.
 */


/**
 * 测试基本数据
 * 32个用户
 */
public class TestDataSupport {

    private static Log logger = LogFactory.getLog(TestDataSupport.class);

    public static final long T_USER_ID_BASIC = 32;

    public static final long T_ORDER_ID_BASIC = 2000;

    public static final long T_ORDER_SKU_BASIC = 2000;

    public static final String[] t_user_header =
        new String[] {"insert:INSERT INTO  t_user(id,channel,app_id,identifier,birth_date,status) VALUES(?,?,?,?,?,?)  ON DUPLICATE KEY UPDATE status=status",
            "types:long\tint\tstring\tstring\tdate\tstring"};

    public static final String[] t_order_header =
        new String[] {"insert:INSERT INTO  t_order(id,user_id,total_price,channel,status) VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE status=status",
            "types:long\tlong\tdecimal\tint\tstring"};

    public static final String[] t_order_sku_header =
        new String[] {"insert:INSERT INTO  t_order_sku(id,order_id,sku_stock_id,buy_num,buy_price) VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE buy_price=buy_price ",
            "types:long\tlong\tlong\tint\tdecimal"};

    public static final int[] channelArray = new int[] {1, 2, 3, 4, 5};

    public static final String[] appArray = new String[] {"app_1", "app_2"};

    public static final LocalDate[] birthDateArray = new LocalDate[] {LocalDate.of(1980, 10, 1), LocalDate.of(1980, 9, 1), LocalDate.of(1980, 9, 2), LocalDate.of(1990, 9, 1)};

    public static final long[] sku_stock_id_array = new long[] {101, 102, 103, 104, 105};

    public static final Map<Long, BigDecimal> stockIdPriceMap = new HashMap<>();

    static {
        for (int i = 0; i < sku_stock_id_array.length; i++) {
            stockIdPriceMap.put(sku_stock_id_array[i], new BigDecimal((sku_stock_id_array[i] % 100) * 10));
        }
    }

    //@Test
    public static void loadData() throws Exception {
        SqlCVSFileReader fileReader = new SqlCVSFileReader(TestSupport.toFullPath("data/user/t_user.cvs"));
        List<SqlParameter> sqlParameters = null;
        logger.info("sql=" + fileReader.getSql());
        while ((sqlParameters = fileReader.nextSqlLine()) != null) {
            logger.info(sqlParameters.toString());
        }
    }

    //@Test
    public static void initDataFile() throws Exception {
        PrintWriter userWriter = TestSupport.getPrintWriter("data/user/t_user.cvs");
        PrintWriter orderWriter = TestSupport.getPrintWriter("data/order/t_order.cvs");
        PrintWriter skuWriter = TestSupport.getPrintWriter("data/order/t_order_sku.cvs");
        try {
            userWriter.println(t_user_header[0]);
            userWriter.println(t_user_header[1]);
            orderWriter.println(t_order_header[0]);
            orderWriter.println(t_order_header[1]);
            skuWriter.println(t_order_sku_header[0]);
            skuWriter.println(t_order_sku_header[1]);
            IdStock orderIdStock = new IdStock(T_ORDER_ID_BASIC);
            IdStock orderSkuIdStock = new IdStock(T_ORDER_SKU_BASIC);
            for (int userId = 1; userId <= T_USER_ID_BASIC; userId++) {
                userWriter.print(userId);
                userWriter.print("\t\t");
                userWriter.print(random(channelArray));
                userWriter.print("\t\t");
                userWriter.print(random(appArray));
                userWriter.print("\t\t");
                userWriter.print("user_" + userId);
                userWriter.print("\t\t");
                userWriter.print(random(birthDateArray));
                userWriter.print("\t\t");
                userWriter.print("dataCreate");
                userWriter.println();
                int orderNum = new Random().nextInt(3);
                for (int i = 0; i < orderNum; i++) {
                    long orderId = orderIdStock.nextId();
                    int skuNum = new Random().nextInt(3) + 1;
                    orderWriter.print(orderId);
                    orderWriter.print("\t\t");
                    orderWriter.print(userId);
                    orderWriter.print("\t\t");
                    BigDecimal totalPrice = new BigDecimal("0");
                    for (int k = 0; k < skuNum; k++) {
                        skuWriter.print(orderSkuIdStock.nextId());
                        skuWriter.print("\t\t");
                        skuWriter.print(orderId);
                        skuWriter.print("\t\t");
                        long skuId = random(sku_stock_id_array);
                        skuWriter.print(skuId);
                        skuWriter.print("\t\t");
                        BigDecimal price = stockIdPriceMap.get((skuId));
                        int buyNum = new Random().nextInt(2) + 1;
                        skuWriter.print(buyNum);
                        skuWriter.print("\t\t");
                        skuWriter.print(price.toString());
                        skuWriter.print("\t\t");
                        skuWriter.println();
                        totalPrice = totalPrice.add(price.multiply(new BigDecimal(buyNum)));
                    }
                    orderWriter.print(totalPrice.toString());
                    orderWriter.print("\t\t");
                    orderWriter.print(random(channelArray));
                    orderWriter.print("\t\t");
                    orderWriter.print("dataCreate");
                    orderWriter.println();
                }
            }
        } finally {
            TestSupport.closeFile(userWriter);
            TestSupport.closeFile(orderWriter);
            TestSupport.closeFile(skuWriter);
        }
    }

    public static int random(int[] array) {
        return array[new Random().nextInt(array.length)];
    }

    public static String random(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    public static LocalDate random(LocalDate[] array) {
        return array[new Random().nextInt(array.length)];
    }

    public static long random(long[] array) {
        return array[new Random().nextInt(array.length)];
    }


}
