package org.the.force.jdbc.partition.engine.parser.copy;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlQueryExpr;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLQueryExpr;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuji on 2017/5/24.
 */
public class SqlObjCopier {

    private static Log logger = LogFactory.getLog(SqlObjCopier.class);

    private Set<String> parentFields = Sets.newHashSet("parent");

    private Set<String> excludeRootFields;

    private SimpleLinkedList<Object[]> replaceObjPairs = new SimpleLinkedList<>();

    private boolean useEqualsWhenReplace = false;

    private LogicDbConfig logicDbConfig;

    public SqlObjCopier() {

    }

    public SqlObjCopier(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    public boolean isUseEqualsWhenReplace() {
        return useEqualsWhenReplace;
    }

    public void setUseEqualsWhenReplace(boolean useEqualsWhenReplace) {
        this.useEqualsWhenReplace = useEqualsWhenReplace;
    }

    public SqlObjCopier(String... excludeRootFields) {
        this.excludeRootFields = Sets.newHashSet(excludeRootFields);
    }

    public void addReplaceObj(Object originObj, Object targetObj) {
        replaceObjPairs.add(new Object[] {originObj, targetObj});
    }

    public <T> T copy(T source) {
        try {
            return copy(source, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T copy(T source, boolean inRootFields) throws Exception {
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            return source;
        } else if (source instanceof Number) {
            return source;
        } else if (source instanceof java.util.Date) {
            return source;
        } else if (source instanceof Boolean) {
            return source;
        } else if (source instanceof Character) {
            return (T) source.getClass().getConstructor(new Class[] {String.class}).newInstance(source.toString());
        } else if (source instanceof SqlInSubQueriedExpr) {
            return source;
        } else if (source instanceof SQLInSubQueryExpr) {
            if (logicDbConfig != null) {
                return (T) new SqlInSubQueriedExpr(logicDbConfig, (SQLInSubQueryExpr) source);
            }
        } else if (source instanceof SqlQueryExpr) {
            return source;
        } else if (source instanceof SQLQueryExpr) {
            if (logicDbConfig != null) {
                return (T) new SqlQueryExpr(logicDbConfig, (SQLQueryExpr) source);
            }
        }
        Class<?> clazz = source.getClass();
        if (clazz.isEnum()) {
            return source;
        } else if (clazz.isPrimitive()) {
            return source;
        }
        //集合类
        if (source instanceof Collection) {
            return (T) copy0((Collection) source);
        } else if (source instanceof Map) {
            return (T) copy0((Map) source);
        } else if (clazz.isArray()) {
            return (T) copyArray(source);
        }
        Object target;
        try {
            Constructor constructor = clazz.getConstructor(new Class[] {});
            constructor.setAccessible(true);
            target = constructor.newInstance();
        } catch (NoSuchMethodException e) {
            if (inRootFields) {
                throw new RuntimeException(MessageFormat.format("{0},没有默认的无参构造器，无法深度复制", clazz.getName()));
            }
            logger.warn(MessageFormat.format("{0},没有默认的无参构造器，无法深度复制", clazz.getName()));
            return source;
        }
        List<Field> list = loadFields(clazz);
        w:
        for (Field pd : list) {
            String name = pd.getName();
            if (inRootFields && excludeRootFields != null && excludeRootFields.contains(name)) {
                continue;
            }
            final Object value = pd.get(source);
            if (parentFields.contains(name)) {
                pd.set(target, value);
                continue;
            }
            SimpleLinkedList.Node<Object[]> node = replaceObjPairs.first();
            while (node != null) {
                Object[] pair = node.item();
                if (pair[0] == value || (isUseEqualsWhenReplace() && pair[0].equals(value))) {
                    pd.set(target, pair[1]);
                    replaceObjPairs.remove(node);
                    continue w;
                }
                node = node.next();
            }
            pd.set(target, copy(value, false));
        }
        return (T) target;
    }


    private Collection copy0(Collection source) throws Exception {
        Collection dest = source.getClass().newInstance();
        for (Object o : source) {
            dest.add(copy(o, false));
        }
        return dest;
    }

    private Object copyArray(Object source) throws Exception {
        int length = Array.getLength(source);
        Object dest = Array.newInstance(source.getClass().getComponentType(), length);
        if (length == 0) {
            return dest;
        }
        for (int i = 0; i < length; i++) {
            Array.set(dest, i, copy(Array.get(source, i), false));
        }
        return dest;
    }

    private Map copy0(Map source) throws Exception {
        Map dest = source.getClass().newInstance();
        for (Object o : source.entrySet()) {
            Map.Entry e = (Map.Entry) o;
            dest.put(copy(e.getKey(), false), copy(e.getValue(), false));
        }
        return dest;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public void setLogicDbConfig(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    //===================属性缓存==============

    private static ConcurrentMap<Class<?>, List<Field>> classFieldsCache = new MapMaker().weakValues().concurrencyLevel(50).makeMap();


    private static List<Field> loadFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        if (clazz == null || clazz == Object.class) {
            return list;
        }
        List<Field> fields = classFieldsCache.get(clazz);
        if (fields == null) {
            Field[] fieldArray = clazz.getDeclaredFields();
            fields = new ArrayList<>(fieldArray.length);
            for (Field f : fieldArray) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                f.setAccessible(true);
                fields.add(f);
            }
            classFieldsCache.put(clazz, fields);
        }
        list.addAll(fields);
        list.addAll(loadFields(clazz.getSuperclass()));
        return list;
    }
}
