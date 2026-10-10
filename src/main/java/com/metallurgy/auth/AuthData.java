package com.metallurgy.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthData {
    private String token;
    private long expiresIn;
    private AuthUser user;
    public AuthData() {}
    public AuthData(String token, long expiresIn, AuthUser user) { this.token = token; this.expiresIn = expiresIn; this.user = user; }
    public String getToken() { return token; }
    public long getExpiresIn() { return expiresIn; }
    public AuthUser getUser() { return user; }
}
