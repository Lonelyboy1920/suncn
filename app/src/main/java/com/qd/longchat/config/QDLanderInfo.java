package com.qd.longchat.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.longchat.base.model.QDLoginInfo;
import com.longchat.base.util.QDStringUtil;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/14 下午3:39
 * 登陆用户信息保存到sp中
 */

public class QDLanderInfo {

    private final static String SP_NAME = "com.qd.longchat";
    private final static String DOMAIN = "domain";
    private final static String ADDRESS = "address";
    private final static String PORT = "port";
    private final static String ACCOUNT = "account";
    private final static String PASSWORD = "password";
    private final static String SSID = "ssid";
    private final static String WEBAPISERVER = "webApiServer";
    private final static String WEBFILESERVER = "webFileServer";
    private final static String FILESERVER = "fileServer";
    private final static String CLIENTFLAG = "clientFlag";
    private final static String PIC = "pic";
    private final static String ADDINCUSTOMURL = "addinCustomUrl";
    private final static String ENABLEADDINCUSTOM = "enableAddinCustom";
    private final static String ISLOGINFORMOBILE = "isLoginForMobile";
    private final static String MEDIASERVER = "mediaServer";
    private final static String VERIFYTOKEN = "verifyToken";
    private final static String TOKEN = "token";
    private final static String NAME = "name";
    private final static String ID = "id";
    private final static String ISLOGIN = "isLogin";
    private final static String SELFDEPTINFO = "selfDeptInfo";
    private final static String ATTACHSIZELIMIT = "attachSizeLimit";
    private final static String BATCHPERSONLIMIT = "batchPersonLimit";
    private final static String MSGACE = "msgAce";
    private final static String PROFILEEDIT = "profileEdit";
    private final static String PROFILEVIEW = "profileView";
    private final static String LEVEL = "level";
    private final static String LOGINTOKEN = "loginToken";

    private Context context;
    /**
     * 域名
     */
    private String domain;
    /**
     * 服务器地址
     */
    private String address;
    /**
     * 服务器端口
     */
    private int port;
    /**
     * 登陆名
     */
    private String account;
    /**
     * 用户id
     */
    private String id;
    /**
     * 登陆用户名
     */
    private String name;
    /**
     * 登陆密码
     */
    private String password;
    /**
     * ssid
     */
    private String ssid;
    /**
     * web接口服务地址
     */
    private String webApiServer;
    /**
     * web文件服务地址
     */
    private String webFileServer;
    /**
     * 文件服务地址
     */
    private String fileServer;
    /**
     * 音视频服务地址
     */
    private String mediaServer;
    /**
     * （重庆项目需求）
     */
    private String verifyToken;
    /**
     * 登陆token
     */
    private String token;
    /**
     * 系统权限
     */
    private int clientFlag;
    /**
     * 登陆头像
     */
    private String pic;
    /**
     * 应用首页第三方接入地址
     */
    private String addinCustomUrl;
    /**
     * 是否应用首页是第三方url地址
     */
    private String enableAddinCustom;
    /**
     * 是否是手机登陆
     */
    private boolean isLoginForMobile;
    /**
     * 是否登录
     */
    private boolean isLogin;
    /**
     * 我的部门信息
     */
    private String selfDeptInfo;
    /**
     * 附件大小限制
     */
    private int attachSizeLimit;
    /**
     * 群发人数限制
     */
    private int batchPersonLimit;
    /**
     * 消息权限
     */
    private int msgAce;
    /**
     * 信息修改权限
     */
    private int profileEdit;
    /**
     * 信息查看权限
     */
    private int profileView;
    /**
     * 等级
     */
    private int level;

    private String loginToken;

    private static QDLanderInfo instance;

    public static QDLanderInfo getInstance() {
        if (instance == null) {
            instance = new QDLanderInfo();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        initData();
    }

    private void initData() {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        this.id = sp.getString(ID, "");
        this.domain = sp.getString(DOMAIN, "");
        this.address = sp.getString(ADDRESS, "");
        this.port = sp.getInt(PORT, 0);
        this.account = sp.getString(ACCOUNT, "");
        this.password = sp.getString(PASSWORD, "");
        this.ssid = sp.getString(SSID, "");
        this.webApiServer = sp.getString(WEBAPISERVER, "");
        this.webFileServer = sp.getString(WEBFILESERVER, "");
        this.fileServer = sp.getString(FILESERVER, "");
        this.clientFlag = sp.getInt(CLIENTFLAG, 0);
        this.pic = sp.getString(PIC, "");
        this.addinCustomUrl = sp.getString(ADDINCUSTOMURL, "");
        this.enableAddinCustom = sp.getString(ENABLEADDINCUSTOM, "");
        this.isLoginForMobile = sp.getBoolean(ISLOGINFORMOBILE, false);
        this.mediaServer = sp.getString(MEDIASERVER, "");
        this.verifyToken = sp.getString(VERIFYTOKEN, "");
        this.token = sp.getString(TOKEN, "");
        this.name = sp.getString(NAME, "");
        this.isLogin = sp.getBoolean(ISLOGIN, false);
        this.selfDeptInfo = sp.getString(SELFDEPTINFO, "");
        this.attachSizeLimit = sp.getInt(ATTACHSIZELIMIT, 0);
        this.batchPersonLimit = sp.getInt(BATCHPERSONLIMIT, 0);
        this.msgAce = sp.getInt(MSGACE, 0);
        this.profileEdit = sp.getInt(PROFILEEDIT, 0);
        this.profileView = sp.getInt(PROFILEVIEW, 0);
        this.level = sp.getInt(LEVEL, 99);
        this.loginToken = sp.getString(LOGINTOKEN, "");
    }

    public void updateSp(QDLoginInfo loginInfo) {
        this.account = loginInfo.getAccount();
        this.ssid = loginInfo.getSSID();
        this.pic = loginInfo.getConfig(QDLoginInfo.upic);
        this.clientFlag = QDStringUtil.strToInt(loginInfo.getConfig(QDLoginInfo.cFlag));
        this.name = loginInfo.getUserName();
        this.webFileServer = loginInfo.getWebFileServer();
        this.webApiServer = loginInfo.getWebApiServer();
        this.fileServer = loginInfo.getFileServer();
        this.mediaServer = loginInfo.getMediaServer();
        this.selfDeptInfo = loginInfo.getConfig(QDLoginInfo.owngroupinfo);
        this.id = loginInfo.getUserID();
        this.isLogin = true;
        this.attachSizeLimit = loginInfo.getAttachSizeLimit();
        this.batchPersonLimit = loginInfo.getBatchPersonLimit();
        this.msgAce = loginInfo.getMsgAce();
        this.profileView = loginInfo.getProfileView();
        this.profileEdit = loginInfo.getProfileEdit();
        this.level = QDStringUtil.strToLevel(loginInfo.getConfig(QDLoginInfo.useclevel));
        this.loginToken = loginInfo.getConfig("logintoken");
        save();
    }

    public void save() {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(DOMAIN, domain);
        editor.putString(ADDRESS, address);
        editor.putInt(PORT, port);
        editor.putString(ACCOUNT, account);
        editor.putString(PASSWORD, password);
        editor.putString(SSID, ssid);
        editor.putString(WEBAPISERVER, webApiServer);
        editor.putString(WEBFILESERVER, webFileServer);
        editor.putString(FILESERVER, fileServer);
        editor.putInt(CLIENTFLAG, clientFlag);
        editor.putString(PIC, pic);
        editor.putString(ADDINCUSTOMURL, addinCustomUrl);
        editor.putString(ENABLEADDINCUSTOM, enableAddinCustom);
        editor.putBoolean(ISLOGINFORMOBILE, isLoginForMobile);
        editor.putString(MEDIASERVER, mediaServer);
        editor.putString(VERIFYTOKEN, verifyToken);
        editor.putString(TOKEN, token);
        editor.putString(NAME, name);
        editor.putBoolean(ISLOGIN, isLogin);
        editor.putString(SELFDEPTINFO, selfDeptInfo);
        editor.putString(ID, id);
        editor.putInt(ATTACHSIZELIMIT, attachSizeLimit);
        editor.putInt(BATCHPERSONLIMIT, batchPersonLimit);
        editor.putInt(MSGACE, msgAce);
        editor.putInt(PROFILEVIEW, profileView);
        editor.putInt(PROFILEEDIT, profileEdit);
        editor.putInt(LEVEL, level);
        editor.putString(LOGINTOKEN, loginToken);
        editor.apply();
    }

    public void loginOut() {
        isLogin = false;
        save();
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return 5551;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    public int getClientFlag() {
        return clientFlag;
    }

    public void setClientFlag(int clientFlag) {
        this.clientFlag = clientFlag;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAddinCustomUrl() {
        return addinCustomUrl;
    }

    public void setAddinCustomUrl(String addinCustomUrl) {
        this.addinCustomUrl = addinCustomUrl;
    }

    public String getEnableAddinCustom() {
        return enableAddinCustom;
    }

    public void setEnableAddinCustom(String enableAddinCustom) {
        this.enableAddinCustom = enableAddinCustom;
    }

    public boolean isLoginForMobile() {
        return isLoginForMobile;
    }

    public void setLoginForMobile(boolean loginForMobile) {
        isLoginForMobile = loginForMobile;
    }

    public String getMediaServer() {
        return mediaServer;
    }

    public void setMediaServer(String mediaServer) {
        this.mediaServer = mediaServer;
    }

    public String getVerifyToken() {
        return verifyToken;
    }

    public void setVerifyToken(String verifyToken) {
        this.verifyToken = verifyToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getSelfDeptInfo() {
        return selfDeptInfo;
    }

    public void setSelfDeptInfo(String selfDeptInfo) {
        this.selfDeptInfo = selfDeptInfo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getAttachSizeLimit() {
        return attachSizeLimit;
    }

    public void setAttachSizeLimit(int attachSizeLimit) {
        this.attachSizeLimit = attachSizeLimit;
    }

    public int getBatchPersonLimit() {
        return batchPersonLimit;
    }

    public void setBatchPersonLimit(int batchPersonLimit) {
        this.batchPersonLimit = batchPersonLimit;
    }

    public int getMsgAce() {
        return msgAce;
    }

    public void setMsgAce(int msgAce) {
        this.msgAce = msgAce;
    }

    public int getProfileEdit() {
        return profileEdit;
    }

    public void setProfileEdit(int profileEdit) {
        this.profileEdit = profileEdit;
    }

    public int getProfileView() {
        return profileView;
    }

    public void setProfileView(int profileView) {
        this.profileView = profileView;
    }

    public String getWebApiServer() {
        return webApiServer;
    }

    public void setWebApiServer(String webApiServer) {
        this.webApiServer = webApiServer;
    }

    public String getWebFileServer() {
        return webFileServer;
    }

    public void setWebFileServer(String webFileServer) {
        this.webFileServer = webFileServer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
}
