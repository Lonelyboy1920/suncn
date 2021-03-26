package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class TabBean {
    private String name;
    private String type;
    private String value;
    private String strType;
    private String strName;
    private int intCount;
    public int getIntCount() {
        return intCount;
    }

    public String getStrType() {
        return strType;
    }

    public String getStrName() {
        return strName;
    }

    private ArrayList<TabBean>objList;

    public ArrayList<TabBean> getObjList() {
        return objList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
