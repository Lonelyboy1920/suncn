package com.suncn.ihold_zxztc.bean;

/**
 * 检查更新版本(AutoUpdateServlet)
 */
public class AutoUpdateBean extends BaseGlobal {
    private String strVersionName; // 版本代号
    private int intVersionCode; // 版本序号
    private String strFilePath; // 更新地址
    private String strInfo; // 更新内容
    private String strForceUpdate; // 0-正常更新；1-强制更新
    private boolean blHasUpdate; // 是否需要更新
    private String strFileMd5; // 文件MD5
    private int intFileSize; // 文件大小

    public String getStrForceUpdate() {
        return strForceUpdate;
    }

    public String getStrVersionName() {
        return strVersionName;
    }

    public int getIntVersionCode() {
        return intVersionCode;
    }

    public String getStrFilePath() {
        return strFilePath;
    }

    public String getStrInfo() {
        return strInfo;
    }

    public boolean isBlHasUpdate() {
        return blHasUpdate;
    }

    public void setBlHasUpdate(boolean blHasUpdate) {
        this.blHasUpdate = blHasUpdate;
    }

    public String getStrFileMd5() {
        return strFileMd5;
    }

    public int getIntFileSize() {
        return intFileSize;
    }
}
