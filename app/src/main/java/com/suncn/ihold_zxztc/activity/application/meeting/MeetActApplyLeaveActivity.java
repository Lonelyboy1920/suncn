package com.suncn.ihold_zxztc.activity.application.meeting;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.GridImageAdapter;
import com.suncn.ihold_zxztc.bean.LeaveTypeListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.library_basic.util.PictureSelectorUtil;

import java.io.File;
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

public class MeetActApplyLeaveActivity extends BaseActivity {
    @BindView(id = R.id.ll_leave_type, click = true)
    private LinearLayout leaveType_Layout;//请假类型
    @BindView(id = R.id.tv_leave_type)
    private TextView leaveType_TextView;//请假类型TextView
    @BindView(id = R.id.et_reason)
    private EditText reason_EditText;//请假理由EditText
    @BindView(id = R.id.rv_img)
    private RecyclerView fileScan_MyGridView;//附件MyGridview

    private ArrayList<LeaveTypeListBean.LeaveTypeBean> leaveTypeBeans;
    private String[] leaveTypeArray;
    private String leaveTypeId;//请假类型
    private GridImageAdapter photoAdapter;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String strId;
    private String strAttendId;
    private int sign;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        if (resultCode == RESULT_OK) {
            // 图片选择结果回调
            selectList = PictureSelector.obtainMultipleResult(data);
            // 例如 LocalMedia 里面返回三种path
            // 1.media.getPath(); 为原图path
            // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
            // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
            // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
            for (LocalMedia media : selectList) {
                Log.i("图片-----》", media.getPath());
            }
            photoAdapter.setList(selectList);
            photoAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_meet_act_apply_leave);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        setHeadTitle("请假申请");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strId = bundle.getString("strId");
            strAttendId = bundle.getString("strAttendId");
            sign = bundle.getInt("sign", 0);
        }
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText("确定");
        goto_Button.setTextSize(16);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        fileScan_MyGridView.setLayoutManager(manager);
        //初始化自定义Adapter，onAddPicListener是添加图片的点击监听器，onPicClickListener是添加图片成功以后，点击放大的监听器。
        photoAdapter = new GridImageAdapter(activity, onAddPicListener);
        photoAdapter.setSelectMax(9);
        fileScan_MyGridView.setAdapter(photoAdapter);
    }

    private AlertView alertView;
    private final GridImageAdapter.onAddPicClickListener onAddPicListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            List<PermissionItem> permissionItems = new ArrayList<>();
            permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.string_storage), R.drawable.permission_ic_storage));
            permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, getString(R.string.string_camera), R.drawable.permission_ic_camera));
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
                            alertView = new AlertView(null, null, getString(R.string.string_cancle), null,
                                    //new String[]{"相册", "拍照", "扫描"},
                                    new String[]{getString(R.string.string_album), getString(R.string.string_take_photo)},
                                    activity, AlertView.Style.ActionSheet, new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    switch (position) {
                                        case 1:  //拍照
                                            PictureSelectorUtil.commonActionCamera(activity, PictureMimeType.ofImage(), selectList, 9, R.style.picture_Sina_style);
                                            break;
                                        case 0:  //相册选择
                                            PictureSelectorUtil.commonActionPhoto(activity, PictureMimeType.ofImage(), selectList, 9, R.style.picture_Sina_style);

                                            break;
                                    }

                                }
                            });
                            alertView.show();
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

    private void doLogic(int what, Object object) {
        prgDialog.closePrgDialog();
        String totastMessage;
        switch (what) {
            case 0:
                try {
                    LeaveTypeListBean leaveTypeListBean = (LeaveTypeListBean) object;
                    leaveTypeBeans = leaveTypeListBean.getObjList();
                    if (leaveTypeBeans != null && leaveTypeBeans.size() > 0) {
                        leaveTypeArray = new String[leaveTypeBeans.size()];
                        for (int i = 0; i < leaveTypeBeans.size(); i++) {
                            leaveTypeArray[i] = leaveTypeBeans.get(i).getStrAbsentName();
                        }
                        showChooseDialog(leaveTypeArray, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    totastMessage = getString(R.string.data_error);
                }
                break;
            case 1:
                setResult(RESULT_OK);
                finish();
                break;
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_leave_type:
                getLeaveTypeList();
                break;
            case R.id.btn_goto:
                if (GIStringUtil.isBlank(leaveTypeId)) {
                    showToast("请选择请假原因");
                    return;
                }
                submitSign();
                break;

        }
    }

    /**
     * 提交
     */
    private void submitSign() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strAttendId", strAttendId);
        List<File> fileParamList = new ArrayList<>();
        textParamMap.put("strAbsentType", leaveTypeId);//请假类型
        textParamMap.put("strReason", reason_EditText.getText().toString().trim());
        if (photoAdapter != null) {
            selectList = photoAdapter.getList();
            for (int i = 0; i < selectList.size(); i++) {
                File file = new File(selectList.get(i).getCompressPath());
                fileParamList.add(file);
            }
        }
        List<MultipartBody.Part> partList = Utils.filesToMultipartBodyParts(fileParamList);
        if (sign == DefineUtil.wdhd) {
            textParamMap.put("strEventId", strId);
            doRequestNormal(ApiManager.getInstance().EventMemApplyServlet(partList, textParamMap), 1);
        } else {
            textParamMap.put("strMeetId", strId);
            doRequestNormal(ApiManager.getInstance().MeetMemApplyServlet(partList, textParamMap), 1);

        }
    }

    /**
     * 获取请假类型列表
     */
    private void getLeaveTypeList() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<String, String>();
        doRequestNormal(ApiManager.getInstance().getLeaveTypeList(textParamMap), 0);
    }


    private void showChooseDialog(final String[] menuArray, final int i) {

        final NormalListDialog normalListDialog = new NormalListDialog(activity, menuArray);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                leaveType_TextView.setText(menuArray[position]);
                leaveTypeId = leaveTypeBeans.get(position).getStrAbsentType();
                normalListDialog.dismiss();
            }
        });

        normalListDialog.title(" " + getString(R.string.string_please_select_the_type_of_leave));
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        normalListDialog.titleTextColor(Color.WHITE);
        normalListDialog.show();
    }
}
