package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.model.gd.QDFileModel;
import com.longchat.base.model.gd.QDGroupFileModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDGroupFileAdapter;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.io.File;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/2 下午2:34
 */
public class QDGroupFileActivity extends QDBaseActivity {

    private final static int REQUEST_SELECT_FILE = 1000;

    @BindView(R2.id.view_gf_title)
    View viewTitle;
    @BindView(R2.id.lv_gf_list)
    SwipeMenuListView lvList;

    @BindString(R2.string.group_info_file)
    String strTitle;
    @BindColor(R2.color.colorSwipeMenuDelete)
    int colorMenuDel;
    @BindString(R2.string.conversation_del)
    String strDel;
    @BindString(R2.string.str_upload)
    String strUpload;
    @BindString(R2.string.file_loading)
    String strLoading;
    @BindString(R2.string.str_opening)
    String strOpening;
    @BindString(R2.string.get_group_file_error)
    String strGetError;
    @BindView(R2.id.view_place)
    View viewPlace;
    private String groupId;
    private QDGroupFileAdapter adapter;
    private List<QDGroupFileModel.ListBean> listBeans;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_group_file);
        ButterKnife.bind(this);
        viewPlace.setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText(strUpload);
        groupId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID);
        adapter = new QDGroupFileAdapter(context);
        lvList.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                menu.addMenuItem(QDUtil.createMenuItem(context, QDUtil.dp2px(context, 90), strDel, colorMenuDel));
            }
        };
        lvList.setMenuCreator(creator);

        QDClient.getInstance().getGroupFiles(groupId, new QDResultCallBack<List<QDGroupFileModel.ListBean>>() {
            @Override
            public void onError(String errorMsg) {
                QDUtil.showToast(context, strGetError + errorMsg);
            }

            @Override
            public void onSuccess(List<QDGroupFileModel.ListBean> listBeanList) {
                listBeans = listBeanList;
                adapter.setListBeanList(listBeanList);
            }
        });

        lvList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                QDGroupFileModel.ListBean bean = listBeans.get(position);
                if (index == 0) {
                    QDClient.getInstance().deleteGroupFile(bean.getGroupId(), bean.getFileId(), new QDResultCallBack() {

                        @Override
                        public void onError(String errorMsg) {

                        }

                        @Override
                        public void onSuccess(Object o) {
                            listBeans.remove(position);
                            adapter.setListBeanList(listBeans);
                        }
                    });
                }
                return false;
            }
        });
    }

    @OnItemClick(R2.id.lv_gf_list)
    public void onItemClick(int position) {
        QDGroupFileModel.ListBean bean = listBeans.get(position);
        String url = QDUtil.getWebFileServer() + bean.getUrlOriginal();
        int index = url.lastIndexOf("/");
        String fileName = url.substring(index + 1);
        final String path = QDStorePath.TEMP_PATH + fileName;
        File file = new File(path);
        if (!file.exists()) {
            getWarningDailog().setTip(strOpening);
            getWarningDailog().show();
            QDFileManager.getInstance().downloadFile(path, url, new QDFileDownLoadCallBack() {
                @Override
                public void onDownLoading(int per) {

                }

                @Override
                public void onDownLoadSuccess() {
                    getWarningDailog().dismiss();
                    QDUtil.openFile(context, path);
                }

                @Override
                public void onDownLoadFailed(String msg) {
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, context.getResources().getString(R.string.im_text_download_failed) + msg);
                }
            });
        } else {
            QDUtil.openFile(context, path);
        }

    }

    @OnClick(R2.id.tv_title_right)
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, context.getResources().getString(R.string.select_file)), REQUEST_SELECT_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            QDUtil.showToast(context, context.getResources().getString(R.string.file_management_application_not_found));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_SELECT_FILE) {
            getWarningDailog().setTip(strLoading);
            getWarningDailog().show();
            try {
                String path;
                if (Build.VERSION.SDK_INT >= 19) {
                    path = QDUtil.handleImageOnKitKat(context, data);
                } else {
                    path = QDUtil.handleImageBeforeKitKat(context, data);
                }
                if (!path.contains(".")) {
                    QDUtil.showToast(context, context.getResources().getString(R.string.file_must_contain_suffixes));
                    return;
                }
                File file = new File(path);
                if (file.length() == 0) {
                    QDUtil.showToast(context, context.getResources().getString(R.string.file_length_is_zero));
                    return;
                }
                QDClient.getInstance().uploadGroupFile(file, groupId, new QDFileCallBack<QDFileModel>() {
                    @Override
                    public void onUploading(String path, int per) {

                    }

                    @Override
                    public void onUploadFailed(String errorMsg) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, context.getResources().getString(R.string.file_load_failed));
                    }

                    @Override
                    public void onUploadSuccess(QDFileModel model) {
                        getWarningDailog().dismiss();
                        QDGroupFileModel.ListBean bean = new QDGroupFileModel.ListBean();
                        bean.setCreateTime(model.getCreateTime());
                        bean.setCreateUid(model.getCreaterId());
                        bean.setCreateUname(model.getCreaterName());
                        bean.setType(model.getType());
                        bean.setExt(model.getExt());
                        bean.setUrlOriginal(model.getOriginal());
                        bean.setUrlThumbpic(model.getThumbPic());
                        bean.setId(model.getId());
                        bean.setGroupId(model.getGroupId());
                        bean.setName(model.getName());
                        bean.setSize(model.getSize());
                        bean.setStatus(model.getStatus());
                        bean.setFileId(model.getFileId());
                        listBeans.add(bean);
                        adapter.setListBeanList(listBeans);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
