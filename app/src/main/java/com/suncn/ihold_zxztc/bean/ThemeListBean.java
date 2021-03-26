package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2019-05-13.
 * 主题列表 SubjectListServlet
 */

public class ThemeListBean extends BaseGlobal {
    private ArrayList<ThemeBean> hotSubjectList;//热门主题
    private ArrayList<ThemeBean> subjectList;//推荐主题

    public ArrayList<ThemeBean> getHotSubjectList() {
        return hotSubjectList;
    }

    public ArrayList<ThemeBean> getSubjectList() {
        return subjectList;
    }

    public class ThemeBean {
        private String strId;//主键id
        private String strTitle;//标题
        private String strPubDate;//发布日期
        private int intDiscussCount;//评论数量
        private int intBrowseCount;//阅读数量
        private String strRankImgUrl;//热门排序
        private String strRankImgBgUrl;//热门背景
        private String strHotImgUrl;//推荐图片
        private String strUrl;//详情Url
        private String strViewPermissions;//0否 1是可以查看权限
        private String strDateState;//主题回复的有效时间状态 -1 已结束 1 进行中 0未开始 只有进行中才能评论

        public String getStrHotImgUrl() {
            return strHotImgUrl;
        }

        public String getStrViewPermissions() {
            return strViewPermissions;
        }

        public String getStrRankImgBgUrl() {
            return strRankImgBgUrl;
        }

        public String getStrDateState() {
            return strDateState;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrRankImgUrl() {
            return strRankImgUrl;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public int getIntDiscussCount() {
            return intDiscussCount;
        }

        public int getIntBrowseCount() {
            return intBrowseCount;
        }

        public String getStrUrl() {
            return strUrl;
        }
    }
}
