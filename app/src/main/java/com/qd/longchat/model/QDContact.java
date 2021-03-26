package com.qd.longchat.model;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/25 下午6:25
 */

public class QDContact {

    public final static int COUNT = 11;
    public final static int TYPE_SELF_DEPT = 0; //我的部门
    public final static int TYPE_GROUP = 1; //群组
    public final static int TYPE_CONTACT = 2; //通讯录
    public final static int TYPE_NEW_FRIEND = 3; //新的好友
    public final static int TYPE_USER = 4; //用户
    public final static int TYPE_SPACE = 5; //间距
    public final static int TYPE_TITLE = 6; //头部
    public final static int TYPE_FRIEND = 7; //好友
    public final static int TYPE_COMPANY = 8; //公司
    public final static int TYPE_DEPT = 9; //部门
    public final static int TYPE_LINKMAN = 10;

    private int type;
    private String id;
    private String icon;
    private String name;
    private String subname;
    private String funcName;
    private int index;
    private String code;
    private String cid;
    private String nameSp;
    private String nameAp;
    private int level;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getNameSp() {
        return nameSp;
    }

    public void setNameSp(String nameSp) {
        this.nameSp = nameSp;
    }

    public String getNameAp() {
        return nameAp;
    }

    public void setNameAp(String nameAp) {
        this.nameAp = nameAp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
