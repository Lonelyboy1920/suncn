package com.suncn.ihold_zxztc.bean.chat;

import com.suncn.ihold_zxztc.bean.BaseGlobal;

import java.util.List;

/**
 * 群组信息
 */
public class ChatRoomInfoData extends BaseGlobal {
    private List<RoomMemberBean> objData;
    private String naturalName; // 聊天室名称
    private String description; // 群公告
    private int loginIntIsAdmin; // 是否是管理员

    public int getLoginIntIsAdmin() {
        return loginIntIsAdmin;
    }

    public List<RoomMemberBean> getObjData() {
        return objData;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public String getDescription() {
        return description;
    }

    public static class RoomMemberBean {
        private String jid;
        private String nickname;
        private String strPathUrl;
        private int intIsAdmin;


        public RoomMemberBean(String jid, String nickname, String url) {
            this.jid = jid;
            this.nickname = nickname;
            this.strPathUrl = url;
        }

        public String getJid() {
            return jid;
        }

        public String getNickname() {
            return nickname;
        }

        public String getStrPathUrl() {
            return strPathUrl;
        }

        public int getIntIsAdmin() {
            return intIsAdmin;
        }
    }
}
