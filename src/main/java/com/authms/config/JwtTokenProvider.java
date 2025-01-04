package com.authms.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {


    private final String jwtSecret = "YourSuperSecretKey1234567890123456"; // At least 32 characters
    private final long jwtExpirationMs = 3600000; // 1 hour
    private final long refreshExpirationMs = 86400000; // 24 hours
    private static final String COOKIE_NAME = "JWT";
    private final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());


    /**
     * Generate JWT Token.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities()
                .stream()
                .map(auth -> new CustomGrantedAuthority(auth.getAuthority().replace("ROLE_", "")))  // Remove the "ROLE_" prefix
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))  // Add roles
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Validate JWT Token.
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            System.out.println("Token validated successfully. Claims: " + claimsJws.getBody());
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            System.out.println("JWT Token expired: " + ex.getMessage());
        } catch (io.jsonwebtoken.SignatureException ex) {
            System.out.println("JWT Signature does not match: " + ex.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            System.out.println("Malformed JWT Token: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Invalid JWT Token: " + ex.getMessage());
        }
        return false;
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extract Username from JWT Token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> roles = claims.get("roles", List.class);  // Extract roles from the JWT claim
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String resolveTokenFromRequest(HttpServletRequest request) {
        String token = null;
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        return token;
    }

    public void addTokenToCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true); // Prevent client-side access to cookie
        cookie.setMaxAge((int) (jwtExpirationMs / 1000)); // Set expiration to 1 week
        cookie.setPath("/"); // Make the cookie available to all paths
        response.addCookie(cookie);
    }
}

