package com.suncn.ihold_zxztc.hst;


import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.hst.fsp.FspEngine;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.EventBusCarrier;
import com.suncn.ihold_zxztc.bean.MeetUserBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.RecyclerViewSpacesItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 视频会议主页
 */
public class MainMeetActivity extends BaseActivity {
    @BindView(id = R.id.my_sf)
    private LinearLayout llSf;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.tv_leave, click = true)
    private TextView tvLeave;
    @BindView(id = R.id.tv_audio, click = true)
    private TextView tvAudio;
    @BindView(id = R.id.tv_video, click = true)
    private TextView tvVideo;
    @BindView(id = R.id.tv_user, click = true)
    private TextView tvUser;
    @BindView(id = R.id.tv_camare, click = true)
    private TextView tvCamare;
    @BindView(id = R.id.tv_main_user)
    private TextView tvMainUser;
    @BindView(id = R.id.tv_name)
    private TextView tvName;
    private MeetJoinUserAdapter adapter;
    private ArrayList<MeetUserBean> userBeans = new ArrayList<>();
    private ArrayList<MeetUserBean> allUserList = new ArrayList<>();
    private boolean isCreate;//会议创建人，默认主持人
    private int intCamaretype = 0;//0前置，1后置
    private String mainViewUserId;//大屏的用户id
    private String mainVideoId;//大屏的视频id
    private Handler m_handler = new Handler();
    private SurfaceView surfaceView;//大屏的sf
    private String strId = "";
    private String strName="";

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_meet_main);
    }

    @Override
    public void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        //默认进来大屏显示自己
        surfaceView = new SurfaceView(activity);
        llSf.addView(surfaceView);
        FspManager.getInstance().setMySf(surfaceView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String strJointUserId = bundle.getString("strJointUserId");
            strId = bundle.getString("strId", "");
            isCreate = bundle.getBoolean("isCreate");
            strName=bundle.getString("strName");
            String[] uses = strJointUserId.split(",");
            for (String use : uses) {
                MeetUserBean meetUserBean = new MeetUserBean();
                if (use.contains("/")) {
                    meetUserBean.setStrUserId(use.split("/")[0]);
                    meetUserBean.setStrUserName(use.split("/")[1]);
                } else {
                    meetUserBean.setStrUserId(use);
                }
                allUserList.add(meetUserBean);
            }
        }
        tvName.setText(strName);
        mainViewUserId = GISharedPreUtil.getString(activity, "strLoginUserId");
        //可能在LoginActivity 切换 到 Mainactivtiy期间， 收到了 sdk的onRemoteVideoEvent
        //将保存的视频列表逐一打开
        setUserData();
        //这里存在广播接收到以后还未打开activity，在这重新获取数据
        m_handler.post(new Runnable() {
            @Override
            public void run() {
                FspManager fsm = FspManager.getInstance();
                for (Pair<String, String> remoteVideo : fsm.getRemoteVideos()) {
                    for (MeetUserBean userBean : userBeans) {
                        if (userBean.getStrUserId().equals(remoteVideo.first)) {
                            userBean.setVideoId(remoteVideo.second);
                        }
                    }
                }
                initList();
            }
        });
        //默认打开本地视频和音频
        FspManager.getInstance().publishVideo(true, surfaceView);
        FspManager.getInstance().startPublishAudio();
        tvAudio.setSelected(false);

    }


    private void initList() {
        LinearLayoutManager layoutManagerTop = new LinearLayoutManager(activity);
        layoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
        HashMap<String, Integer> hashMapTop = new HashMap<>();
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(hashMapTop));
        hashMapTop.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 3);
        hashMapTop.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 3);
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(hashMapTop));
        //完成layoutManager设置
        recyclerView.setLayoutManager(layoutManagerTop);
        adapter = new MeetJoinUserAdapter(activity);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> a, @NonNull View view, int position) {
                //如果点击是自己
                if (userBeans.get(position).getStrUserId().equals(GISharedPreUtil.getString(activity, "strLoginUserId"))) {
                    userBeans.remove(position);
                    //将在大屏的用户数据加入到下面的列表中
                    MeetUserBean userBean = new MeetUserBean();
                    userBean.setStrUserId(mainViewUserId);
                    userBean.setVideoId(mainVideoId);
                    userBeans.add(userBean);
                    //设置大屏的用户id
                    mainViewUserId = GISharedPreUtil.getString(activity, "strLoginUserId");
                } else {
                    String userid = userBeans.get(position).getStrUserId();
                    String vid = userBeans.get(position).getVideoId();
                    //把点击的用户从列表中移出,将大屏数据加到列表中
                    userBeans.remove(position);
                    MeetUserBean userBean = new MeetUserBean();
                    userBean.setStrUserId(mainViewUserId);
                    userBean.setVideoId(mainVideoId);
                    userBeans.add(userBean);
                    //如果点击的不是自己，则将点击的数据加到大屏数据
                    mainViewUserId = userid;
                    mainVideoId = vid;
                }
                adapter.setList(userBeans);
                adapter.notifyDataSetChanged();
                llSf.removeAllViews();
                surfaceView = new SurfaceView(activity);
                llSf.addView(surfaceView);
                if (mainViewUserId.equals(GISharedPreUtil.getString(activity, "strLoginUserId"))) {
                    FspManager.getInstance().publishVideo(true, surfaceView);
                } else {
                    if (GIStringUtil.isBlank(mainVideoId)) {
                        llSf.removeAllViews();
                    } else {
                        FspManager.getInstance().setRemoteVideoRender(mainViewUserId, mainVideoId, surfaceView, FspEngine.RENDER_MODE_CROP_FILL);
                    }

                }
                tvMainUser.setText(mainViewUserId);
            }
        });

        adapter.addData(userBeans);
        adapter.notifyDataSetChanged();
    }

    private void setUserData() {
        tvMainUser.setText(mainViewUserId);
        userBeans = new ArrayList<>();
        for (String groupUser : FspManager.getInstance().getGroupUsers()) {
            if (groupUser.equals(mainViewUserId)) {
                continue;
            }
            MeetUserBean userBean = new MeetUserBean();
            userBean.setStrUserId(groupUser);
            userBean.setVideo(true);
            userBean.setAudio(true);
            userBeans.add(userBean);
        }
        if (adapter != null) {
            adapter.setList(userBeans);
            adapter.notifyDataSetChanged();
        }
    }

    private void remindJoin(String strUserId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strUserId", strUserId);
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().SendInviteMeetingMsg(textParamMap),0);
    }

    //人员入会离会
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemoteUserEvent(FspEvents.RemoteUserEvent event) {
        if (event.eventtype == FspEngine.REMOTE_USER_JOIN_GROUP) {
            MeetUserBean userBean = new MeetUserBean();
            userBean.setStrUserId(event.userid);
            userBeans.add(userBean);
            adapter.setList(userBeans);
            showToast(event.userid + "加入会议");
        } else if (event.eventtype == FspEngine.REMOTE_USER_LEAVE_GROUP) {
            MeetUserBean userBean = new MeetUserBean();
            userBean.setStrUserId(event.userid);
            for (MeetUserBean bean : userBeans) {
                if (bean.getStrUserId().equals(event.userid)) {
                    userBeans.remove(bean);
                    break;
                }
            }
            adapter.setList(userBeans);
            showToast(event.userid + "离开会议");
        }
        if (event.userid.equals(mainViewUserId)){
            mainViewUserId =userBeans.get(0).getStrUserId();
            llSf.removeAllViews();
            surfaceView = new SurfaceView(activity);
            llSf.addView(surfaceView);
            if (mainViewUserId.equals(GISharedPreUtil.getString(activity, "strLoginUserId"))) {
                FspManager.getInstance().publishVideo(true, surfaceView);
            } else {
                if (GIStringUtil.isBlank(mainVideoId)) {
                    llSf.removeAllViews();
                } else {
                    FspManager.getInstance().setRemoteVideoRender(mainViewUserId, mainVideoId, surfaceView, FspEngine.RENDER_MODE_CROP_FILL);
                }

            }
            tvMainUser.setText(mainViewUserId);
            userBeans.remove(0);
        }
        adapter.notifyDataSetChanged();
        if (userBeans.size() == 0) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    //打开关闭视频
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteVideo(FspEvents.RemoteVideoEvent event) {
        GILogUtil.d("走了这个方法-------");
        if (event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED) {
            if (event.userid.equals(mainViewUserId)) {
                surfaceView = new SurfaceView(activity);
                llSf.addView(surfaceView);
                FspManager.getInstance().publishVideo(true, surfaceView);
                return;
            }
            for (MeetUserBean allDatum : adapter.getData()) {
                if (allDatum.getStrUserId().equals(event.userid)) {
                    allDatum.setVideoId(event.videoid);
                    allDatum.setVideo(true);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        } else if (event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STOPED) {
            if (event.userid.equals(mainViewUserId)) {
                llSf.removeAllViews();
                return;
            }
            if (event.userid.equals(mainViewUserId)) {
                return;
            }
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

    //收到指令
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgIncome(FspEvents.ChatMsgItem msgItem) {
        GILogUtil.e("离开会议指定----------");
        if (msgItem.msg.equals(DefineUtil.suncnOpenAudio)) {
            //关闭摄像头
            FspManager fspManger = FspManager.getInstance();
            if (fspManger.startPublishAudio()) {
                tvAudio.setSelected(false);
            }
            return;
        } else if (msgItem.msg.equals(DefineUtil.suncnCloseAudio)) {
            //关闭摄像头
            FspManager fspManger = FspManager.getInstance();
            if (fspManger.stopPublishAudio()) {
                tvAudio.setSelected(true);
            }
            return;
        } else if (msgItem.msg.equals(DefineUtil.suncnExitMeet)) {
            leaveGroup();
            return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier eventBusCarrier) {
        if (eventBusCarrier.getEventType() == 88) {
            if (eventBusCarrier.getObject().equals("0")) {
                FspManager.getInstance().stopVideoPublish();
                surfaceView.setVisibility(View.INVISIBLE);
                tvVideo.setSelected(true);
            } else {
                if (intCamaretype == 0) {
                    FspManager.getInstance().publishVideo(true, surfaceView);
                } else {
                    FspManager.getInstance().publishVideo(false, surfaceView);
                }
                surfaceView.setVisibility(View.VISIBLE);
                tvVideo.setSelected(false);
            }
        }
        if (eventBusCarrier.getEventType() == 89) {
            if (FspManager.getInstance().isAudioPublishing()) {
                FspManager.getInstance().stopPublishAudio();
                tvAudio.setSelected(true);
            } else {
                FspManager.getInstance().startPublishAudio();
                tvAudio.setSelected(false);
            }
        }
    }
    protected boolean leaveGroup() {
        // join group
        boolean result = FspManager.getInstance().leaveGroup();
        if (!result) {
            Toast.makeText(getApplicationContext(), "离开组失败",
                    Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private long m_exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - m_exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出会议",
                        Toast.LENGTH_SHORT).show();
                m_exitTime = System.currentTimeMillis();
                return true;
            } else {
                leaveGroup();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_leave:
                leaveGroup();
                break;
            case R.id.tv_audio:
                if (FspManager.getInstance().isAudioPublishing()) {
                    FspManager.getInstance().stopPublishAudio();
                    tvAudio.setSelected(true);
                } else {
                    FspManager.getInstance().startPublishAudio();
                    tvAudio.setSelected(false);
                }
                break;
            case R.id.tv_video:
                GILogUtil.e("1111", FspManager.getInstance().isVideoPublishing());
                if (FspManager.getInstance().isVideoPublishing()) {
                    FspManager.getInstance().stopVideoPublish();
                    surfaceView.setVisibility(View.INVISIBLE);
                    tvVideo.setSelected(true);
                } else {
                    if (intCamaretype == 0) {
                        FspManager.getInstance().publishVideo(true, surfaceView);
                    } else {
                        FspManager.getInstance().publishVideo(false, surfaceView);
                    }
                    surfaceView.setVisibility(View.VISIBLE);
                    tvVideo.setSelected(false);
                }
                break;
            case R.id.tv_user:
                Bundle bundle = new Bundle();
                bundle.putSerializable("objList", allUserList);
                bundle.putSerializable("objUserList", (Serializable) adapter.getData());
                bundle.putBoolean("isCreate", isCreate);
                bundle.putString("strId",strId);
                showActivity(activity, MeetUserListActivity.class, bundle);
                break;
            case R.id.tv_camare:
                if (intCamaretype == 0) {
                    intCamaretype = 1;
                    if (FspManager.getInstance().isVideoPublishing()) {
                        if (mainViewUserId.equals(GISharedPreUtil.getString(activity, "strLoginUserId"))) {
                            FspManager.getInstance().publishVideo(false, surfaceView);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    intCamaretype = 0;
                    if (FspManager.getInstance().isVideoPublishing()) {
                        if (mainViewUserId.equals(GISharedPreUtil.getString(activity, "strLoginUserId"))) {
                            FspManager.getInstance().publishVideo(true, surfaceView);
                        }else {
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
                break;
        }
    }
}
