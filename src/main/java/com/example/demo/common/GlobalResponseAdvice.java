package com.example.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//响应拦截器
@Slf4j
@RestControllerAdvice(basePackages = "com.example.demo.modules.student")
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 如果返回值已经是 Result 类型，不再包装
        return !returnType.getParameterType().equals(Result.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, //返回响应数据
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        String uri = request.getURI().getPath();
        log.info("响应拦截 — URI: {}, 返回类型: {}", uri, body != null ? body.getClass().getSimpleName() : "null");

        // 1. void 方法返回 null → 直接返回成功无数据
        if (body == null) {
            log.info("成功响应 [200] — URI: {}, data: null", uri);
            return Result.success();
        }

        // 2. 文件下载 / 二进制流 → 不包装，原样返回
        if (body instanceof byte[] || body instanceof org.springframework.core.io.Resource) {
            log.info("成功响应 [200] — URI: {}, 文件流，跳过包装", uri);
            return body;
        }

        // 3. String → 手动拼 JSON（否则 Spring 会用 StringHttpMessageConverter 报类型转换错误）
        if (body instanceof String) {
            log.info("成功响应 [200] — URI: {}, data: {}", uri, body);
            return "{\"code\":200,\"message\":\"success\",\"data\":\"" + body + "\"}";
        }

        // 4. 已经是 Result → 不重复包装（supports 已过滤，兜底）
        if (body instanceof Result) {
            return body;
        }

        // 5. 普通对象 → 包装为统一格式
        log.info("成功响应 [200] — URI: {}, data: {}", uri, body);
        return Result.success(body);
    }


}