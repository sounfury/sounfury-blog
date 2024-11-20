package org.sounfury.satoken.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.sounfury.core.convention.errorcode.BaseErrorCode.HTTP_FORBIDDEN;

/**
 * SaToken异常处理器
 *
 * @author Lion Li
 */
@Slf4j
@RestControllerAdvice
public class SaTokenExceptionHandler {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return Results.failure(new ClientException(HTTP_FORBIDDEN));
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        return Results.failure(new ClientException(HTTP_FORBIDDEN));
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',认证失败'{}',无法访问系统资源", requestURI, e.getMessage());
        return Results.failure(new ClientException(HTTP_FORBIDDEN));
    }

}
