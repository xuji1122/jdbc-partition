package org.the.force.jdbc.partition.resource.db;

import net.sf.json.JSONObject;
import org.the.force.jdbc.partition.rule.config.DataNode;

/**
 * Created by xuji on 2017/5/14.
 */
public class PhysicDbConfigImpl implements PhysicDbConfig {
    private final String physicDbName;//原生的dbName
    private final String url;

    public PhysicDbConfigImpl(DataNode physicDb,String physicDbName) throws Exception{
        String json = physicDb.getData();
        JSONObject physicDbJonObject = JSONObject.fromObject(json);
        this.physicDbName = physicDbName;
        this.url = physicDbJonObject.getString("url");
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
