package com.kevin.todo_app.helpers;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.security.Principal;

public class AuthHelpers {
    public static Mono<String> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName); // o .getPrincipal() dependiendo del tipo de principal
    }
}
