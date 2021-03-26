package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * Author :Sea
 * Date :2019/11/7 13:48
 * PackageName:com.suncn.ihold_zxztc.bean
 * Desc: 滁州市政协 提案分发类型
 */
public class MotionDistTypeServletBean extends BaseGlobal {
    private List<ObjListBean> objList;

    public List<ObjListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjListBean> objList) {
        this.objList = objList;
    }

    public class ObjListBean {
        private String strName;

        public String getStrName() {
            return strName;
        }

        public void setStrName(String strName) {
            this.strName = strName;
        }
    }

}
