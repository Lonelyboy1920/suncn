package com.suncn.ihold_zxztc.bean;

public class TuLingRequestBean {
    private int reqType = 0; // 输入类型:0-文本(默认)、1-图片、2-音频
    private PerceptionBean perception; // 输入信息
    private UserInfoBean userInfo; // 用户参数

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public PerceptionBean getPerception() {
        return perception;
    }

    public void setPerception(PerceptionBean perception) {
        this.perception = perception;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public class PerceptionBean {
        private InputTextBean inputText; // 文本信息
        private SelfInfoBean selfInfo; // 客户端属性

        public InputTextBean getInputText() {
            return inputText;
        }

        public void setInputText(InputTextBean inputText) {
            this.inputText = inputText;
        }

        public SelfInfoBean getSelfInfo() {
            return selfInfo;
        }

        public void setSelfInfo(SelfInfoBean selfInfo) {
            this.selfInfo = selfInfo;
        }
    }

    public class InputTextBean {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public class SelfInfoBean {
        private LocationBean location; // 地理位置信息

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }
    }

    public class LocationBean {
        private String city; // 所在城市
        private String province; // 省份
        private String street; // 街道

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }

    public class UserInfoBean {
        private String apiKey = "a2100cb9817e4e3aa7009d888e416d97"; // 机器人标识
        private String userId = "admin"; // 用户唯一标识

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
