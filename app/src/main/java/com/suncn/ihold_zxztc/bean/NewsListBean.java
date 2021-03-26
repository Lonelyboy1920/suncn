package com.suncn.ihold_zxztc.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 新闻资讯列表
 */
public class NewsListBean extends BaseGlobal {
    private ArrayList<NewsBean> objList;
    private ArrayList<NewsBean> bannerNewsList; // 轮播图

    public ArrayList<NewsBean> getObjList() {
        return objList;
    }

    public ArrayList<NewsBean> getBannerNewsList() {
        return bannerNewsList;
    }

    public class NewsBean extends BaseTypeBean implements Serializable {
        private String strId; // 信息id
        private String strTitle; // 标题
        private String strImagePath; //  图片路径
        private String strThumbPath; //  图片路径
        private String strPubUnit; // 新闻发布单位
        private String strPubDate; // 消息发布时间
        private String strUrl; // banner图url
        private String strTopState;//是否置顶（0-不置顶；1-置顶）
        private String strHotState;//是否热帖（0-否，1-是）
        private String strFileType;//文件类型
        private String strRecord;//是否已读
        private int intReplyCount;//评论数量
        private objShare objShare;

        public NewsListBean.objShare getObjShare() {
            return objShare;
        }

        public int getIntReplyCount() {
            return intReplyCount;
        }

        public String getStrRecord() {
            return strRecord;
        }

        public void setStrRecord(String strRecord) {
            this.strRecord = strRecord;
        }


        public String getStrThumbPath() {
            return strThumbPath;
        }

        public String getStrFileType() {
            return strFileType;
        }

        private ArrayList<ImageInfo> imageWithVideoPathList;

        public String getStrTopState() {
            return strTopState;
        }

        public String getStrHotState() {
            return strHotState;
        }

        public ArrayList<ImageInfo> getImageWithVideoPathList() {
            return imageWithVideoPathList;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrImagePath() {
            return strImagePath;
        }

        public String getStrPubUnit() {
            return strPubUnit;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public String getStrUrl() {
            return strUrl;
        }


    }

    public class objShare implements Serializable {
        private String strIMGUrl;
        private String strTitle;
        private String strContent;

        public String getStrContent() {
            return strContent;
        }

        public String getStrIMGUrl() {
            return strIMGUrl;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrPageUrl() {
            return strPageUrl;
        }

        private String strPageUrl;
    }

    public class ImageInfo {
        private String strImagePath;//视频默认图片
        private String strVideoImagePath;//视频、图片路径
        private String strPrimaryImagePath;
        private String strThumbPath;

        public String getStrThumbPath() {
            return strThumbPath;
        }

        public String getStrPrimaryImagePath() {
            return strPrimaryImagePath;
        }

        public String getStrVideoImagePath() {
            return strVideoImagePath;
        }

        public String getStrImagePath() {
            return strImagePath;
        }
    }
}
