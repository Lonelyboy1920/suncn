package com.suncn.ihold_zxztc.bean.chat;

import com.suncn.ihold_zxztc.bean.BaseGlobal;

import java.util.List;

/**
 * 群组信息
 */
public class ContactSearchListBean extends BaseGlobal {
    private List<ContactSearchBean> objData;
    private List<ContactSearchBean> objList;

    public List<ContactSearchBean> getObjList() {
        return objList;
    }

    public List<ContactSearchBean> getObjData() {
        return objData;
    }

    public static class ContactSearchBean {
        //群组
        private String roomID;
        private String naturalName;
        private String strLinkId;
        private String creationDate;
        private String subject;
        //联系人
        private String strUserId;
        private String strPathUrl;
        private String strSector;
        private String strName;
        private String strOpenState;//0代表未开通登陆 1代表已开通登陆

        public String getRoomID() {
            return roomID;
        }

        public String getNaturalName() {
            return naturalName;
        }

        public String getStrLinkId() {
            return strLinkId;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public String getSubject() {
            return subject;
        }

        public String getStrUserId() {
            return strUserId;
        }

        public String getStrPathUrl() {
            return strPathUrl;
        }

        public String getStrSector() {
            return strSector;
        }

        public String getStrName() {
            return strName;
        }

        public String getStrOpenState() {
            return strOpenState;
        }
    }
}
