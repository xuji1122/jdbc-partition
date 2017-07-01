package org.the.force.jdbc.partition.resource.db;

/**
 * Created by xuji on 2017/5/19.
 */
public interface PhysicDbConfig  {

    //同一个逻辑数据库范围内，唯一性约束
    String getPhysicDbName();

    String getUrl();

}
