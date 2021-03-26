package com.suncn.ihold_zxztc.activity.application.meeting;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.GridImageAdapter;
import com.suncn.ihold_zxztc.adapter.Photo_RVAdapter;
import com.suncn.ihold_zxztc.bean.AttendStateListBean;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.LeaveTypeListBean;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MenuItemEditLayout;
import com.suncn.library_basic.util.PictureSelectorUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;
import okhttp3.MultipartBody;
import skin.support.content.res.SkinCompatResources;

/**
 * 会议活动报名
 * 修改参与状态
 */
public class MeetAct_SignUpActivity extends BaseActivity {
    @BindView(id = R.id.tv_title)
    private TextView title_TextView;//标题
    @BindView(id = R.id.tv_start_time)
    private TextView startTime_TextView;//开始时间
    @BindView(id = R.id.tv_start_date_tag)
    private TextView startDateTag_TextView;//开始时间Tag
    @BindView(id = R.id.tv_end_time)
    private TextView endTime_TextView;//结束时间
    @BindView(id = R.id.tv_end_date_tag)
    private TextView endDateTag_TextView;//结束时间Tag
    @BindView(id = R.id.tv_place)
    private TextView place_TextView;//地点
    @BindView(id = R.id.tv_place_tag)
    private TextView placeTag_TextView;//地点Tag
    @BindView(id = R.id.tv_state, click = true)
    private MenuItemEditLayout state_TextView;//出席状态
    @BindView(id = R.id.ll_leave)
    private LinearLayout leave_LinearLayout;
    @BindView(id = R.id.tv_leave_type)
    private TextView leaveType_TextView;//请假类型TextView
    @BindView(id = R.id.ll_leave_type, click = true)
    private LinearLayout leaveType_Layout;//请假类型
    @BindView(id = R.id.et_reason)
    private EditText reason_EditText;//请假理由EditText
    @BindView(id = R.id.rv_img)
    private RecyclerView fileScan_MyGridView;//附件MyGridview
    @BindView(id = R.id.ll_deduction)
    private LinearLayout llDeduction;//扣分类型（苏州）
    @BindView(id = R.id.rg_deduction)
    private RadioGroup rgDeduction;
    @BindView(id = R.id.rb_yes)
    private RadioButton rbYes;
    @BindView(id = R.id.rb_no)
    private RadioButton rbNo;
    private ArrayList<ObjFileBean> scanFileList;
    private GridImageAdapter photoAdapter; // 请假材料Adapter
    private String title;
    private String startTime;
    private String endTime;
    private String place;
    private String headTitle;
    private String[] leaveTypeArray;
    private String[] menuArray;
    private ArrayList<LeaveTypeListBean.LeaveTypeBean> leaveTypeBeans;
    private ArrayList<AttendStateListBean.AttendStateBean> attendStateBeans;
    private String leaveTypeId;//请假类型
    private String stateId="";
    private String strId;//会议、活动id
    private int sign;//会议、活动类别
    private String reason;//请假原因
    private boolean isWorker;
    private boolean isFromRemindList;
    private String strAttendId = "";//出席情况id
    private String strLeaveType = "1";
    private List<LocalMedia> selectList = new ArrayList<>();
    private ArrayList<String> oldImgList = new ArrayList<>();//编辑时已有的图片地址

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
        setStatusBar();
        setContentView(R.layout.activity_meetact_signup);
        isShowBackBtn = true;
    }

    @Override
    public void initData() {
        int intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
        goto_Button.setText(getString(R.string.string_submit));
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.refreshFontType(activity, "2");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        scanFileList = new ArrayList<>();
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        fileScan_MyGridView.setLayoutManager(manager);
        //初始化自定义Adapter，onAddPicListener是添加图片的点击监听器，onPicClickListener是添加图片成功以后，点击放大的监听器。
        photoAdapter = new GridImageAdapter(activity, onAddPicListener);
        photoAdapter.setSelectMax(9);
        fileScan_MyGridView.setAdapter(photoAdapter);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        llDeduction.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isWorker = bundle.getBoolean("isWorker", false);
            isFromRemindList = bundle.getBoolean("isFromRemindList", false);
            if (ProjectNameUtil.isSZSZX(activity) && intUserRole == 1) {
                llDeduction.setVisibility(View.VISIBLE);
                strLeaveType = bundle.getString("strLeaveType", "");
                if (strLeaveType.equals("1")) {
                    rbYes.setChecked(true);
                } else {
                    rbNo.setChecked(true);
                }
            }
            if (isWorker) {
                strAttendId = bundle.getString("strAttendId");
            }
            headTitle = bundle.getString("headTitle");
            setHeadTitle(headTitle);
            sign = bundle.getInt("sign");
            if (sign == DefineUtil.wdhd || sign == DefineUtil.hdgl) {
                startDateTag_TextView.setText(getString(R.string.string_start_date_no_line));
                endDateTag_TextView.setText(getString(R.string.string_end_date_no_line));
                placeTag_TextView.setText(getString(R.string.string_activity_address));
            } else {
                placeTag_TextView.setText(getString(R.string.string_meeting_address));
                startDateTag_TextView.setText(getString(R.string.string_starting_time_no_line));
                endDateTag_TextView.setText(getString(R.string.string_end_time_no_line));
            }
            fileScan_MyGridView.setVisibility(View.VISIBLE);
            GILogUtil.i("sign==================" + sign);
            strId = bundle.getString("strId");
            title = bundle.getString("strName");
            startTime = bundle.getString("strStartDate");
            endTime = bundle.getString("strEndDate");
            place = bundle.getString("strPlace");
            title_TextView.setText(title);
            startTime_TextView.setText(startTime);
            endTime_TextView.setText(endTime);
            place_TextView.setText(place);
            if (bundle.getBoolean("isFromDetail", false)) {
                String state = bundle.getString("state", "");
                leaveTypeId = bundle.getString("leaveTypeId");
                reason = bundle.getString("strReason");
                if (GIStringUtil.isNotBlank(state)) {
                    stateId = Utils.getAttendSTValue(state) + "";
                }
                state_TextView.setValue(state);
                scanFileList = (ArrayList<ObjFileBean>) bundle.getSerializable("FileList");
                if (stateId.equals("0")) {
                    setOldContent();
                    leaveType_TextView.setText(leaveTypeId);
                    reason_EditText.setText(reason);
                }
            }
        }
        getAttendStateList();
        super.initData();
    }

    /**
     * 设置已发布的内容
     */
    private void setOldContent() {
        for (ObjFileBean objFileBean : scanFileList) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath(Utils.formatFileUrl(activity, objFileBean.getStrFile_url()));
            localMedia.setCutPath(Utils.formatFileUrl(activity, objFileBean.getStrFile_url()));
            localMedia.setCompressPath(Utils.formatFileUrl(activity, objFileBean.getStrFile_url()));
            localMedia.setMimeType("image");
            localMedia.setChooseModel(PictureMimeType.ofImage());
            selectList.add(localMedia);
            oldImgList.add(Utils.formatFileUrl(activity, objFileBean.getStrFile_url()));
        }
        photoAdapter.setList(selectList);
        photoAdapter.notifyDataSetChanged();

    }

    @Override
    public void setListener() {
        super.setListener();
        rgDeduction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbYes.getId()) {
                    strLeaveType = "1";
                } else {
                    strLeaveType = "0";
                }
            }
        });
    }

    //图片点击事件
    private Photo_RVAdapter.onPicClickListener onPicClickListener = new Photo_RVAdapter.onPicClickListener() {
        @Override
        public void onPicClick(View view, int position) {
        }
    };


    /**
     * 返回结果
     *
     * @param what
     * @param obj
     */
    private void doLogic(Object obj, int what) {
        prgDialog.closePrgDialog();
        String totastMessage = null;
        switch (what) {
            case -1:
                try {
                    AttendStateListBean attendStateListBean = (AttendStateListBean) obj;
                    attendStateBeans = attendStateListBean.getObjList();
                    if (attendStateBeans != null && attendStateBeans.size() > 0) {
                        menuArray = new String[attendStateBeans.size()];
                        for (int i = 0; i < attendStateBeans.size(); i++) {
                            menuArray[i] = attendStateBeans.get(i).getStrAttendResultName();
                        }
                        if (attendStateBeans.size() == 1) {
                            state_TextView.setValue(attendStateBeans.get(0).getStrAttendResultName());
                            stateId = attendStateBeans.get(0).getStrAttendResult();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    totastMessage = getString(R.string.data_error);
                }
                break;
            case 0:
                try {
                    LeaveTypeListBean leaveTypeListBean = (LeaveTypeListBean) obj;
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
                prgDialog.closePrgDialog();
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                    totastMessage = getString(R.string.string_submit_successful);
                    GISharedPreUtil.setValue(activity, "isRefreshMeetAct", true);
                    if (isFromRemindList) {
                        setResult(-2);
                    } else {
                        setResult(RESULT_OK);
                    }
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    totastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (totastMessage != null) {
            showToast(totastMessage);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_state:
                showChooseDialog(menuArray, 0);
                break;
            case R.id.ll_leave_type:
                getLeaveTypeList();
                break;
            case R.id.btn_goto:
                if (GIStringUtil.isBlank(stateId)) {
                    showToast(getString(R.string.string_please_select_the_presence_status));
                    return;
                }
                submitSign();
                break;
            default:
                break;
        }
    }

    /**
     * 提交会议、活动报名
     */
    private void submitSign() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("strAttendId", strAttendId);
        textParamMap.put("strAttendResult", stateId);//出席状态0-请假、1-出席,2-缺席
        if (sign == DefineUtil.wdhd || sign == DefineUtil.xghd || sign == DefineUtil.hdgl) {
            doRequestNormal(ApiManager.getInstance().UpdateEventAttendServlet(textParamMap), 1);
        } else {
            doRequestNormal(ApiManager.getInstance().UpdateMeetAttendServlet(textParamMap), 1);
        }

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

    /**
     * 获取请假类型列表
     */
    private void getLeaveTypeList() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<String, String>();
        doRequestNormal(ApiManager.getInstance().getLeaveTypeList(textParamMap), 0);
    }

    /**
     * 获取出席状态
     */
    private void getAttendStateList() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<String, String>();
        if (sign == DefineUtil.wdhd || sign == DefineUtil.hdgl) {
            textParamMap.put("strId", strId);
            doRequestNormal(ApiManager.getInstance().actState(textParamMap), -1);
        } else {
            textParamMap.put("strId", strId);
            doRequestNormal(ApiManager.getInstance().meetingState(textParamMap), -1);
        }
    }

    private void showChooseDialog(final String[] menuArray, final int i) {

        final NormalListDialog normalListDialog = new NormalListDialog(activity, menuArray);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (i == 0) {
                    String state = menuArray[position];
                    state_TextView.setValue(state);
                    if (state.equals("请假")) {
                        stateId = "0";
                    } else if (state.equals("出席")) {
                        stateId = "1";
                    } else {
                        stateId = "2";
                    }
                } else {
                    leaveType_TextView.setText(menuArray[position]);
                    leaveTypeId = leaveTypeBeans.get(position).getStrAbsentType();
                }
                normalListDialog.dismiss();
            }
        });
        if (i == 0) {
            normalListDialog.title(getString(R.string.string_select_presence_status));
        } else {
            normalListDialog.title(" " + getString(R.string.string_please_select_the_type_of_leave));
        }
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        normalListDialog.titleTextColor(Color.WHITE);
        normalListDialog.show();
    }


}
