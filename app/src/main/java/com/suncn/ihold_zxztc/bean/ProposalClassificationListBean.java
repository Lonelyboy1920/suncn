package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-8-31.
 * 提案分类MotionPreTopicListServlet
 */

public class ProposalClassificationListBean extends BaseGlobal {
    private ArrayList<GroupProposalClassification> objList;

    public ArrayList<GroupProposalClassification> getObjList() {
        return objList;
    }

    public class GroupProposalClassification {
        private String strCaseTypeId;//类别的id
        private String strCaseTypeName;//类别的名称
        private ArrayList<ChildProposalClassification> caseTopic_list;
        private boolean isChecked;

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public String getStrCaseTypeId() {
            return strCaseTypeId;
        }

        public String getStrCaseTypeName() {
            return strCaseTypeName;
        }

        public ArrayList<ChildProposalClassification> getCaseTopic_list() {
            return caseTopic_list;
        }
    }

    public class ChildProposalClassification {
        private String strCaseTypeId;//类别的id
        private String strCaseTypeName;//类别的名称
        private ArrayList<ProposalClassification> caseTopic_list;//第三极集合
        private boolean isChecked;

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public ArrayList<ProposalClassification> getCaseTopic_list() {
            return caseTopic_list;
        }

        public String getStrCaseTypeId() {
            return strCaseTypeId;
        }

        public String getStrCaseTypeName() {
            return strCaseTypeName;
        }
    }

    public class ProposalClassification {
        private String strCaseTypeId;//类别的id
        private String strCaseTypeName;//类别的名称
        private boolean isChecked;

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public String getStrCaseTypeId() {
            return strCaseTypeId;
        }

        public String getStrCaseTypeName() {
            return strCaseTypeName;
        }
    }
}
