package org.sounfury.aki.infrastructure.llm.tools.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.domain.llm.tools.model.ToolMetadata;
import org.sounfury.aki.domain.llm.tools.service.ToolConfigurationService;
import org.sounfury.aki.domain.llm.tools.service.ToolDiscoveryService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 工具发现服务实现
 * 通过扫描@Tool注解发现系统中的工具
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolDiscoveryServiceImpl implements ToolDiscoveryService {

    private final ApplicationContext applicationContext;
    private final ToolConfigurationService toolConfigurationService;
    
    private final Map<String, ToolMetadata> toolMetadataCache = new ConcurrentHashMap<>();

    @Override
    public List<ToolMetadata> discoverAllTools() {
        if (toolMetadataCache.isEmpty()) {
            scanAndCacheTools();
        }
        return toolMetadataCache.values().stream()
                .map(metadata -> ToolMetadata.builder()
                        .name(metadata.getName())
                        .description(metadata.getDescription())
                        .className(metadata.getClassName())
                        .methodName(metadata.getMethodName())
                        .enabled(toolConfigurationService.isToolEnabled(metadata.getName()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getAllToolNames() {
        if (toolMetadataCache.isEmpty()) {
            scanAndCacheTools();
        }
        return toolMetadataCache.keySet();
    }

    @Override
    public ToolMetadata getToolMetadata(String toolName) {
        if (toolMetadataCache.isEmpty()) {
            scanAndCacheTools();
        }
        ToolMetadata metadata = toolMetadataCache.get(toolName);
        if (metadata != null) {
            return ToolMetadata.builder()
                    .name(metadata.getName())
                    .description(metadata.getDescription())
                    .className(metadata.getClassName())
                    .methodName(metadata.getMethodName())
                    .enabled(toolConfigurationService.isToolEnabled(toolName))
                    .build();
        }
        return null;
    }

    /**
     * 扫描并缓存所有工具
     */
    private void scanAndCacheTools() {
        try {
            // 获取所有Spring Bean
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            
            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);
                Class<?> beanClass = bean.getClass();
                
                // 扫描类中的所有方法
                for (Method method : beanClass.getDeclaredMethods()) {
                    Tool toolAnnotation = method.getAnnotation(Tool.class);
                    if (toolAnnotation != null) {
                        String toolName = getToolName(method, toolAnnotation);
                        String description = toolAnnotation.description();
                        
                        ToolMetadata metadata = ToolMetadata.builder()
                                .name(toolName)
                                .description(description)
                                .className(beanClass.getSimpleName())
                                .methodName(method.getName())
                                .enabled(false) // 初始状态，实际状态由配置服务决定
                                .build();
                        
                        toolMetadataCache.put(toolName, metadata);
                        log.debug("发现工具: {} - {}", toolName, description);
                    }
                }
            }
            
            log.info("工具扫描完成，共发现 {} 个工具", toolMetadataCache.size());
            
        } catch (Exception e) {
            log.error("扫描工具时发生异常", e);
        }
    }

    /**
     * 获取工具名称
     * 如果@Tool注解指定了name，使用指定的name，否则使用方法名
     */
    private String getToolName(Method method, Tool toolAnnotation) {
        String annotationName = toolAnnotation.name();
        if (annotationName != null && !annotationName.trim().isEmpty()) {
            return annotationName.trim();
        }
        return method.getName();
    }
}
