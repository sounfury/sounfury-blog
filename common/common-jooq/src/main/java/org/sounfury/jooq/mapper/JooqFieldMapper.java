package org.sounfury.jooq.mapper;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JooqFieldMapper {
    // 缓存getter方法，避免重复反射
    private static final ConcurrentHashMap<Class<?>, Map<String, Method>> GETTER_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 将实体对象映射为JOOQ字段Map
     * @param entity 实体对象
     * @param table JOOQ表定义
     * @return 字段映射Map
     */
    public static <R extends Record, T> Map<Field<?>, Object> toFieldMap(T entity, Table<R> table) {
        Map<Field<?>, Object> values = new HashMap<>();
        if (entity == null) return values;
        
        // 获取实体类的getter方法映射
        Map<String, Method> getterMap = getGetterMethods(entity.getClass());
        
        // 遍历表字段
        for (Field<?> field : table.fields()) {
            String fieldName = field.getName();
            
            // 构造getter方法名（如：title -> getTitle）
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getter = getterMap.get(getterName);
            
            if (getter != null) {
                try {
                    Object value = getter.invoke(entity);
                    if (value != null) {  // 跳过null值
                        values.put(field, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get value for field: " + fieldName, e);
                }
            }
        }
        
        return values;
    }
    
    /**
     * 获取类的所有getter方法
     */
    private static Map<String, Method> getGetterMethods(Class<?> clazz) {
        return GETTER_CACHE.computeIfAbsent(clazz, k -> {
            Map<String, Method> getterMap = new HashMap<>();
            for (Method method : clazz.getMethods()) {
                if (isGetter(method)) {
                    getterMap.put(method.getName(), method);
                }
            }
            return getterMap;
        });
    }
    
    /**
     * 判断是否为getter方法
     */
    private static boolean isGetter(Method method) {
        if (!method.getName().startsWith("get")) return false;
        if (method.getParameterTypes().length != 0) return false;
        if (void.class.equals(method.getReturnType())) return false;
        return true;
    }
}
