package com.qd.longchat.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDCloudManager;
import com.longchat.base.model.gd.QDCloudFile;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.activity.QDModifyInfoActivity;
import com.qd.longchat.cloud.adapter.QDCloudAdapter;
import com.qd.longchat.model.QDCloud;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/1 下午2:23
 */
public class QDCloudSearchActivity extends QDBaseActivity {

    @BindView(R2.id.et_search_name)
    EditText etSearch;
    @BindView(R2.id.tv_os_cancel)
    TextView tvCancel;
    @BindView(R2.id.lv_os_list)
    ListView lvList;
    @BindView(R2.id.tv_os_tag)
    TextView tvTag;

    @BindString(R2.string.cloud_complete)
    String strComplete;
    @BindString(R2.string.conversation_del)
    String strDel;
    @BindString(R2.string.cloud_rename)
    String strRename;
    @BindString(R2.string.cloud_download)
    String strDownload;
    @BindString(R2.string.cloud_forward)
    String strForward;
    @BindString(R2.string.cloud_rename_error)
    String strRenameError;
    @BindString(R2.string.cloud_search_file_error)
    String strSearchFileError;

    private String rootId;
    private QDCloudAdapter adapter;
    private List<QDCloud> cloudList;
    private QDCloud seletedCloud;
    private QDAlertView alertView;
    private String folderId;

    private QDCloudAdapter.OnMoreClickListener moreClickListener = new QDCloudAdapter.OnMoreClickListener() {
        @Override
        public void onMoreClick(QDCloud cloud, int type) {
            seletedCloud = cloud;
            alertView.setTitle(cloud.getName());
            alertView.setSelectList(strRename, strDownload, strDel);
            alertView.show();
        }
    };

    private QDAlertView.OnStringItemClickListener stringItemClickListener = new QDAlertView.OnStringItemClickListener() {
        @Override
        public void onItemClick(String str, int position) {
            if (str.equalsIgnoreCase(strDel)) {
                QDCloudManager.getInstance().deleteFile(rootId, seletedCloud.getId(), deleteListener);
            } else if (str.equalsIgnoreCase(strRename)) {
                Intent intent = new Intent(context, QDModifyInfoActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, strRename);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_RIGHT_NAME, strComplete);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, seletedCloud.getName());
                if (seletedCloud.getType() == QDCloud.TYPE_FILE) {
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FILE, true);
                }
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SELECT_LIMIT, false);
                startActivityForResult(intent, 1000);
            }
        }
    };

    private QDResultCallBack deleteListener = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {

        }

        @Override
        public void onSuccess(Object o) {
            adapter.getCloudList().remove(seletedCloud);
            adapter.notifyDataSetChanged();
        }
    };

    private QDResultCallBack renameListener = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, strRenameError + errorMsg);
        }

        @Override
        public void onSuccess(Object o) {
            setResult(RESULT_OK);
            finish();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_search);
        ButterKnife.bind(this);

        rootId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_ROOT_ID);
        initAlert();
        cloudList = new ArrayList<>();
        adapter = new QDCloudAdapter(context);
        adapter.setListener(moreClickListener);
        lvList.setAdapter(adapter);
    }

    @OnEditorAction(R2.id.et_search_name)
    public boolean onEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String searchTxt = etSearch.getText().toString();
            if (!TextUtils.isEmpty(searchTxt)) {
                QDCloudManager.getInstance().searchFileFromRemote(rootId, searchTxt, new QDResultCallBack<List<QDCloudFile>>() {
                    @Override
                    public void onError(String errorMsg) {
                        QDUtil.showToast(context, strSearchFileError + errorMsg);
                    }

                    @Override
                    public void onSuccess(List<QDCloudFile> fileList) {
                        for (QDCloudFile file : fileList) {
                            cloudList.add(initFile(file));
                        }
                        if (cloudList.size() != 0) {
                            lvList.setVisibility(View.VISIBLE);
                            tvTag.setVisibility(View.GONE);
                            adapter.setCloudList(cloudList);
                        } else {
                            lvList.setVisibility(View.GONE);
                            tvTag.setVisibility(View.VISIBLE);
                        }
                        adapter.setCloudList(cloudList);
                    }
                });
            }
        }
        return false;
    }

    private QDCloud initFile(QDCloudFile file) {
        QDCloud cloud = new QDCloud();
        cloud.setId(file.getId());
        cloud.setType(QDCloud.TYPE_FILE);
        cloud.setName(file.getName());
        cloud.setRootId(rootId);
        cloud.setSize((long) file.getSize());
        cloud.setMd5(file.getMd5());
        cloud.setOriginalUrl(file.getUrlOriginal());
        cloud.setCreaterId(file.getCreateUid());
        cloud.setCreaterName(file.getCreateUname());
        cloud.setCreateTime(file.getCreateTime());
        cloud.setExt(file.getExt());
        return cloud;
    }

    @OnClick(R2.id.tv_os_cancel)
    public void onClick(View view) {
        setResult(RESULT_OK);
        finish();
    }

    private void initAlert() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Center)
                .setOnStringItemClickListener(stringItemClickListener)
                .setDismissOutside(true)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String renameString = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO);
            QDCloudManager.getInstance().renameFile(renameString, seletedCloud.getRootId(), seletedCloud.getId(), seletedCloud.getPid(), renameListener);
        }
    }
}
