package com.qd.longchat.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDMessageHelper;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDSelfFilePagerAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDFile;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/16 上午9:19
 */
public class QDSelfFileActivity extends QDBaseActivity {

    private static final String[] TYPE_DOC = {".txt", ".rtf", ".doc", ".docx", ".xls", ".xlsx", ".ppt",
            ".pptx", ".htm", ".html", ".wpd", ".pdf"};

    private static final String[] TYPE_PIC = {".bmp", ".jpg", ".png", ".tiff", ".gif", ".pcx", ".tga",
            ".exif", ".fpx", ".svg", ".psd", ".cdr", ".pcd", ".dxf", ".ufo", ".eps", ".ai", ".raw", ".wmf"};

    public static final String[] TYPE_AV = {".wmv", ".asf", ".asx", ".rm", ".rmvb", ".mpg", ".mpeg",
            ".mpe", ".3gp", ".mov", ".mp4", ".m4v", ".avi", ".dat", ".mkv", ".flv", ".vob"};

    @BindView(R2.id.view_group_title)
    View viewTitle;
    @BindView(R2.id.view_group_search)
    View viewSearch;
    @BindView(R2.id.tl_group_layout)
    TabLayout tlTabLayout;
    @BindView(R2.id.vp_group_pager)
    ViewPager viewPager;
    @BindView(R2.id.ll_group_parent_layout)
    LinearLayout llPatentLayout;

    @BindString(R2.string.file_all)
    String strAll;
    @BindString(R2.string.file_document)
    String strDocument;
    @BindString(R2.string.file_pic)
    String strPic;
    @BindString(R2.string.file_video)
    String strVideo;
    @BindString(R2.string.file_other)
    String strOther;
    @BindString(R2.string.self_file)
    String strTitle;
    @BindString(R2.string.from_app)
    String strFromApp;
    @BindString(R2.string.self_file_to)
    String strTo;
    @BindString(R2.string.self_file_from)
    String strFrom;
    @BindString(R2.string.self_file_from_group)
    String strFromGroup;

    private String[] tabTitles;
    private QDSelfFilePagerAdapter pagerAdapter;
    private List<QDFile> mFileList;
    private List<QDFile> docFileList;
    private List<QDFile> imgFileList;
    private List<QDFile> otherFileList;
    private List<QDFile> videoFileList;

    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String path = QDStorePath.APP_PATH;
            File file = new File(path);
            File[] appFiles = file.listFiles();
            for (File appFile : appFiles) {
                mFileList.add(initFileFromApp(appFile));
            }
            List<QDMessage> messageList = QDMessageHelper.getFileMessage();
            for (QDMessage message : messageList) {
                mFileList.add(initFileFromMessage(message));
            }
            mFileList = QDSortUtil.sorSelfFile(mFileList);
            for (QDFile mFile : mFileList) {
                if (isDoc(mFile)) {
                    docFileList.add(mFile);
                } else if (isPic(mFile)) {
                    imgFileList.add(mFile);
                } else if (isAV(mFile)) {
                    videoFileList.add(mFile);
                } else {
                    otherFileList.add(mFile);
                }
            }
            handler.sendEmptyMessage(0);
        }
    };

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        viewSearch.setVisibility(View.GONE);
        tabTitles = new String[]{strAll, strDocument, strPic, strVideo, strOther};
        mFileList = new ArrayList<>();
        docFileList = new ArrayList<>();
        imgFileList = new ArrayList<>();
        videoFileList = new ArrayList<>();
        otherFileList = new ArrayList<>();
        getWarningDailog().show();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                getWarningDailog().dismiss();
                pagerAdapter = new QDSelfFilePagerAdapter(context, tabTitles, mFileList, docFileList, imgFileList, videoFileList, otherFileList);
                viewPager.setAdapter(pagerAdapter);
                tlTabLayout.setupWithViewPager(viewPager);
            }
        };
        handler.post(runnable);
    }

    private QDFile initFileFromApp(File file) {
        QDFile mFile = new QDFile();
        mFile.setPath(file.getPath());
        mFile.setDate(file.lastModified());
        mFile.setInfo(strFromApp);
        mFile.setSize(QDUtil.changFileSizeToString(file.length()));
        mFile.setName(file.getName());
        return mFile;
    }

    private QDFile initFileFromMessage(QDMessage message) {
        QDFile mFile = new QDFile();
        mFile.setPath(message.getFilePath());
        String content = message.getContent();
        IMBTFFile imbtfFile = IMBTFFile.fromBTFXml(content);
        mFile.setSize(QDUtil.changFileSizeToString(imbtfFile.getSize()));
        String info;
        String senderId = message.getSenderId();
        if (senderId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
            if (TextUtils.isEmpty(message.getGroupId())) {
                info = strFrom + message.getReceiverName();
            } else {
                info = strFromGroup + "(" + message.getSenderName() + ")";
            }
        } else {
            info = strFrom + message.getSenderName();
        }
        mFile.setInfo(info);
        mFile.setUrl(imbtfFile.getFsHost() + imbtfFile.getOriginal());
        mFile.setDate(message.getCreateDate() / 1000);
        mFile.setName(imbtfFile.getName());
        return mFile;
    }

    private boolean isDoc(QDFile file) {
        String name = file.getName();
        if (name.contains(".")) {
            String suffix = name.substring(name.lastIndexOf("."));
            List docList = Arrays.asList(TYPE_DOC);
            if (docList.contains(suffix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPic(QDFile file) {
        String name = file.getName();
        if (name.contains(".")) {
            String suffix = name.substring(name.lastIndexOf("."));
            List docList = Arrays.asList(TYPE_PIC);
            if (docList.contains(suffix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAV(QDFile file) {
        String name = file.getName();
        if (name.contains(".")) {
            String suffix = name.substring(name.lastIndexOf("."));
            List docList = Arrays.asList(TYPE_AV);
            if (docList.contains(suffix)) {
                return true;
            }
        }
        return false;
    }
}
