package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * 新闻资讯栏目列表
 */
public class CircleTagListBean extends BaseGlobal {
    private List<CircleTagBean> objList;

    public List<CircleTagBean> getObjList() {
        return objList;
    }

    public static class CircleTagBean {
        private List<CircleTagBean>objTagList;

        public List<CircleTagBean> getObjTagList() {
            return objTagList;
        }

        private String strTypeName;
        private String strLabelName;
        private String strCode;
        private int intFollow;

        public void setIntFollow(int intFollow) {
            this.intFollow = intFollow;
        }

        public int getIntFollow() {
            return intFollow;
        }

        public String getStrTypeName() {
            return strTypeName;
        }

        public void setStrTypeName(String strTypeName) {
            this.strTypeName = strTypeName;
        }

        public String getStrLabelName() {
            return strLabelName;
        }

        public void setStrLabelName(String strLabelName) {
            this.strLabelName = strLabelName;
        }

        public String getStrCode() {
            return strCode;
        }

        public void setStrCode(String strCode) {
            this.strCode = strCode;
        }
    }
}
