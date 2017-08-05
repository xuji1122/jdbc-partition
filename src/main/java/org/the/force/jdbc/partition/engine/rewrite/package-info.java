/**
 * Created by xuji on 2017/7/28.
 * sql改写
 * 其实就是输出sql,确定实际发给数据库的参数
 * 但是
 * 1，在存在子查询的情况下，sql改写是需要获取子查询的结果集作为单表sql的参数
 * 2，在单库多表的查询sql中，支持把多表的sql组装为union查询sql
 */
package org.the.force.jdbc.partition.engine.rewrite;
