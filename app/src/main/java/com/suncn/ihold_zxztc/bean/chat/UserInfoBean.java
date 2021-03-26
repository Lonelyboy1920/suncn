package com.suncn.ihold_zxztc.bean.chat;

/**
 * 最近聊天列表用户信息
 */
public class UserInfoBean {
    private String strMsgType; // 消息类型
    private String strLinkId; // 用户聊天id
    private String strLinkName; // 用户昵称
    private String strPathUrl; // 用户头像
    private String strMsgContent; // 最后一条消息内容
    private String strSendDate; // 最后一条消息发送时间
    private String roomID; //会议室id
    private String naturalName; //会议室名称
    private String creationDate; //创建时间
    private String strId; //消息的唯一主键
    private int intReadStateNo; //未读消息数
    private int isTop;

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public void setIntReadStateNo(int intReadStateNo) {
        this.intReadStateNo = intReadStateNo;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public int getIntReadStateNo() {
        return intReadStateNo;
    }

    public void setStrMsgType(String strMsgType) {
        this.strMsgType = strMsgType;
    }

    public void setStrLinkId(String strLinkId) {
        this.strLinkId = strLinkId;
    }

    public void setStrLinkName(String strLinkName) {
        this.strLinkName = strLinkName;
    }

    public void setStrPathUrl(String strPathUrl) {
        this.strPathUrl = strPathUrl;
    }

    public void setStrSendDate(String strSendDate) {
        this.strSendDate = strSendDate;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setNaturalName(String naturalName) {
        this.naturalName = naturalName;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setStrMsgContent(String strMsgContent) {
        this.strMsgContent = strMsgContent;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getStrMsgType() {
        return strMsgType;
    }

    public String getStrLinkId() {
        return strLinkId;
    }

    public String getStrLinkName() {
        return strLinkName;
    }

    public String getStrPathUrl() {
        return strPathUrl;
    }

    public String getStrMsgContent() {
        return strMsgContent;
    }

    public String getStrSendDate() {
        return strSendDate;
    }

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "strMsgType='" + strMsgType + '\'' +
                ", strLinkId='" + strLinkId + '\'' +
                ", strLinkName='" + strLinkName + '\'' +
                ", strPathUrl='" + strPathUrl + '\'' +
                ", strMsgContent='" + strMsgContent + '\'' +
                ", strSendDate='" + strSendDate + '\'' +
                ", roomID='" + roomID + '\'' +
                ", naturalName='" + naturalName + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", intReadStateNo=" + intReadStateNo +
                '}';
    }
}
