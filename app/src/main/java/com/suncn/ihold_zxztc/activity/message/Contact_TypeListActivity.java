package com.suncn.ihold_zxztc.activity.message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gavin.giframe.ui.BindView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Contact_WY_ExLVAdapter;
import com.suncn.ihold_zxztc.bean.ZxtaJointMemListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.view.forscrollview.MyScrollExpandableListView;

import java.util.HashMap;
import java.util.List;

/**
 * 通讯录列表（按类型分类）
 */
public class Contact_TypeListActivity extends BaseActivity {
    @BindView(id = R.id.lv_recent_contact)
    private ListView listView;//当前通讯录列表ListView
    @BindView(id = R.id.exListview)
    private MyScrollExpandableListView exListView;
    @BindView(id = R.id.tv_empty)
    private ImageView empty_TextView;
    @BindView(id = R.id.rl_contact)
    private RelativeLayout llContact;

    private Contact_WY_ExLVAdapter adapter;
    private String strType; // 1-界别，2-党派，3-专委会
    private boolean isCreateGroup = false;
    private boolean isAddMember = false;//是否为邀请新成员入群
    private String strGroupId = "";//群id
    private List<ZxtaJointMemListBean.ZxtaJointMemBean> zxtaJointMemBeans;
    private boolean isCommissioner = false; // 委员管理

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_contact_main);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isCommissioner = bundle.getBoolean("isCommissioner", false);
            strType = bundle.getString("strType");
            setHeadTitle(bundle.getString("headTitle"));
        }
        getListData(true);
    }

    /**
     * 委员列表
     */
    private void getListData(boolean isTrue) {
        if (isTrue)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", strType); // 1表示沟通页面人员信息(包含群)
        if (isAddMember) {
            textParamMap.put("strGroupId", strGroupId); // 邀请新成员入群
        }
        if (isCommissioner) {
            doRequestNormal(ApiManager.getInstance().getMemDutyList(textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().getContactList(textParamMap), 0);
        }
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    ZxtaJointMemListBean zxtaJointMemListBean = (ZxtaJointMemListBean) obj;
                    listView.setVisibility(View.GONE);
                    zxtaJointMemBeans = zxtaJointMemListBean.getObjList();
                    if (zxtaJointMemBeans != null && zxtaJointMemBeans.size() > 0) {
                        exListView.setVisibility(View.VISIBLE);
                        empty_TextView.setVisibility(View.GONE);
                        llContact.setVisibility(View.VISIBLE);
                        adapter = new Contact_WY_ExLVAdapter(activity, zxtaJointMemBeans, false); // 必须重新new，否则ExpandableListView刷新会出问题
                        adapter.setPaved(false);
                        adapter.setCommissioner(isCommissioner);
                        adapter.setStrUrl(zxtaJointMemListBean.getStrUrl());  //在Contact_MainFragment调用了这个方法，而这个里面却没有调用
                        exListView.setAdapter(adapter);
                    } else {
                        exListView.setVisibility(View.GONE);
                        empty_TextView.setVisibility(View.VISIBLE);
                        llContact.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }
}
