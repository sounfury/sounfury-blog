package org.sounfury.jooq.mapper;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;

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

            // 将下划线风格转换为驼峰风格
            String camelCaseFieldName = toCamelCase(fieldName);

            // 尝试多种getter方法名
            String[] getterNameVariants = {
                    "get" + camelCaseFieldName.substring(0, 1).toUpperCase() + camelCaseFieldName.substring(1),
                    camelCaseFieldName,
                    "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)
            };

            Method getter = null;
            for (String getterName : getterNameVariants) {
                getter = getterMap.get(getterName);
                if (getter != null) {
                    break;
                }
            }

            if (getter != null) {
                try {
                    Object value = getter.invoke(entity);
                    if (value != null) { // 跳过null值
                        // 类型校验和转换
                        Object convertedValue = tryConvertToJooqType(value, field.getType());
                        if (convertedValue != null) {
                            values.put(field, convertedValue);
                        } else {
                            // 无法转换，记录警告
                            System.err.println("Unable to convert value for field: " + fieldName +
                                    ", expected: " + field.getType().getName() +
                                    ", actual: " + value.getClass().getName());
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get value for field: " + fieldName, e);
                }
            }
        }

        return values;
    }

    /**
     * 将下划线风格转换为驼峰风格
     * @param underscoreName 下划线风格的名称
     * @return 驼峰风格的名称
     */
    private static String toCamelCase(String underscoreName) {
        if (underscoreName == null || underscoreName.isEmpty()) {
            return underscoreName;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < underscoreName.length(); i++) {
            char currentChar = underscoreName.charAt(i);

            if (currentChar == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(currentChar));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }

        return result.toString();
    }

    /**
     * 尝试将值转换为JOOQ支持的类型
     * @param value 原始值
     * @param targetType 目标类型
     * @return 转换后的值，或null（如果无法转换）
     */
    private static Object tryConvertToJooqType(Object value, Class<?> targetType) {
        try {
            // 处理无符号类型
            if (targetType == UByte.class && value instanceof Number) {
                return UByte.valueOf(((Number) value).byteValue());
            }
            if (targetType == UShort.class && value instanceof Number) {
                return UShort.valueOf(((Number) value).shortValue());
            }
            if (targetType == UInteger.class && value instanceof Number) {
                return UInteger.valueOf(((Number) value).intValue());
            }
            if (targetType == ULong.class && value instanceof Number) {
                return ULong.valueOf(((Number) value).longValue());
            }

            // 类型完全匹配
            if (targetType.isInstance(value)) {
                return value;
            }

            // 字符串到数字的转换
            if (value instanceof String) {
                String stringValue = (String) value;
                if (targetType == Integer.class) {
                    return Integer.parseInt(stringValue);
                } else if (targetType == Long.class) {
                    return Long.parseLong(stringValue);
                } else if (targetType == Double.class) {
                    return Double.parseDouble(stringValue);
                } else if (targetType == UInteger.class) {
                    return UInteger.valueOf(stringValue);
                } else if (targetType == ULong.class) {
                    return ULong.valueOf(stringValue);
                }
            }
        } catch (Exception e) {
            // 转换失败，返回null
            System.err.println("Failed to convert value: " + value + " to type: " + targetType.getName());
        }
        return null;
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
