package com.qd.longchat.cloud.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDDownloadInfo;
import com.longchat.base.dao.QDUploadInfo;
import com.longchat.base.databases.QDDownloadInfoHelper;
import com.longchat.base.databases.QDUploadInfoHelper;
import com.longchat.base.manager.QDCloudFileManager;
import com.longchat.base.manager.QDCloudManager;
import com.longchat.base.model.gd.QDCloudBean;
import com.longchat.base.model.gd.QDCloudFile;
import com.longchat.base.model.gd.QDCloudFolder;
import com.longchat.base.model.gd.QDPersonalCloudModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDFileActivity;
import com.qd.longchat.activity.QDModifyInfoActivity;
import com.qd.longchat.cloud.activity.QDCloudFileActivity;
import com.qd.longchat.cloud.activity.QDCloudMainActivity;
import com.qd.longchat.cloud.activity.QDCloudSearchActivity;
import com.qd.longchat.cloud.adapter.QDCloudAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.fragment.QDBaseFragment;
import com.qd.longchat.model.QDCloud;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static android.app.Activity.RESULT_OK;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/3 上午10:59
 */
public class QDCloudDetailFragment extends QDBaseFragment {

    private final static int REQUEST_FOR_CREATE_FOLDER = 1000;
    private final static int REQUEST_FOR_SELECT_FILE = 1001;
    private final static int REQUEST_FOR_RENAME = 1002;
    private final static int REQUEST_FOR_FILE = 1003;
    private final static int REQUEST_FOR_SEARCH = 1004;

    private final static int SORT_SIZE_UP = 3;
    private final static int SORT_SIZE_DOWN = 2;
    private final static int SORT_TIME_UP = 1;
    private final static int SORT_TIME_DOWN = 0;

    @BindView(R2.id.view_cloud_title)
    View viewTitle;
    @BindView(R2.id.lv_cloud_list)
    ListView lvList;

    @BindString(R2.string.cloud_create_folder)
    String strCreateFolder;
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
    @BindString(R2.string.cloud_fetch_person_error)
    String strFetchPersonError;
    @BindString(R2.string.cloud_fetch_company_error)
    String strFetchCompanyError;
    @BindString(R2.string.cloud_no_permission_del)
    String strNoPerDel;
    @BindString(R2.string.cloud_no_permission_download)
    String strNoPerDownload;
    @BindString(R2.string.cloud_no_permission_upload)
    String strNoPerUpload;
    @BindString(R2.string.cloud_no_permission_modify)
    String strNoPerModify;
    @BindString(R2.string.cloud_no_permission_create)
    String strNoPerCreate;
    @BindString(R2.string.cloud_file_exist)
    String strFileExist;
    @BindString(R2.string.cloud_pc_upload)
    String strPcUpload;
    @BindString(R2.string.cloud_create_folder_error)
    String strCreateFolderError;

    private String titleName;
    private int cloudType;
    private String rootId;
    private String folderId;
    private int power;
    private List<QDCloud> cloudList;
    private QDAlertView alertView;
    private QDCloud seletedCloud;
    private String renameString;
    private int sortIndex;
    private QDCloudAdapter adapter;


    private QDResultCallBack<QDPersonalCloudModel> personListener = new QDResultCallBack<QDPersonalCloudModel>() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, strFetchPersonError + errorMsg);
        }

        @Override
        public void onSuccess(QDPersonalCloudModel model) {
            cloudList.clear();
            if (TextUtils.isEmpty(rootId))
                rootId = model.getRoot().getId();
            List<QDCloudFolder> folderList = model.getFolders();
            List<QDCloudFile> fileList = model.getFiles();
            if (folderList != null && folderList.size() != 0) {
                for (QDCloudFolder folder : folderList) {
                    cloudList.add(initFolder(folder));
                }
            }
            if (fileList != null && fileList.size() != 0) {
                for (QDCloudFile file : fileList) {
                    cloudList.add(initFile(file));
                }
            }
            adapter.setCloudList(cloudList);
            sortFile();
        }
    };

    private QDResultCallBack<ArrayList<QDCloudBean>> companyListener = new QDResultCallBack<ArrayList<QDCloudBean>>() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, strFetchCompanyError);
        }

        @Override
        public void onSuccess(ArrayList<QDCloudBean> beans) {
            for (QDCloudBean bean : beans) {
                cloudList.add(initCloud(bean));
            }
            adapter.setCloudList(cloudList);
        }
    };

    private QDCloudAdapter.OnMoreClickListener moreClickListener = new QDCloudAdapter.OnMoreClickListener() {
        @Override
        public void onMoreClick(QDCloud cloud, int type) {
            seletedCloud = cloud;
            alertView.setTitle(cloud.getName());
            if (type == QDCloud.TYPE_FOLDER) {
                alertView.setSelectList(strRename, strDel);
            } else {
                alertView.setSelectList(strRename, strDownload, strDel);
            }
            alertView.show();
        }
    };

    private QDAlertView.OnStringItemClickListener stringItemClickListener = new QDAlertView.OnStringItemClickListener() {
        @Override
        public void onItemClick(String str, int position) {
            if (str.equalsIgnoreCase(strDel)) {
                if (power != 0 && (power & QDCloud.ACE_MODIFY) == 0) {
                    QDUtil.showToast(context, strNoPerDel);
                    return;
                }
                if (seletedCloud.getType() == QDCloud.TYPE_FOLDER) {
                    QDCloudManager.getInstance().deleteFolder(rootId, seletedCloud.getId(), deleteListener);
                } else {
                    QDCloudManager.getInstance().deleteFile(rootId, seletedCloud.getId(), deleteListener);
                }
            } else if (str.equalsIgnoreCase(strRename)) {
                if (power != 0 && (power & QDCloud.ACE_MODIFY) == 0) {
                    QDUtil.showToast(context, strNoPerModify);
                    return;
                }
                Intent intent = new Intent(context, QDModifyInfoActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, strRename);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_RIGHT_NAME, strComplete);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, seletedCloud.getName());
                if (seletedCloud.getType() == QDCloud.TYPE_FILE) {
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FILE, true);
                }
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SELECT_LIMIT, false);
                startActivityForResult(intent, REQUEST_FOR_RENAME);
            } else if (str.equalsIgnoreCase(strDownload)) {
                if (power != 0 && (power & QDCloud.ACE_DOWNLOAD) == 0) {
                    QDUtil.showToast(context, strNoPerDownload);
                    return;
                }
                String path = QDStorePath.CLOUD_PATH + seletedCloud.getName();
                File file = new File(path);
                if (!file.exists()) {
                    QDDownloadInfo info = getDownloadInfo(seletedCloud, path);
                    QDUtil.showToast(context, context.getResources().getString(R.string.cloud_add_download_list));
                    QDDownloadInfoHelper.insert(info);
                    QDCloudFileManager.downloadFile(info);
                } else {
                    if (file.length() == seletedCloud.getSize()) {
                        QDUtil.showToast(context, context.getResources().getString(R.string.cloud_downloaded_open));
                    } else {
                        QDDownloadInfo info = QDDownloadInfoHelper.getInfoById(seletedCloud.getMd5());
                        if (info == null) {
                            file.delete();
                            info = getDownloadInfo(seletedCloud, path);
                            QDUtil.showToast(context, context.getResources().getString(R.string.cloud_add_download_list));
                            QDDownloadInfoHelper.insert(info);
                            QDCloudFileManager.downloadFile(info);
                        } else {
                            int state = info.getState();
                            if (state == QDDownloadInfo.TYPE_COMPLETE) {
                                QDUtil.showToast(context, context.getResources().getString(R.string.cloud_downloaded_open));
                            } else if (state == QDDownloadInfo.TYPE_PAUSE) {
                                QDUtil.showToast(context, context.getResources().getString(R.string.cloud_add_download_list));
                                QDCloudFileManager.downloadFile(info);
                            } else {
                                QDUtil.showToast(context, context.getResources().getString(R.string.str_downloading));
                            }
                        }
                    }
                }
            }
        }
    };

    private QDDownloadInfo getDownloadInfo(QDCloud cloud, String path) {
        QDDownloadInfo info = new QDDownloadInfo();
        info.setMd5(cloud.getMd5());
        info.setReadLength(0);
        info.setUrl(QDUtil.getWebFileServer() + cloud.getOriginalUrl());
        info.setTotalLength(cloud.getSize());
        info.setPath(path);
        info.setState(QDDownloadInfo.TYPE_DOWNLOAD);
        info.setName(cloud.getName());
        info.setCreateDate(System.currentTimeMillis());
        return info;
    }

    private QDResultCallBack renameListener = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, context.getResources().getString(R.string.cloud_rename_error) + errorMsg);
        }

        @Override
        public void onSuccess(Object o) {
            int index = adapter.getCloudList().indexOf(seletedCloud);
            seletedCloud.setName(renameString);
            adapter.getCloudList().remove(index);
            adapter.getCloudList().add(index, seletedCloud);
            adapter.notifyDataSetChanged();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        titleName = ((QDCloudMainActivity) context).getTitleName();
        cloudType = ((QDCloudMainActivity) context).getCloudType();
        rootId = ((QDCloudMainActivity) context).getRootId();
        folderId = ((QDCloudMainActivity) context).getFolderId();
        power = ((QDCloudMainActivity) context).getPower();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnItemClick(R2.id.lv_cloud_list)
    public void onItemClick(int position) {
        QDCloud cloud = adapter.getItem(position);
        cloudType = cloud.getType();
        switch (cloudType) {
            case QDCloud.TYPE_FOLDER:
            case QDCloud.TYPE_CLOUD:
                Intent intent = new Intent(context, QDCloudMainActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, cloud.getName());
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_TYPE, cloudType);
                if (cloudType == QDCloud.TYPE_FOLDER) {
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_FOLDER_ID, cloud.getId());
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_ROOT_ID, rootId);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_POWER, power);
                } else {
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_ROOT_ID, cloud.getId());
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_POWER, cloud.getPower());
                }
                startActivity(intent);
                break;
            case QDCloud.TYPE_FILE:
                if (cloud.getStatus() == QDCloud.STATUS_UPLOAD) {
                    cloud.setStatus(QDCloud.STATUS_PAUSE_UPLOAD);
                    QDCloudFileManager.setUploadStatusPause(cloud.getMd5());
                    adapter.getCloudList().remove(position);
                    adapter.getCloudList().add(position, cloud);
                    adapter.notifyDataSetChanged();
                } else if (cloud.getStatus() == QDCloud.STATUS_PAUSE_UPLOAD) {
                    cloud.setStatus(QDCloud.STATUS_UPLOAD);
                    QDUploadInfoHelper.updateState(cloud.getMd5(), QDUploadInfo.TYPE_UPLOAD);
                    adapter.getCloudList().remove(position);
                    adapter.getCloudList().add(position, cloud);
                    adapter.notifyDataSetChanged();
                } else {
                    QDDownloadInfo info = QDDownloadInfoHelper.getInfoById(cloud.getMd5());
                    String path = QDStorePath.CLOUD_PATH + cloud.getName();
                    File file = new File(path);
                    if (!file.exists()) {
                        if (power != 0 && (power & QDCloud.ACE_DOWNLOAD) == 0) {
                            QDUtil.showToast(context, strNoPerDownload);
                            return;
                        }
                        Intent i = new Intent(context, QDCloudFileActivity.class);
                        i.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD, cloud);
                        startActivity(i);
                    } else {
                        if (file.length() == cloud.getSize()) {
                            QDUtil.openFile(context, path);
                        } else {
                            if (info == null) {
                                if (power != 0 && (power & QDCloud.ACE_DOWNLOAD) == 0) {
                                    QDUtil.showToast(context, strNoPerDownload);
                                    return;
                                }
                                file.delete();
                                Intent i = new Intent(context, QDCloudFileActivity.class);
                                i.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD, cloud);
                                startActivity(i);
                            } else {
                                int state = info.getState();
                                if (state == QDDownloadInfo.TYPE_COMPLETE) {
                                    QDUtil.openFile(context, path);
                                } else {
                                    if (power != 0 && (power & QDCloud.ACE_DOWNLOAD) == 0) {
                                        QDUtil.showToast(context, strNoPerDownload);
                                        return;
                                    }
                                    Intent i = new Intent(context, QDCloudFileActivity.class);
                                    i.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD, cloud);
                                    i.putExtra(QDIntentKeyUtil.INTENT_DOWNLOAD_INFO, info);
                                    startActivity(i);
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    @OnClick({R2.id.tv_title_right, R2.id.tv_title_fun, R2.id.view_cloud_search})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_title_right) {
            showPopupWindow(tvTitleRight);

        } else if (i == R.id.tv_title_fun) {
            showSortPopupWindow(tvTitleFun);

        } else if (i == R.id.view_cloud_search) {
            Intent intent = new Intent(context, QDCloudSearchActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_ROOT_ID, rootId);
            startActivityForResult(intent, REQUEST_FOR_SEARCH);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTitleView(viewTitle);
        tvTitleName.setText(titleName);
        cloudList = new ArrayList<>();
        if (cloudType == QDCloud.TYPE_COMPANY_CLOUD) {
            tvTitleRight.setVisibility(View.GONE);
            tvTitleFun.setVisibility(View.GONE);
        } else {
            tvTitleRight.setVisibility(View.VISIBLE);
            tvTitleRight.setBackgroundResource(R.drawable.ic_add);
            tvTitleFun.setVisibility(View.VISIBLE);
            tvTitleFun.setBackgroundResource(R.drawable.ic_sort);
        }
        getData();
        adapter = new QDCloudAdapter(getContext());
        adapter.setListener(moreClickListener);
        lvList.setAdapter(adapter);
        adapter.setCloudList(cloudList);
        initAlert();

    }

    private void getData() {
        switch (cloudType) {
            case QDCloud.TYPE_PERSON_CLOUD:
                QDCloudManager.getInstance().getPersonalCloud(personListener);
                break;
            case QDCloud.TYPE_COMPANY_CLOUD:
                QDCloudManager.getInstance().getCompanyCloud(companyListener);
                break;
            case QDCloud.TYPE_FOLDER:
                QDCloudManager.getInstance().previewFolder(rootId, folderId, personListener);
                break;
            case QDCloud.TYPE_CLOUD:
                QDCloudManager.getInstance().previewCloudDetail(rootId, personListener);
                break;
        }
    }

    private QDCloud initFolder(QDCloudFolder bean) {
        QDCloud cloud = new QDCloud();
        cloud.setId(bean.getId());
        cloud.setName(bean.getName());
        cloud.setRootId(bean.getRootId());
        cloud.setPid(bean.getPid());
        cloud.setCreaterId(bean.getCreateUid());
        cloud.setCreaterName(bean.getCreateUname());
        cloud.setType(QDCloud.TYPE_FOLDER);
        return cloud;
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

    private QDCloud initCloud(QDCloudBean bean) {
        QDCloud cloud = new QDCloud();
        cloud.setId(bean.getId());
        cloud.setType(QDCloud.TYPE_CLOUD);
        cloud.setName(bean.getName());
        cloud.setCreaterId(bean.getCreateUid());
        cloud.setCreaterName(bean.getCreateUname());
        cloud.setCreateTime(bean.getCreateTime());
        cloud.setPower(bean.getPower());
        return cloud;
    }

    private void sortFile() {
        switch (sortIndex) {
            case SORT_TIME_DOWN:
                adapter.setCloudList(QDSortUtil.sortCloudByTimeDown(adapter.getCloudList()));
                break;
            case SORT_TIME_UP:
                adapter.setCloudList(QDSortUtil.sortCloudByTimeUp(adapter.getCloudList()));
                break;
            case SORT_SIZE_DOWN:
                adapter.setCloudList(QDSortUtil.sortCloudBySizeDown(adapter.getCloudList()));
                break;
            case SORT_SIZE_UP:
                adapter.setCloudList(QDSortUtil.sortCloudBySizeUp(adapter.getCloudList()));
                break;
        }
    }

    private void initAlert() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Center)
                .setOnStringItemClickListener(stringItemClickListener)
                .setDismissOutside(true)
                .build();
    }

    private void showPopupWindow(View view) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.im_popup_menu_cloud_add, null);
        LinearLayout viewUpload = contentView.findViewById(R.id.lin_popup_cloud_upload_file);
        LinearLayout viewCreate = contentView.findViewById(R.id.lin_popup_cloud_create_folder);
        final PopupWindow recentPopup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        viewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (power != 0 && (power & QDCloud.ACE_CREATE) == 0) {
                    QDUtil.showToast(context, strNoPerUpload);
                    recentPopup.dismiss();
                    return;
                }
                for (QDCloud cloud : cloudList) {
                    if (cloud.getStatus() == QDCloud.STATUS_UPLOAD) {
                        QDUtil.showToast(context, context.getResources().getString(R.string.cloud_have_upload_task));
                        return;
                    }
                }
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(Intent.createChooser(intent, context.getResources().getString(R.string.select_file)), REQUEST_FOR_SELECT_FILE);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    QDUtil.showToast(context, context.getResources().getString(R.string.file_management_application_not_found));
//                }

                Intent intent = new Intent(context, QDFileActivity.class);
                startActivityForResult(intent, REQUEST_FOR_SELECT_FILE);
                recentPopup.dismiss();
            }
        });

        viewCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (power != 0 && (power & QDCloud.ACE_CREATE) == 0) {
                    QDUtil.showToast(context, strNoPerCreate);
                    recentPopup.dismiss();
                    return;
                }
                Intent intent = new Intent(context, QDModifyInfoActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, strCreateFolder);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_RIGHT_NAME, strComplete);
                startActivityForResult(intent, REQUEST_FOR_CREATE_FOLDER);
                recentPopup.dismiss();
            }
        });

        recentPopup.setTouchable(true);
        recentPopup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        recentPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        recentPopup.setBackgroundDrawable(new BitmapDrawable());
        recentPopup.showAsDropDown(view);
        backgroundAlpha(0.8f);
    }

    private void showSortPopupWindow(View view) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.im_popup_menu_cloud_sort, null);
        LinearLayout viewTimeSortDesc = contentView.findViewById(R.id.lin_popup_time_sort_desc);
        LinearLayout viewTimeSortAsc = contentView.findViewById(R.id.lin_popup_time_sort_asc);
        LinearLayout viewSizeSortDesc = contentView.findViewById(R.id.lin_popup_size_sort_desc);
        LinearLayout viewSizeSortAsc = contentView.findViewById(R.id.lin_popup_size_sort_asc);
        final ImageView ivTimeSortDesc = contentView.findViewById(R.id.iv_item_time_sort_desc);
        final ImageView ivTimeSortAsc = contentView.findViewById(R.id.iv_item_time_sort_asc);
        final ImageView ivSizeSortDesc = contentView.findViewById(R.id.iv_item_size_sort_desc);
        final ImageView ivSizeSortAsc = contentView.findViewById(R.id.iv_item_size_sort_asc);

        final PopupWindow recentPopup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        switch (sortIndex) {
            case SORT_TIME_DOWN:
                ivTimeSortDesc.setVisibility(View.VISIBLE);
                break;
            case SORT_TIME_UP:
                ivTimeSortAsc.setVisibility(View.VISIBLE);
                break;
            case SORT_SIZE_DOWN:
                ivSizeSortDesc.setVisibility(View.VISIBLE);
                break;
            case SORT_SIZE_UP:
                ivSizeSortAsc.setVisibility(View.VISIBLE);
                break;
        }

        viewTimeSortDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivTimeSortDesc.getVisibility() != View.VISIBLE) {
                    invisibleImage(ivTimeSortDesc, ivTimeSortAsc, ivSizeSortDesc, ivSizeSortAsc);
                    ivTimeSortDesc.setVisibility(View.VISIBLE);
                    sortIndex = SORT_TIME_DOWN;
                    sortFile();
                    recentPopup.dismiss();
                }
            }
        });

        viewTimeSortAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivTimeSortAsc.getVisibility() != View.VISIBLE) {
                    invisibleImage(ivTimeSortDesc, ivTimeSortAsc, ivSizeSortDesc, ivSizeSortAsc);
                    ivTimeSortAsc.setVisibility(View.VISIBLE);
                    sortIndex = SORT_TIME_UP;
                    sortFile();
                    recentPopup.dismiss();
                }
            }
        });

        viewSizeSortDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivSizeSortDesc.getVisibility() != View.VISIBLE) {
                    invisibleImage(ivTimeSortDesc, ivTimeSortAsc, ivSizeSortDesc, ivSizeSortAsc);
                    ivSizeSortDesc.setVisibility(View.VISIBLE);
                    sortIndex = SORT_SIZE_DOWN;
                    sortFile();
                    recentPopup.dismiss();
                }
            }
        });

        viewSizeSortAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivSizeSortAsc.getVisibility() != View.VISIBLE) {
                    invisibleImage(ivTimeSortDesc, ivTimeSortAsc, ivSizeSortDesc, ivSizeSortAsc);
                    ivSizeSortAsc.setVisibility(View.VISIBLE);
                    sortIndex = SORT_SIZE_UP;
                    sortFile();
                    recentPopup.dismiss();
                }
            }
        });
        recentPopup.setTouchable(true);
        recentPopup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        recentPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        recentPopup.setBackgroundDrawable(new BitmapDrawable());
        recentPopup.showAsDropDown(view);
        backgroundAlpha(0.8f);
    }

    private void invisibleImage(ImageView imageView1, ImageView imageView2, ImageView imageView3, ImageView imageView4) {
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float alpha) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FOR_CREATE_FOLDER) {
                String name = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO);
                QDCloudManager.getInstance().createFolder(rootId, name, folderId, new QDResultCallBack<QDCloudFolder>() {
                    @Override
                    public void onError(String errorMsg) {
                        QDUtil.showToast(context, strCreateFolderError + errorMsg);
                    }

                    @Override
                    public void onSuccess(QDCloudFolder folder) {
                        adapter.getCloudList().add(0, initFolder(folder));
                        adapter.notifyDataSetChanged();
                    }
                });
            } else if (requestCode == REQUEST_FOR_SELECT_FILE) {
                File file = (File) data.getSerializableExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_FILE);
                String path = file.getPath();
                if (!path.contains(".")) {
                    QDUtil.showToast(context, context.getResources().getString(R.string.file_must_contain_suffixes));
                    return;
                }
                if (file.length() == 0) {
                    QDUtil.showToast(context, context.getResources().getString(R.string.file_length_is_zero));
                    return;
                }
                int size = (int) (file.length() / (1024 * 1024));
                if (size >= 50) {
                    QDUtil.showToast(context, strPcUpload);
                    return;
                }

                try {
//                    String path;
//                    if (Build.VERSION.SDK_INT >= 19) {
//                        path = QDUtil.handleImageOnKitKat(context, data);
//                    } else {
//                        path = QDUtil.handleImageBeforeKitKat(context, data);
//                    }
//                    File file = new File(path);
//                    if (file.length() == 0) {
//                        QDUtil.showToast(context, context.getResources().getString(R.string.file_length_is_zero));
//                        return;
//                    }
//
//                    int size = (int) (file.length() / (1024 * 1024));
//                    if (size >= 50) {
//                        QDUtil.showToast(context, strPcUpload);
//                        return;
//                    }
//
                    String fileName = file.getName();
//                    if (!fileName.contains(".")) {
//                        QDUtil.showToast(context, context.getResources().getString(R.string.file_must_contain_suffixes));
//                        return;
//                    }
                    int index = fileName.lastIndexOf(".");
                    QDCloud cloud = new QDCloud();
                    cloud.setCreaterName(QDLanderInfo.getInstance().getName());
                    cloud.setCreaterId(QDLanderInfo.getInstance().getId());
                    cloud.setRootId(rootId);
                    cloud.setMd5(QDUtil.getFileMd5(file));
                    cloud.setName(fileName);
                    cloud.setExt(fileName.substring(index + 1));
                    cloud.setPath(file.getPath());
                    cloud.setType(QDCloud.TYPE_FILE);
                    cloud.setStatus(QDCloud.STATUS_UPLOAD);
                    cloud.setSize(file.length());
                    cloud.setPid(folderId);
                    cloud.setCreateTime(System.currentTimeMillis());
                    checkFileIsInRemote(cloud, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_FOR_RENAME) {
                renameString = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO);
                if (seletedCloud.getType() == QDCloud.TYPE_FOLDER) {
                    QDCloudManager.getInstance().renameFolder(renameString, rootId, seletedCloud.getId(), folderId, renameListener);
                } else {
                    QDCloudManager.getInstance().renameFile(renameString, rootId, seletedCloud.getId(), folderId, renameListener);
                }
            } else if (requestCode == REQUEST_FOR_SEARCH) {
                getData();
            }
        }
    }

    /**
     * 判断服务器是否存在改文件
     * @param cloud
     * @param file
     */
    private void checkFileIsInRemote(final QDCloud cloud, final File file) {
        QDCloudManager.getInstance().checkFileIsInRemote(rootId, cloud.getName(), cloud.getSize(), cloud.getMd5(), folderId, new QDResultCallBack<QDCloudFile>() {

            @Override
            public void onError(String errorMsg) {
                if (errorMsg.equalsIgnoreCase("1018111")) {
                    QDUtil.showToast(context, strFileExist);
                } else {
                    adapter.getCloudList().add(0, cloud);
                    sortFile();
                }
            }

            @Override
            public void onSuccess(QDCloudFile cloudFile) {
                cloud.setId(cloudFile.getId());
                cloud.setOriginalUrl(cloudFile.getUrlOriginal());
                cloud.setStatus(QDCloud.STATUS_NORMAL);
                cloud.setCreateTime(cloudFile.getCreateTime());
                adapter.getCloudList().add(0, cloud);
                sortFile();
            }
        });
    }


}
