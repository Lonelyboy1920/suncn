package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 单位
 */
public class MyUnit implements Serializable {
    private String strUnit_id; //部门ID
    private String strUnit_name; //部门名称
    private String strParentId;
    private ArrayList<BaseUser> objUsers_list;
    private ArrayList<MyUnit> objUnits_list; // 组织机构记录集合
    private boolean checked;
    private int position;

    public String getStrParentId() {
        return strParentId;
    }

    public void setStrParentId(String strParentId) {
        this.strParentId = strParentId;
    }

    public String getStrUnit_id() {
        return strUnit_id;
    }

    public void setStrUnit_id(String strUnit_id) {
        this.strUnit_id = strUnit_id;
    }

    public String getStrUnit_name() {
        return strUnit_name;
    }

    public void setStrUnit_name(String strUnit_name) {
        this.strUnit_name = strUnit_name;
    }

    public ArrayList<BaseUser> getObjUsers_list() {
        return objUsers_list;
    }

    public void setObjUsers_list(ArrayList<BaseUser> objUsers_list) {
        this.objUsers_list = objUsers_list;
    }

    public ArrayList<MyUnit> getObjUnits_list() {
        return objUnits_list;
    }

    public void setObjUnits_list(ArrayList<MyUnit> objUnits_list) {
        this.objUnits_list = objUnits_list;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
