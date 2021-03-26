package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by moos on 2018/4/20.
 */
public class CommentDetailBean extends BaseGlobal {
    private ArrayList<ReplyBean> objList;
    private CircleListBean.DynamicBean dynamic;
    private String loginUserPraiseState;//1已赞0未赞

    public String getLoginUserPraiseState() {
        return loginUserPraiseState;
    }

    public ArrayList<ReplyBean> getObjList() {
        return objList;
    }

    public CircleListBean.DynamicBean getDynamic() {
        return dynamic;
    }
    //    public Dynamic getDynamic() {
//        return dynamic;
//    }
//
//    public class Dynamic implements Serializable {
//        private String strPubUserName;
//        private String strPubUserId;
//        private String strPubDate;
//        private ArrayList<ImgBean> picList;
//        private String strContent;
//        private String loginUserPraiseState;
//        private String intShareCount;
//        private int intPraiseCount;
//        private boolean isNotice;
//        private String strDuty;
//        private String strPhotoPath;
//        private String strShowInterestButton;
//        private int intDiscussCount;
//
//        public String getStrDuty() {
//            return strDuty;
//        }
//
//        public String getStrShowInterestButton() {
//            return strShowInterestButton;
//        }
//
//        public String getStrPhotoPath() {
//            return strPhotoPath;
//        }
//
//        public boolean isNotice() {
//            return isNotice;
//        }
//
//        public String getStrPubUserName() {
//            return strPubUserName;
//        }
//
//        public String getStrPubUserId() {
//            return strPubUserId;
//        }
//
//        public String getStrPubDate() {
//            return strPubDate;
//        }
//
//        public ArrayList<ImgBean> getPicList() {
//            return picList;
//        }
//
//        public String getStrContent() {
//            return strContent;
//        }
//
//        public String getLoginUserPraiseState() {
//            return loginUserPraiseState;
//        }
//
//        public String getIntShareCount() {
//            return intShareCount;
//        }
//
//        public int getIntPraiseCount() {
//            return intPraiseCount;
//        }
//
//        public int getIntDiscussCount() {
//            return intDiscussCount;
//        }
//
//        public void setIntDiscussCount(int intDiscussCount) {
//            this.intDiscussCount = intDiscussCount;
//        }
//    }
//
//    public class ImgBean  implements Serializable{
//        private String strImagePath;
//        private String strThumbPath;
//        private String strVideoImagePath;
//        private String strFileType;//附件类型
//
//        public String getStrFileType() {
//            return strFileType;
//        }
//
//        public String getStrVideoImagePath() {
//            return strVideoImagePath;
//        }
//
//
//        public String getStrThumbPath() {
//            return strThumbPath;
//        }
//
//        public String getStrImagePath() {
//            return strImagePath;
//        }
//    }
}
