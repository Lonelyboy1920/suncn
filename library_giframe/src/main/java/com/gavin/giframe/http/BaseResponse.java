package com.gavin.giframe.http;

/**
 * Created by goldze on 2017/5/10.
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
public class BaseResponse<T> {
    private int code;
    private String msg;
    private String strSid;
    private T data;

    public String getStrSid() {
        return strSid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public boolean isOk() {
        return code == 0;
    }


    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
