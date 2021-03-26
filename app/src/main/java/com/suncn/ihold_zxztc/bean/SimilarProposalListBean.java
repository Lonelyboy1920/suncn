package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by whh on 2018-8-22.
 * 相似提案
 */
public class SimilarProposalListBean extends BaseGlobal {
    private boolean isSaveData;//是否能继续提交
    private String strCheckMsg;//提示语
    private ArrayList<SimilarProposal> objList;

    public boolean isSaveData() {
        return isSaveData;
    }

    public String getStrCheckMsg() {
        return strCheckMsg;
    }

    public ArrayList<SimilarProposal> getObjList() {
        return objList;
    }

    public class SimilarProposal implements Serializable {
        private String strDestId;//id
        private String strDestTitle;//标题
        private String strDestSourceName;//来源
        private String strReportDate;//发布日期
        private String strContentLikePer;//相似率

        public String getStrDestId() {
            return strDestId;
        }

        public String getStrDestTitle() {
            return strDestTitle;
        }

        public String getStrDestSourceName() {
            return strDestSourceName;
        }

        public String getStrReportDate() {
            return strReportDate;
        }

        public String getStrContentLikePer() {
            return strContentLikePer;
        }
    }
}
