package com.metallurgy.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.metallurgy.common.BusinessException;
import com.metallurgy.entity.SysUser;
import com.metallurgy.mapper.SysUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class AuthService {
    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(SysUserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) { this.userMapper = userMapper; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService; }

    public AuthData register(RegisterRequest request) {
        if (userMapper.selectCount(new QueryWrapper<SysUser>().eq("username", request.getUsername())) > 0) throw new BusinessException(409, "用户名已存在");
        if (userMapper.selectCount(new QueryWrapper<SysUser>().eq("email", request.getEmail())) > 0) throw new BusinessException(409, "邮箱已存在");
        SysUser user = new SysUser(); user.setUsername(request.getUsername()); user.setEmail(request.getEmail()); user.setPassword(passwordEncoder.encode(request.getPassword())); user.setRealName(request.getUsername()); user.setRole("PROCUREMENT"); user.setStatus(1); userMapper.insert(user);
        return new AuthData(null, 0, toAuthUser(user));
    }

    public AuthData login(LoginRequest request) {
        SysUser user = userMapper.selectOne(new QueryWrapper<SysUser>().and(w -> w.eq("username", request.getUsername()).or().eq("email", request.getUsername())));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) throw new BusinessException(401, "用户名或密码不正确");
        if (user.getStatus() == null || user.getStatus() != 1) throw new BusinessException(403, "账号已停用");
        String token = jwtService.issue(user.getId(), user.getUsername(), user.getRole());
        return new AuthData(token, jwtService.getExpirationMs(), toAuthUser(user));
    }

    public AuthUser current(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) throw new BusinessException(401, "登录状态已失效");
        return toAuthUser(user);
    }

    private AuthUser toAuthUser(SysUser user) { return new AuthUser(user.getId(), user.getUsername(), user.getEmail(), user.getRealName(), user.getRole(), Arrays.asList("dashboard:read", "material:read", "supplier:read")); }
}
