package com.authms.controller;

import com.authms.config.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/secure")
public class SecureController {

    private final JwtTokenProvider jwtTokenProvider;

    public SecureController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/protected")
    public ResponseEntity<String> getProtectedResource(HttpServletRequest request) {
        // Get token from Authorization header
        String token = jwtTokenProvider.resolveTokenFromRequest(request);
        
        // If the token is missing from the header, try to get it from the cookie
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // Validate token and perform authorization check
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        // Proceed with the secure action
        return ResponseEntity.ok("Secure resource accessed successfully");
    }
}
