package com.suncn.ihold_zxztc.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.gavin.giframe.utils.GIStringUtil;

import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 回复
 */
public class ReplyBean extends BaseGlobal implements MultiItemEntity {
    private String strReplyUserName; //评论者的名字
    private String strReplyUserId;
    private String strReplyUserPhoto; //评论者的图片路径
    private String strReplyDate; //评论时间
    private String strReplyUserTempName; //评论者界别或者单位名称
    private String strReplyContent;//评论内容
    private String loginUserPraiseState = "0";//当前登录人是否对评论点赞 0 否 1是
    private int intPraiseCount;//点赞数量

    private String strReplyId; //评论的主键
    private String strChoiceState; //是否精选评论 0 普通 1精选 当 intUserRole 为 1 时 strChoiceState 为 0 时 显示精选按钮 效果精选置顶

    private String strReplyParentId;//评论的父级的id
    private int intReplyCount;//回复数量
    private ArrayList<ReplyBean> childList; // 评论的子集集合
    private int position;
    private boolean isShow;//查看更多按钮的显示与隐藏
    private int intCount;//0 取消点赞、取消收藏； 1点赞、收藏成功
    private String strHideState; // 评论是否可删除（1-可删除；0-不可删除）
    private Object adapter;
    private boolean isShowAllLines; // 是否显示所有行数
    private boolean isShowAllChild; // 是否显示所有子评论
    private String strHasDel;//是否可删除
    private int strType = 0;
    private String strRepliedUserId;
    private String strRepliedUserName;

    public void setStrRepliedUserId(String strRepliedUserId) {
        this.strRepliedUserId = strRepliedUserId;
    }

    public void setStrRepliedUserName(String strRepliedUserName) {
        this.strRepliedUserName = strRepliedUserName;
    }

    public String getStrRepliedUserId() {
        return strRepliedUserId;
    }

    public String getStrRepliedUserName() {
        return strRepliedUserName;
    }

    public String getStrReplyUserId() {
        return strReplyUserId;
    }

    public void setStrHasDel(String strHasDel) {
        this.strHasDel = strHasDel;
    }

    public String getStrHasDel() {
        return strHasDel;
    }

    public ReplyBean() {
    }

    public boolean isShowAllChild() {
        return isShowAllChild;
    }

    public void setShowAllChild(boolean showAllChild) {
        isShowAllChild = showAllChild;
    }

    public Object getAdapter() {
        return adapter;
    }

    public void setAdapter(Object adapter) {
        this.adapter = adapter;
    }

    public void setStrHideState(String strHideState) {
        this.strHideState = strHideState;
    }

    public String getStrHideState() {
        return strHideState;
    }


    public String getStrChoiceState() {
        return strChoiceState;
    }

    public void setIntCount(int intCount) {
        this.intCount = intCount;
    }

    public boolean isShowAllLines() {
        return isShowAllLines;
    }

    public void setShowAllLines(boolean showAllLines) {
        isShowAllLines = showAllLines;
    }

    public int getIntCount() {
        return intCount;
    }

    public int getIntReplyCount() {
        return intReplyCount;
    }

    public void setIntReplyCount(int intReplyCount) {
        this.intReplyCount = intReplyCount;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getStrReplyId() {
        return strReplyId;
    }

    public void setStrReplyId(String strReplyId) {
        this.strReplyId = strReplyId;
    }

    public String getStrReplyUserPhoto() {
        return strReplyUserPhoto;
    }

    public void setStrReplyUserPhoto(String strReplyUserPhoto) {
        this.strReplyUserPhoto = strReplyUserPhoto;
    }

    public String getStrReplyDate() {
        return strReplyDate;
    }

    public void setStrReplyDate(String strReplyDate) {
        this.strReplyDate = strReplyDate;
    }

    public String getStrReplyUserName() {
        return strReplyUserName;
    }

    public void setStrReplyUserId(String strReplyUserId) {
        this.strReplyUserId = strReplyUserId;
    }

    public void setStrReplyUserName(String strReplyUserName) {
        this.strReplyUserName = strReplyUserName;
    }

    public String getStrReplyUserTempName() {
        return strReplyUserTempName;
    }

    public void setStrReplyUserTempName(String strReplyUserTempName) {
        this.strReplyUserTempName = strReplyUserTempName;
    }

    public String getStrReplyParentId() {
        return strReplyParentId;
    }

    public void setStrReplyParentId(String strReplyParentId) {
        this.strReplyParentId = strReplyParentId;
    }

    public String getStrReplyContent() {
        try {
            if (GIStringUtil.isBlank(strReplyContent)) {
                return strReplyContent;
            } else {
                return URLDecoder.decode(strReplyContent, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setStrReplyContent(String strReplyContent) {
        this.strReplyContent = strReplyContent;
    }

    public String getLoginUserPraiseState() {
        return loginUserPraiseState;
    }

    public void setLoginUserPraiseState(String loginUserPraiseState) {
        this.loginUserPraiseState = loginUserPraiseState;
    }

    public int getIntPraiseCount() {
        return intPraiseCount;
    }

    public void setIntPraiseCount(int intPraiseCount) {
        this.intPraiseCount = intPraiseCount;
    }

    public ArrayList<ReplyBean> getChildList() {
        if (childList == null) {
            childList = new ArrayList<>();
        }
        return childList;
    }

    public void setChildList(ArrayList<ReplyBean> childList) {
        this.childList = childList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getItemType() {
        return strType;
    }

}
