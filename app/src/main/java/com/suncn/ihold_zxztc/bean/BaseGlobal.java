package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;

/**
 * 通用实体类
 */
public class BaseGlobal implements Serializable {
    private String strRlt; // true:合法用户;false:用户名或密码错误;error:服务端错误
    private String strError; // strRlt为error时，补充说明错误信息；strRlt为true时，此项不存在
    private String strSid; // strRlt为true时，返回服务端产生的随机ID
    private int intCurPage; // 当前页数
    private int intPageSize; // 每页记录数
    private int intAllCount; // 记录总数

    public String getStrRlt() {
        return strRlt;
    }

    public String getStrError() {
        return strError;
    }

    public String getStrSid() {
        return strSid;
    }

    public int getIntCurPage() {
        return intCurPage;
    }

    public int getIntPageSize() {
        return intPageSize;
    }

    public int getIntAllCount() {
        return intAllCount;
    }
}
