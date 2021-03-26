package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-8-28.
 * 提案管理主页面菜单列表MotionIndexServlet
 * 社情民意主界面
 * 文史资料主界面
 */
public class ProposalManagementListBean extends BaseGlobal {
    private ArrayList<ProposalManagementList> objList;

    public ArrayList<ProposalManagementList> getObjList() {
        return objList;
    }

    public class ProposalManagementList {
        private String strName;//分类名称
        private ArrayList<ProposalManagement> motionNav_list;
        private ArrayList<ProposalManagement> infoNav_list; // 社情民意/文史资料功能页

        public ArrayList<ProposalManagement> getInfoNav_list() {
            return infoNav_list;
        }

        public String getStrName() {
            return strName;
        }

        public ArrayList<ProposalManagement> getMotionNav_list() {
            return motionNav_list;
        }
    }

    public class ProposalManagement {
        private String strName;//名称
        private int intState;//办理状态（必填）
        private int intNumber;//待办数量
        private String strIcon;//图标
        //社情民意唯一标识 2待审信息 4已采编信息 5未签收 6办理中 7延期审核 8已申退 12已办结
        //文史资料唯一标识: -1待审信息 -2已采编信息
        private String strOnlyType;
        private int postion;
        private String strBgSign;//背景标记

        public String getStrBgSign() {
            return strBgSign;
        }

        public String getStrOnlyType() {
            return strOnlyType;
        }

        public int getPostion() {
            return postion;
        }

        public void setPostion(int postion) {
            this.postion = postion;
        }

        public String getStrIcon() {
            return strIcon;
        }

        public String getStrName() {
            return strName;
        }

        public int getIntState() {
            return intState;
        }

        public int getIntNumber() {
            return intNumber;
        }
    }
}
