package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * 柱形图bean
 */
public class BarChartsBean extends BaseGlobal {

    private ArrayList<ObjListBean> activeByDayList;
    private String strRankUrl;//排名
    private String strInstallUrl;//安装情况

    public String getStrRankUrl() {
        return strRankUrl;
    }

    public String getStrInstallUrl() {
        return strInstallUrl;
    }

    public ArrayList<ObjListBean> getActiveByDayList() {
        return activeByDayList;
    }

    public static class ObjListBean {
        /**
         * strTime : 1
         * strName : 小李
         */
        private int intCount;//登录人数
        private String strDate;//日期
        private int intTime;//登录次数

        public int getIntTime() {
            return intTime;
        }

        public int getIntCount() {
            return intCount;
        }

        public String getStrDate() {
            return strDate;
        }
    }
}
