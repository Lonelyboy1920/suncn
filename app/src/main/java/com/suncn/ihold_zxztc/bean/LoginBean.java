package com.suncn.ihold_zxztc.bean;

import com.gavin.giframe.utils.GIStringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 登录(AuthServlet)
 */
public class LoginBean extends BaseGlobal {
    private OpenfireConfigBean objChatData;
    private OpenfireConfigBean objQDData;
    private AppConfig cppccAppConfig;
    private AnData objAn;//安安提醒数据
    private AppInitData appInit;//app个性化设置
    private ArrayList<MulUnitBean.MulUnit> objList;
    private objHst objHSTData;//好视通数据
    private String strLoginUserType;//11为领导用户
    private String strName; // 真实姓名
    private String strMsg; // 提示语
    private int intAdmin;//是否是管理员 1是
    private int intUserRole = 0; // 用户类型（ 0-委员 1-工作人员 2-承办单位、3-承办系统）
    private String strPubUnitId; // 承办单位/人id
    private int intGroup;//是否是团体用户1是0否
    private String strDuty; // 委员，工作人员，承办单位
    private int intIsCBXT; // 是否是承办系统（只有用户类型为2时显示，返回值1代表承办系统0代表承办单位）
    private String strUnitId; // 用户所属部门
    private int intIdentity; // 该用户有几种身份（1.一种2两种）
    private String strMobile; // 手机号
    private String strWebUrl; // 门户信息的服务地址（用于首页门户数据从另外一个服务中获取的时候）
    private int intMeeting; // 会议通知条数
    private int intActivity; // 活动通知条数
    private int intNotice; // 提案征集条数
    private String strSector; // 界别
    private String strFaction; // 党派
    private String strPathUrl; // 头像URL
    private String strUserName; // 游客姓名
    private String strUserId;
    private String strShowMemExamNav;//显示履职下面的导航 0、显示 1不显示
    private String isExistUserByCppccSession;//判断委员是否有提案提交的操作   true 显示  false 不显示
    private int intContactRole;//3代表主席，4代表常委5代表委员
    private String strProjectName; // 友盟推送的别名前缀
    private String strLoginUserId; // 用户名
    private String strShowRootUnit;//是否是多角色
    private String strDefalutRootUnitName;//当前角色名称
    private String strDefalutRootUnitId;//当前角色id
    private String strMotionTitle;// 提案标题动态配置（题目/案由）
    private String strRootIp; // 多级联动时，配置不同地区的服务器地址
    private String strRootPort; // 多级联动时，配置不同地区的服务器端口
    private String strDefalutUserId;
    private String strNewsDelStateByAdmin;//新闻的修改权限
    private String strCircleDelStateByAdmin;//圈子的修改权限
    private String strShowLzIconState;//是否显示履职图标
    private String strIsHaveEvent;//是否弹出运营活动弹窗 1有0无
    private String strLoginHaiBiAccessToken;//委员学习的token
    private String strIsCollective;//是否是集体用户
    private String strTitle;  //提案标题动态配置（题目/案由）
    private String hasHeadConfig;
    private String strTitleUrl;//头部标题
    private String strBackUrl;//头部背景
    private String strAppStartFile; // 应用闪屏地址

    public objHst getObjHSTData() {
        return objHSTData;
    }

    public String getHasHeadConfig() {
        return hasHeadConfig;
    }

    public void setStrAppStartFile(String strAppStartFile) {
        this.strAppStartFile = strAppStartFile;
    }

    public String getStrAppStartFile() {
        return strAppStartFile;
    }
    public String getStrTitle() {
        return strTitle;
    }

    public String getStrTitleUrl() {
        return strTitleUrl;
    }

    public String getStrBackUrl() {
        return strBackUrl;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getStrIsCollective() {
        return strIsCollective;
    }

    public class OpenfireConfigBean implements Serializable {
        private String strQDUserName;
        private String strQDUserPassword;
        private String strQDMAINSERVER;
        private String strQDMAINSERVERPORT;

        public String getStrQDUserPassword() {
            return strQDUserPassword;
        }

        public String getStrQDMAINSERVER() {
            return strQDMAINSERVER;
        }

        public String getStrQDMAINSERVERPORT() {
            if (GIStringUtil.isBlank(strQDMAINSERVERPORT)) {
                return "0";
            }
            return strQDMAINSERVERPORT;
        }

        public String getStrQDUserName() {
            return strQDUserName;
        }
    }

    public class AppConfig implements Serializable {
        private String strContentType;//提交提案显示几段式:当为1时,显示情况分析(名称为动态,指向strContentTitle1),当为2时,显示情况分析、具体建议(名称为动态,执行strContentTitle2)
        private String strContentTitle1;//情况分析-名称动态
        private String strContentTitle2;//具体建议-名称动态
        private String strFlowType;//提案的审核流程（0的时候，提案的详情里的审核意见和委员反馈的操作是不可见的--特殊状态）
        private String strUseCaseNo;//是否启用案号 0显示1不显示
        private String strUseCbdw;//是否启用承办单位 0显示1不显示(提案的详情时对承办单位的信息进行显示或隐藏)
        private String strUseCbxt;//是否启用承办系统 0显示1不显示(审核时、待交办时有交办信息的根据分发类型显示是否有承办系统的)
        private String strShowDygc;//是否显示调研过程 0显示1不显示
        private String strShowXsfs;//是否显示协商方式 0显示1不显示
        private String strShowTjcbdw;//是否显示建议承办单位 0显示1不显示
        private String strFlowShowCbdwPre;//是否显示预办理
        private String strFlowCaseDistType;//0-详情不显示办理情况，其他显示
        private String strSubmitFlow;//0提交至待接收，1提交至待审核 （在机关委审核时用到了）
        private String strUseInfoDist;//0-显示社情民意交办，1-不显示
        private String strUseQDVideo;//是否启动启达的视频会议
        private String strUseQDIM;//是否开启启达IM
        private String strUseMobileBook;//判断是否显示通讯录   0显示通讯录  1显示沟通
        private String strUseMemberExam;//0-显示雷达图 履职字段优化 为履职等级:优秀，1-隐藏 履职得分:50
        private String strUseYouMeng;//是否使用有盟0-用，1-不用
        private String strUseSaoMQianD;//是否使用二维码签到功能0-用，1-不用
        private String strUseCbdwBack;//是否开启承办单位申退0-可申退，1-不可申退
        private String strUseSupportMotion;//是否启用附议选项 0显示1不显示
        private String strUseAllyMotion;//是否启用联名选项 0显示1不显示
        private String strShowDutyCard;//是否显示电子履职卡
        private String strShowAnAn;//是否显示安安
        private String strUseHST;

        public String getStrUseHST() {
            return strUseHST;
        }

        public String getStrShowAnAn() {
            return strShowAnAn;
        }

        public String getStrShowDutyCard() {
            return strShowDutyCard;
        }

        public String getStrUseQDIM() {
            return strUseQDIM;
        }

        public String getStrUseQDVideo() {
            return strUseQDVideo;
        }

        public String getStrUseSupportMotion() {
            return strUseSupportMotion;
        }

        public String getStrUseAllyMotion() {
            return strUseAllyMotion;
        }

        public String getStrUseCbdwBack() {
            return strUseCbdwBack;
        }

        public String getStrUseYouMeng() {
            return strUseYouMeng;
        }

        public String getStrUseSaoMQianD() {
            return strUseSaoMQianD;
        }

        public String getStrUseMemberExam() {
            return strUseMemberExam;
        }

        public String getStrUseMobileBook() {
            return strUseMobileBook;
        }

        public String getStrUseInfoDist() {
            return strUseInfoDist;
        }

        public String getStrSubmitFlow() {
            return strSubmitFlow;
        }

        public String getStrFlowCaseDistType() {
            return strFlowCaseDistType;
        }

        public String getStrFlowShowCbdwPre() {
            return strFlowShowCbdwPre;
        }

        public String getStrContentType() {
            return strContentType;
        }

        public String getStrContentTitle1() {
            return strContentTitle1;
        }

        public String getStrContentTitle2() {
            return strContentTitle2;
        }

        public String getStrFlowType() {
            return strFlowType;
        }

        public String getStrUseCaseNo() {
            return strUseCaseNo;
        }

        public String getStrUseCbdw() {
            return strUseCbdw;
        }

        public String getStrUseCbxt() {
            return strUseCbxt;
        }

        public String getStrShowDygc() {
            return strShowDygc;
        }

        public String getStrShowXsfs() {
            return strShowXsfs;
        }

        public String getStrShowTjcbdw() {
            return strShowTjcbdw;
        }
    }

    public class AppInitData implements Serializable {
        private String strAppHomeTop; // 热点主界面头部背景图片地址
        private String strAppName; // 热点主界面头部项目名称图片地址
        private String strAppHomeCentreFile; // 热点主界面专题图片点击后跳转的页面地址（运营策划）
        private String strAppHomeCentreFileUrl; // 热点主界面专题图片地址（运营策划）


        public String getStrAppHomeCentreFile() {
            return strAppHomeCentreFile;
        }

        public String getStrAppHomeCentreFileUrl() {
            return strAppHomeCentreFileUrl;
        }

        public String getStrAppHomeTop() {
            return strAppHomeTop;
        }

        public String getStrAppName() {
            return strAppName;
        }


        public void setStrAppHomeTop(String strAppHomeTop) {
            this.strAppHomeTop = strAppHomeTop;
        }

        public void setStrAppName(String strAppName) {
            this.strAppName = strAppName;
        }

        public void setStrAppHomeCentreFile(String strAppHomeCentreFile) {
            this.strAppHomeCentreFile = strAppHomeCentreFile;
        }

        public void setStrAppHomeCentreFileUrl(String strAppHomeCentreFileUrl) {
            this.strAppHomeCentreFileUrl = strAppHomeCentreFileUrl;
        }

    }

    public class AnData implements Serializable {
        private String strTitle;
        private String strIds;

        public String getStrIds() {
            return strIds;
        }

        public String getStrTitle() {
            return strTitle;
        }
    }

    public void setAppInit(AppInitData appInit) {
        this.appInit = appInit;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }

    public void setStrUserId(String strUserId) {
        this.strUserId = strUserId;
    }

    public void setStrPathUrl(String strPathUrl) {
        this.strPathUrl = strPathUrl;
    }

    public String getStrLoginHaiBiAccessToken() {
        return strLoginHaiBiAccessToken;
    }

    public String getStrIsHaveEvent() {
        return strIsHaveEvent;
    }

    public AppInitData getAppInit() {
        return appInit;
    }

    public String getStrShowLzIconState() {
        return strShowLzIconState;
    }

    public String getStrCircleDelStateByAdmin() {
        return strCircleDelStateByAdmin;
    }

    public String getStrNewsDelStateByAdmin() {
        return strNewsDelStateByAdmin;
    }

    public String getStrDefalutUserId() {
        return strDefalutUserId;
    }

    public String getStrRootIp() {
        return strRootIp;
    }

    public String getStrRootPort() {
        return strRootPort;
    }

    public String getStrMotionTitle() {
        return strMotionTitle;
    }

    public String getStrDefalutRootUnitId() {
        return strDefalutRootUnitId;
    }

    public String getStrDefalutRootUnitName() {
        return strDefalutRootUnitName;
    }

    public String getStrShowRootUnit() {
        return strShowRootUnit;
    }

    public ArrayList<MulUnitBean.MulUnit> getObjList() {
        return objList;
    }

    public int getIntGroup() {
        return intGroup;
    }

    public int getIntAdmin() {
        return intAdmin;
    }

    public String getStrProjectName() {
        return strProjectName;
    }

    public String getStrLoginUserId() {
        return strLoginUserId;
    }

    public OpenfireConfigBean getObjQDData() {
        return objQDData;
    }

    public OpenfireConfigBean getObjChatData() {
        return objChatData;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public String getStrLoginUserType() {
        return strLoginUserType;
    }

    public int getIntContactRole() {
        return intContactRole;
    }

    public String getIsExistUserByCppccSession() {
        return isExistUserByCppccSession;
    }

    public String getStrFaction() {
        return strFaction;
    }

    public String getStrShowMemExamNav() {
        return strShowMemExamNav;
    }

    public String getStrUserId() {
        return strUserId;
    }

    public String getStrPhotoUrl() {
        return strPathUrl;
    }

    public String getStrSector() {
        return strSector;
    }

    public int getIntMeeting() {
        return intMeeting;
    }

    public int getIntActivity() {
        return intActivity;
    }

    public int getIntNotice() {
        return intNotice;
    }

    public String getStrWebUrl() {
        return strWebUrl;
    }

    public String getStrMobile() {
        return strMobile;
    }

    public int getIntIdentity() {
        return intIdentity;
    }

    public String getStrMsg() {
        return strMsg;
    }

    public String getStrUnitId() {
        return strUnitId;
    }

    public String getStrName() {
        return strName;
    }

    public int getIntUserRole() {
        return intUserRole;
    }

    public String getStrPubUnitId() {
        return strPubUnitId;
    }

    public String getStrDuty() {
        return strDuty;
    }

    public int getIntIsCBXT() {
        return intIsCBXT;
    }

    public AppConfig getCppccAppConfig() {
        return cppccAppConfig;
    }

    public AnData getObjAn() {
        return objAn;
    }
    public class objHst implements Serializable{
        private String strHSTAppId;
        private String strHSTAppSecret;

        public String getStrHSTAppId() {
            return strHSTAppId;
        }

        public String getStrHSTAppSecret() {
            return strHSTAppSecret;
        }

        public String getStrHSTAppServerUrl() {
            return strHSTAppServerUrl;
        }

        private String strHSTAppServerUrl;

    }
}


