package com.suncn.ihold_zxztc.hst;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.hst.fsp.FspEngine;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.MeetUserBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.RecyclerView;

public class MeetUserListActivity extends BaseActivity {
    private RecyclerView easyRecyclerView;
    private MeetUserListAdapter adapter;
    private ArrayList<MeetUserBean> objAllList;//总人数
    private ArrayList<MeetUserBean> objUserList;//在线人数
    @BindView(id = R.id.tv_close, click = true)
    private TextView tvClose;
    @BindView(id = R.id.tv_quite, click = true)
    private TextView tvQuite;
    @BindView(id = R.id.tv_name)
    private TextView tvName;
    private int curindex = 0;
    private boolean isCreate;
    private String strId = "";

    @Override
    public void setRootView() {
        setContentView(R.layout.popup_meet_user_list);
        getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                showToast("提醒成功");
            }
        };
        EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        objAllList = (ArrayList<MeetUserBean>) bundle.getSerializable("objList");
        objUserList = (ArrayList<MeetUserBean>) bundle.getSerializable("objUserList");
        isCreate = bundle.getBoolean("isCreate", false);
        strId = bundle.getString("strId", "");
        easyRecyclerView = findViewById(R.id.recyclerView);
        findViewById(R.id.ll_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvName.setText("参会人员（" + FspManager.getInstance().getGroupUsers().size() + "/" + objAllList.size() + ")");
        initRecyclerView();
        if (isCreate) {
            tvQuite.setVisibility(View.VISIBLE);
        } else {
            tvQuite.setVisibility(View.GONE);
        }
        adapter.setCreate(isCreate);

    }

    public void remindJoin(String strUserId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strUserId", strUserId);
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().SendInviteMeetingMsg(textParamMap), 0);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, easyRecyclerView, false, false, R.color.main_bg, 0.5f, 0);
        adapter = new MeetUserListAdapter(activity);
        easyRecyclerView.setAdapter(adapter);
        for (MeetUserBean meetUserBean : objAllList) {
            for (MeetUserBean userBean : objUserList) {
                if (userBean.getStrUserId().equals(meetUserBean.getStrUserId())) {
                    meetUserBean.setAudio(userBean.isAudio());
                    meetUserBean.setVideo(userBean.isVideo());
                    meetUserBean.setOnline(true);
                }
            }
            if (meetUserBean.getStrUserId().equals(GISharedPreUtil.getString(activity, "strLoginUserId"))) {
                meetUserBean.setAudio(FspManager.getInstance().isAudioPublishing());
                meetUserBean.setVideo(FspManager.getInstance().isVideoPublishing());
                meetUserBean.setOnline(true);
            }
        }
        adapter.getData().clear();
        adapter.addData(objAllList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.tv_quite:
                if (tvQuite.getText().toString().equals("解除全体禁言")) {
                    tvQuite.setText("全体禁言");
                    FspManager.getInstance().sendGroupMsg(DefineUtil.suncnOpenAudio);
                } else {
                    tvQuite.setText("解除全体禁言");
                    FspManager.getInstance().sendGroupMsg(DefineUtil.suncnCloseAudio);
                }
                break;
        }
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.push_bottom_out, 0);
        super.onPause();
    }

    //人员入会离会
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemoteUserEvent(FspEvents.RemoteUserEvent event) {
        if (event.eventtype == FspEngine.REMOTE_USER_JOIN_GROUP) {
            MeetUserBean userBean = new MeetUserBean();
            userBean.setStrUserId(event.userid);
            userBean.setOnline(true);
            userBean.setAudio(true);
            userBean.setVideo(true);
            objAllList.add(userBean);
            adapter.setList(objAllList);
        } else if (event.eventtype == FspEngine.REMOTE_USER_LEAVE_GROUP) {
            MeetUserBean userBean = new MeetUserBean();
            userBean.setStrUserId(event.userid);
            for (MeetUserBean bean : objAllList) {
                if (bean.getStrUserId().equals(event.userid)) {
                    objAllList.remove(bean);
                    break;
                }
            }
            adapter.setList(objAllList);
            showToast(event.userid + "离开会议");
        }
        adapter.notifyDataSetChanged();
        tvName.setText("参会人员（" + FspManager.getInstance().getGroupUsers().size() + "/" + objAllList.size() + ")");
    }

    //打开关闭视频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteVideo(FspEvents.RemoteVideoEvent event) {
        if (event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED) {
            for (MeetUserBean allDatum : adapter.getData()) {
                if (allDatum.getStrUserId().equals(event.userid)) {
                    allDatum.setVideoId(event.videoid);
                    allDatum.setVideo(true);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        } else if (event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STOPED) {
            for (MeetUserBean allDatum : adapter.getData()) {
                if (allDatum.getStrUserId().equals(event.userid)) {
                    allDatum.setVideoId("");
                    allDatum.setVideo(false);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    //打开关闭音频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteAudio(FspEvents.RemoteAudioEvent event) {
        if (event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STARTED) {
            for (MeetUserBean allDatum : adapter.getData()) {
                if (allDatum.getStrUserId().equals(event.userid)) {
                    allDatum.setAudio(true);
                    break;
                }
            }
        } else if (event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STOPED) {
            for (MeetUserBean allDatum : adapter.getData()) {
                if (allDatum.getStrUserId().equals(event.userid)) {
                    allDatum.setAudio(false);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}
