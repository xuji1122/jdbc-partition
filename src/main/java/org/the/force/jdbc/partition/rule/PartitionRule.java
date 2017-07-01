package org.the.force.jdbc.partition.rule;

import java.util.SortedSet;

/**
 * Created by xuji on 2017/5/14.
 */
public interface PartitionRule {

    /**
     * 分库分表的路由规则
     * 根据 分库分表的字段、sql操作的类型等元素，选择所有可能涉及到的分区
     * <p>
     * 最好返回一个分区结果，如果返回多个Partition那么同一个sql将会在所列举的分区都执行
     *
     * @param partitionEvent          分库分表事件，提供尽可能多的分库分表场景的要素，
     *                                注意，为了保证通过数据库Driver的url就可以访问和操作逻辑数据库，数据库所有的配置信息都同步到zk上，因此partitionEvent携带了数据库配置信息
     * @param partitionColumnValueSet 分库分表所依赖的的分区字段的取值的组合，columnName不重复
     *                                如果一个用于分区的columnName具有多个value,则PartitionRule会调用多次，每次传入一个value,返回的Partition会被去重(参见Partition的hashcode()和equals()方法)，所以你不必担心会重复选择Partition
     * @return 不重复的Partition  Partition相等的标准依据{@link Partition}hashcode()和equals()方法
     */
    SortedSet<Partition> selectPartitions(PartitionEvent partitionEvent, SortedSet<PartitionColumnValue> partitionColumnValueSet);


    /**
     * 分区规则的类型
     * 既可以用于标识{@link Partition}的类型，也用来区分{@link PartitionColumnConfig}的功能，PartitionColumnConfig是原因，Partition是结果
     * PartitionColumnConfig是用来定位选择Partition的依据
     */
    enum RuleType {
        None,//不分库也不分表
        DB,//只用于分库
        TABLE_IN_DB,//用于某个物理库之内的表
        TABLE,//分库分表一步到位,物理表全局唯一，通过物理表直接关联到物理库，做好了物理表的选择就确定了物理库
        ;

        public static RuleType valueOfString(String str) {
            str = str.trim().toUpperCase();
            return RuleType.valueOf(str);
        }

    }


}
