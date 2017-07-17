package org.the.force.jdbc.partition.common;

import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
            String s = (String) source;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                if (ch == '"') {
                    stringBuilder.append('\\');
                }
                stringBuilder.append(ch);
            }
            sb.append("\"").append(s).append("\"");
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
        List<Method> methodList = Lists.newArrayList(methods);
        Collections.sort(methodList, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        sb.append("{");
        boolean first = true;
        for (Method method : methodList) {
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
            if (key.equalsIgnoreCase("")) {

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
        if (object1 instanceof Collection<?> && object2 instanceof Collection<?>) {
            return equals((Collection<Object>) object1, (Collection<Object>) object2);
        }
        return object1.equals(object2);
    }

    public static boolean equals(List<Object> list1, List<Object> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null) {
            return false;
        }
        if (list1.size() != list2.size()) {
            return false;
        }
        int size = list1.size();
        for (int i = 0; i < size; i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Collection<Object> c1, Collection<Object> c2) {
        if (c1 == c2) {
            return true;
        }
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1.size() != c2.size()) {
            return false;
        }
        if (c1 instanceof List<?> && c2 instanceof List<?>) {
            return equals((List<Object>) c1, (List<Object>) c2);
        }
        Iterator<Object> e1 = c1.iterator();
        Iterator<Object> e2 = c2.iterator();
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            Object o2 = e2.next();
            if (o1 == null) {
                return false;
            }
            if (!o1.equals(o2))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }
}
