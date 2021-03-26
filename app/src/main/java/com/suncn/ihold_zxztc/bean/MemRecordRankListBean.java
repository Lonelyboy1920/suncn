package com.suncn.ihold_zxztc.bean;

import com.suncn.ihold_zxztc.bean.MemRecordScoreBean.ScoreRankListBean.RankListBean;

import java.util.List;

public class MemRecordRankListBean extends BaseGlobal {

    private List<RankListBean> objList;

    public List<RankListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<RankListBean> objList) {
        this.objList = objList;
    }
}
