package com.czsc.result;

public class Result {
    //接口返回状态标志:1:代表接口正常处理,返回成功; 0:代表处理异常,返回失败
    private Integer status;
    private String message;
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Result(Integer status, String message) {
        super();
        this.status = status;
        this.message = message;
    }
    public Result() {
        super();
    }
    @Override
    public String toString() {
        return "Result [status=" + status + ", message=" + message + "]";
    }
}
