package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * 主题议政回复列表
 */
public class ReplyListBean extends BaseGlobal {
    private String strListenContent;//播报内容
    private String loginUserPraiseState;//当前登录人是否对新闻点赞 0 否 1是 ;
    private String loginUserCollectState;//当前登录人是否对新闻收藏 0 否 1是
    private int intPraiseCount;//点赞数
    private int intBrowseCount;//浏览数
    private String strDateState; // 主题议政主题回复的有效时间状态 -1 已结束 1 进行中 0未开始 只有进行中才能评论
    private ArrayList<ReplyBean> objList;
    private int intAllRecordCount;

    public int getIntAllRecordCount() {
        return intAllRecordCount;
    }

    public int getIntBrowseCount() {
        return intBrowseCount;
    }

    public String getStrDateState() {
        return strDateState;
    }

    public int getIntPraiseCount() {
        return intPraiseCount;
    }

    public String getLoginUserPraiseState() {
        return loginUserPraiseState;
    }

    public String getLoginUserCollectState() {
        return loginUserCollectState;
    }

    public String getStrListenContent() {
        return strListenContent;
    }

    public ArrayList<ReplyBean> getObjList() {
        return objList;
    }
}
