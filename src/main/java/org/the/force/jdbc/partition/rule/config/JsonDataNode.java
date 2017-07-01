package org.the.force.jdbc.partition.rule.config;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.the.force.jdbc.partition.exception.PartitionConfigException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/5/21.
 */
public class JsonDataNode implements DataNode {

    private final JSONObject current;

    public JsonDataNode(JSONObject current) {
        this.current = current;
    }

    public String getKey() {
        return current().getString("key");
    }

    public String getData() throws Exception {
        return current().getString("config");
    }

    public List<DataNode> children() throws Exception {
        List<DataNode> list = new ArrayList<>();
        if (!current().has("children")) {
            return list;
        }
        JSONArray jsonArray = current().getJSONArray("children");
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            Object obj = jsonArray.get(i);
            if (!(obj instanceof JSONObject)) {
                throw new PartitionConfigException("children element is not a JSONObject");
            }
            JSONObject jsonObject = (JSONObject) obj;
            JsonDataNode jsonDataNode = new JsonDataNode(jsonObject);
            list.add(jsonDataNode);
        }
        return list;
    }
    public JSONObject current() {
        return current;
    }


    public DataNode children(String key) throws Exception{
        List<DataNode> ds = children();
        ds = ds.stream().filter(dataNode -> dataNode.getKey().equals(key)).collect(Collectors.toList());
        if(ds==null||ds.isEmpty()){
            return null;
        }
        return ds.get(0);
    }

}
