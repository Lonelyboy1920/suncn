package com.suncn.ihold_zxztc.bean;

/**
 * 个人信息UserServlet
 * Created by daiyy on 2017/6/9.
 */

public class UserBean extends BaseGlobal {
    private String strName; // 姓名
    private int intUserRole; // 用户类型（0-委员；1-工作人员；2-承办单位）
    private String strPubUnitId; // 承办单位/人id
    private String strMobile; // 用户手机号码
    private String strPathUrl; // 头像地址
    private String strDuty; // 职务
    private String strSector; // 界别
    private String strLeader; // 分管领导
    private String strLeaderMobile; // 分管领导手机
    private String strDepartment; // 部门负责人
    private String strDepartmentMobile; // 部门负责人手机
    private String strFaction; // 党派
    private String strLinkAdd; // 联系地址
    private String strEmail; // 邮箱
    private String strSessionCode; // 第**界政协委员
    private String strUnitName;//所属单位
    private String strOpenMobile; // 手机号是否显示（0显示 1不显示）
    private int intLoginUserType; // 0 委员 1机关委用户 2上报单位用户 0显示界别、党派、届次号 1显示单位的名称 2显示领导名称、领导电话部门领导名称、部门领导电话 其他字段不做控制
    private String strOfficeMobile; // 办公电话
    private String strQDUserId;//启达用户的id
    private String strUserId;

    public String getStrUserId() {
        return strUserId;
    }

    private String strOpeningBank; // 开户行
    private String strCardNumber; // 卡号
    private String strCarCompensate; // 是否公务员 0：是；1-否
    private String strFax;
    private String strPostalCode;
    private String strIsPosted;
    private String strOPhone;
    private String strManagerOneName;
    private String strManagerOnePhone;
    private String strManagerTwoName;
    private String strManagerTwoPhone;
    private String strUnitType;

    public String getStrUnitType() {
        return strUnitType;
    }

    public String getStrManagerOneName() {
        return strManagerOneName;
    }

    public String getStrManagerOnePhone() {
        return strManagerOnePhone;
    }

    public String getStrManagerTwoName() {
        return strManagerTwoName;
    }

    public String getStrManagerTwoPhone() {
        return strManagerTwoPhone;
    }

    public String getStrOPhone() {
        return strOPhone;
    }

    public String getStrFax() {
        return strFax;
    }

    public String getStrIsPosted() {
        return strIsPosted;
    }

    public String getStrPostalCode() {
        return strPostalCode;
    }

    public String getStrQDUserId() {
        return strQDUserId;
    }

    public String getStrOfficeMobile() {
        return strOfficeMobile;
    }

    public String getStrOpeningBank() {
        return strOpeningBank;
    }

    public String getStrCardNumber() {
        return strCardNumber;
    }

    public String getStrCarCompensate() {
        return strCarCompensate;
    }

    public int getIntLoginUserType() {
        return intLoginUserType;
    }

    public String getStrOpenMobile() {
        return strOpenMobile;
    }

    public String getStrUnitName() {
        return strUnitName;
    }

    public String getStrSessionCode() {
        return strSessionCode;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public int getIntUserRole() {
        return intUserRole;
    }

    public void setIntUserRole(int intUserRole) {
        this.intUserRole = intUserRole;
    }

    public String getStrPubUnitId() {
        return strPubUnitId;
    }

    public void setStrPubUnitId(String strPubUnitId) {
        this.strPubUnitId = strPubUnitId;
    }

    public String getStrMobile() {
        return strMobile;
    }

    public void setStrMobile(String strMobile) {
        this.strMobile = strMobile;
    }

    public String getStrPathUrl() {
        return strPathUrl;
    }

    public void setStrPathUrl(String strPathUrl) {
        this.strPathUrl = strPathUrl;
    }

    public String getStrDuty() {
        return strDuty;
    }

    public void setStrDuty(String strDuty) {
        this.strDuty = strDuty;
    }

    public String getStrSector() {
        return strSector;
    }

    public void setStrSector(String strSector) {
        this.strSector = strSector;
    }

    public String getStrLeader() {
        return strLeader;
    }

    public void setStrLeader(String strLeader) {
        this.strLeader = strLeader;
    }

    public String getStrLeaderMobile() {
        return strLeaderMobile;
    }

    public void setStrLeaderMobile(String strLeaderMobile) {
        this.strLeaderMobile = strLeaderMobile;
    }

    public String getStrDepartment() {
        return strDepartment;
    }

    public void setStrDepartment(String strDepartment) {
        this.strDepartment = strDepartment;
    }

    public String getStrDepartmentMobile() {
        return strDepartmentMobile;
    }

    public void setStrDepartmentMobile(String strDepartmentMobile) {
        this.strDepartmentMobile = strDepartmentMobile;
    }

    public String getStrFaction() {
        return strFaction;
    }

    public void setStrFaction(String strFaction) {
        this.strFaction = strFaction;
    }

    public String getStrLinkAdd() {
        return strLinkAdd;
    }

    public void setStrLinkAdd(String strLinkAdd) {
        this.strLinkAdd = strLinkAdd;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }
}
