package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * 我的收藏
 */
public class CollectionListBean extends BaseGlobal {

    private ArrayList<CollectionList> objList;

    public ArrayList<CollectionList> getObjList() {
        return objList;
    }

    public class CollectionList {
        /* "strTitle": "5555555555555555555",
         "strId": "0754ed75b4784c9e87d51e0fc1d741c4",
         "strFileType": "image",
          "strUrl": "ios\/SubjectView?strSid=4ad6048c35334b92b565cbe6738567d0&strId=0754ed75b4784c9e87d51e0fc1d741c4",
          "strPubUnit": "委员联络处",
         "imageWithVideoPathList": [],
         "strPubDate": "2019-06-21 16:56"*/
        private String strTitle;
        private String strId;
        private String strUrl;
        private String strFileType;
        private String strPubUnit;
        private String strPubDate;
        private String strSourceType;//1新闻 2主题
        private ArrayList<imgBean> imageWithVideoPathList;
        private String strDateState;
        private String strRootUnitName;//新闻具体来源,机构
        private NewsListBean.objShare objShare;

        public NewsListBean.objShare getObjShare() {
            return objShare;
        }

        public String getStrRootUnitName() {
            return strRootUnitName;
        }

        public String getStrDateState() {
            return strDateState;
        }

        public String getStrSourceType() {
            return strSourceType;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrFileType() {
            return strFileType;
        }

        public String getStrPubUnit() {
            return strPubUnit;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public ArrayList<imgBean> getImageWithVideoPathList() {
            return imageWithVideoPathList;
        }

        public class imgBean {
            private String strImagePath;
            private String strThumbPath;

            public String getStrThumbPath() {
                return strThumbPath;
            }

            public String getStrImagePath() {
                return strImagePath;
            }
        }

    }
}
