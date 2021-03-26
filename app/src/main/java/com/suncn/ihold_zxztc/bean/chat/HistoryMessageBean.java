package com.suncn.ihold_zxztc.bean.chat;

/**
 * 历史消息记录
 */
public class HistoryMessageBean {
    private String strSendUser; // 发送人
    private String strMsgContent; // 消息内容
    private String strSendDate; //时间
    private String strLinkName; //姓名
    private String strPathUrl; //头像
    private String strLinkId; //聊天id
    private int intUserType; //0 本人 1否

    public String getStrLinkName() {
        return strLinkName;
    }

    public String getStrPathUrl() {
        return strPathUrl;
    }

    public String getStrLinkId() {
        return strLinkId;
    }

    public String getStrSendUser() {
        return strSendUser;
    }

    public String getStrMsgContent() {
        return strMsgContent;
    }

    public String getStrSendDate() {
        return strSendDate;
    }

    public int getIntUserType() {
        return intUserType;
    }
}
