package org.the.force.jdbc.partition.common.json;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;

/**
 */
public class JsonParser {
    JsonLexer jsonLexer;
    JsonToken currentToken;

    public JsonParser(String jsonText) {
        JsonLexer jsonLexer = new JsonLexer(jsonText);
        this.jsonLexer = jsonLexer;
        this.currentToken = jsonLexer.getNextToken();
    }

    public <T> T parse() {
        Object json = getJson();
        if (currentToken.type != JsonTokenType.EOF) {
            error(MessageFormat.format("json没有正常结束 \n\rjson={0},\n\rcurrentText={1},\n\rlast_token={2}", jsonLexer.getText(), jsonLexer.getCurrentText(), currentToken));
        }
        return (T) json;
    }

    private Object getJson() {
        /**
         * JSON:{key:v}|number|array|string
         * key:string|(canbeJOSN)
         * v:JOSN
         * number:(0..9)+
         * string:"(c)+"
         * array:[json(,josn)*]
         * 没有实现布尔值  应该很简单  增加词法分析
         */
        while (currentToken.type != JsonTokenType.EOF) {
            if (currentToken.type == JsonTokenType.NUMBER) {

                try {
                    BigDecimal decimal = new BigDecimal(currentToken.value);
                    eat(JsonTokenType.NUMBER);
                    if (decimal.scale() == 0) {
                        return decimal.longValue();
                    } else {
                        return decimal;
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException(
                        MessageFormat.format("json解析异常 \n\rjson={0}\n\rcurrentText={1}\n\rlast_token={2}", jsonLexer.getText(), jsonLexer.getCurrentText(), currentToken), e);
                }

            }
            //
            if (currentToken.type == JsonTokenType.STRING) {
                String value = currentToken.value;
                eat(JsonTokenType.STRING);
                return value;
            }
            //[
            if (currentToken.type == JsonTokenType.LEFT_BRACKET) {
                eat(JsonTokenType.LEFT_BRACKET);
                if (currentToken.type == JsonTokenType.RIGHT_BRACKET) {
                    eat(JsonTokenType.RIGHT_BRACKET);
                    return new ArrayList<>();
                }
                ArrayList<Object> list = getList();
                eat(JsonTokenType.RIGHT_BRACKET);
                return list;
            }
            //{
            if (currentToken.type == JsonTokenType.LEFT_BRACE) {
                eat(JsonTokenType.LEFT_BRACE);
                if (currentToken.type == JsonTokenType.RIGHT_BRACE) {
                    eat(JsonTokenType.RIGHT_BRACE);
                    return null;
                }
                HashMap<String, Object> jo = getMap();
                eat(JsonTokenType.RIGHT_BRACE);
                return jo;
            }
            if (currentToken.type == JsonTokenType.RIGHT_BRACE) {
                return null;
            }
        }
        return null;
    }

    private void eat(JsonTokenType type) {
        if (this.currentToken.type == type) {
            currentToken = jsonLexer.getNextToken();
            return;
        }
        error(MessageFormat.format("json格式不对称 \n\rjson={0}\n\rcurrentText={1}\n\rexpectTokenType={2}", jsonLexer.getText(), jsonLexer.getCurrentText(), type.getDesc()));
    }



    private ArrayList<Object> getList() {
        ArrayList<Object> list = new ArrayList<>();
        Object object = getJson();
        if (object == null) {
            return list;
        }
        list.add(object);
        while (currentToken.type == JsonTokenType.COMMA) {
            eat(JsonTokenType.COMMA);
            list.add(getJson());
        }
        return list;
    }

    private HashMap<String, Object> getMap() {
        HashMap<String, Object> jo = new HashMap<>();
        Object k = getJson();//到key结束
        eat(JsonTokenType.COLON);//key之后的:
        Object v = getJson();

        jo.put(k.toString(), v);
        while (currentToken.type == JsonTokenType.COMMA) {

            eat(JsonTokenType.COMMA);
            k = getJson();//到 key结束
            eat(JsonTokenType.COLON);//key之后的:
            v = getJson();
            jo.put(k.toString(), v);

        }
        return jo;
    }

    private void error(String msg) {
        throw new RuntimeException(msg);
    }


}
