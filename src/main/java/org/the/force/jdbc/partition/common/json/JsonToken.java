package org.the.force.jdbc.partition.common.json;

import java.text.MessageFormat;

/**
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
        this.value = c + "";
    }

    public String toString() {
        return MessageFormat.format("JsonToken({0}, {1})", type, value);
    }
}
