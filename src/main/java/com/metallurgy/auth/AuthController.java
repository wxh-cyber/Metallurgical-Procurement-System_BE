package com.metallurgy.auth;

import com.metallurgy.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }
    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) { return ApiResponse.ok(authService.register(request).getUser()); }
    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest request) { return ApiResponse.ok(authService.login(request)); }
    @GetMapping("/me")
    public ApiResponse<?> me(@RequestAttribute("authUserId") Long userId) { return ApiResponse.ok(Collections.singletonMap("user", authService.current(userId))); }
    @PostMapping("/logout")
    public ApiResponse<?> logout() { return ApiResponse.ok(Collections.singletonMap("message", "已退出登录")); }
}
