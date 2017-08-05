package org.the.force.jdbc.partition.resource.db;

import org.the.force.jdbc.partition.common.json.JsonParser;
import org.the.force.jdbc.partition.config.DataNode;

import java.util.Map;

/**
 * Created by xuji on 2017/5/14.
 */
public class PhysicDbConfigImpl implements PhysicDbConfig {
    private final String physicDbName;//原生的dbName
    private final String url;

    public PhysicDbConfigImpl(DataNode physicDb, String physicDbName) throws Exception {
        String json = physicDb.getData();
        JsonParser jsonParser = new JsonParser(json);
        Map<String, Object> physicDbJonObject = jsonParser.parse();
        this.physicDbName = physicDbName;
        this.url = physicDbJonObject.get("url").toString().trim();
    }

    public String getPhysicDbName() {
        return physicDbName;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PhysicDbConfigImpl that = (PhysicDbConfigImpl) o;

        return getPhysicDbName().equals(that.getPhysicDbName());

    }

    @Override
    public int hashCode() {
        return getPhysicDbName().hashCode();
    }

    @Override
    public String toString() {
        return physicDbName;
    }
}
