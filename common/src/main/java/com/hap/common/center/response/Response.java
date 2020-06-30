package com.hap.common.center.response;


import com.hap.common.center.exception.ExceptionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author robin
 */
@ApiModel("返回数据")
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int NO_LOGIN = -1;

    public static final int SUCCESS = 200;

    public static final int FAIL = 500;

    public static final int NO_PERMISSION = -2;

    @ApiModelProperty("描述")
    private String msg = "操作成功";

    private Response r;

    @ApiModelProperty("code")
    private int code = SUCCESS;


    @ApiModelProperty("对象")
    private Object data;

    public Response() {
        super();
        r = this;
        this.data = "";
    }

    public Response(Object data) {
        super();
        r = this;
        if (data == null) {
            this.data = "";
        } else {
            this.data = data;
        }

    }

    public Response(Object data, String msg) {
        super();
        if (data == null) {
            this.data = "";
        } else {
            this.data = data;
        }
        this.msg = msg;
    }

    public Response(int code, Object data, String msg) {
        super();
        this.code = code;
        if (data == null) {
            this.data = "";
        } else {
            this.data = data;
        }
        this.msg = msg;
    }

    public Response<T> data(Object o) {
        if (r == null) {
            if (this != null) {
                r = this;
            } else {
                r = new Response();
            }
        }
        setD(o);
        return r;
    }

    private void setD(Object o) {
        if (o == null) {
            r.data = "";
        } else {
            r.data = o;
        }
    }

    public Response<T> code(Integer code) {
        if (r == null) {
            if (this != null) {
                r = this;
            } else {
                r = new Response();
            }
        }
        r.code = code;
        return r;
    }

    public Response<T> msg(String msg) {
        if (r == null) {
            if (this != null) {
                r = this;
            } else {
                r = new Response();
            }
        }
        r.msg = msg;
        return r;
    }


    public Response<T> success(Object o, String msg, Integer code) {
        if (r == null) {
            if (this != null) {
                r = this;
            } else {
                r = new Response();
            }
        }
        r.code = code;
        setD(o);
        r.msg = msg;
        return r;
    }

    public Response<T> success(Object o, String msg) {
        if (r == null) {
            if (this != null) {
                r = this;
            } else {
                r = new Response();
            }
        }
        setD(o);
        r.msg = msg;
        return r;
    }

    public Response<T> success(String msg) {
        if (r == null) {
            if (this != null) {
                r = this;
            } else {
                r = new Response();
            }
        }
        r.data = "";
        r.msg = msg;
        return r;
    }

    public Response<T> fail(Object o, String msg) {
        if (r == null) {
            if (this != null) {
                r = this;
            } else {
                r = new Response();
            }
        }
        r.code = FAIL;
        setD(o);
        r.msg = msg;
        return r;
    }

    public Response code(ExceptionEnum enumCode) {
        if (r == null) {
            if (this != null) {
                r = this;
            } else {
                r = new Response();
            }
        }
        r.code = enumCode.getCode();
        r.msg = enumCode.message;
        return r;
    }

    public Response<T> fail(String msg) {
        if (r == null) {
            r = new Response();
        }
        r.code = FAIL;
        r.data = "";
        r.msg = msg;
        return r;
    }

    public Response(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = FAIL;
        this.data = "";
    }


    public Response(Integer code, String message) {
        super();
        this.code = code;
        this.msg = message;
        this.data = "";
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
