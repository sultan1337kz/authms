package com.authms.util;

import com.authms.model.Role;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Stream;

@Aspect
@Component
public class RoleCheckAspect {

    @Around("@annotation(secured)")
    public Object checkRoles(ProceedingJoinPoint joinPoint, Secured secured) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String[] roles = Stream.of(secured.roles()).map(Role::name).toArray(String[]::new);

        if (authentication != null && authentication.getAuthorities() != null) {
            boolean hasRole = Arrays.stream(roles)
                    .anyMatch(role -> authentication.getAuthorities().contains(new SimpleGrantedAuthority(role)));

            if (!hasRole) {
                throw new Exception("User does not have required role.");
            }
        }

        return joinPoint.proceed(); // Proceed if role check passes
    }
}
