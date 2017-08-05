package org.the.force.jdbc.partition.rule;

import com.google.common.collect.Sets;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;
import org.the.force.jdbc.partition.engine.stmt.SqlColumnValue;
import org.the.force.jdbc.partition.engine.value.types.IntValue;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.comparator.NameComparator;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by xuji on 2017/6/19.
 */
@Test(priority = 300)
public class TestPartitionRule {

    private Log logger = LogFactory.getLog(TestPartitionRule.class);

    @Test(priority = 301)
    public void test2() throws Exception {
        LogicDbManager logicDbConfig = TestSupport.partitionDb.ymlLogicDbConfig;
        LogicTableConfig logicTableConfig = logicDbConfig.getLogicTableManager("t_user").getLogicTableConfig()[0];
        PartitionEvent partitionEvent =
            new PartitionEvent("t_user", PartitionEvent.EventType.INSERT, PartitionRule.PartitionSortType.BY_TABLE, logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        TreeSet<PartitionColumnValue> set = new TreeSet<>();
        set.add(new SqlColumnValue("id", new IntValue(7)));
        DefaultPartitionRule defaultPartitionRule = new DefaultPartitionRule();
        SortedSet<Partition> partitions = defaultPartitionRule.selectPartitions(partitionEvent, set);
        logger.info("partitions:" + partitions.toString());
    }

    public void testSort() throws Exception {
        List<String> list = Arrays.asList("t_9", "t_8", "t_11", "t_07", "t_m_01", "t_m_2");
        Set<String> set = Sets.newTreeSet(NameComparator.getSingleton());
        set.addAll(list);
        logger.info(set.toString());
    }
}
