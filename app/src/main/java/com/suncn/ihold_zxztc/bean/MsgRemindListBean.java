package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class MsgRemindListBean extends BaseGlobal {
    private ArrayList<msgRemindBean> objList;

    public ArrayList<msgRemindBean> getObjList() {
        return objList;
    }

    public class msgRemindBean {
        private String strId;
        private String strMsgTypeName;
        private String strMsgContent;
        private String strDistanceTime;
        private String strMobileState;//是否已读

        public void setStrFollowState(String strFollowState) {
            this.strFollowState = strFollowState;
        }

        public String getStrUserId() {
            return strUserId;
        }

        public String getStrUserName() {
            return strUserName;
        }

        public String getStrPhotoPath() {
            return strPhotoPath;
        }

        public String getStrFollowState() {
            return strFollowState;
        }

        private String strUserId;
        private String strUserName;
        private String strPhotoPath;
        private String strFollowState;//空字符串代表普通消息 0 代表未关注 可以点击操作 1代表 已关注不可以点击



        public String getStrId() {
            return strId;
        }

        public String getStrMsgTypeName() {
            return strMsgTypeName;
        }

        public String getStrMsgContent() {
            return strMsgContent;
        }

        public String getStrDistanceTime() {
            return strDistanceTime;
        }

        public String getStrMobileState() {
            return strMobileState;
        }


    }
}
