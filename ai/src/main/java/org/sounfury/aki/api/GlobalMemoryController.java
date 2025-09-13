package org.sounfury.aki.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.aki.application.globalmemory.dto.GlobalMemoryAddReq;
import org.sounfury.aki.application.globalmemory.dto.GlobalMemoryResponse;
import org.sounfury.aki.application.globalmemory.dto.GlobalMemoryUpdateReq;
import org.sounfury.aki.application.globalmemory.service.GlobalMemoryApplicationService;
import org.sounfury.aki.infrastructure.shared.context.UserContextHolder;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 全局记忆管理控制器
 * 提供全局记忆的CRUD接口，仅站长可访问
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/global-memory")
public class GlobalMemoryController {

    private final GlobalMemoryApplicationService globalMemoryApplicationService;

    /**
     * 查询所有全局记忆
     */
    @GetMapping("/list")
    public Result<List<GlobalMemoryResponse>> getAllGlobalMemories() {
//        checkOwnerPermission();
        
        try {
            List<GlobalMemoryResponse> memories = globalMemoryApplicationService.getAllGlobalMemories();
            log.debug("查询到{}条全局记忆", memories.size());
            return Results.success(memories);
        } catch (Exception e) {
            log.error("查询全局记忆失败", e);
            throw new ClientException("查询全局记忆失败");
        }
    }

    /**
     * 根据ID查询全局记忆
     */
    @GetMapping("/{id}")
    public Result<GlobalMemoryResponse> getGlobalMemoryById(@PathVariable("id") @NotNull Long id) {
//        checkOwnerPermission();
        
        try {
            GlobalMemoryResponse memory = globalMemoryApplicationService.getGlobalMemoryById(id);
            return Results.success(memory);
        } catch (IllegalArgumentException e) {
            log.warn("查询全局记忆失败: {}", e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("查询全局记忆失败: {}", id, e);
            throw new ClientException("查询全局记忆失败");
        }
    }

    /**
     * 新增全局记忆
     */
    @PostMapping
    public Result<GlobalMemoryResponse> addGlobalMemory(@Valid @RequestBody GlobalMemoryAddReq request) {
//        checkOwnerPermission();
        
        try {
            GlobalMemoryResponse memory = globalMemoryApplicationService.addGlobalMemory(request);
            log.info("新增全局记忆成功: {}", memory.getId());
            return Results.success(memory);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("新增全局记忆失败: {}", e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("新增全局记忆失败", e);
            throw new ClientException("新增全局记忆失败");
        }
    }

    /**
     * 更新全局记忆
     */
    @PutMapping
    public Result<GlobalMemoryResponse> updateGlobalMemory(@Valid @RequestBody GlobalMemoryUpdateReq request) {
//        checkOwnerPermission();
        
        try {
            GlobalMemoryResponse memory = globalMemoryApplicationService.updateGlobalMemory(request);
            log.info("更新全局记忆成功: {}", memory.getId());
            return Results.success(memory);
        } catch (IllegalArgumentException e) {
            log.warn("更新全局记忆失败: {}", e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("更新全局记忆失败: {}", request.getId(), e);
            throw new ClientException("更新全局记忆失败");
        }
    }

    /**
     * 删除全局记忆
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteGlobalMemory(@PathVariable @NotNull Long id) {
//        checkOwnerPermission();
        
        try {
            globalMemoryApplicationService.deleteGlobalMemory(id);
            log.info("删除全局记忆成功: {}", id);
            return Results.success();
        } catch (IllegalArgumentException e) {
            log.warn("删除全局记忆失败: {}", e.getMessage());
            throw new ClientException(e.getMessage());
        } catch (Exception e) {
            log.error("删除全局记忆失败: {}", id, e);
            throw new ClientException("删除全局记忆失败");
        }
    }

    /**
     * 获取全局记忆统计信息
     */
    @GetMapping("/stats")
    public Result<GlobalMemoryApplicationService.GlobalMemoryStats> getGlobalMemoryStats() {
//
        
        try {
            GlobalMemoryApplicationService.GlobalMemoryStats stats = globalMemoryApplicationService.getGlobalMemoryStats();
            return Results.success(stats);
        } catch (Exception e) {
            log.error("获取全局记忆统计信息失败", e);
            throw new ClientException("获取统计信息失败");
        }
    }

    /**
     * 检查站长权限
     */
    private void checkOwnerPermission() {
        UserContextHolder.UserContext context = UserContextHolder.getContext();
        if (context == null || !context.isOwner()) {
            log.warn("非站长用户尝试访问全局记忆管理接口: {}", context);
            throw new ClientException("权限不足，仅站长可访问");
        }
    }
}
