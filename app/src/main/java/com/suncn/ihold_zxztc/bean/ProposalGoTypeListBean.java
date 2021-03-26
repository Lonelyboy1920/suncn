package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-9-6.
 * 不予立案提案去向MotionCheckResultTypeServlet
 */

public class ProposalGoTypeListBean extends BaseGlobal {
    private ArrayList<ProposalGoType> objList;

    public ArrayList<ProposalGoType> getObjList() {
        return objList;
    }

    public class ProposalGoType {
        private String strName;//名称
        private String strCode;//编码
        private String checked;//是否选择

        public String getStrName() {
            return strName;
        }

        public String getStrCode() {
            return strCode;
        }

        public String getChecked() {
            return checked;
        }
    }
}
