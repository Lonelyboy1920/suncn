package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;

public class MeetUserBean implements Serializable {
    private String strUserName;
    private String strUserId;
    private String videoId;
    private boolean isOnline;
    private boolean isVideo;
    private boolean isAudio;

    public void setAudio(boolean audio) {
        isAudio = audio;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isAudio() {
        return isAudio;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getStrUserId() {
        return strUserId;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserId(String strUserId) {
        this.strUserId = strUserId;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }
}
