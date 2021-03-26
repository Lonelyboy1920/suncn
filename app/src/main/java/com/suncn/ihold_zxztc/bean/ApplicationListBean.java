package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录(AuthServlet)
 */
public class ApplicationListBean extends BaseGlobal {
    private ArrayList<ApplicationBean> objList; // 功能菜单集合
    private String strUseSaoMQianD;

    public String getStrUseSaoMQianD() {
        return strUseSaoMQianD;
    }

    public List<ApplicationBean> getObjList() {
        return objList;
    }

    public static class ApplicationBean implements Serializable {
        private String strName; // 名称
        //strType 类型（01-政协提案, 02-社情民意,03-我的会议,04-会议发言,05-我的活动,07-随手拍,08-我的评估,09-提案线索,
        // 11-文史资料库,13-提案酝酿, 32-双联双创,34-我的短信,35-网络议政,14-提案审核,30-委员查询，31-全会指南，15-提案追踪,
        // 16-委员信息,17-会议管理,18-活动管理,19-社情民意, 20-会议发言，22-提案交办, 23-信息交办,24-提案办理,25-信息办理）
        private String strType;
        private int intCount;//右上角数字

        public int getIntCount() {
            return intCount;
        }

        public void setIntCount(int intCount) {
            this.intCount = intCount;
        }

        public String getStrName() {
            return strName;
        }

        public String getStrType() {
            return strType;
        }

        public void setStrName(String strName) {
            this.strName = strName;
        }

        public void setStrType(String strType) {
            this.strType = strType;
        }
    }

}


