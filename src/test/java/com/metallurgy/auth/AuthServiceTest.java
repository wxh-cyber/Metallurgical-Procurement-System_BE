package com.metallurgy.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.metallurgy.common.BusinessException;
import com.metallurgy.entity.SysUser;
import com.metallurgy.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    private SysUserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userMapper = mock(SysUserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(userMapper, passwordEncoder, jwtService);
    }

    @Test
    void registerRejectsDuplicateUsername() {
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        BusinessException error = assertThrows(BusinessException.class,
                () -> authService.register(registration("taken", "new@example.com", "secret1")));

        assertEquals(409, error.getCode());
        assertEquals("用户名已存在", error.getMessage());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L, 1L);

        BusinessException error = assertThrows(BusinessException.class,
                () -> authService.register(registration("new-user", "taken@example.com", "secret1")));

        assertEquals(409, error.getCode());
        assertEquals("邮箱已存在", error.getMessage());
    }

    @Test
    void registerMapsConcurrentUniqueKeyConflictToSpecificDuplicateMessage() {
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L, 0L, 0L, 1L);
        doThrow(new DuplicateKeyException("duplicate key"))
                .when(userMapper).insert(any(SysUser.class));

        BusinessException error = assertThrows(BusinessException.class,
                () -> authService.register(registration("new-user", "taken@example.com", "secret1")));

        assertEquals(409, error.getCode());
        assertEquals("邮箱已存在", error.getMessage());
    }

    @Test
    void registerReturnsUserWithoutPasswordAfterInsert() {
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L, 0L);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded-secret");
        doAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(7L);
            return 1;
        }).when(userMapper).insert(any(SysUser.class));

        AuthData result = authService.register(registration("new-user", "new@example.com", "secret1"));

        assertNotNull(result.getUser());
        assertEquals(7L, result.getUser().getId());
        assertEquals("new-user", result.getUser().getUsername());
    }

    @Test
    void loginRejectsUnknownAccountWithAuthenticationMessage() {
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        BusinessException error = assertThrows(BusinessException.class,
                () -> authService.login(loginRequest("missing", "secret1")));

        assertEquals(401, error.getCode());
        assertEquals("用户名或密码不正确", error.getMessage());
    }

    private RegisterRequest registration(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    private LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }
}
