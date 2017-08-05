package org.the.force.jdbc.partition.config;

import org.apache.curator.framework.CuratorFramework;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/5/21.
 */
public class ZookeeperDataNode implements DataNode {
    private final ZookeeperDataNode parent;
    private final String path;
    private final CuratorFramework curatorFramework;

    public ZookeeperDataNode(ZookeeperDataNode parent, String path, CuratorFramework curatorFramework) {
        this.parent = parent;
        this.path = path;
        this.curatorFramework = curatorFramework;
    }

    public String getKey() {
        return path;
    }

    public String getPath() {
        StringBuilder sb = new StringBuilder();
        if (parent() != null) {
            sb.append(parent().getPath());
            sb.append("/");
        } else {
            sb.append("/");
        }
        sb.append(path);
        return sb.toString();
    }

    public String getData() throws Exception {
        byte[] data = curatorFramework.getData().forPath(getPath());
        return new String(data, "UTF-8");
    }

    public List<DataNode> children() throws Exception {
        List<String> children = curatorFramework.getChildren().forPath(getPath());
        List<DataNode> list = new ArrayList<>();
        children.stream().forEach(child -> {
            DataNode dataNode = new ZookeeperDataNode(ZookeeperDataNode.this, child, ZookeeperDataNode.this.curatorFramework);
            list.add(dataNode);
        });
        return list;
    }

    public DataNode children(String key) throws Exception {
        List<String> children = curatorFramework.getChildren().forPath(getPath());
        if (children == null || children.isEmpty()) {
            return null;
        }
        children = children.stream().filter(child -> child.equals(key)).collect(Collectors.toList());
        if (children == null || children.isEmpty()) {
            return null;
        }
        DataNode dataNode = new ZookeeperDataNode(ZookeeperDataNode.this, children.get(0), curatorFramework);
        return dataNode;
    }

    public ZookeeperDataNode parent() {
        return parent;
    }
}
