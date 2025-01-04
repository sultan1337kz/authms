package com.authms.util;

import com.authms.model.LoginRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthLoggingAspect {

    @Around("execution(* com.authms.controller.AuthController.login(..))")
    public Object logLoginAttempt(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        LoginRequest loginRequest = (LoginRequest) args[0]; // Assuming the first argument is username

        String username = loginRequest.getUsername();
        System.out.println("---->User " + username + " is attempting to login.");

        Object result = joinPoint.proceed(); // Proceed with the method execution

        System.out.println("---->User " + username + " logged in successfully.");

        return result;
    }

    @Around("execution(* com.authms.controller.AuthController.logout(..))")
    public Object logLogoutAttempt(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String username = (String) args[0]; // Assuming the first argument is username

        System.out.println("User " + username + " is logging out.");

        Object result = joinPoint.proceed(); // Proceed with the method execution

        System.out.println("User " + username + " logged out successfully.");

        return result;
    }
}
