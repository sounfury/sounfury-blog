package org.sounfury.aki.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.llm.llmconfig.dto.CreateLlmConfigurationCommand;
import org.sounfury.aki.application.llm.llmconfig.dto.LlmConfigurationListRequest;
import org.sounfury.aki.application.llm.llmconfig.dto.LlmConfigurationListResponse;
import org.sounfury.aki.application.llm.llmconfig.dto.LlmConfigurationResponse;
import org.sounfury.aki.application.llm.llmconfig.dto.UpdateLlmConfigurationCommand;
import org.sounfury.aki.application.llm.llmconfig.service.LlmConfigurationApplicationService;
import org.sounfury.aki.domain.llm.ModelConfiguration;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

/**
 * LLM配置管理控制器
 * 提供LLM配置的REST API接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/llm-config")
@Validated
public class LlmConfigurationController {

    private final LlmConfigurationApplicationService llmConfigurationService;

    /**
     * 创建LLM配置
     * @param command 创建命令
     * @return 创建后的配置
     */
    @PostMapping
    public Result<LlmConfigurationResponse> createConfiguration(
            @RequestBody @Valid CreateLlmConfigurationCommand command) {
        try {
            log.info("创建LLM配置请求: providerType={}, modelName={}", command.getProviderType(), command.getModelName());
            
            // 执行创建
            ModelConfiguration createdConfig = llmConfigurationService.createConfiguration(command);
            
            // 转换为响应DTO
            LlmConfigurationResponse response = LlmConfigurationResponse.fromDomain(createdConfig);
            
            log.info("创建LLM配置成功: configId={}", createdConfig.getId());
            return Results.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("创建LLM配置参数错误: error={}", e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("创建LLM配置异常", e);
            throw new ClientException("创建LLM配置失败");
        }
    }

    /**
     * 更新LLM配置
     * @param configId 配置ID
     * @param command 更新命令
     * @return 更新后的配置
     */
    @PutMapping("/{configId}")
    public Result<LlmConfigurationResponse> updateConfiguration(
            @PathVariable @NotNull(message = "配置ID不能为空") Integer configId,
            @RequestBody @Valid UpdateLlmConfigurationCommand command) {
        try {
            log.info("更新LLM配置请求: configId={}", configId);
            
            // 设置配置ID
            command.setConfigId(configId);
            
            // 执行更新
            ModelConfiguration updatedConfig = llmConfigurationService.updateConfiguration(command);
            
            // 转换为响应DTO
            LlmConfigurationResponse response = LlmConfigurationResponse.fromDomain(updatedConfig);
            
            log.info("更新LLM配置成功: configId={}", configId);
            return Results.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("更新LLM配置参数错误: configId={}, error={}", configId, e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("更新LLM配置异常: configId={}", configId, e);
            throw new ClientException("更新LLM配置失败");
        }
    }

    /**
     * 启用LLM配置
     * @param configId 配置ID
     * @return 启用后的配置
     */
    @PostMapping("/{configId}/enable")
    public Result<LlmConfigurationResponse> enableConfiguration(
            @PathVariable @NotNull(message = "配置ID不能为空") Integer configId) {
        try {
            log.info("启用LLM配置请求: configId={}", configId);
            
            // 执行启用
            ModelConfiguration enabledConfig = llmConfigurationService.enableConfiguration(configId);
            
            // 转换为响应DTO
            LlmConfigurationResponse response = LlmConfigurationResponse.fromDomain(enabledConfig);
            
            log.info("启用LLM配置成功: configId={}", configId);
            return Results.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("启用LLM配置参数错误: configId={}, error={}", configId, e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("启用LLM配置异常: configId={}", configId, e);
            throw new ClientException("启用LLM配置失败");
        }
    }

    /**
     * 禁用LLM配置
     * @param configId 配置ID
     * @return 禁用后的配置
     */
    @PostMapping("/{configId}/disable")
    public Result<LlmConfigurationResponse> disableConfiguration(
            @PathVariable @NotNull(message = "配置ID不能为空") Integer configId) {
        try {
            log.info("禁用LLM配置请求: configId={}", configId);
            
            // 执行禁用
            ModelConfiguration disabledConfig = llmConfigurationService.disableConfiguration(configId);
            
            // 转换为响应DTO
            LlmConfigurationResponse response = LlmConfigurationResponse.fromDomain(disabledConfig);
            
            log.info("禁用LLM配置成功: configId={}", configId);
            return Results.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("禁用LLM配置参数错误: configId={}, error={}", configId, e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("禁用LLM配置异常: configId={}", configId, e);
            throw new ClientException("禁用LLM配置失败");
        }
    }

    /**
     * 获取全局配置（当前启用的配置）
     * @return 当前启用的配置
     */
    @GetMapping("/global")
    public Result<LlmConfigurationResponse> getGlobalConfiguration() {
        try {
            log.info("获取全局LLM配置请求");
            
            // 获取全局配置
            Optional<ModelConfiguration> globalConfig = llmConfigurationService.getGlobalConfiguration();
            
            if (globalConfig.isEmpty()) {
                log.warn("未找到启用的全局LLM配置");
                return Results.success(null);
            }
            
            // 转换为响应DTO
            LlmConfigurationResponse response = LlmConfigurationResponse.fromDomain(globalConfig.get());
            
            log.info("获取全局LLM配置成功: configId={}", globalConfig.get().getId());
            return Results.success(response);
            
        } catch (Exception e) {
            log.error("获取全局LLM配置异常", e);
            throw new ClientException("获取全局LLM配置失败");
        }
    }

    /**
     * 根据ID获取配置
     * @param configId 配置ID
     * @return 配置信息
     */
    @GetMapping("/{configId}")
    public Result<LlmConfigurationResponse> getConfigurationById(
            @PathVariable @NotNull(message = "配置ID不能为空") Integer configId) {
        try {
            log.info("获取LLM配置请求: configId={}", configId);
            
            // 根据ID获取配置
            Optional<ModelConfiguration> config = llmConfigurationService.getConfigurationById(configId);
            
            if (config.isEmpty()) {
                log.warn("未找到LLM配置: configId={}", configId);
                throw new ClientException("配置不存在");
            }
            
            // 转换为响应DTO
            LlmConfigurationResponse response = LlmConfigurationResponse.fromDomain(config.get());
            
            log.info("获取LLM配置成功: configId={}", configId);
            return Results.success(response);
            
        } catch (ClientException e) {
            // 重新抛出客户端异常
            throw e;
        } catch (Exception e) {
            log.error("获取LLM配置异常: configId={}", configId, e);
            throw new ClientException("获取LLM配置失败");
        }
    }

    /**
     * 分页查询配置列表
     * @param request 查询请求参数
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<LlmConfigurationListResponse> getConfigurationList(@Valid LlmConfigurationListRequest request) {
        try {
            log.info("分页查询LLM配置列表请求: page={}, size={}", request.getPage(), request.getSize());
            
            // 执行分页查询
            LlmConfigurationListResponse response = llmConfigurationService.getConfigurationList(request);
            
            log.info("分页查询LLM配置列表成功: 返回{}条记录", response.getConfigurations().size());
            return Results.success(response);
            
        } catch (Exception e) {
            log.error("分页查询LLM配置列表异常: page={}, size={}", request.getPage(), request.getSize(), e);
            throw new ClientException("查询配置列表失败");
        }
    }
}
