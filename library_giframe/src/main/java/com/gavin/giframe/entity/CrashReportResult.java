package com.gavin.giframe.entity;

public class CrashReportResult {
    private CrashReport result;

    public CrashReport getResult() {
        return result;
    }

    public class CrashReport {
        private String strRlt; // true:合法用户;false:用户名或密码错误;error:服务端错误
        private String strError; // strRlt为error时，补充说明错误信息；strRlt为true时，此项不存在
        private String strSid; // strRlt为true时，返回服务端产生的随机ID

        public String getStrRlt() {
            return strRlt;
        }

        public String getStrError() {
            return strError;
        }

        public String getStrSid() {
            return strSid;
        }
    }
}
