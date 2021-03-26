package com.qd.longchat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDFriendHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.model.gd.QDFileModel;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDGroupBitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/4 上午10:23
 */
public class QDCreateGroupActivity extends QDBaseActivity {

    public final static int REQUEST_CREATE_GROUP = 100;
    public final static int REQUEST_SELECT_ICON = 101;

    @BindView(R2.id.view_cg_title)
    View viewTitle;
    @BindView(R2.id.iv_cg_icon)
    ImageView ivIcon;
    @BindView(R2.id.et_cg_input)
    EditText etInput;
    @BindView(R2.id.btn_cg_next)
    Button btnNext;

    @BindString(R2.string.create_group_title)
    String strTitle;
    @BindString(R2.string.create_group_warning)
    String strWarning;
    @BindString(R2.string.create_group_warning1)
    String strHint;
    @BindString(R2.string.create_group_loading)
    String strLoading;
    @BindString(R2.string.create_group_empty_member)
    String strEmptyMember;
    @BindString(R2.string.create_group_error)
    String strCreateGroupError;
    @BindString(R2.string.create_group_upload_icon_error)
    String strUploadError;
    @BindString(R2.string.network_error)
    String strNetworkError;

    private String groupName;
    private String iconPath;
    private Bitmap mBitmap;

    private List<String> excludedIdList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        excludedIdList = new ArrayList<>();
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        if (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_SECRET)) {
            initExcludedIdList();
        }
        excludedIdList.add(QDLanderInfo.getInstance().getId());
    }

    private void initExcludedIdList() {
        List<String> ids = QDUserHelper.getUserIdsLtSelfLevel();
        List<String> friendIdList = QDFriendHelper.getAllFriendIds();
        for (String friendId : friendIdList) {
            if (ids.contains(friendId)) {
                ids.remove(friendId);
            }
        }
        excludedIdList = ids;
    }

    @OnClick({R2.id.btn_cg_next, R2.id.iv_cg_icon})
    public void OnClick(View view) {
        if (view.getId() == R.id.btn_cg_next) {
            groupName = etInput.getText().toString();
            if (TextUtils.isEmpty(groupName)) {
                QDUtil.showToast(context, strWarning);
                return;
            }
            if (groupName.length() == 1) {
                QDUtil.showToast(context, strHint);
                return;
            }
            Intent intent = new Intent(context, QDContactActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_MULTI);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_TYPE, QDContact.TYPE_CONTACT);
            intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST, (ArrayList<String>) excludedIdList);
            startActivityForResult(intent, REQUEST_CREATE_GROUP);
        } else {
            Intent intent = new Intent(context, QDGroupIconActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_ICON);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CREATE_GROUP:
                    if (!com.longchat.base.util.QDUtil.isNetworkAvailable(context)) {
                        QDUtil.showToast(context, strNetworkError);
                        return;
                    }
                    doOperateGroup(data);
                    break;
                case REQUEST_SELECT_ICON:
                    iconPath = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH);
                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                    mBitmap = QDBitmapUtil.getInstance().createAvatarBitmap(iconPath);
                    ivIcon.setImageBitmap(mBitmap);
                    break;
            }

        }
    }

    private void doOperateGroup(Intent data) {
        List<QDUser> userList = data.getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
        if (userList.size() == 0) {
            QDUtil.showToast(context, strEmptyMember);
            return;
        }
        getWarningDailog().setTip(strLoading);
        getWarningDailog().show();
        final List<Map<String, String>> mapList = new ArrayList<>();
        for (QDUser user : userList) {
            if (!user.getId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", user.getId());
                map.put("name", user.getName());
                mapList.add(map);
            }
        }
        if (!TextUtils.isEmpty(iconPath)) {
            File file = new File(iconPath);
            QDClient.getInstance().uploadFile(file, "avatar", new QDFileCallBack<QDFileModel>() {
                @Override
                public void onUploading(String path, int per) {

                }

                @Override
                public void onUploadFailed(String errorMsg) {
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, strUploadError);
                    createGroup(mapList, "");
                }

                @Override
                public void onUploadSuccess(QDFileModel model) {
                    getWarningDailog().dismiss();
                    createGroup(mapList, model.getOriginal());
                }
            });
        } else {
            List<Bitmap> bitmapList = new ArrayList<>();
            QDUser user = QDUserHelper.getUserById(QDLanderInfo.getInstance().getId());
            bitmapList.add(QDBitmapUtil.getInstance().getBitmap(context, user.getId(), user.getName(), user.getPic()));
            if (userList.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    QDUser tempUser = userList.get(i);
                    bitmapList.add(QDBitmapUtil.getInstance().getBitmap(context, tempUser.getId(), tempUser.getName(), tempUser.getPic()));
                }
            } else {
                for (QDUser tempUser : userList) {
                    bitmapList.add(QDBitmapUtil.getInstance().getBitmap(context, tempUser.getId(), tempUser.getName(), tempUser.getPic()));
                }
            }
            QDGroupBitmap groupBitmap = new QDGroupBitmap(activity,bitmapList);
            Bitmap bitmap = groupBitmap.getGroupBitmap();
            String path = QDStorePath.IMG_PATH + "group_icon.png";
            QDUtil.savePhotoToSD(bitmap, path);
            QDClient.getInstance().uploadFile(new File(path), "avatar", new QDFileCallBack<QDFileModel>() {
                @Override
                public void onUploading(String path, int per) {

                }

                @Override
                public void onUploadFailed(String errorMsg) {
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, strUploadError);
                    createGroup(mapList, "");
                }

                @Override
                public void onUploadSuccess(QDFileModel model) {
                    getWarningDailog().dismiss();
                    createGroup(mapList, model.getOriginal());
                }
            });
            groupBitmap.recycleBitmap();
        }

    }

    private void createGroup(List<Map<String, String>> mapList, String fileId) {

        Gson gson = QDGson.getGson();
        String members = gson.toJson(mapList);

        QDClient.getInstance().createGroup(groupName, members, fileId, new QDResultCallBack<String>() {
            @Override
            public void onError(String errorMsg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, strCreateGroupError + errorMsg);
            }

            @Override
            public void onSuccess(String groupId) {
                getWarningDailog().dismiss();
                Intent groupIntent = new Intent(context, QDGroupChatActivity.class);
                groupIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, groupId);
                startActivity(groupIntent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }
}
