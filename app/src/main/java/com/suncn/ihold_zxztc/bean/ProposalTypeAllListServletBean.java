package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * @author :Sea
 * Date :2020-6-15 11:11
 * PackageName:com.suncn.ihold_zxztc.bean
 * Desc:
 */
public class ProposalTypeAllListServletBean extends BaseGlobal {
    private List<ObjListBean> objList;

    public List<ObjListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjListBean> objList) {
        this.objList = objList;
    }

    public class ObjListBean {
        private String strProposalTypeId;
        private String strProposalTypeName;
        private List<ObjChlidList> objChildList;

        public String getStrProposalTypeId() {
            return strProposalTypeId;
        }

        public void setStrProposalTypeId(String strProposalTypeId) {
            this.strProposalTypeId = strProposalTypeId;
        }

        public String getStrProposalTypeName() {
            return strProposalTypeName;
        }

        public void setStrProposalTypeName(String strProposalTypeName) {
            this.strProposalTypeName = strProposalTypeName;
        }

        public List<ObjChlidList> getObjChildList() {
            return objChildList;
        }

        public void setObjChildList(List<ObjChlidList> objChildList) {
            this.objChildList = objChildList;
        }

        public class ObjChlidList {
            private String strProposalTypeId;
            private String strProposalTypeName;
            private List<ObjChlidListBean> objChildList;

            public List<ObjChlidListBean> getObjChildList() {
                return objChildList;
            }

            public void setObjChildList(List<ObjChlidListBean> objChildList) {
                this.objChildList = objChildList;
            }

            public String getStrProposalTypeId() {
                return strProposalTypeId;
            }

            public void setStrProposalTypeId(String strProposalTypeId) {
                this.strProposalTypeId = strProposalTypeId;
            }

            public String getStrProposalTypeName() {
                return strProposalTypeName;
            }

            public void setStrProposalTypeName(String strProposalTypeName) {
                this.strProposalTypeName = strProposalTypeName;
            }

            public class ObjChlidListBean {
                private String strProposalTypeId;
                private String strProposalTypeName;

                public String getStrProposalTypeId() {
                    return strProposalTypeId;
                }

                public void setStrProposalTypeId(String strProposalTypeId) {
                    this.strProposalTypeId = strProposalTypeId;
                }

                public String getStrProposalTypeName() {
                    return strProposalTypeName;
                }

                public void setStrProposalTypeName(String strProposalTypeName) {
                    this.strProposalTypeName = strProposalTypeName;
                }
            }
        }
    }
}
