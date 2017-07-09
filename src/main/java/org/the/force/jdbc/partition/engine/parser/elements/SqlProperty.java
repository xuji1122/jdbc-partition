package org.the.force.jdbc.partition.engine.parser.elements;

/**
 * Created by xuji on 2017/5/27.
 */
public class SqlProperty {
    private final String ownerName;
    private final String name;

    public SqlProperty(String ownerName, String name) {
        this.ownerName = ownerName;
        this.name = name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getName() {
        return name;
    }
}
