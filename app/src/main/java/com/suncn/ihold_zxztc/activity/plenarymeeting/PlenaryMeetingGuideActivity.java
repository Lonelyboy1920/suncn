package com.suncn.ihold_zxztc.activity.plenarymeeting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gavin.giframe.ui.BindView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.PlenaryMeetingGuideAdapter;
import com.suncn.ihold_zxztc.bean.NewsColumnListBean;

import java.util.HashMap;
import java.util.List;

public class PlenaryMeetingGuideActivity extends BaseActivity {
    @BindView(id = R.id.listview)
    private ListView listView;
    private PlenaryMeetingGuideAdapter adapter;
    private List<NewsColumnListBean.NewsColumnBean> objList;

    @Override
    public void setRootView() {
        setStatusBar();
        isShowBackBtn = true;
        setContentView(R.layout.activity_plenary_meet_guide);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        setHeadTitle("全会指南");
        getListData();

    }

    /**
     * 获取全会模块
     */
    private void getListData() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getPlenaryMeeting(textParamMap), 0);
    }

    @Override
    public void setListener() {
        super.setListener();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsColumnListBean.NewsColumnBean info = objList.get(position);
                Bundle bundle = new Bundle();
                if (info.getStrCode().equals("02")) {//会议日程
                    showActivity(activity, HYRCListActivity.class);
                } else if (info.getStrCode().equals("03")) {//会议议程
                    bundle.putString("strTypeForQH", info.getStrCode());
                    bundle.putString("headTitle", info.getStrName());
                    bundle.putBoolean("isPersonInfo", true);
                    showActivity(activity, WebViewActivity.class, bundle);
                } else if (info.getStrCode().equals("04")) {//全会简报
                    bundle.putString("strTypeForQH", info.getStrCode());
                    bundle.putString("headTitle", info.getStrName());
                    bundle.putBoolean("isPersonInfo", true);
                    showActivity(activity, WebViewActivity.class, bundle);

                } else if (info.getStrCode().equals("05")) {//全会名单
                    bundle.putString("strTypeForQH", info.getStrCode());
                    bundle.putString("headTitle", info.getStrName());
                    bundle.putBoolean("isPersonInfo", true);
                    showActivity(activity, WebViewActivity.class, bundle);
                } else if (info.getStrCode().equals("06")) {//会议相关事项
                    bundle.putString("strTypeForQH", info.getStrCode());
                    bundle.putString("headTitle", info.getStrName());
                    bundle.putBoolean("isPersonInfo", true);
                    showActivity(activity, WebViewActivity.class, bundle);
                } else if (info.getStrCode().equals("07")) {//全会通讯录
                    bundle.putString("strTypeForQH", info.getStrCode());
                    bundle.putString("headTitle", info.getStrName());
                    bundle.putBoolean("isPersonInfo", true);
                    showActivity(activity, WebViewActivity.class, bundle);
                } else if (info.getStrCode().equals("08")) {//住宿安排
                    showActivity(activity, RoomArrangeActivity.class);
                } else if (info.getStrCode().equals("09")) {//乘车安排
                    showActivity(activity, RideArrangeListActivity.class);

                }
            }
        });
    }

    private void doLogic(int sign, Object object) {
        prgDialog.closePrgDialog();
        switch (sign) {
            case 0:
                NewsColumnListBean newsColumnListBean = (NewsColumnListBean) object;
                objList = newsColumnListBean.getObjList();
                adapter = new PlenaryMeetingGuideAdapter(activity, objList);
                listView.setAdapter(adapter);
                break;
        }

    }
}
