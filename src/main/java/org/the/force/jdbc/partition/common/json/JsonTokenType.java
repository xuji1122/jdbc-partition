package org.the.force.jdbc.partition.common.json;

/**
 */
public enum JsonTokenType {
    STRING("\""),// " 或者单引号
    NUMBER("0"),//标识数字 小数 负数 正数 均包括
    LEFT_BRACKET("["),//
    RIGHT_BRACKET("]"), //
    LEFT_BRACE("{"),//
    RIGHT_BRACE("}"),//
    EOF("EOF"),//
    COMMA(","),//
    COLON(":");

    private final String desc;

    JsonTokenType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
