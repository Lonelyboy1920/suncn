package com.qd.longchat.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/12/21 下午5:58
 */
public class QDPersonCard {

    /**
     * code : personcard
     * name : 名片
     * title : 售前经理
     * desc : 企达信息-售前部
     * icon : 图片
     * ext_data :
     */

    private String code;
    private String name;
    private String title;
    private String desc;
    private String icon;
    @SerializedName("ext_data")
    private String extData;
    @SerializedName("userid")
    private String userId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getExtData() {
        return extData;
    }

    public void setExtData(String extData) {
        this.extData = extData;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
