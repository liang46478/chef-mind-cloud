package com.chefmind.admin.aspect;

import com.chefmind.admin.entity.OperationLog;
import com.chefmind.admin.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 操作日志 AOP 切面
 * 自动记录所有管理员操作
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;

    @Around("execution(* com.chefmind.admin.controller..*.*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String result = "success";
        String detail = "";

        try {
            Object retVal = joinPoint.proceed();
            return retVal;
        } catch (Exception e) {
            result = "error";
            detail = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            try {
                HttpServletRequest request = ((ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes()).getRequest();

                OperationLog opLog = new OperationLog();
                opLog.setOperator(request.getHeader("X-User-Id"));
                opLog.setAction(request.getMethod() + " " + request.getRequestURI());
                opLog.setResourceType(getResourceType(request.getRequestURI()));
                opLog.setDetail(detail);
                opLog.setResult(result);
                opLog.setDurationMs(duration);
                opLog.setIpAddress(request.getRemoteAddr());
                operationLogMapper.insert(opLog);
            } catch (Exception e) {
                log.warn("Failed to log operation: {}", e.getMessage());
            }
        }
    }

    private String getResourceType(String uri) {
        if (uri.contains("/users")) return "user";
        if (uri.contains("/recipes")) return "recipe";
        if (uri.contains("/ingredients")) return "ingredient";
        if (uri.contains("/recommend-config")) return "recommend_config";
        if (uri.contains("/prompts")) return "prompt";
        if (uri.contains("/dashboard")) return "dashboard";
        return "other";
    }
}
