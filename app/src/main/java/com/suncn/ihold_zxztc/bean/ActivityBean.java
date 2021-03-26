package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class ActivityBean extends BaseGlobal {
    private String intBirthDayNumber;//当日生日的人数 去除当前登陆人
    private String strBirthDayOtherUserName;//当日生日的人数 去除当前登陆人 为一人时 显示生日人的姓名
    private String strLoginBirthDayUserName;//当日生日的登录人是否过生日 非空表示过生日
    private String intFlowerNumber;//当日生日的登录人 收到的鲜花数量
    private String strBirthPopupHideState;//送1 隐藏生日弹框 0 弹出生日框
    private String strFlowerPopupHideState;//收 1 隐藏生日弹框 0 弹出生日框
    private String strIsHaveEvent;//是否弹出运营活动弹窗 1有0无
    private ArrayList<bannerBean> bannerList;
    private ArrayList<bannerBean> popList;

    public ArrayList<bannerBean> getBannerList() {
        return bannerList;
    }

    public ArrayList<bannerBean> getPopList() {
        return popList;
    }

    public class bannerBean {
        private String strPicUrl;
        private String strUrl;

        public String getStrPicUrl() {
            return strPicUrl;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrId() {
            return strId;
        }

        private String strId;

    }

    public String getStrIsHaveEvent() {
        return strIsHaveEvent;
    }

    public String getIntBirthDayNumber() {
        return intBirthDayNumber;
    }

    public String getStrBirthDayOtherUserName() {
        return strBirthDayOtherUserName;
    }

    public String getStrLoginBirthDayUserName() {
        return strLoginBirthDayUserName;
    }

    public String getIntFlowerNumber() {
        return intFlowerNumber;
    }

    public String getStrBirthPopupHideState() {
        return strBirthPopupHideState;
    }

    public String getStrFlowerPopupHideState() {
        return strFlowerPopupHideState;
    }
}
