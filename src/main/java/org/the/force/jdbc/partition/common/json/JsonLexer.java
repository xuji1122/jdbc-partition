package org.the.force.jdbc.partition.common.json;

import java.text.MessageFormat;

/**
 * Created by xuji on 2017/7/2.
 * 词法分析器
 */
public class JsonLexer {
    final String text;
    int pos;

    char endChar;

    public JsonLexer(String text) {
        this.text = text;
        this.pos = 0;
        this.endChar = this.text.charAt(pos);
    }

    public JsonToken get_next_token() {
        while (this.endChar != Character.MIN_VALUE) {
            char current = text.charAt(pos);
            if (isSpace(current)) {
                skipWhitespace();
                continue;
            }
            if (Character.isDigit(current) || current == '-') {
                //当前是数字
                return new JsonToken(JsonTokenType.NUMBER, number());
            }
            if (current == '{') {
                advance();
                return new JsonToken(JsonTokenType.LEFT_BRACE, '{');
            }

            if (current == '}') {
                advance();
                return new JsonToken(JsonTokenType.RIGHT_BRACE, '}');
            }
            if (current == '[') {
                advance();
                return new JsonToken(JsonTokenType.LEFT_BRACKET, '[');
            }
            if (current == ']') {
                advance();
                return new JsonToken(JsonTokenType.RIGHT_BRACKET, ']');
            }
            if (current == ':') {
                advance();
                return new JsonToken(JsonTokenType.COLON, ':');
            }
            if (current == '"' || current == '\'') {
                advance();
                if (this.endChar == Character.MIN_VALUE) {
                    error(MessageFormat.format("json字符串非正常结束:\n\rjson={0},\n\rcurrentText={1}", getText(), getCurrentText()));
                }
                return new JsonToken(JsonTokenType.STRING, string(current));
            }
            if (current == ',') {
                advance();
                return new JsonToken(JsonTokenType.COMMA, ',');
            }
            error(MessageFormat.format("无法识别token:\n\rjson={0}\n\rcurrentText={1}", getText(), getCurrentText()));
        }

        return new JsonToken(JsonTokenType.EOF, "EOF");
    }

    private void error(String msg) {

        throw new RuntimeException(msg);

    }

    private void advance() {
        this.pos += 1;
        if (pos > text.length() - 1) {
            this.endChar = Character.MIN_VALUE;
        } else {

        }
    }

    private void skipWhitespace() {
        while (this.endChar != Character.MIN_VALUE && isSpace(text.charAt(pos))) {
            advance();
        }
    }

    private boolean isSpace(char c) {
        return c <= ' ';
    }

    private String number() {
        StringBuilder sb = new StringBuilder();
        boolean ten = false;//标识小数点
        boolean first = true;//标识第一个字符
        char ch;
        while (true) {
            ch = text.charAt(pos);
            if (ch == '-') {
                if (!first) {
                    error(MessageFormat.format("负数符号只能在第一位:\n\rjson={0}\n\rcurrentText={1}", getText(), getCurrentText()));
                }
            } else if (ch == '.') {
                if (first) {
                    error(MessageFormat.format("数字不能以小数点打头:\n\rjson={0}\n\rcurrentText={1}", getText(), getCurrentText()));
                }
                if (ten) {
                    error(MessageFormat.format("数字类型小数点多于一个:\n\rjson={0}\n\rcurrentText={1}", getText(), getCurrentText()));
                }
                ten = true;
            } else {
                if (!Character.isDigit(ch)) {
                    break;
                }
            }
            sb.append(ch);
            first = false;
            advance();
            if (this.endChar == Character.MIN_VALUE) {
                break;
            }
        }
        return sb.toString();
    }

    private String string(char tokenType) {
        StringBuilder sb = new StringBuilder();
        char ch;
        while (true) {
            ch = text.charAt(pos);
            if (ch == tokenType) {
                break;
            }
            sb.append(ch);
            advance();
            if (this.endChar == Character.MIN_VALUE) {
                break;
            }
        }
        advance();
        return sb.toString();
    }

    public String getText() {
        return text;
    }

    public String getCurrentText() {
        return text.substring(0, pos + 1);
    }
}
