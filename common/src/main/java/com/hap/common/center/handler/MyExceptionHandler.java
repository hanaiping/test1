package com.hap.common.center.handler;

import cn.hutool.core.io.IoUtil;
import com.hap.common.center.exception.ExceptionEnum;
import com.hap.common.center.exception.MyException;
import com.hap.common.center.response.Response;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

//异常捕获类
@RestControllerAdvice
public class MyExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(value = MyException.class)//捕捉已知BaqiException
    @ResponseStatus(HttpStatus.OK)//返回httpstatus 200
    public Response rrkErrorHandler(HttpServletRequest request, Throwable e) {
        Response my = new Response();
        MyException r = (MyException) e;
        my.setCode(r.getCode());
        my.setMsg(r.getMessage());
        if (StringUtil.isNullOrEmpty(my.getMsg())) {
            my.setCode(ExceptionEnum.UNKOW_ERROR.code);
            my.setMsg(ExceptionEnum.UNKOW_ERROR.message + "," + e.getMessage());
        }
        logErrorMsg(request, my, e);
        return my;
    }


    @ExceptionHandler(value = ServletException.class)//捕捉已知BaqiException
    @ResponseStatus(HttpStatus.OK)//返回httpstatus 200
    public Response MediaType(HttpServletRequest request, Throwable e) {
        Response my = new Response();
        String errMsg = e.getMessage();
        my.setCode(ExceptionEnum.UNKOW_ERROR.code);
        my.setMsg(errMsg);
        if (errMsg == null) {
            my.setCode(ExceptionEnum.UNKOW_ERROR.code);
            my.setMsg(ExceptionEnum.UNKOW_ERROR.message + "," + e.getMessage());
        }
        logErrorMsg(request, my, e);
        my.msg(errMsg);
        return my;
    }


    @ExceptionHandler(value = HystrixRuntimeException.class)//捕捉已知BaqiException
    @ResponseStatus(HttpStatus.OK)//返回httpstatus 200
    public Response hystrixexception(HttpServletRequest request, Throwable e) {
        Response my = new Response();
        String errMsg = e.getMessage();
        my.setCode(ExceptionEnum.UNKOW_ERROR.code);
        my.setMsg(errMsg);
        if (errMsg == null) {
            my.setCode(ExceptionEnum.UNKOW_ERROR.code);
            my.setMsg(ExceptionEnum.UNKOW_ERROR.message + "," + e.getMessage());
        }
        logErrorMsg(request, my, e);
        my.msg(errMsg);
        return my;
    }

    @ExceptionHandler(value = HystrixBadRequestException.class)//捕捉已知BaqiException
    @ResponseStatus(HttpStatus.OK)//返回httpstatus 200
    public Response hystrixBadexception(HttpServletRequest request, Throwable e) {
        Response my = new Response();
        String errMsg = e.getMessage();
        my.setCode(ExceptionEnum.UNKOW_ERROR.code);
        my.setMsg(errMsg);
        if (errMsg == null) {
            my.setCode(ExceptionEnum.UNKOW_ERROR.code);
            my.setMsg(e.getMessage());
        }
        logErrorMsg(request, my, e);
        my.msg(errMsg);
        return my;
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)//controller缺少参数
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//返回httpstatus 500
    public Response missingParamErrorHandler(HttpServletRequest request, Throwable e) {
        Response my = new Response();
        //缺少参数
        my.setCode(ExceptionEnum.MISS_REQUESTED_PARAM.code);
        my.setMsg(ExceptionEnum.MISS_REQUESTED_PARAM.message + "," + e.getMessage());
        logErrorMsg(request, my, e);
        return my;
    }

    @ExceptionHandler(value = Exception.class)//捕捉所有的异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//返回httpstatus 500
    public Response defaultErrorHandler(HttpServletRequest request, Throwable e) {
        Response my = new Response();
        //系统错误，非BaqiException
        my.setCode(ExceptionEnum.UNKOW_ERROR.code);
        my.setMsg(ExceptionEnum.UNKOW_ERROR.message + "," + e.getMessage());
        logErrorMsg(request, my, e);
        log.error("error content:{}", my);
        return my;
    }

    @ExceptionHandler(value = Throwable.class)//捕捉所有的异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//返回httpstatus 500
    public Response serviceErrorHandler(HttpServletRequest request, Throwable e) {
        Response my = new Response();
        //系统错误，非BaqiException
        my.setCode(ExceptionEnum.UNKOW_ERROR.code);
        my.setMsg(ExceptionEnum.UNKOW_ERROR.message + "," + e.getMessage());
        logErrorMsg(request, my, e);
        return my;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = BindException.class)
    public Response handleMethodBindException(HttpServletRequest request, BindException e) {
        Response my = new Response();
        BindingResult result = e.getBindingResult();
        StringBuffer msg = new StringBuffer("验证错误： ");
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                msg.append(error.getDefaultMessage()).append("\n");
            }
        }
        my.setCode(500);
        my.setMsg(msg.toString());
        logErrorMsg(request, my, e);
        return my;
    }

    /**
     * 方法参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        Response my = new Response();
        my.code(500);
        my.setMsg(e.getBindingResult().getFieldError().getDefaultMessage());
        logErrorMsg(request, my, e);
        return my;
    }

    /**
     * ValidationException
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ValidationException.class)
    public Response handleValidationException(HttpServletRequest request, ValidationException e) {
        Response my = new Response();
        String message = e.getCause().getMessage();
        my.code(500);
        my.setMsg(message);
        logErrorMsg(request, my, e);
        return my;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public Response handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        Response my = new Response();
        String message = e.getConstraintViolations().iterator().next().getMessage();
        my.code(500);
        my.setMsg(message);
        logErrorMsg(request, my, e);
        return my;
    }


    private void logErrorMsg(HttpServletRequest request, Response my, Throwable e) {
        //请求uri
        String uri = request.getRequestURI();
        //请求参数
        Enumeration<String> enu = request.getParameterNames();
        StringBuilder param = new StringBuilder();
        while (enu.hasMoreElements()) {
            String paraName = enu.nextElement();
            if (param.length() > 0) {
                param.append("; ");
            }
            param.append(paraName).append("=").append(request.getParameter(paraName));
        }

        StringBuffer header = new StringBuffer();
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            header.append(name).append("=").append(request.getHeader(name)).append(";");
        }
        StringBuilder errMsg = new StringBuilder();
        errMsg.append(System.lineSeparator()).append("请求uri：").append(uri).append(System.lineSeparator())
                .append("header：").append(header.toString()).append(" ").append(System.lineSeparator())
                .append("param：").append(param.toString()).append(" ").append(System.lineSeparator())
                .append("报错信息：").append(my.getMsg()).append(" ").append(System.lineSeparator())
                .append("trace：").append(getExceptionStackTrace(e)).append(" ").append(System.lineSeparator());
        log.error(errMsg.toString(), e);
    }

    private String getRequestBodyJson(InputStream inputStream) {
        String line = IoUtil.read(inputStream, "utf-8");
        return line;
    }

    public String getExceptionStackTrace(Throwable anexcepObj) {
        StringWriter sw = null;
        PrintWriter printWriter = null;
        try {
            if (anexcepObj != null) {
                sw = new StringWriter();
                printWriter = new PrintWriter(sw);
                anexcepObj.printStackTrace(printWriter);
                printWriter.flush();
                sw.flush();
                return sw.toString();
            } else {
                return null;
            }
        } finally {
            try {
                if (sw != null) {
                    sw.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
