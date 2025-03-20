package com.xiyun.saltedfishnetdish.exception;


import com.xiyun.saltedfishnetdish.pojo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类 - 统一处理控制器层抛出的异常
 *
 * 使用@RestControllerAdvice注解结合@ExceptionHandler实现全局异常捕获
 * 本类会处理所有未被特定异常处理器捕获的Exception类型异常
 */
@RestControllerAdvice// 组合注解：@ControllerAdvice + @ResponseBody，自动将返回值转为JSON
public class GlobalExceptionHandler {
    /**
     * 通用异常处理方法 - 捕获所有Exception及其子类异常
     * @param e 捕获到的异常对象
     * @return 统一错误响应结构
     *
     * @apiNote:
     * 1. 打印异常堆栈跟踪（生产环境替换为日志记录）
     * 2. 判断异常消息有效性
     * 3. 返回统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        return Result.error(StringUtils.hasLength(e.getMessage())? e.getMessage() : "操作失败");
    }
}
