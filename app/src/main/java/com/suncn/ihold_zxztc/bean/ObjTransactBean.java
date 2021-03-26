package com.suncn.ihold_zxztc.bean;

import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.io.Serializable;

/**
 * 办理信息
 */
public class ObjTransactBean implements Serializable {
    private String strId; // 记录Id
    private int intIsReply; // 答复内容标识（0—没有答复内容；1-有答复内容）
    private String strOverDate; // 完成时间（格式“yyyy-mm-dd”）

    private String strRecUnitName; // 承办单位名称
    private String strAttendType; // 办理类型
    private String strAttendState; // 办理状态
    private String dtAttendDate; // 办理日期
    private String strReSolveLevel; // 解决程度
    private String strReplyType; // 答复方式
    private String strReplyContent; // 答复内容
    private String strReplyMen; // 回复人
    private String strFile_name; // 附件名称
    private String strFile_url; // 附件url
    private String strQuality;//提案质量
    private String strRecDate;  //签收时间
    private String strResolveLevel; //解决程度 为空直接 一行隐藏
    private String strFinishDate;//办结时间

    public String getStrRecDate() {
        return strRecDate;
    }

    public void setStrRecDate(String strRecDate) {
        this.strRecDate = strRecDate;
    }

    public String getStrResolveLevel() {
        return strResolveLevel;
    }

    public void setStrResolveLevel(String strResolveLevel) {
        this.strResolveLevel = strResolveLevel;
    }

    public String getStrFinishDate() {
        return strFinishDate;
    }

    public void setStrFinishDate(String strFinishDate) {
        this.strFinishDate = strFinishDate;
    }

    public String getStrQuality() {
        return strQuality;
    }

    public String getStrFile_name() {
        return strFile_name;
    }

    public String getStrFile_url() {
        return strFile_url;
    }

    public String getStrId() {
        return strId;
    }

    public int getIntIsReply() {
        return intIsReply;
    }

    public String getStrOverDate() {
        return strOverDate;
    }

    public String getStrAttendUnitName() {
        return strRecUnitName;
    }

    public String getStrAttendType() {
        return strAttendType;
    }

    public String getStrAttendState() {
        return strAttendState;
    }

    public String getDtAttendDate() {
        return dtAttendDate;
    }

    public String getStrReSolveLevel() {
        return strReSolveLevel;
    }

    public String getStrReplyType() {
        return strReplyType;
    }

    public String getStrReplyContent() {
        return GIStringUtil.htmlEscapeCharsToString(strReplyContent);
    }

    public String getStrReplyMen() {
        return strReplyMen;
    }

}
