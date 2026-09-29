package com.comedor.backend.chatbot.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ChatbotAuthorizationService {

    public Set<String> currentAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptySet();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean hasAny(String... requiredAuthorities) {
        Set<String> available = currentAuthorities();
        for (String required : requiredAuthorities) {
            if (available.contains(required)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAll(String... requiredAuthorities) {
        Set<String> available = currentAuthorities();
        for (String required : requiredAuthorities) {
            if (!available.contains(required)) {
                return false;
            }
        }
        return true;
    }
}
