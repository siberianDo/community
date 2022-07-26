package com.our.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * aop测试
 */
//@Component
//@Aspect
public class AlphaAspect {

    //声明切点
    @Pointcut("execution(* com.our.community.service.*.*(..))")
    public void pointcut() {

    }

    //切点之前执行
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    //切点之后执行
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    //返回值以后执行
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    //抛异常的时候执行
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    //前后都想织入逻辑
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        //调取目标组件方法
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }
}