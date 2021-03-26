package com.suncn.ihold_zxztc.bean.chat;

import java.io.Serializable;

/**
 * 消息内容
 */
public class MessageInfo implements Serializable {
    private int type; // 消息类型  左/右
    private String content; // 消息内容
    private String filepath; // 文件地址
    private int sendState; //发送状态
    private String time; // 时间
    private String header; // 头像url
    private String imageUrl; // 图片的url
    private long voiceTime; // 语音消息的时间
    private String msgId;  // 消息id
    private String groupNickname; // 群昵称
    private String strMsgType; // 消息类型:1单聊 3群聊
    private String strLinkId; // 连接id
    private String strTitle; // 联系人、群组名称

    public String getStrTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getStrLinkId() {
        return strLinkId;
    }

    public void setStrLinkId(String strLinkId) {
        this.strLinkId = strLinkId;
    }


    public String getStrMsgType() {
        return strMsgType;
    }

    public void setStrMsgType(String strMsgType) {
        this.strMsgType = strMsgType;
    }

    public String getGroupNickname() {
        return groupNickname;
    }

    public void setGroupNickname(String groupNickname) {
        this.groupNickname = groupNickname;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(long voiceTime) {
        this.voiceTime = voiceTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", filepath='" + filepath + '\'' +
                ", sendState=" + sendState +
                ", time='" + time + '\'' +
                ", header='" + header + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", voiceTime=" + voiceTime +
                ", msgId='" + msgId + '\'' +
                ", groupNickname='" + groupNickname + '\'' +
                ", strMsgType='" + strMsgType + '\'' +
                ", strLinkId='" + strLinkId + '\'' +
                '}';
    }
}
