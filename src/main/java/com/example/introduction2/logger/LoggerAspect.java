package com.example.introduction2.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerAspect {

    // Logger
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* com.example.introduction2..*.*(..))")
    public void logMethodAccessBefore(JoinPoint joinPoint) {
        log.debug("***** Starting: " + joinPoint.getSignature().getName() + " *****");
    }

    @After("execution(* com.example.introduction2..*.*(..))")
    public void logMethodAccessAfter(JoinPoint joinPoint) {
        log.debug("***** Completed: " + joinPoint.getSignature().getName() + " *****");
    }
}
