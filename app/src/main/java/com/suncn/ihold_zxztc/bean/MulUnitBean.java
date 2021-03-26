package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MulUnitBean extends BaseGlobal {
    private ArrayList<MulUnit> objList;

    public ArrayList<MulUnit> getObjList() {
        return objList;
    }

    public class MulUnit  implements Serializable {

        /**
         * strRootUnitId : cdece00c5730444e80a2f8fb48149c92
         * intUserRole : 0
         * strRootUnitName : 合肥市政协政协委员
         * strRootUnitIconPathUrl :
         * strRootUnitDefalut : 1
         * strRootUnitPasswordState : 1
         * strRootUnitFlagState : 1
         * strRootUnitState : 1
         */

        private String strRootUnitId;
        private int intUserRole;
        private String strRootUnitName;
        private String strRootUnitIconPathUrl;
        private String strRootUnitDefalut;
        private String strRootUnitPasswordState;
        private String strRootUnitFlagState;
        private String strRootUnitState;

        public String getStrRootUnitId() {
            return strRootUnitId;
        }

        public void setStrRootUnitId(String strRootUnitId) {
            this.strRootUnitId = strRootUnitId;
        }

        public int getIntUserRole() {
            return intUserRole;
        }

        public void setIntUserRole(int intUserRole) {
            this.intUserRole = intUserRole;
        }

        public String getStrRootUnitName() {
            return strRootUnitName;
        }

        public void setStrRootUnitName(String strRootUnitName) {
            this.strRootUnitName = strRootUnitName;
        }

        public String getStrRootUnitIconPathUrl() {
            return strRootUnitIconPathUrl;
        }

        public void setStrRootUnitIconPathUrl(String strRootUnitIconPathUrl) {
            this.strRootUnitIconPathUrl = strRootUnitIconPathUrl;
        }

        public String getStrRootUnitDefalut() {
            return strRootUnitDefalut;
        }

        public void setStrRootUnitDefalut(String strRootUnitDefalut) {
            this.strRootUnitDefalut = strRootUnitDefalut;
        }

        public String getStrRootUnitPasswordState() {
            return strRootUnitPasswordState;
        }

        public void setStrRootUnitPasswordState(String strRootUnitPasswordState) {
            this.strRootUnitPasswordState = strRootUnitPasswordState;
        }

        public String getStrRootUnitFlagState() {
            return strRootUnitFlagState;
        }

        public void setStrRootUnitFlagState(String strRootUnitFlagState) {
            this.strRootUnitFlagState = strRootUnitFlagState;
        }

        public String getStrRootUnitState() {
            return strRootUnitState;
        }

        public void setStrRootUnitState(String strRootUnitState) {
            this.strRootUnitState = strRootUnitState;
        }
    }
}
