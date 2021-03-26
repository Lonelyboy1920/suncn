package com.suncn.ihold_zxztc.activity.plenarymeeting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.RoomArrangeBean;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

/**
 * 住宿安排
 */
public class RoomArrangeActivity extends BaseActivity {
    @BindView(id = R.id.tv_room_name)
    private TextView tvRoomName;
    @BindView(id = R.id.tv_room_address)
    private TextView tvRoomDddress;
    @BindView(id = R.id.tv_room_num)
    private TextView tvRoomNum;
    @BindView(id = R.id.tv_in_time)
    private TextView tvInTime;
    @BindView(id = R.id.tv_out_time)
    private TextView tvOutTime;
    @BindView(id = R.id.tv_res_address)
    private TextView tvResAddress;
    @BindView(id = R.id.tv_see_address, click = true)
    private TextView tvSeeAddress;
    @BindView(id = R.id.tv_see_order, click = true)
    private TextView tvSeeOrder;
    private RoomArrangeBean roomArrangeBean;
    @BindView(id = R.id.ll_room_num)
    private LinearLayout llRoomNum;
    @BindView(id = R.id.ll_res_address)
    private LinearLayout llResAddress;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_room_arrange);
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
        setHeadTitle("住宿安排");
        getRoomArrange();
        if (ProjectNameUtil.isGZSZX(activity)){
            llRoomNum.setVisibility(View.GONE);
            llResAddress.setVisibility(View.GONE);
        }
    }

    private void getRoomArrange() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().RoomArrangeServlet(textParamMap), 0);
    }

    private void doLogic(int sign, Object object) {
        prgDialog.closePrgDialog();
        switch (sign) {
            case 0:
                try {
                    roomArrangeBean = (RoomArrangeBean) object;
                    tvRoomName.setText(roomArrangeBean.getStrHotelName());
                    tvRoomDddress.setText(roomArrangeBean.getStrHotelPlace());
                    tvRoomNum.setText(roomArrangeBean.getStrRoomNumber());
                    tvInTime.setText(roomArrangeBean.getStrInTime());
                    tvOutTime.setText(roomArrangeBean.getStrOutTime());
                    tvResAddress.setText(roomArrangeBean.getStrEatPlace());
                } catch (Exception e) {

                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_see_address:
                Utils.openLocalMap(activity, roomArrangeBean.getStrHotelPlace());
                break;
            case R.id.tv_see_order:
                Bundle bundle = new Bundle();
                bundle.putString("strHotelId", roomArrangeBean.getStrHotelId());
                showActivity(activity, MenuListActivity.class, bundle);
                break;
        }
    }
}
