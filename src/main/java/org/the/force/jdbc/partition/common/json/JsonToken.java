package org.the.force.jdbc.partition.common.json;

/**
 * Created by xuji on 2017/7/2.
 */
public class JsonToken {
    JsonTokenType type;
    String value;

    public JsonToken(JsonTokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    public JsonToken(JsonTokenType type, char c) {
        this.type = type;
        this.value = c+"";
    }
    public String toString() {
        return String.format("JsonToken({%s}, {%s})", type, value);
    }
}
