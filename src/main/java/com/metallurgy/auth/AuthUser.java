package com.metallurgy.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class AuthUser {
    private Long id;
    private String username;
    private String email;
    private String realName;
    private String role;
    private List<String> permissions;
    public AuthUser(Long id, String username, String email, String realName, String role, List<String> permissions) { this.id = id; this.username = username; this.email = email; this.realName = realName; this.role = role; this.permissions = permissions; }
    public Long getId() { return id; } public String getUsername() { return username; } public String getEmail() { return email; }
    public String getRealName() { return realName; } public String getRole() { return role; } public List<String> getPermissions() { return permissions; }
}
