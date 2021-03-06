package com.suncn.ihold_zxztc.activity.circle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIFileUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIMyIntent;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.ShowVideoActivity;
import com.suncn.ihold_zxztc.adapter.GridImageAdapter;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.CircleListBean;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.bean.TakeImgTypeListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.library_basic.util.GlideEngine;
import com.suncn.library_basic.util.PictureSelectorUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;
import okhttp3.MultipartBody;
import skin.support.content.res.SkinCompatResources;

/**
 * ???????????????
 */
public class Circle_AddDynamicActivity extends BaseActivity {
    @BindView(id = R.id.rv_img)
    private RecyclerView img_RecyclerView;//????????????RecyclerView
    @BindView(id = R.id.et_content)
    private EditText content_EditText;//??????
    @BindView(id = R.id.ll_remark)
    private LinearLayout llRemark;
    @BindView(id = R.id.et_remark)
    private EditText etRemark;

    private String headTitle;
    private ArrayList<TakeImgTypeListBean.TakeImgType> takeImgTypes;//????????????
    private String[] typeArray;//??????????????????
    private String content;//??????
    private GridImageAdapter photoAdapter;
    private CircleListBean.DynamicBean dynamicBean;
    private String strId;
    List<LocalMedia> selectList = new ArrayList<>();
    private ArrayList<String> oldImgList = new ArrayList<>();//??????????????????????????????

    public static final String ACTION_OPEN_CIRCLE_ADD = "com.suncn.ihold_zxztc.activity.circle.action.DYNAMIC_OPEN";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    prgDialog.closePrgDialog();
                    content_EditText.setText("");
                    showToast("??????????????????????????????");
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                case 2:
                case 3:
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        File file = (File) bundle.getSerializable("file");
                        if (file != null) {
                            ObjFileBean objFile = new ObjFileBean();
                            objFile.setStrFile_url(file.getPath());
                            objFile.setStrFile_Type(GIMyIntent.getMIMEType(file));
                            objFile.setStrFile_name(GIFileUtil.getFileName(file.getPath()));
                        }
                    } else {
                        showToast("???????????????????????????");
                    }
                    break;
                case PictureConfig.CHOOSE_REQUEST:// PictureSelector??????/????????????????????????
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // ?????? LocalMedia ??????????????????path
                    // 1.media.getPath(); ?????????path
                    // 2.media.getCutPath();????????????path????????????media.isCut();?????????true
                    // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true
                    // ????????????????????????????????????????????????????????????????????????????????????
                    for (LocalMedia media : selectList) {
                        GILogUtil.i("getPath-----???" + media.getPath());
                        GILogUtil.i("getCompressPath-----???" + media.getCompressPath());
                    }
                    photoAdapter.setList(selectList);
                    photoAdapter.notifyDataSetChanged();
                    setSaveBtnState();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_circle_add_dynamic);
        isShowBackBtn = true;
        setStatusBar();
    }

    @Override
    public void initData() {
        super.initData();
        //?????????????????????????????????????????????
        setHeadTitle("");
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        img_RecyclerView.setLayoutManager(manager);
        //??????????????????Adapter???onAddPicListener????????????????????????????????????onPicClickListener?????????????????????????????????????????????????????????
        photoAdapter = new GridImageAdapter(activity, onAddPicListener);
        //??????????????????????????????????????????
        photoAdapter.setSelectMax(9);
        img_RecyclerView.setAdapter(photoAdapter);
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getResources().getString(R.string.font_send));
        goto_Button.setTextColor(getResources().getColor(R.color.font_source));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            headTitle = bundle.getString("headTitle");
            dynamicBean = (CircleListBean.DynamicBean) bundle.getSerializable("dynamicBean");
            if (dynamicBean != null && GISharedPreUtil.getInt(activity, "intUserRole") == 1
                    && !GISharedPreUtil.getString(activity, "strUserId").toLowerCase().equals(dynamicBean.getStrPubUserId().toLowerCase())) {
                llRemark.setVisibility(View.VISIBLE);
            } else {
                llRemark.setVisibility(View.GONE);
            }
            GILogUtil.e("??????id====" + GISharedPreUtil.getString(activity, "strUserId"));
            GILogUtil.e("??????id====" + dynamicBean.getStrPubUserId());
            setOldContent();
            setHeadTitle(headTitle);
            setSaveBtnState();
        }
    }

    /**
     * ????????????????????????
     */
    private void setOldContent() {
        ArrayList<CircleListBean.DynamicBean.picBaen> picList = dynamicBean.getPicList();
        for (CircleListBean.DynamicBean.picBaen picBaen : picList) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath(Utils.formatFileUrl(activity, picBaen.getStrImagePath()));
            localMedia.setCutPath(Utils.formatFileUrl(activity, picBaen.getStrImagePath()));
            localMedia.setCompressPath(Utils.formatFileUrl(activity, picBaen.getStrImagePath()));
            localMedia.setMimeType("image");
            localMedia.setChooseModel(PictureMimeType.ofImage());
            selectList.add(localMedia);
            oldImgList.add(Utils.formatFileUrl(activity, picBaen.getStrImagePath()));
        }
        photoAdapter.setList(selectList);
        photoAdapter.notifyDataSetChanged();
        content_EditText.setText(dynamicBean.getStrContent());
        strId = dynamicBean.getStrId();
    }

    @Override
    public void setListener() {
        super.setListener();
        photoAdapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    switch (media.getChooseModel()) {
                        case PictureConfig.TYPE_IMAGE:
                            // ???????????? ???????????????????????????
//                            PictureSelector.create(activity).externalPicturePreview(position, "/" + GIFileCst.DIR_APP + "/DownloadImage", selectList, 0);
                            PictureSelector.create(activity)
                                    .themeStyle(R.style.picture_Sina_style) // xml????????????
                                    .isNotPreviewDownload(false)// ????????????????????????????????????
                                    //.bindCustomPlayVideoCallback(callback)// ???????????????????????????????????????????????????????????????????????????
                                    .loadImageEngine(GlideEngine.createGlideEngine())// ??????????????????????????????????????????
                                    .openExternalPreview(position, selectList);
                            break;
                        case PictureConfig.TYPE_VIDEO:
                            Intent intent = new Intent(activity, ShowVideoActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("url", media.getPath());
                            bundle.putString("imgPath", media.getPath());
                            intent.putExtras(bundle);
                            activity.startActivity(intent);
                            break;
                    }
                }
            }
        });
        content_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (GIStringUtil.isBlank(editable.toString())) {
                    setSaveBtnState();
                } else {
                    goto_Button.setEnabled(true);
                    goto_Button.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                }
            }
        });
        etRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (GIStringUtil.isBlank(s.toString())) {
                    setSaveBtnState();
                } else {
                    goto_Button.setEnabled(true);
                    goto_Button.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }


    private AlertView alertView1;
    private AlertView alertView2;

    private final GridImageAdapter.onAddPicClickListener onAddPicListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            List<PermissionItem> permissionItems = new ArrayList<>();
            permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "??????", R.drawable.permission_ic_storage));
            permissionItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "??????", R.drawable.permission_ic_storage));
            permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "??????", R.drawable.permission_ic_camera));
            HiPermission.create(activity)
                    .permissions(permissionItems)
                    .style(R.style.PermissionBlueStyle)
                    .checkMutiPermission(new PermissionCallback() {
                        @Override
                        public void onClose() {
                            GILogUtil.i("onClose" + "1");
                        }

                        @Override
                        public void onFinish() {
                            GIUtil.closeSoftInput(activity);
                            alertView1 = new AlertView(null, null, "??????", null,
                                    //new String[]{"??????", "??????", "??????"},
                                    new String[]{"???????????????", "??????"},
                                    activity, AlertView.Style.ActionSheet, new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    //??????TakePhoto????????????
                                    switch (position) {
                                        case 1:  //??????
                                            alertView1.dismissImmediately();
                                            alertView2 = new AlertView(null, null, "??????", null,
                                                    //new String[]{"??????", "??????", "??????"},
                                                    new String[]{"??????", "??????"},
                                                    activity, AlertView.Style.ActionSheet, new OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Object o, int position) {
                                                    //??????TakePhoto????????????
                                                    switch (position) {
                                                        case 0:
                                                            if (photoAdapter.getList().size() == 1 && photoAdapter.getList().get(0).getChooseModel() == PictureMimeType.ofVideo()) {
                                                                showToast("?????????????????????????????????");
                                                                return;
                                                            }
                                                            photoAdapter.setSelectMax(9);
                                                            PictureSelectorUtil.commonActionCamera(activity, PictureMimeType.ofImage(), selectList, 9, R.style.picture_Sina_style);
                                                            break;
                                                        case 1:
                                                            if (photoAdapter.getList().size() > 0 && photoAdapter.getList().get(0).getChooseModel() == PictureMimeType.ofImage()) {
                                                                showToast("?????????????????????????????????");
                                                                return;
                                                            }
                                                            photoAdapter.setSelectMax(1);
                                                            PictureSelectorUtil.commonActionCamera(activity, PictureMimeType.ofVideo(), selectList, 1, R.style.picture_Sina_style);
                                                            break;
                                                    }
                                                }
                                            });
                                            alertView2.show();
                                            break;
                                        case 0:  //????????????
                                            alertView1.dismissImmediately();
                                            alertView2 = new AlertView(null, null, "??????", null,
                                                    //new String[]{"??????", "??????", "??????"},
                                                    new String[]{"??????", "??????"},
                                                    activity, AlertView.Style.ActionSheet, new OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Object o, int position) {
                                                    //??????TakePhoto????????????
                                                    switch (position) {
                                                        case 0:
                                                            if (photoAdapter.getList().size() == 1 && photoAdapter.getList().get(0).getChooseModel() == PictureMimeType.ofVideo()) {
                                                                showToast("?????????????????????????????????");
                                                                return;
                                                            }
                                                            photoAdapter.setSelectMax(9);
                                                            PictureSelectorUtil.commonActionPhoto(activity, PictureMimeType.ofImage(), selectList, 9, SkinCompatResources.getInstance().isDefaultSkin() ? R.style.picture_Sina_style : R.style.picture_Sina_styleBlue);
                                                            break;
                                                        case 1:
                                                            if (photoAdapter.getList().size() > 0 && photoAdapter.getList().get(0).getChooseModel() == PictureMimeType.ofImage()) {
                                                                showToast("?????????????????????????????????");
                                                                return;
                                                            }
                                                            photoAdapter.setSelectMax(1);
                                                            PictureSelectorUtil.commonActionPhoto(activity, PictureMimeType.ofVideo(), selectList, 1, R.style.picture_Sina_style);
                                                            break;
                                                    }
                                                }
                                            });
                                            alertView2.show();
                                            break;
                                    }
                                }
                            });
                            alertView1.show();
                        }

                        @Override
                        public void onDeny(String permission, int position) {
                            GILogUtil.i("onDeny" + "1");
                        }

                        @Override
                        public void onGuarantee(String permission, int position) {
                        }
                    });
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                content = content_EditText.getText().toString().trim();
                if (GIStringUtil.isBlank(content)
                        && (photoAdapter.getList() == null || photoAdapter.getList().size() == 0)) {
                    showToast("?????????????????????");
                } else if (llRemark.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(etRemark.getText().toString())) {
                    showToast("?????????????????????");
                } else {
                    submitTakeImg();
                }
                break;
            default:
                break;
        }
    }


    /**
     * ????????????
     */
    private void submitTakeImg() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<>();
        try {
            textParamMap.put("strContent", URLEncoder.encode(content,"utf-8"));//??????
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String strFileIds = "";
        List<File> fileParamList = new ArrayList<>();
        if (photoAdapter != null) {
            selectList = photoAdapter.getList();
            for (int i = 0; i < selectList.size(); i++) {
                File file = null;
                if (selectList.get(i).getChooseModel() == PictureMimeType.ofVideo()) {
                    file = new File(selectList.get(i).getPath());
                    if (!oldImgList.contains(selectList.get(i).getPath())) {
                        fileParamList.add(file);
                    }
                } else {
                    file = new File(selectList.get(i).getCompressPath());
                    if (!oldImgList.contains(selectList.get(i).getCompressPath())) {
                        fileParamList.add(file);
                    }
                }

            }
        }
        List<MultipartBody.Part> partList = Utils.filesToMultipartBodyParts(fileParamList);
        if (GIStringUtil.isNotBlank(strId)) {
            for (int i = 0; i < oldImgList.size(); i++) {
                for (LocalMedia localMedia : selectList) {
                    if (localMedia.getPath().equals(oldImgList.get(i))) {
                        strFileIds = strFileIds + dynamicBean.getPicList().get(i).getStrFileId() + ",";
                    }
                }
            }
            textParamMap.put("strId", strId);
            textParamMap.put("strFileIds", strFileIds);
            if (llRemark.getVisibility() == View.VISIBLE) {
                textParamMap.put("strRemark", etRemark.getText().toString());
            }
            doRequestNormal(ApiManager.getInstance().updateDynamicServlet(partList, textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().addDynamic(partList, textParamMap), 0);
        }
    }

    private void setSaveBtnState() {
        if (photoAdapter.getList().size() > 0 || content_EditText.getText().length() > 0) {
            goto_Button.setEnabled(true);
            goto_Button.setTextColor(getResources().getColor(R.color.red));
        } else {
            goto_Button.setEnabled(false);
            goto_Button.setTextColor(getResources().getColor(R.color.font_source));
        }
    }

    /**
     * ????????????
     */
    private void doLogic(int what, Object obj) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        try {
            switch (what) {
                case 0:
                    prgDialog.closePrgDialog();
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                    toastMessage = "???????????????";
                    DefineUtil.isNeedRefresh = true;
                    PictureFileUtils.deleteAllCacheDirFile(activity); // ??????PictureSelector??????(??????????????????????????????????????????????????????????????????)
                    finish();
                    break;
                case 1:
                    prgDialog.closePrgDialog();
                    TakeImgTypeListBean takeImgTypeListBean = (TakeImgTypeListBean) obj;
                    if (takeImgTypeListBean.getStrRlt().equals("true")) {
                        takeImgTypes = takeImgTypeListBean.getObjList();
                        if (takeImgTypes != null && takeImgTypes.size() > 0) {
                            typeArray = new String[takeImgTypes.size()];
                            for (int i = 0; i < takeImgTypes.size(); i++) {
                                typeArray[i] = takeImgTypes.get(i).getStrName();
                            }
                        } else {
                            showToast("????????????");
                        }
                    } else {
                        toastMessage = takeImgTypeListBean.getStrError();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }
}
