package org.the.force.jdbc.partition.common.json;

/**
 * Created by xuji on 2017/7/2.
 */
public enum JsonTokenType {
    STRING("\""),// " 或者单引号
    NUMBER("0"),//标识数字 小数 负数 正数 均包括
    LEFTBRACKET("["),//
    RIGHTBRACKET("]"), //
    LEFTbrace("{"),//
    RIGHTbrace("}"),//
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
