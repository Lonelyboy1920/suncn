package com.suncn.ihold_zxztc.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;

/**
 * 回复
 */
public class ReplyChildBean extends ReplyBean implements MultiItemEntity {

    @Override
    public int getItemType() {
        return 1;
    }

}
