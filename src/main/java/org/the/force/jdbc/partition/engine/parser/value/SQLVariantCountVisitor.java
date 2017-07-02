package org.the.force.jdbc.partition.engine.parser.value;

import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/5/26.
 */
public class SQLVariantCountVisitor extends AbstractVisitor {


    private List<Integer> indexes = new ArrayList<>();

    public boolean visit(SQLVariantRefExpr x) {
        indexes.add(x.getIndex());
        return false;
    }

    public int getTotal() {
        return indexes.size();
    }

    public int getOriginStartIndex() {
        if (indexes.isEmpty()) {
            return -1;
        }
        return indexes.get(0);
    }

    public int getOriginEndIndex() {
        if (indexes.isEmpty()) {
            return 0;
        }
        return indexes.get(indexes.size() - 1) + 1;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }
}
