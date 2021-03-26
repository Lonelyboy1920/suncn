package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by whh on 2018-6-20.
 * 视频会议列表
 */

public class VideoMtListBean extends BaseGlobal {
    private ArrayList<VideoMt> objList;

    public ArrayList<VideoMt> getObjList() {
        return objList;
    }

    public static class VideoMt implements Serializable {
        private long longVideoMeetId;//id
        private String strVideoMeetName;//视频会议名称
        private String strVideoMeetStartTime;//视频会议开始时间
        private String strVideoMeetEndTime;//视频会议结束时间
        private String strVideoMeetPassword;//入会密码
        private String strVMCompereUserName;//会议主持人
        private String strVMComperePassword;//会议主持人入会密码
        private int intState;//视频会议状态1-未开始，2-可入会，3-已结束
        private String strVideoMeetType;//1-星澜，2-随锐

        public String getStrVideoMeetType() {
            return strVideoMeetType;
        }

        public VideoMt() {

        }

        public long getLongVideoMeetId() {
            return longVideoMeetId;
        }

        public String getStrVideoMeetName() {
            return strVideoMeetName;
        }

        public String getStrVideoMeetStartTime() {
            return strVideoMeetStartTime;
        }

        public String getStrVideoMeetEndTime() {
            return strVideoMeetEndTime;
        }

        public String getStrVideoMeetPassword() {
            return strVideoMeetPassword;
        }

        public String getStrVMCompereUserName() {
            return strVMCompereUserName;
        }

        public String getStrVMComperePassword() {
            return strVMComperePassword;
        }

        public int getIntState() {
            return intState;
        }
    }
}
