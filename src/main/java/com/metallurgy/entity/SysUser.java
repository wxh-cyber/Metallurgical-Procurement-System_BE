package com.metallurgy.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId private Long id;
    private String username;
    private String email;
    private String password;
    private String realName;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    public Long getId() { return id; } public void setId(Long value) { id = value; }
    public String getUsername() { return username; } public void setUsername(String value) { username = value; }
    public String getEmail() { return email; } public void setEmail(String value) { email = value; }
    public String getPassword() { return password; } public void setPassword(String value) { password = value; }
    public String getRealName() { return realName; } public void setRealName(String value) { realName = value; }
    public String getRole() { return role; } public void setRole(String value) { role = value; }
    public Integer getStatus() { return status; } public void setStatus(Integer value) { status = value; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime value) { createTime = value; }
    public LocalDateTime getUpdateTime() { return updateTime; } public void setUpdateTime(LocalDateTime value) { updateTime = value; }
}
