package com.ecommerce.inventory_service.infrastructure.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Aspect 
@Component 
public class LoggingAspect {

    @Around("execution (* com.ecommerce.inventory_service..*(..))")
    public Object logger(ProceedingJoinPoint jp) throws Throwable {
        long beginTime = 0;
        long endTime = 0;
        log.info("%s::%s() begins.".formatted(jp.getSignature().getDeclaringType().getSimpleName(), jp.getSignature().getName()));

        try {
            beginTime = System.nanoTime();
            return jp.proceed();
        } catch (Exception e) {
            log.error("Exception happend", e);
            throw e;
        } 
        finally {
            endTime = System.nanoTime();
            log.info("%s::%s() ends. Total time: %d".formatted(jp.getSignature().getDeclaringType().getSimpleName(), jp.getSignature().getName(), endTime - beginTime));
        }        
    }
}
