package com.qd.longchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDMessage;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDGroupChatActivity;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDAlertView;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/30 上午11:44
 */

public class QDGroupViewPagerAdapter extends PagerAdapter {

    private Context context;
    private String[] tabTitles;
    private List<QDGroup> mineGroupList;
    private List<QDGroup> joinedGroupList;
    private boolean isForward;
    private QDAlertView alertView;
    private QDMessage message;

    public QDGroupViewPagerAdapter(Context context, String[] tabTitles, List<QDGroup> mineGroupList, List<QDGroup> joinedGroupList, boolean isForward) {
        this.context = context;
        this.tabTitles = tabTitles;
        this.mineGroupList = mineGroupList;
        this.joinedGroupList = joinedGroupList;
        this.isForward = isForward;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setVerticalScrollBarEnabled(false);
        final List<QDGroup> groupList;
        if (position == 0) {
            groupList = mineGroupList;
        } else {
            groupList = joinedGroupList;
        }
        QDGroupAdapter adapter = new QDGroupAdapter(context, groupList);
        listView.setAdapter(adapter);
        container.addView(listView, params);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isForward) {
                    initAlert(groupList.get(position));
                } else {
                    QDGroup group = groupList.get(position);
                    Intent intent = new Intent(context, QDGroupChatActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, group.getId());
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_USER, false);
                    context.startActivity(intent);
                }
            }
        });
        listView.setTag(position);
        return listView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void setMineGroupList(List<QDGroup> mineGroupList) {
        this.mineGroupList = mineGroupList;
    }

    public void setJoinedGroupList(List<QDGroup> joinedGroupList) {
        this.joinedGroupList = joinedGroupList;
    }

    private void initAlert(final QDGroup group) {
        if (alertView == null) {
            alertView = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Alert)
                    .setContent(context.getResources().getString(R.string.contact_is_forward))
                    .isHaveCancelBtn(true)
                    .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                        @Override
                        public void onClick(boolean isSure) {
                            if (isSure) {
                                QDClient.getInstance().forwardGMessage(group, message, new QDResultCallBack() {
                                    @Override
                                    public void onError(String errorMsg) {

                                    }

                                    @Override
                                    public void onSuccess(Object o) {

                                    }
                                });
                                ((Activity) context).finish();
                            }
                        }
                    }).build();
        }
        alertView.show();
    }

    public void setMessage(QDMessage message) {
        this.message = message;
    }
}
