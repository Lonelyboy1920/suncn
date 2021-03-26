package com.suncn.ihold_zxztc.bean.chat;

import com.suncn.ihold_zxztc.bean.BaseGlobal;

import java.util.ArrayList;

/**
 * 最近聊天记录列表数据
 */
public class RecentMessageListData extends BaseGlobal {
    private ArrayList<UserInfoBean> objData;
    private ArrayList<NoticeInfo> objList;

    public ArrayList<NoticeInfo> getObjList() {
        return objList;
    }

    public ArrayList<UserInfoBean> getObjData() {
        return objData;
    }

    public class NoticeInfo {
        private String strSourceType;
        private String strTitle;
        private int intNotReadCount;

        public String getStrSourceType() {
            return strSourceType;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public int getIntNotReadCount() {
            return intNotReadCount;
        }
    }
}
