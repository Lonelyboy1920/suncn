package com.qd.longchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.longchat.base.dao.QDApp;
import com.longchat.base.dao.QDPlug;
import com.longchat.base.databases.QDAppHelper;
import com.longchat.base.databases.QDPlugHelper;
import com.qd.longchat.R;
import com.qd.longchat.acc.activity.QDAccSelfActivity;
import com.qd.longchat.activity.QDWebActivity;
import com.qd.longchat.adapter.QDWorkAdapter;
import com.qd.longchat.adapter.QDWorkPicAdapter;
import com.qd.longchat.cloud.activity.QDCloudMainActivity;
import com.qd.longchat.order.activity.QDOrderActivity;
import com.qd.longchat.sign.QDSignActivity;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/4/13 下午5:38
 */

public class QDWorkFragment extends QDBaseFragment {

    private static final String TO_GZH = "gzh";
    private static final String TO_SIGNIN = "signin";
    private static final String TO_MEETING = "meeting";
    private static final String TO_LIVING = "liveBroadcast";
    private static final String TO_COMPUTER = "myDevice";
    private StickyGridHeadersGridView gridView;
    private QDWorkAdapter adapter;
    private ViewPager picPager;
    private LinearLayout containerLayout;
    private RelativeLayout picLayout;
    private QDWorkPicAdapter picAdapter;
    private List<QDApp> appList;
    private List<QDPlug> picList;
    private int count;
    private long lastItemClickTime;

    public interface OnViewPagerClickListener {
        void onClick(int position, String url);
    }

    private OnViewPagerClickListener onViewPagerClickListener = new OnViewPagerClickListener() {
        @Override
        public void onClick(int position, String url) {
            QDPlug plug = picList.get(position % count);
            if (TextUtils.isEmpty(url)) {
                return;
            }
            if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")) {
                toWebView(url, plug.getTargetType());
            } else {
                toWebView(QDUtil.getWebApiServer() + url, plug.getTargetType());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.im_fragment_work, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = view.findViewById(R.id.gv_work);
        picPager = view.findViewById(R.id.vp_work);
        containerLayout = view.findViewById(R.id.ll_point_container);
        picLayout = view.findViewById(R.id.rl_work_pic_layout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initRoundView();
        setListener();
        startTask();
    }

    private void initData() {
        appList = QDAppHelper.loadAll();
        List<QDPlug> pics = QDPlugHelper.getPlugByType(QDPlug.TYPE_PIC);
        adapter = new QDWorkAdapter(getContext(), appList);
        gridView.setAdapter(adapter);

        count = pics.size();
        if (count > 3) {
            picList = pics.subList(0, 3);
        } else {
            picList = pics;
        }
        count = picList.size();
        if (count != 0) {
            picLayout.setVisibility(View.VISIBLE);
            picAdapter = new QDWorkPicAdapter(getContext(), picList);
            picPager.setAdapter(picAdapter);
            picPager.setCurrentItem(0);
            picAdapter.setOnViewPagerClickListener(onViewPagerClickListener);
        } else {
            picLayout.setVisibility(View.GONE);
        }
    }

    private void initRoundView() {
        if (count <= 1) {
            return;
        }
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            ImageView iv = new ImageView(getContext());
            if (i == 0) {
                iv.setBackgroundResource(R.drawable.im_pic_round_selected);
            } else {
                iv.setBackgroundResource(R.drawable.im_pic_round_normal);
            }
            params.setMargins(0, 0, 10, 0);
            containerLayout.addView(iv, params);
        }
    }

    private void setListener() {
        picPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (count <= 1) {
                    return;
                }
                clearPointBack();
                containerLayout.getChildAt(position % count).setBackgroundResource(R.drawable.im_pic_round_selected);
                handler.removeCallbacks(runnable);
                index = position + 1;
                startTask();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastItemClickTime < 200) {
                    return;
                } else {
                    lastItemClickTime = currentTime;
                }

                QDApp app = adapter.getItem(position);
                if (app.getCode().equalsIgnoreCase("addon_pan")) {
                    Intent intent = new Intent(context, QDCloudMainActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_IS_HOME, true);
                    context.startActivity(intent);
                } else if (app.getCode().equalsIgnoreCase("addon_sign")) {
                    boolean isHave = AndPermission.hasPermissions(context, Permission.Group.LOCATION);
                    if (!isHave) {
                        QDUtil.getPermission(context, Permission.Group.LOCATION);
                    } else {
                        Intent intent = new Intent(context, QDSignActivity.class);
                        context.startActivity(intent);
                    }
                } else if (app.getCode().equalsIgnoreCase("addon_acc")) {
                    Intent intent = new Intent(context, QDAccSelfActivity.class);
                    context.startActivity(intent);
                } else if (app.getCode().equalsIgnoreCase("addon_instr")) {
                    Intent intent = new Intent(context, QDOrderActivity.class);
                    context.startActivity(intent);
                } else {
                    String targetType = app.getMobileTargetType();
                    int type = 1;
                    if (!TextUtils.isEmpty(targetType)) {
                        type = Integer.valueOf(targetType);
                    }
                    String url = app.getMobileTargetUrl();
                    toWebView(QDUtil.replaceWebServerAndToken(url), type);
                }
            }
        });

    }

    private void clearPointBack() {
        int count = containerLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            containerLayout.getChildAt(i).setBackgroundResource(R.drawable.im_pic_round_normal);
        }
    }

    private Handler handler = new Handler();
    private int index = 1;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            picPager.setCurrentItem(index, false);
        }
    };

    private void startTask() {
        if (count > 1) {
            handler.postDelayed(runnable, 2000);
        }
    }

    private void toWebView(String url, int targetType) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (targetType == 1) {
            Intent intent = new Intent(context, QDWebActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_WEB_URL, url);
            startActivity(intent);
        } else {
            QDUtil.toBrowser(getActivity(), url);
        }
    }

}
