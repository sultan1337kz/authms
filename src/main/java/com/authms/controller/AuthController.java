package com.authms.controller;

import com.authms.config.JwtTokenProvider;
import com.authms.model.JwtResponse;
import com.authms.model.LoginRequest;
import com.authms.model.RegisterRequest;
import com.authms.model.User;
import com.authms.service.RefreshTokenService;
import com.authms.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );

        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(loginRequest.getUsername());
        String username = loginRequest.getUsername();

        JwtResponse jwtResponse = new JwtResponse(token,refreshToken,username);

        // Set token as a cookie
        tokenProvider.addTokenToCookie(response, token);

        response.setHeader("X-Auth-Token-Expires-In", "604800"); // Token expiry in seconds (7 days)
        response.setHeader("X-Application-Message", "Login successful. Welcome!");


        // Return the token in the response as well
        return ResponseEntity.ok(jwtResponse);

    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(username, null)
        );

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", refreshToken,
                "tokenType", "Bearer"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenProvider.resolveTokenFromRequest(request);

        if (token != null && tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);
            refreshTokenService.deleteRefreshTokenByUser(username);

            // Clear any refresh token cookies
            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok("User successfully logged out.");
        }

        return ResponseEntity.badRequest().body("Invalid token or user already logged out.");
    }

}
