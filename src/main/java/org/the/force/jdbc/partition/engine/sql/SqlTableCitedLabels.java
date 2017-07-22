package org.the.force.jdbc.partition.engine.sql;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/7/6.
 * 在sql语句中某一个tableSource被引用的所有列
 */
public class SqlTableCitedLabels {

    private boolean beCitedAll;

    private Map<String,String> citedLabels = new LinkedHashMap<>();

    public boolean isBeCitedAll() {
        return beCitedAll;
    }

    public void setBeCitedAll(boolean referAll) {
        this.beCitedAll = referAll;
    }

    public void addReferLabel(String refer) {
         citedLabels.put(refer.toLowerCase(),refer);
    }

    public Collection<String> getCitedLabels() {
        return citedLabels.values();
    }

}
