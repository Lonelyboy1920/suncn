package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-5-18.
 */

public class ProposalTabListBean extends BaseGlobal {
    private ArrayList<ProposalTab> objList;

    public ArrayList<ProposalTab> getObjList() {
        return objList;
    }

    public class ProposalTab {
        private String strName;
        private String strType;
        private String strId;

        public String getStrId() {
            return strId;
        }
        public String getStrName() {
            return strName;
        }

        public String getStrType() {
            return strType;
        }
    }
}
