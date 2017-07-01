package org.the.force.jdbc.partition.rule.config;

import org.apache.curator.framework.CuratorFramework;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/5/21.
 */
public class ZKDataNode implements DataNode {
    private final ZKDataNode parent;
    private final String path;
    private final CuratorFramework curatorFramework;

    public ZKDataNode(ZKDataNode parent, String path, CuratorFramework curatorFramework) {
        this.parent = parent;
        this.path = path;
        this.curatorFramework = curatorFramework;
    }
    public String getKey(){
        return path;
    }
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        if (parent() != null) {
            sb.append(parent().getPath());
            sb.append("/");
        }
        sb.append(path);
        return sb.toString();
    }

    public String getData() throws Exception {
        byte[] data = curatorFramework.getData().forPath("/" + getPath());
        return new String(data, "UTF-8");
    }

    public List<DataNode> children() throws Exception {
        List<String> children = curatorFramework.getChildren().forPath("/" + getPath());
        List<DataNode> list = new ArrayList<>();
        children.stream().forEach(child -> {
            DataNode dataNode = new ZKDataNode(ZKDataNode.this, child, ZKDataNode.this.curatorFramework);
            list.add(dataNode);
        });
        return list;
    }

    public DataNode children(String key) throws Exception {
        List<String> children = curatorFramework.getChildren().forPath("/" + getPath());
        if(children==null||children.isEmpty()){
            return null;
        }
        children = children.stream().filter(child->child.equals(key)).collect(Collectors.toList());
        if(children==null||children.isEmpty()){
            return null;
        }
        DataNode dataNode = new ZKDataNode(ZKDataNode.this, children.get(0), curatorFramework);
        return dataNode;
    }

    public ZKDataNode parent() {
        return parent;
    }
}
