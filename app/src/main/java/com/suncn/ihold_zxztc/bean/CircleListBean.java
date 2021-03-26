package com.suncn.ihold_zxztc.bean;

import com.gavin.giframe.utils.GIStringUtil;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class CircleListBean extends BaseGlobal {
    private int intNotReadCount;

    public int getIntNotReadCount() {
        return intNotReadCount;
    }

    private ArrayList<RecommendBean> recommendList;
    private ArrayList<RecommendBean> myNoticeList;

    public ArrayList<RecommendBean> getMyNoticeList() {
        return myNoticeList;
    }

    public ArrayList<RecommendBean> noticeList;
    public ArrayList<RecommendBean> userList;

    public ArrayList<RecommendBean> getUserList() {
        return userList;
    }

    public ArrayList<RecommendBean> getNoticeList() {
        return noticeList;
    }

    private ArrayList<DynamicBean> dynamicList;

    public ArrayList<DynamicBean> getDynamicList() {
        return dynamicList;
    }

    public ArrayList<RecommendBean> getRecommendList() {
        return recommendList;
    }

    public class RecommendBean {
        private String strUserId;
        private String strUserName;
        private String strPhotoPath;
        private String strNoticeUserPhotoPath;
        private String strNoticeUserDuty;
        private String strNoticeUserSector;
        private String strNoticeUserName;
        private String strNoticeDate;
        private String strLoginUseState;//是否是本人

        public String getStrLoginUseState() {
            return strLoginUseState;
        }

        public String getStrUserPhotoPath() {
            return strUserPhotoPath;
        }

        public String getStrSector() {
            return strSector;
        }

        private String strUserPhotoPath;
        private String strSector;

        public String getStrNoticeUserPhotoPath() {
            return strNoticeUserPhotoPath;
        }

        public String getStrNoticeUserDuty() {
            return strNoticeUserDuty;
        }

        public String getStrNoticeUserSector() {
            return strNoticeUserSector;
        }

        public String getStrNoticeUserName() {
            return strNoticeUserName;
        }

        public String getStrNoticeDate() {
            return strNoticeDate;
        }

        public String getIntDynamicCount() {
            return intDynamicCount;
        }

        private String intDynamicCount;


        public String getStrUserId() {
            return strUserId;
        }

        public String getStrUserName() {
            return strUserName;
        }

        public String getStrPhotoPath() {
            return strPhotoPath;
        }

        public String getStrDuty() {
            return strDuty;
        }

        private String strDuty;

    }

    public class DynamicBean implements Serializable {
        private String strId;
        private String strContent;
        private ArrayList<picBaen> picList;
        private String strPubUserId;
        private String strPubUserName;
        private String strPubDate;
        private String loginUserPraiseState;
        private int intPraiseCount;
        private int intDiscussCount;
        private String strPhotoPath; // 发布人图像
        private boolean isNotice;
        private String strShowInterestButton;//0不显示关注按钮 1显示
        private int intShareCount;
        private String strDuty; // 发布人职位
        private String strHasDel;//是否可以删除
        private NewsListBean.objShare objShare;

        public NewsListBean.objShare getObjShare() {
            return objShare;
        }

        public String getStrHasDel() {
            return strHasDel;
        }

        public String getStrDuty() {
            return strDuty;
        }

        public String getStrShowInterestButton() {
            return strShowInterestButton;
        }

        public void setIntShareCount(int intShareCount) {
            this.intShareCount = intShareCount;
        }

        public void setLoginUserPraiseState(String loginUserPraiseState) {
            this.loginUserPraiseState = loginUserPraiseState;
        }

        public void setIntDiscussCount(int intDiscussCount) {
            this.intDiscussCount = intDiscussCount;
        }

        public int getIntShareCount() {
            return intShareCount;
        }

        public void setNotice(boolean notice) {
            isNotice = notice;
        }

        public String getStrPhotoPath() {
            return strPhotoPath;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrContent() {
            try {
                if (GIStringUtil.isBlank(strContent)){
                    return  strContent;
                }
                return URLDecoder.decode(strContent,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return strContent;
        }

        public ArrayList<picBaen> getPicList() {
            return picList;
        }

        public String getStrPubUserId() {
            return strPubUserId;
        }

        public String getStrPubUserName() {
            return strPubUserName;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public String getLoginUserPraiseState() {
            return loginUserPraiseState;
        }

        public int getIntPraiseCount() {
            return intPraiseCount;
        }

        public void setIntPraiseCount(int intPraiseCount) {
            this.intPraiseCount = intPraiseCount;
        }

        public int getIntDiscussCount() {
            return intDiscussCount;
        }

        public boolean isNotice() {
            return isNotice;
        }

        public class picBaen implements Serializable {
            private String strPrimaryImagePath;
            private String strImagePath;
            private String strThumbPath;
            private String strFileId;
            private String strVideoImagePath;
            private String strFileType;//附件类型

            public String getStrFileType() {
                return strFileType;
            }

            public String getStrVideoImagePath() {
                return strVideoImagePath;
            }

            public String getStrFileId() {
                return strFileId;
            }

            public String getStrThumbPath() {
                return strThumbPath;
            }

            public String getStrImagePath() {
                return strImagePath;
            }

            public String getStrPrimaryImagePath() {
                return strPrimaryImagePath;
            }
        }
    }

}
