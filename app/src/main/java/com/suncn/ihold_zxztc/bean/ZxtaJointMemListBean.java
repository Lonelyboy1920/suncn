package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 联名委员选择信息ZxtaJointMemServlet
 */
public class ZxtaJointMemListBean extends BaseGlobal {
    private int intRoomSizeByLoginUser;//群组数量
    private String strNewUser;//0-用户信息变化，1-群组信息变，2-两者都变
    private ArrayList<ZxtaJointMemBean> objList;
    private ArrayList<ZxtaJointMemBean> objReportList;
    private ArrayList<MyUnit> objUnits_list;
    private ArrayList<BaseUser> objSimpleList; // 市政协一级通讯录
    private String strUrl;

    public String getStrUrl() {
        return strUrl;
    }

    public int getIntRoomSizeByLoginUser() {
        return intRoomSizeByLoginUser;
    }

    public ArrayList<BaseUser> getObjSimpleList() {
        return objSimpleList;
    }

    public ArrayList<MyUnit> getObjUnits_list() {
        return objUnits_list;
    }

    public String getStrNewUser() {
        return strNewUser;
    }

    public ArrayList<ZxtaJointMemBean> getObjList() {
        return objList;
    }

    public ArrayList<ZxtaJointMemBean> getObjReportList() {
        return objReportList;
    }

    //委员、机构
    public static class ZxtaJointMemBean implements Serializable {
        private String strName;
        private ArrayList<BaseUser> objChildList;

        public String getStrName() {
            return strName;
        }

        public ArrayList<BaseUser> getObjChildList() {
            return objChildList;
        }

        public void setObjChildList(ArrayList<BaseUser> objChildList) {
            this.objChildList = objChildList;
        }
    }
}
