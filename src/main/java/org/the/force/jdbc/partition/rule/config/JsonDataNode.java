package org.the.force.jdbc.partition.rule.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/5/21.
 */
public class JsonDataNode implements DataNode {

    private static Log logger = LogFactory.getLog(JsonDataNode.class);

    private final String key;
    private final String data;
    private final JsonDataNode parent;
    private final Set<JsonDataNode> children = new LinkedHashSet<>();

    public JsonDataNode(JsonDataNode parent, String key, Map<String, Object> map) {
        this.parent = parent;
        this.key = key;
        Object c = map.get("children");
        if (c == null) {
            //叶子节点
            this.data = BeanUtils.toJson(map);
        } else {
            Map<String, Object> dataObject = (Map<String, Object>) map.get("data");
            String jsonData = BeanUtils.toJson(dataObject);
            if (jsonData.equalsIgnoreCase("null")) {
                this.data = null;
            } else {
                this.data = jsonData;
            }
            if (c instanceof Map<?, ?>) {
                Map<String, Object> childs = (Map<String, Object>) c;
                childs.forEach((k, v) -> {
                    JsonDataNode cNode = new JsonDataNode(JsonDataNode.this, k, (Map<String, Object>) v);
                    children.add(cNode);
                });
            } else if (c instanceof Collection<?>) {
                Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) c;
                AtomicInteger count = new AtomicInteger(0);
                collection.forEach(v -> {
                    JsonDataNode cNode = new JsonDataNode(JsonDataNode.this, count.get() + "", v);
                    count.addAndGet(1);
                    children.add(cNode);
                });
            }
        }

    }

    public String getKey() {
        return key;
    }

    public String getData() {
        return data;
    }

    public String getPath() {
        StringBuilder sb = new StringBuilder();
        if (parent != null) {
            sb.append(parent.getPath());
            sb.append("/");
        }
        sb.append(key);
        return sb.toString();
    }

    public List<DataNode> getChildren() {
        return children.stream().collect(Collectors.toList());
    }

    public List<DataNode> children() {
        return getChildren();
    }

    public DataNode children(String key) {
        List<DataNode> ds = getChildren();
        ds = ds.stream().filter(dataNode -> key.equals(dataNode.getKey())).collect(Collectors.toList());
        if (ds == null || ds.isEmpty()) {
            return null;
        }
        return ds.get(0);
    }


    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        JsonDataNode that = (JsonDataNode) o;

        if (!getKey().equals(that.getKey()))
            return false;
        if (parent == null) {
            if (that.parent == null) {
                return true;
            } else {
                return false;
            }
        }
        return parent.equals(that.parent);
    }



    public int hashCode() {
        int result = getKey().hashCode();
        if (parent == null) {
            return result;
        }
        result = 31 * result + parent.hashCode();
        return result;
    }

    //数据写入zk
    public void writeToZk(CuratorFramework curatorFramework) throws Exception {
        createNode(curatorFramework);
        setData(curatorFramework);
        for (JsonDataNode jsonDataNode : children) {
            jsonDataNode.writeToZk(curatorFramework);
        }
    }

    private void createNode(CuratorFramework curatorFramework) throws Exception {
        try {
            String path = "/" + getPath();
            Stat stat = curatorFramework.checkExists().forPath(path);
            if (stat == null) {
                String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, null);
                logger.info("path=" + path + ",result=" + result);
            }
        } catch (KeeperException.NodeExistsException e) {

        }
    }

    private void setData(CuratorFramework curatorFramework) throws Exception {
        if (this.data == null) {
            return;
        }
        String path = "/" + getPath();
        byte[] data = this.data.getBytes("UTF-8");
        Stat stat = curatorFramework.setData().forPath(path, data);
        logger.info("setTableData:" + stat.toString());
    }
}
