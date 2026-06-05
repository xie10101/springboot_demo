package com.example.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.StringJoiner;

@Slf4j
@RestControllerAdvice(basePackages = "com.example.demo.modules.student")
public class GlobalExceptionHandler {

    // ==================== 业务异常 ====================

    /** 兜底：所有未捕获的异常 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error("系统内部错误，请联系管理员");
    }

    /** 运行时异常 */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return Result.error(e.getMessage());
    }

    // ==================== SpringMVC 内置异常 ====================

    /** 参数校验失败（@Valid / @Validated） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidation(MethodArgumentNotValidException e) {
        StringJoiner errors = new StringJoiner(", ");
        e.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage())
        );
        log.warn("参数校验失败: {}", errors);
        return Result.error(400, errors.toString());
    }

    /** JSON 请求体格式错误 / 反序列化失败 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return Result.error(400, "请求参数格式错误，请检查JSON格式");
    }

    /** 请求方式错误（GET 调 POST 等） */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("不支持的请求方式: {}", e.getMessage());
        return Result.error(405, "不支持的请求方式: " + e.getMethod());
    }

    /** 404 — 接口不存在 */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNotFound(NoHandlerFoundException e) {
        log.warn("接口不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        return Result.error(404, "接口不存在: " + e.getRequestURL());
    }

    /** 404 — 静态资源不存在（Spring Boot 3.x） */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return Result.error(404, "资源不存在");
    }
}