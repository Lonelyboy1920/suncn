package com.suncn.ihold_zxztc.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class BaseTypeBean extends BaseGlobal implements MultiItemEntity {
    private int intItemType;
    private int intCount;
    private int intNotReadCount;

    public int getIntNotReadCount() {
        return intNotReadCount;
    }

    public int getIntCount() {
        return intCount;
    }

    public void setIntItemType(int intItemType) {
        this.intItemType = intItemType;
    }

    @Override
    public int getItemType() {
        return intItemType;
    }
}
