package com.metallurgy.config;

import com.metallurgy.auth.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    public JwtInterceptor(JwtService jwtService) { this.jwtService = jwtService; }
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) { response.setStatus(401); return false; }
        try { Claims claims = jwtService.parse(header.substring(7)); request.setAttribute("authUserId", Long.valueOf(claims.getSubject())); request.setAttribute("authRole", claims.get("role", String.class)); return true; }
        catch (Exception ex) { response.setStatus(401); return false; }
    }
}
