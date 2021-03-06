package org.the.force.jdbc.partition.rule.comparator;

import java.util.Comparator;

/**
 * Created by xuji on 2017/5/20.
 * partition或资源名字排序工具类
 * 根据排序规则确定 第几个 partition属于第几个  取模的顺序
 */
public class NameComparator implements Comparator<String> {

    private static NameComparator nameComparator = new NameComparator();

    public static NameComparator getSingleton() {
        return nameComparator;
    }

    private final char suffixSeparator;

    private NameComparator() {
        String str = System.getProperty("jdbc.partition.suffix.separator", "_");
        this.suffixSeparator = str.charAt(0);
    }

    public int compare(String o1, String o2) {
        int i1 = o1.lastIndexOf(suffixSeparator);
        int i2 = o2.lastIndexOf(suffixSeparator);
        if (i1 < 0 || i2 < 0 || i1 >= o1.length() - 1 || i2 >= o2.length() - 1) {
            return o1.compareTo(o2);
        }
        int c = o1.substring(0, i1).compareTo(o2.substring(0, i2));
        if (c != 0) {
            return c;
        }
        String s1 = o1.substring(i1 + 1);
        String s2 = o2.substring(i2 + 1);
        if (s1.charAt(0) > '9' || s1.charAt(0) < '0') {
            return s1.compareTo(s2);
        } else {
            try {
                return Integer.parseInt(s1) - Integer.parseInt(s2);
            } catch (NumberFormatException e) {
                return s1.compareTo(s2);
            }
        }
    }

    public static String trimSuffix(String name) {
        int index = name.lastIndexOf(nameComparator.suffixSeparator);
        if (index > 0 && index < name.length() - 1) {
            String suffix = name.substring(index + 1);
            if (isNumber(suffix)) {
                return name.substring(0, index);
            }
        }
        return name;
    }

    public static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }
}
