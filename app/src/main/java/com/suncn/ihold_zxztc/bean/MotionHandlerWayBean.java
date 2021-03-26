package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * Created by whh on 2018-8-2.
 * 提案提交协商方式MotionHandlerWay
 */

public class MotionHandlerWayBean extends BaseGlobal {
    private List<ObjListBean> objList;

    public List<ObjListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjListBean> objList) {
        this.objList = objList;
    }

    public class ObjListBean {
        private String strName;//显示的值
        private String strCode;//0-不选中，1-选中
        private String checked;

        public String getChecked() {
            return checked;
        }

        public void setChecked(String checked) {
            this.checked = checked;
        }

        public String getStrName() {
            return strName;
        }


        public String getStrCode() {
            return strCode;
        }
    }
}
