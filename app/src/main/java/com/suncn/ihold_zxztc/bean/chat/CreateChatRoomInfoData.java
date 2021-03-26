package com.suncn.ihold_zxztc.bean.chat;

import com.suncn.ihold_zxztc.bean.BaseGlobal;

/**
 * 群组创建
 */
public class CreateChatRoomInfoData extends BaseGlobal {
    private String strMsgContent; // 创建成功的消息
    private String strLinkId; // 群聊id
    private String naturalName; // 群名称

    public String getStrMsgContent() {
        return strMsgContent;
    }

    public String getStrLinkId() {
        return strLinkId;
    }

    public String getNaturalName() {
        return naturalName;
    }
}
