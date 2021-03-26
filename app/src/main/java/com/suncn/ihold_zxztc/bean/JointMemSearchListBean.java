package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * 联名委员选择信息ZxtaJointMemServlet
 */
public class JointMemSearchListBean extends BaseGlobal {

    private ArrayList<BaseUser> objList;
    private String strUrl;

    public String getStrUrl() {
        return strUrl;
    }

    public ArrayList<BaseUser> getObjList() {
        return objList;
    }
}
