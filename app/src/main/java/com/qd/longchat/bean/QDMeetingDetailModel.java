//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qd.longchat.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QDMeetingDetailModel {
    private String id;
    private String title;
    private String content;
    private String attachs;
    private int status;
    @SerializedName("start_time")
    private long startTime;
    @SerializedName("end_time")
    private long endTime;
    @SerializedName("pc_url")
    private String pcUrl;
    @SerializedName("mobile_url")
    private String mobileUrl;
    @SerializedName("create_uid")
    private String createUid;
    @SerializedName("create_uname")
    private String createUname;
    @SerializedName("create_time")
    private long createTime;
    private String update_uid;
    @SerializedName("update_uname")
    private String updateUname;
    @SerializedName("update_time")
    private long updateTime;
    private List<QDMeetingDetailModel.UsersBean> users;

    public QDMeetingDetailModel() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachs() {
        return this.attachs;
    }

    public void setAttachs(String attachs) {
        this.attachs = attachs;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getUpdate_uid() {
        return this.update_uid;
    }

    public void setUpdate_uid(String update_uid) {
        this.update_uid = update_uid;
    }

    public String getUpdateUname() {
        return this.updateUname;
    }

    public void setUpdateUname(String updateUname) {
        this.updateUname = updateUname;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public List<QDMeetingDetailModel.UsersBean> getUsers() {
        return this.users;
    }

    public void setUsers(List<QDMeetingDetailModel.UsersBean> users) {
        this.users = users;
    }

    public static class UsersBean {
        private String id;
        @SerializedName("meeting_id")
        private String meetingId;
        @SerializedName("user_id")
        private String userId;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("user_avatar")
        private String userAvatar;
        private int role;

        public UsersBean() {
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMeetingId() {
            return this.meetingId;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public String getUserId() {
            return this.userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return this.userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserAvatar() {
            return this.userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public int getRole() {
            return this.role;
        }

        public void setRole(int role) {
            this.role = role;
        }
    }
}
