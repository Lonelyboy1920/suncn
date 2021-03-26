package com.suncn.ihold_zxztc.activity.circle;

import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.FollowSettingLabelExAdapter;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.CircleTagListBean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 标签设置
 */
public class FollowSettingActivity extends BaseActivity {
    @BindView(id = R.id.exListView)
    ExpandableListView exListView;
    public Set<String> strCodeSet = new HashSet<>();
    public Set<String> strNameSet = new HashSet<>();
    private List<CircleTagListBean.CircleTagBean> objList;
    private FollowSettingLabelExAdapter exAdapter;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_follow_setting);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_all_tags));
        back_Button.setText("\ue63f");
        goto_Button.setText(getString(R.string.string_save));
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setTextColor(getResources().getColor(R.color.font_content));
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        exAdapter = new FollowSettingLabelExAdapter(activity, objList);
        exListView.setAdapter(exAdapter);
        getColumnInfo();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                saveTagFollow();
                break;
        }
    }

    /**
     * 保存关注的标签
     */
    private void saveTagFollow() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        String strFollowTagCode = "";
        String strFollowTagName = "";
        for (String s : strCodeSet) {
            strFollowTagCode = strFollowTagCode + s + ",";
        }
        for (String s : strNameSet) {
            strFollowTagName = strFollowTagName + s + ",";
        }
        textParamMap.put("strCode", strFollowTagCode);
        textParamMap.put("strName", strFollowTagName);
        doRequestNormal(ApiManager.getInstance().SetLabelServlet(textParamMap), 2);

    }

    @Override
    public void setListener() {
        super.setListener();
        exListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return true;
            }
        });
        exListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    /**
     * 获取标签信息
     */
    private void getColumnInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().GetLabelServlet(textParamMap), 1);
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        prgDialog.closePrgDialog();
        switch (sign) {
            case 1:
                try {
                    CircleTagListBean newsColumnListBean = (CircleTagListBean) data;
                    objList = newsColumnListBean.getObjList();
                    exAdapter.setObjList(objList);
                    exAdapter.notifyDataSetChanged();
                    for (int i = 0; i < objList.size(); i++) {
                        exListView.expandGroup(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 2:
                setResult(RESULT_OK);
                finish();
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }

}
