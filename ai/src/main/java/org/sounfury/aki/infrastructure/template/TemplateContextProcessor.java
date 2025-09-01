package org.sounfury.aki.infrastructure.template;

import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.prompt.contract.TemplateAlias;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板上下文处理器
 * 负责处理 @TemplateAlias 注解，为模板渲染提供字段别名映射
 */
@Slf4j
@Component
public class TemplateContextProcessor {

    /**
     * 类型映射缓存，避免重复反射
     */
    private final Map<Class<?>, Map<String, String>> aliasCache = new ConcurrentHashMap<>();

    /**
     * 处理上下文对象，应用模板别名映射
     * 
     * @param context 原始上下文对象
     * @return 处理后的上下文对象（可能是代理对象）
     */
    public Object processContext(Object context) {
        if (context == null) {
            return null;
        }

        Class<?> contextClass = context.getClass();
        Map<String, String> aliasMap = getAliasMapping(contextClass);
        
        if (aliasMap.isEmpty()) {
            // 没有别名映射，直接返回原对象
            return context;
        }

        // 创建支持别名访问的代理对象
        return createAliasProxy(context, aliasMap);
    }

    /**
     * 获取类的别名映射关系
     */
    private Map<String, String> getAliasMapping(Class<?> clazz) {
        return aliasCache.computeIfAbsent(clazz, this::extractAliasMapping);
    }

    /**
     * 通过反射提取类的别名映射
     */
    private Map<String, String> extractAliasMapping(Class<?> clazz) {
        Map<String, String> aliasMap = new HashMap<>();
        
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            TemplateAlias alias = field.getAnnotation(TemplateAlias.class);
            if (alias != null) {
                String aliasName = alias.value();
                String fieldName = field.getName();
                
                if (aliasName != null && !aliasName.trim().isEmpty()) {
                    aliasMap.put(aliasName, fieldName);
                    log.debug("发现模板别名映射: {} -> {}", aliasName, fieldName);
                }
            }
        }
        
        log.debug("类 {} 的别名映射: {}", clazz.getSimpleName(), aliasMap);
        return aliasMap;
    }

    /**
     * 创建支持别名访问的包装对象
     * 使用Map包装而不是动态代理，更适合Handlebars的反射机制
     */
    private Object createAliasProxy(Object target, Map<String, String> aliasMap) {
        try {
            // 创建一个Map来包装原对象，支持别名访问
            Map<String, Object> wrapperMap = new HashMap<>();

            // 添加原对象的所有属性
            Class<?> targetClass = target.getClass();
            Field[] fields = targetClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(target);
                String fieldName = field.getName();

                // 添加原字段名访问
                wrapperMap.put(fieldName, fieldValue);

                // 检查是否有别名，添加别名访问
                TemplateAlias alias = field.getAnnotation(TemplateAlias.class);
                if (alias != null && alias.value() != null && !alias.value().trim().isEmpty()) {
                    wrapperMap.put(alias.value(), fieldValue);
                    log.debug("添加别名映射: {} -> {}", alias.value(), fieldName);
                }
            }

            return wrapperMap;

        } catch (Exception e) {
            log.error("创建别名包装对象失败", e);
            return target;
        }
    }


}
