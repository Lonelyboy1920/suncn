package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-5-18.
 */

public class TakeImgTypeListBean extends BaseGlobal {
    private ArrayList<TakeImgType> objList;

    public ArrayList<TakeImgType> getObjList() {
        return objList;
    }

    public class TakeImgType{
        private String strName;
        private String strCode;

        public String getStrName() {
            return strName;
        }

        public String getStrCode() {
            return strCode;
        }
    }
}
