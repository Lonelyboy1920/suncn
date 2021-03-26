package com.suncn.ihold_zxztc.bean;

import java.util.List;

public class MorePublicOpinionsListBean extends BaseGlobal {

    private List<MorePublicOpinionBean> objList;

    public List<MorePublicOpinionBean> getObjList() {
        return objList;
    }

    public void setObjList(List<MorePublicOpinionBean> objList) {
        this.objList = objList;
    }

    public static class MorePublicOpinionBean {
        /**
         * strPhotoPath : /res/upload/mobile/publicationicon.png
         * strTitle : 关于加强在回迁小区建立“常态”培训教育的建议
         * strIssue : 2019年 第134期
         * strId : d1bf579fdfbd4129824250a11c86422a
         * strType : 社情民意
         * strUrl : /ios/PublicationView?strId=d1bf579fdfbd4129824250a11c86422a
         * strPubDate : 2019-06-12
         */

        private String strPhotoPath;
        private String strTitle;
        private String strIssue;
        private String strId;
        private String strType;
        private String strUrl;
        private String strPubDate;

        public String getStrPhotoPath() {
            return strPhotoPath;
        }

        public void setStrPhotoPath(String strPhotoPath) {
            this.strPhotoPath = strPhotoPath;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public void setStrTitle(String strTitle) {
            this.strTitle = strTitle;
        }

        public String getStrIssue() {
            return strIssue;
        }

        public void setStrIssue(String strIssue) {
            this.strIssue = strIssue;
        }

        public String getStrId() {
            return strId;
        }

        public void setStrId(String strId) {
            this.strId = strId;
        }

        public String getStrType() {
            return strType;
        }

        public void setStrType(String strType) {
            this.strType = strType;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public void setStrUrl(String strUrl) {
            this.strUrl = strUrl;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public void setStrPubDate(String strPubDate) {
            this.strPubDate = strPubDate;
        }
    }
}
