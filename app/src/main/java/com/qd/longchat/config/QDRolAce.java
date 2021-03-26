package com.qd.longchat.config;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/25 下午2:19
 */
public class QDRolAce {

    /**
     * 客户端禁止修改代码
     */
    public final static int ACE_CLIENT_BAN_MODIFY_PASSWORD = 1;
    /**
     * 客户端禁止群共享文件
     */
    public final static int ACE_CLIENT_BAN_GROUP_FILE = 8;
    /**
     * 客户端禁止语音
     */
    public final static int ACE_CLIENT_BAN_AUDIO = 16;
    /**
     * 客户端禁止视频
     */
    public final static int ACE_CLIENT_BAN_VIDEO = 32;
    /**
     * 客户端禁止按在线状态排序
     */
    public final static int ACE_CLIENT_BAN_SORT_ONLINE = 64;
    /**
     * 客户端开启水印
     */
    public final static int ACE_CLIENT_OPEN_WATERMARK = 128;
    /**
     * 启用高管模式
     *
     */
    public final static int ACE_CLIENT_SECRET = 512;
    /**
     * 启用群组销毁功能
     */
    public final static int ACE_CLIENT_OPEN_GROUP_DROP_MESSAGE = 8192;
    /**
     * 启用密聊功能
     */
    public final static int ACE_CLIENT_OPEN_SECRET_CHAT = 16384;
    /**
     * 启用敏感词过滤功能
     */
    public final static int ACE_CLIENT_OPEN_BAD_WORD = 32768;

    /**
     * 禁止查看手机号
     */
    public final static int ACE_PV_BAN_MOBILE = 1;
    /**
     * 禁止查看短号
     */
    public final static int ACE_PV_BAN_OPHONE = 2;
    /**
     * 禁止查看Email
     */
    public final static int ACE_PV_BAN_EMAIL = 4;
    /**
     * 禁止查看房间号
     */
    public final static int ACE_PV_BAN_ROOM_NO = 8;

    /**
     * 禁止发送文件
     */
    public final static int ACE_MA_BAN_SEND_FILE = 1;
    /**
     * 禁止语音通话
     */
    public final static int ACE_MA_BAN_AUDIO = 2;
    /**
     * 禁止视频通话
     */
    public final static int ACE_MA_BAN_VIDEO = 4;
    /**
     * 禁止移动端下载文件
     */
    public final static int ACE_MA_BAN_DOWNLOAD_FILE = 16;
    /**
     * 禁止移动端下载文件
     */
    public final static int ACE_MA_BAN_ADD_CUSTOMER = 32;

    /**
     * 禁止属性修改
     */
    public final static int ACE_PE_BAN_PROPERTY = 1;
    /**
     * 禁止职位修改
     */
    public final static int ACE_PE_BAN_JOB = 2;
    /**
     * 禁止签名修改
     */
    public final static int ACE_PE_BAN_NOTE = 4;
    /**
     * 禁止头像修改
     */
    public final static int ACE_PE_BAN_ICON = 8;
}
