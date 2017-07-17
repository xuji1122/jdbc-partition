package org.the.force.jdbc.partition.engine.sqlelements;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xuji on 2017/7/6.
 * 在sql语句中某一个tableSource引用的所有列
 */
public class SqlTableRefers {

    private boolean referAll;

    private Set<String> refers = new LinkedHashSet<>();

    public boolean isReferAll() {
        return referAll;
    }

    public void setReferAll(boolean referAll) {
        this.referAll = referAll;
    }

    public void addRefer(String refer) {
         refers.add(refer);
    }

    public Set<String> getRefers() {
        return refers;
    }

}
