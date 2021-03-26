package com.qd.longchat.activity;

import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.longchat.base.dao.QDDownloadInfo;
import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/28 上午10:42
 */
public class QDFileDisplayActivity extends QDBaseActivity implements TbsReaderView.ReaderCallback {

    @BindView(R2.id.view_file_title)
    View viewTitle;
    @BindView(R2.id.frame_layout)
    FrameLayout frameLayout;

    @BindString(R2.string.file_not_support)
    String strNotSupport;

    private TbsReaderView tbsReaderView;
    private String filePath;
    private QDDownloadInfo info;
    private boolean isRemove;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tbsReaderView = new TbsReaderView(context, this);
        frameLayout.addView(tbsReaderView, params);
        filePath = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH);
        isRemove = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_REMOTE, false);
        int index = filePath.lastIndexOf("/");
        tvTitleName.setText(filePath.substring(index + 1));

        String bsReaderTemp = Environment.getExternalStorageDirectory() + File.separator + "TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);

        if (!bsReaderTempFile.exists()) {
            QDLog.d("TAG", "准备创建/storage/emulated/0/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if(!mkdir){
                QDLog.e("TAG", "创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", bsReaderTemp);
        boolean result = tbsReaderView.preOpen(getFileType(filePath), false);
        if (result) {
            tbsReaderView.openFile(bundle);
        } else {
            QDUtil.showToast(this, strNotSupport);
            finish();
        }
    }

    public String getFileType(String filePath) {
        if (!filePath.contains(".")) {
            return "";
        }
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1);
        return ext;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRemove) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tbsReaderView.onStop();
    }
}
