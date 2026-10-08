package com.metallurgy.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;

    /** 显式保留全参构造器，避免不同 JDK/Lombok 注解处理器组合下静态工厂无法推断构造方法。 */
    public ApiResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(200, "success", data); }
    public static <T> ApiResponse<T> fail(int code, String msg) { return new ApiResponse<>(code, msg, null); }
}
