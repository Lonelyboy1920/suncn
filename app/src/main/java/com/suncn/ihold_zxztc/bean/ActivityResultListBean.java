package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * 活动成果
 */
public class ActivityResultListBean extends BaseGlobal {
    private List<ActivityResult> objList;

    public List<ActivityResult> getObjList() {
        return objList;
    }

    public class ActivityResult {
        private String strEventResultId;//活动成果主键
        private String strUpUserName;//提交活动成果人姓名
        private String strContent;//活动成果内容
        private String strUrl;  //  详情url
        private String strPubDate; // 提交日期
        private List<ObjFileBean> affix;

        public String getStrPubDate() {
            return strPubDate;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrEventResultId() {
            return strEventResultId;
        }

        public String getStrUpUserName() {
            return strUpUserName;
        }

        public String getStrContent() {
            return strContent;
        }

        public List<ObjFileBean> getAffix() {
            return affix;
        }
    }
}
