package org.the.force.jdbc.partition.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xuji on 2017/7/29.
 */
public class YmlFileConfigUrl implements ConfigUrl {

    private final String url;

    public YmlFileConfigUrl(String url) {
        int index = url.indexOf("://");
        this.url = url.substring(index + 3);
    }

    public DataNode getLogicDbConfigNode(Properties info) {
        Yaml yml = new Yaml();
        Map<String, Object> object;
        try {
            object = (Map<String, Object>) yml.load(new FileInputStream(new File(url)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Map.Entry<String, Object> entry = object.entrySet().iterator().next();
        return new JsonDataNode(null, entry.getKey().toLowerCase(), (Map<String, Object>) entry.getValue());
    }

    public String toString() {
        return "file://" + url;
    }
}
