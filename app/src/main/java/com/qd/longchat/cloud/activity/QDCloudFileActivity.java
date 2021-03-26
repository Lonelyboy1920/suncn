package com.qd.longchat.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.dao.QDDownloadInfo;
import com.longchat.base.databases.QDDownloadInfoHelper;
import com.longchat.base.manager.QDCloudFileManager;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.activity.QDFileDisplayActivity;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDCloud;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/31 下午1:53
 */
public class QDCloudFileActivity extends QDBaseActivity implements QDFileDownLoadCallBack {

    private final static int REQUEST_FOR_RENAME = 1002;

    @BindView(R2.id.view_cf_title)
    View viewTitle;
    @BindView(R2.id.iv_cf_icon)
    ImageView ivIcon;
    @BindView(R2.id.tv_cf_name)
    TextView tvName;
    @BindView(R2.id.tv_cf_size)
    TextView tvSize;
    @BindView(R2.id.pb_cf_bar)
    ProgressBar pbBar;
    @BindView(R2.id.btn_cf_btn)
    Button btnOpen;

    @BindString(R2.string.cloud_file_title)
    String strTile;
    @BindString(R2.string.cloud_file_download)
    String strDownload;
    @BindString(R2.string.cloud_file_open)
    String strOpen;

    private QDCloud cloud;
    private boolean isDownload;
    private String renameString;
    private QDDownloadInfo info;

    @BindString(R2.string.conversation_del)
    String strDel;
    @BindString(R2.string.cloud_rename)
    String strRename;
    @BindString(R2.string.cloud_forward)
    String strForward;
    @BindString(R2.string.cloud_complete)
    String strComplete;
    @BindString(R2.string.cloud_pause_download)
    String strPauseDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_file);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleRight.setVisibility(View.GONE);
        tvTitleName.setText(strTile);
        tvTitleRight.setBackgroundResource(R.drawable.ic_title_more);
        cloud = (QDCloud) getIntent().getSerializableExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD);
        info = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_DOWNLOAD_INFO);
        tvName.setText(cloud.getName());
        tvSize.setText(QDUtil.changFileSizeToString(cloud.getSize()));
        ivIcon.setImageResource(QDUtil.getFileIconByName(context, cloud.getName()));

        if (info != null) {
            if (info.getState() == QDDownloadInfo.TYPE_DOWNLOAD) {
                pbBar.setVisibility(View.VISIBLE);
                tvSize.setVisibility(View.GONE);
                pbBar.setProgress((int) (info.getReadLength() * 100 / info.getTotalLength()));
                QDCloudFileManager.downloadFile(info, this);
                btnOpen.setText(R.string.cloud_pause_download);
            } else {
                pbBar.setVisibility(View.GONE);
                tvSize.setVisibility(View.VISIBLE);
                tvSize.setText(R.string.cloud_pause_download);
                btnOpen.setText(strDownload);
            }
        }
    }

    @OnClick({R2.id.tv_title_right, R2.id.btn_cf_btn})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_cf_btn) {
            String text = btnOpen.getText().toString();
            final String path = QDStorePath.CLOUD_PATH + cloud.getName();
            if (text.equalsIgnoreCase(strOpen)) {
                if (path.toLowerCase().endsWith("jpg") || path.toLowerCase().endsWith("jpeg") || path.toLowerCase().endsWith("png") || path.toLowerCase().endsWith("bmp")) {
                    Intent i = new Intent(context, QDPicActivity.class);
                    List<String> pathList = new ArrayList<>(1);
                    pathList.add(path);
                    i.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) pathList);
                    startActivity(i);
                } else {
                    Intent i = new Intent(context, QDFileDisplayActivity.class);
                    i.putExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH, path);
                    context.startActivity(i);
                }
            } else if (text.equalsIgnoreCase(strDownload)) {
                btnOpen.setText(R.string.cloud_pause_download);
                pbBar.setVisibility(View.VISIBLE);
                tvSize.setVisibility(View.GONE);
                if (info == null) {
                    info = new QDDownloadInfo();
                    info.setMd5(cloud.getMd5());
                    info.setReadLength(0);
                    info.setUrl(QDUtil.getWebFileServer() + cloud.getOriginalUrl());
                    info.setTotalLength(cloud.getSize());
                    info.setPath(path);
                    info.setState(QDDownloadInfo.TYPE_DOWNLOAD);
                    info.setName(cloud.getName());
                    info.setCreateDate(System.currentTimeMillis());
                    QDDownloadInfoHelper.insert(info);
                } else {
                    QDDownloadInfoHelper.updateStateAndReadLength(cloud.getMd5(), QDDownloadInfo.TYPE_DOWNLOAD, info.getReadLength());
                }
                QDCloudFileManager.downloadFile(info, this);
            } else if (text.equalsIgnoreCase(strPauseDownload)) {
                btnOpen.setText(strDownload);
                pbBar.setVisibility(View.GONE);
                tvSize.setVisibility(View.VISIBLE);
                tvSize.setText(R.string.cloud_pause_download);
                QDCloudFileManager.cancel(cloud.getMd5());
            }
        }
    }

    @Override
    public void onDownLoading(int per) {
        pbBar.setProgress(per);
    }

    @Override
    public void onDownLoadSuccess() {
        pbBar.setVisibility(View.INVISIBLE);
        tvSize.setVisibility(View.VISIBLE);
        tvSize.setText(QDUtil.changFileSizeToString(cloud.getSize()));
        btnOpen.setText(strOpen);
        isDownload = true;
    }

    @Override
    public void onDownLoadFailed(String msg) {
        pbBar.setVisibility(View.INVISIBLE);
        tvSize.setVisibility(View.VISIBLE);
        tvSize.setText(QDUtil.changFileSizeToString(cloud.getSize()));
        QDUtil.showToast(context, context.getResources().getString(R.string.file_download_failed) + ": " + msg);
    }
}
