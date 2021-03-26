package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * Created by whh on 2018-8-2.
 *  公共数据字典数据根据类型区分
 */

public class ParamByTypeBean extends BaseGlobal {
    private List<Param> objList;

    public List<Param> getObjList() {
        return objList;
    }
    public class Param{
        private String strName; // 名称
        private String strCode; // 编码
        private String strPubType;
        private String strPubTypeName;

        public String getStrPubType() {
            return strPubType;
        }

        public String getStrPubTypeName() {
            return strPubTypeName;
        }

        public String getStrName() {
            return strName;
        }

        public String getStrCode() {
            return strCode;
        }
    }
}
