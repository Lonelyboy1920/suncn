package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.longchat.base.dao.QDDept;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDSelectedMemberAdapter;
import com.longchat.base.dao.QDUser;
import com.qd.longchat.util.QDIntentKeyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/3 下午2:02
 */
public class QDSelectedMemberListActivity extends QDBaseActivity {

    @BindView(R2.id.view_gm_title)
    View viewTitle;
    @BindView(R2.id.lv_gm_list)
    ListView lvList;

    @BindString(R2.string.selected_title_name)
    String strTitle;
    @BindString(R2.string.title_sure)
    String strSure;

    private List<QDUser> userList;
    private List<String> idList;
    private List<String> deptIdList;
    private List<QDDept> deptList;
    private QDSelectedMemberAdapter adapter;

    private QDSelectedMemberAdapter.OnRemoveListener listener = new QDSelectedMemberAdapter.OnRemoveListener() {
        @Override
        public void onRemoveUser(QDUser user) {
            userList.remove(user);
            idList.remove(user.getId());
            adapter.setUserList(userList);
        }

        @Override
        public void onRemoveDept(QDDept dept) {
            deptList.remove(dept);
            deptIdList.remove(dept.getId());
            adapter.setDeptList(deptList);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText(strSure);
        userList = getIntent().getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
        idList = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST);
        deptIdList = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_ID_LIST);
        deptList = getIntent().getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_LIST);
        adapter = new QDSelectedMemberAdapter(context, userList, deptList, listener);
        lvList.setAdapter(adapter);
    }

    @OnClick(R2.id.tv_title_right)
    public void onClick() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) idList);
        intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_ID_LIST, (ArrayList<String>) deptIdList);
        intent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) userList);
        intent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_LIST, (ArrayList<? extends Parcelable>) deptList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
