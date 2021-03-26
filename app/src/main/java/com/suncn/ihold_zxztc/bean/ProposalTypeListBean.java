package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-5-18.
 */

public class ProposalTypeListBean extends BaseGlobal {
    private ArrayList<ProposalType> objList;

    public ArrayList<ProposalType> getObjList() {
        return objList;
    }

    public class ProposalType {
        private String strCaseTypeName;
        private String strCaseTypeId;

        public String getStrCaseTypeName() {
            return strCaseTypeName;
        }

        public String getStrCaseTypeId() {
            return strCaseTypeId;
        }
    }
}
