package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * 新闻资讯栏目列表
 */
public class NewsColumnListBean extends BaseGlobal {
    private List<NewsColumnBean> newsColumnList;//新闻首页栏目集合
    private List<NewsColumnBean> objMyList;//栏目设置页面我的栏目集合
    private List<NewsColumnBean> objList;//栏目设置页面更多栏目集合

    public List<NewsColumnBean> getObjMyList() {
        return objMyList;
    }

    public List<NewsColumnBean> getObjList() {
        return objList;
    }

    public List<NewsColumnBean> getNewsColumnList() {
        return newsColumnList;
    }

    public static class NewsColumnBean {
        private String strCode;
        private String strId; // 栏目id
        private String strType; // 全局搜索code
        private String strName; // 栏目姓名
        private String strDate;//时间
        private String strDateOrder;
        private String strFormatDate;
        private String showRootUnit;//是否显示独立机构数据
        private boolean isCanUnsubscribe; //是否可取消订阅

        public boolean isCanUnsubscribe() {
            if ("-1".equals(strId) || "-2".equals(strId))
                return false;
            return true;
        }

        public String getShowRootUnit() {
            return showRootUnit;
        }

        public String getStrDateOrder() {
            return strDateOrder;
        }

        public String getStrFormatDate() {
            return strFormatDate;
        }

        public String getStrDate() {
            return strDate;
        }

        public String getStrCode() {
            return strCode;
        }

        public String getStrType() {
            return strType;
        }

        public void setStrName(String strName) {
            this.strName = strName;
        }

        public String getStrId() {
            return strId;
        }

        public void setStrId(String strId) {
            this.strId = strId;
        }

        public String getStrName() {
            return strName;
        }

    }
}
