package com.qd.longchat.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDMessageHelper;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDFileAdapter;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDFile;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/4 下午4:10
 */

public class QDFileActivity extends QDBaseActivity {

    @BindView(R2.id.view_file_title)
    View viewTitle;
    @BindView(R2.id.file_list)
    ListView listView;
    @BindView(R2.id.tv_select)
    TextView tvSelect;
    @BindView(R2.id.ll_pop_layout)
    LinearLayout llPopLayout;

    @BindString(R2.string.str_phone_storage)
    String strPhoneStorage;
    @BindString(R2.string.str_msg_file)
    String strMsgFile;

    private QDFileAdapter adapter;
    private File currentFile;
    private boolean isMsgFile; //是否是消息文件列表
    private List<QDFile> msgFileList;

    FileFilter filter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return !pathname.isHidden();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_file);
        findViewById(R.id.view_place).setVisibility(View.VISIBLE);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        //请求授权
        HiPermission.create(activity).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void onGuarantee(String permisson, int position) { // 同意/已授权
                //msgFileList = getMsgFile();
                if (msgFileList == null) {
                    msgFileList = new ArrayList<>();
                }
                if (msgFileList.size() == 0) {
                    tvTitleName.setText(strPhoneStorage);
                    isMsgFile = false;
                    tvSelect.setText(strPhoneStorage);
                    currentFile = Environment.getExternalStorageDirectory();
                    List<QDFile> fileList = getFileList(currentFile);
                    adapter = new QDFileAdapter(activity, fileList);
                } else {
                    tvTitleName.setText(strMsgFile);
                    isMsgFile = true;
                    tvSelect.setText(strMsgFile);
                    adapter = new QDFileAdapter(activity, msgFileList);
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onClose() { // 用户关闭权限申请
            }

            @Override
            public void onFinish() { // 所有权限申请完成
            }

            @Override
            public void onDeny(String permisson, int position) { // 拒绝
            }
        });

    }

    private List<QDFile> getMsgFile() {
        List<QDFile> mFileList = new ArrayList<>();
        String path = QDStorePath.APP_PATH;
        File file = new File(path);
        if (file == null) {
            return null;
        }
        File[] appFiles = file.listFiles();
        if (appFiles == null) {
            return null;
        }
        for (File appFile : appFiles) {
            mFileList.add(initFileFromApp(appFile));
        }
        List<QDMessage> messageList = QDMessageHelper.getExistFileMessage();
        for (QDMessage message : messageList) {
            String p = message.getFilePath();
            File f = new File(p);
            if (f.exists()) {
                mFileList.add(initFileFromMessage(message));
            }
        }
        return QDSortUtil.sorSelfFile(mFileList);
    }

    private QDFile initFileFromApp(File file) {
        QDFile mFile = new QDFile();
        mFile.setPath(file.getPath());
        mFile.setDate(file.lastModified());
        mFile.setSize(QDUtil.changFileSizeToString(file.length()));
        mFile.setName(file.getName());
        mFile.setDirectory(false);
        return mFile;
    }

    private QDFile initFileFromMessage(QDMessage message) {
        QDFile mFile = new QDFile();
        mFile.setPath(message.getFilePath());
        String content = message.getContent();
        IMBTFFile imbtfFile = IMBTFFile.fromBTFXml(content);
        if (imbtfFile == null) {
            return mFile;
        }
        mFile.setDate(message.getCreateDate() / 1000);
        mFile.setName(imbtfFile.getName());
        mFile.setDirectory(false);
        mFile.setSize(QDUtil.changFileSizeToString(imbtfFile.getSize()));
        return mFile;
    }

    private List<QDFile> getFileList(File file) {
        List<QDFile> mFileList = new ArrayList<>();
        File[] files = file.listFiles(filter);
        List<File> fileList = QDSortUtil.sortFile(Arrays.asList(files));
        for (File f : fileList) {
            QDFile mFile = new QDFile();
            mFile.setDate(f.lastModified());
            mFile.setDirectory(f.isDirectory());
            if (f.isDirectory()) {
                mFile.setTotal(f.listFiles().length);
            } else {
                mFile.setSize(QDUtil.changFileSizeToString(f.length()));
            }
            mFile.setName(f.getName());
            mFile.setPath(f.getPath());
            mFileList.add(mFile);
        }
        return mFileList;
    }

    @OnClick({R2.id.tv_select, R2.id.ll_pop_layout, R2.id.tv_msg_file, R2.id.tv_phone_storage})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_select) {
            llPopLayout.setVisibility(View.VISIBLE);

        } else if (i == R.id.ll_pop_layout) {
            llPopLayout.setVisibility(View.GONE);

        } else if (i == R.id.tv_msg_file) {
            llPopLayout.setVisibility(View.GONE);
            if (tvSelect.getText().toString().equalsIgnoreCase(strPhoneStorage)) {
                isMsgFile = true;
                tvTitleSunname.setVisibility(View.GONE);
                tvSelect.setText(strMsgFile);
                tvTitleName.setText(strMsgFile);
                adapter.setFileList(getMsgFile());
            }

        } else if (i == R.id.tv_phone_storage) {
            llPopLayout.setVisibility(View.GONE);
            if (tvSelect.getText().toString().equalsIgnoreCase(strMsgFile)) {
                isMsgFile = false;
                tvSelect.setText(strPhoneStorage);
                tvTitleName.setText(strPhoneStorage);
                currentFile = Environment.getExternalStorageDirectory();
                List<QDFile> fileList = getFileList(currentFile);
                adapter.setFileList(fileList);
            }

        }
    }

    @OnItemClick(R2.id.file_list)
    public void onItemClick(int position) {
        currentFile = new File(adapter.getItem(position).getPath());
        if (isMsgFile) {
            Intent intent = new Intent();
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_FILE, currentFile);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            if (currentFile.isDirectory()) {
                addItem(currentFile);
                adapter.setFileList(getFileList(currentFile));
            } else {
                Intent intent = new Intent();
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_FILE, currentFile);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void addItem(File file) {
        if (tvTitleSunname.getVisibility() == View.GONE) {
            tvTitleSunname.setVisibility(View.VISIBLE);
            tvTitleSunname.setText(file.getName());
        } else {
            tvTitleSunname.append("/" + file.getName());
        }
    }

    private void deleteLastItem() {
        String content = tvTitleSunname.getText().toString();
        if (content.contains("/")) {
            int index = content.lastIndexOf("/");
            tvTitleSunname.setText(content.substring(0, index));
        } else {
            tvTitleSunname.setText("");
            tvTitleSunname.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        if (isMsgFile) {
            finish();
        } else {
            if (currentFile.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                finish();
            } else {
                deleteLastItem();
                String fileName = currentFile.getAbsolutePath();
                String newFileName = fileName.substring(0, fileName.lastIndexOf("/"));
                currentFile = new File(newFileName);
                adapter.setFileList(getFileList(currentFile));
            }
        }
    }
}
