package org.the.force.jdbc.partition.common;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * Created by xuji on 2017/7/2.
 */
public class BeanUtils {

    public static String toJson(Object object) {
        StringBuilder sb = new StringBuilder();
        toJson(object, sb);
        return sb.toString();
    }

    private static void toJson(Object source, StringBuilder sb) {
        if (source == null) {
            sb.append("null");
            return;
        }
        if (source instanceof String) {
            sb.append("\"").append(source.toString()).append("\"");
            return;
        } else if (source instanceof Number) {
            sb.append(source.toString());
            return;
        } else if (source instanceof java.util.Date) {
            sb.append("\"").append(source.toString()).append("\"");
            return;
        } else if (source instanceof Boolean) {
            sb.append(source.toString());
            return;
        } else if (source instanceof Character) {
            sb.append("\"").append(source.toString()).append("\"");
            return;
        }
        Class<?> clazz = source.getClass();
        if (clazz.isEnum()) {
            sb.append("\"").append(source.toString()).append("\"");
            return;
        } else if (clazz.isPrimitive()) {
            sb.append(source.toString());
            return;
        }
        //集合类
        if (source instanceof Collection) {
            toJson((Collection) source, sb);
            return;
        } else if (source instanceof Map) {
            toJson((Map) source, sb);
            return;
        } else if (clazz.isArray()) {
            toJsonArray(source, sb);
            return;
        }
        Method[] methods = source.getClass().getMethods();
        sb.append("{");
        boolean first = true;
        for (Method method : methods) {
            Class<?>[] ps = method.getParameterTypes();
            if (ps != null && ps.length > 0) {
                continue;
            }
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            String key;
            String name = method.getName();
            if (name.startsWith("is") && name.length() > 2) {
                key = name.substring(name.indexOf("is") + 2);
            } else if (name.startsWith("get") && name.length() > 3) {
                key = name.substring(name.indexOf("get") + 3);
            } else {
                continue;
            }
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            if(key.equalsIgnoreCase("")){

            }
            sb.append("\"");
            sb.append(Character.toLowerCase(key.charAt(0)));
            if (key.length() > 1) {
                sb.append(key.substring(1));
            }
            sb.append("\"").append(":");
            try {
                Object value = method.invoke(source, new Object[] {});
                toJson(value, sb);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        sb.append("}");
    }

    private static void toJson(Collection source, StringBuilder sb) {
        sb.append("[");
        boolean first = true;
        for (Object o : source) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            toJson(o, sb);
        }
        sb.append("]");
    }

    private static void toJsonArray(Object source, StringBuilder sb) {
        int length = Array.getLength(source);
        sb.append("[");
        for (int i = 0; i < length; i++) {
            Object obj = Array.get(source, i);
            if (i > 0) {
                sb.append(",");
            }
            toJson(obj, sb);
        }
        sb.append("]");
    }

    private static void toJson(Map source, StringBuilder sb) {
        sb.append("{");
        boolean first = true;
        for (Object o : source.entrySet()) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            Map.Entry e = (Map.Entry) o;
            String key = e.getKey().toString();
            sb.append("\"").append(key).append("\"").append(":");
            Object value = e.getValue();
            toJson(value, sb);
        }
        sb.append("}");
    }

    public static boolean equals(final Object object1, final Object object2) {
        if (object1 == object2) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);
    }
}
