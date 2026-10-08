package com.metallurgy.auth;

import com.metallurgy.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // 阶段 2 接入数据库校验和 JWT；阶段 1 保留稳定接口形状。
    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(Collections.singletonMap("message", "登录接口将在阶段 2 实现"));
    }
}
