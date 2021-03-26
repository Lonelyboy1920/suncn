//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qd.longchat.bean;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class QDMeetingModel implements Serializable {
    @SerializedName("start_time")
    private long startTime;
    private String content;
    private String users;
    @SerializedName("end_time")
    private long endTime;
    private String title;
    @SerializedName("pc_url")
    private String pcUrl;
    @SerializedName("mobile_url")
    private String mobileUrl;
    private String id;
    @SerializedName("create_time")
    private long createTime;
    @SerializedName("create_uid")
    private String createUid;
    @SerializedName("create_uname")
    private String createUname;
    @SerializedName("update_time")
    private long updateTime;

    public QDMeetingModel() {
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsers() {
        return this.users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPcUrl() {
        return this.pcUrl;
    }

    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    public String getMobileUrl() {
        return this.mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUid() {
        return this.createUid;
    }

    public void setCreateUid(String createUid) {
        this.createUid = createUid;
    }

    public String getCreateUname() {
        return this.createUname;
    }

    public void setCreateUname(String createUname) {
        this.createUname = createUname;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
