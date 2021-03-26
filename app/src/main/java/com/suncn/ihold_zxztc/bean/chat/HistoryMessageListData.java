package com.suncn.ihold_zxztc.bean.chat;


import com.suncn.ihold_zxztc.bean.BaseGlobal;

import java.util.List;

/**
 * 历史消息列表数据
 */
public class HistoryMessageListData extends BaseGlobal {
    private List<HistoryMessageBean> objData;

    public List<HistoryMessageBean> getObjData() {
        return objData;
    }
}
