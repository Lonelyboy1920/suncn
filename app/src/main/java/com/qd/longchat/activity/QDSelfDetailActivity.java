package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qd.longchat.R;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.model.gd.QDFileModel;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.holder.QDBaseInfoHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;
import com.qd.longchat.view.QDDateSelectView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/8 下午4:09
 */
public class QDSelfDetailActivity extends QDBaseActivity implements QDResultCallBack<Boolean> {

    private final static int REQUEST_NAME = 1000;
    private final static int REQUEST_JOB = 1001;
    private final static int REQUEST_MOBILE = 1003;
    private final static int REQUEST_OPHONE = 1004;
    private final static int REQUEST_EMAIL = 1005;
    private final static int REQUEST_SELECT_PHOTO = 1006;
    private final static int REQUEST_CAMERA = 1007;
    private final static int REQUEST_CLIPPING = 1008;

    @BindView(R2.id.view_self_title)
    View viewTitle;
    @BindView(R2.id.rl_self_info_layout)
    RelativeLayout rlSelfLayout;
    @BindView(R2.id.iv_self_icon)
    ImageView ivSelfIcon;
    @BindView(R2.id.iv_self_arrow)
    ImageView ivSelfArrow;
    @BindView(R2.id.view_self_job)
    View viewJob;
    @BindView(R2.id.view_self_name)
    View viewName;
    @BindView(R2.id.view_self_sex)
    View viewSex;
    @BindView(R2.id.view_self_birthday)
    View viewBirthday;
    @BindView(R2.id.view_self_scan_code)
    View viewScanCode;
    @BindView(R2.id.view_self_dept)
    View viewDept;
    @BindView(R2.id.view_self_mobile)
    View viewMobile;
    @BindView(R2.id.view_self_ophone)
    View viewOphone;
    @BindView(R2.id.view_self_email)
    View viewEmail;

    @BindString(R2.string.self_title)
    String strTitle;
    @BindString(R2.string.self_job)
    String strJob;
    @BindString(R2.string.self_name)
    String strName;
    @BindString(R2.string.self_sex)
    String strSex;
    @BindString(R2.string.self_birthday)
    String strBirthday;
    @BindString(R2.string.self_qrcode)
    String strQrcode;
    @BindString(R2.string.self_dept)
    String strDept;
    @BindString(R2.string.self_mobile)
    String strMobile;
    @BindString(R2.string.self_ophone)
    String strOphone;
    @BindString(R2.string.self_email)
    String strEmail;
    @BindString(R2.string.text_camera)
    String strCamera;
    @BindString(R2.string.text_album)
    String strAlbum;
    @BindString(R2.string.sex_male)
    String strMale;
    @BindString(R2.string.sex_female)
    String strFemale;

    @BindString(R2.string.ace_self_ban_property)
    String strAceProperty;
    @BindString(R2.string.ace_self_ban_job)
    String strAceJob;
    @BindString(R2.string.ace_self_ban_icon)
    String strAceIcon;
    @BindString(R2.string.upload_pic_error)
    String strUploadPicError;
    @BindString(R2.string.modify_info_error)
    String strModifyError;

    private QDUser user;
    private QDDateSelectView customDatePicker1;
    private String date;

    private File cameraFile;

    private String tempPath;

    private int profileEdit;

    private QDFileCallBack callBack = new QDFileCallBack<QDFileModel>() {
        @Override
        public void onUploading(String path, int per) {

        }

        @Override
        public void onUploadFailed(String errorMsg) {
            QDUtil.showToast(context, strUploadPicError + errorMsg);
        }

        @Override
        public void onUploadSuccess(QDFileModel model) {
            String original = model.getOriginal();
            Map map = new HashMap();
            map.put("avatar", original);
            QDClient.getInstance().updateSelfInfo(map, QDSelfDetailActivity.this);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_detail);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        user = QDUserHelper.getUserById(QDLanderInfo.getInstance().getId());
        profileEdit = QDLanderInfo.getInstance().getProfileEdit();
        initDatePicker();
        initUI();
        initAlert();
    }

    private void initUI() {
        if (QDUtil.isHaveSelfAce(profileEdit, QDRolAce.ACE_PE_BAN_ICON)) {
            ivSelfArrow.setVisibility(View.INVISIBLE);
        } else {
            ivSelfArrow.setVisibility(View.VISIBLE);
        }
        QDBitmapUtil.getInstance().createAvatar(context, user.getId(), user.getName(), user.getPic(), ivSelfIcon);
        initJob();
        initName();
        initSex();
        initBirthday();
        initScanCode();
        initDept();
        initMobile();
        initOphone();
        initEmail();
    }

    private void initJob() {
        if (QDUtil.isHaveSelfAce(profileEdit, QDRolAce.ACE_PE_BAN_JOB)) {
            initItem(viewJob, strJob, user.getJob(), View.VISIBLE, View.GONE);
        } else {
            initItem(viewJob, strJob, user.getJob(), View.VISIBLE, View.VISIBLE);
        }
    }

    private void initName() {
        initItem(viewName, strName, user.getName(), View.VISIBLE, View.GONE);
    }

    private void initSex() {
        initItem(viewSex, strSex, QDUtil.getUserSex(context, user.getSex()), View.VISIBLE, View.VISIBLE);
    }

    private void initBirthday() {
        if (TextUtils.isEmpty(user.getBirthday())) {
            initItem(viewBirthday, strBirthday, "1980-01-01", View.VISIBLE, View.VISIBLE);
        } else {
            initItem(viewBirthday, strBirthday, user.getBirthday(), View.VISIBLE, View.VISIBLE);
        }
    }

    private void initScanCode() {
        initItem(viewScanCode, strQrcode, "", View.GONE, View.VISIBLE, R.drawable.ic_scan_code);
    }

    private void initDept() {
        String dept = "";
        String deptInfo = QDLanderInfo.getInstance().getSelfDeptInfo();
        if (!TextUtils.isEmpty(deptInfo)) {
            try {
                if (deptInfo.contains(";")) {
                    String[] depts = deptInfo.split(";");
                    String[] infos = depts[0].split(",");
                    dept = infos[2];
                } else {
                    String[] infos = deptInfo.split(",");
                    dept = infos[2];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initItem(viewDept, strDept, dept, View.VISIBLE, View.GONE);
    }

    private void initMobile() {
        if (QDUtil.isHaveSelfAce(profileEdit, QDRolAce.ACE_PE_BAN_PROPERTY)) {
            initItem(viewMobile, strMobile, user.getMobile(), View.VISIBLE, View.GONE);
        } else {
            initItem(viewMobile, strMobile, user.getMobile(), View.VISIBLE, View.VISIBLE);
        }
    }

    private void initOphone() {
        if (QDUtil.isHaveSelfAce(profileEdit, QDRolAce.ACE_PE_BAN_PROPERTY)) {
            initItem(viewOphone, strOphone, user.getOphone(), View.VISIBLE, View.GONE);
        } else {
            initItem(viewOphone, strOphone, user.getOphone(), View.VISIBLE, View.VISIBLE);
        }
    }

    private void initEmail() {
        initItem(viewEmail, strEmail, user.getEmail(), View.GONE, View.VISIBLE);
    }

    private void initItem(View view, String tag, String info, int visible, int visible1) {
        initItem(view, tag, info, visible, visible1, -1);
    }

    private void initItem(View view, String tag, String info, int visible, int visible1, int resource) {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(view);
        holder.tvItemName.setText(tag);
        holder.tvItemInfo.setText(info);
        holder.ivItemArrow.setVisibility(visible1);
        holder.ivItemLine.setVisibility(visible);
        if (resource != -1) {
            holder.ivItemArrow.setImageResource(resource);
        }
    }


    @OnClick({R2.id.rl_self_info_layout, R2.id.view_self_job, R2.id.view_self_sex,
            R2.id.view_self_birthday, R2.id.view_self_dept, R2.id.view_self_mobile, R2.id.view_self_ophone,
            R2.id.view_self_email, R2.id.view_self_scan_code})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rl_self_info_layout) {
            if (QDUtil.isHaveSelfAce(profileEdit, QDRolAce.ACE_PE_BAN_ICON)) {
//                    QDUtil.showToast(context, strAceIcon);
                return;
            }
            alertView.show();

        } else if (i == R.id.view_self_job) {
            if (QDUtil.isHaveSelfAce(profileEdit, QDRolAce.ACE_PE_BAN_JOB)) {
//                    QDUtil.showToast(context, strAceJob);
                return;
            }
            toModifyActivity(strJob, REQUEST_JOB, user.getJob());

//            case R.id.view_self_name:
//                toModifyActivity(strName, REQUEST_NAME);
//                break;
        } else if (i == R.id.view_self_sex) {
            QDAlertView alertView1 = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Bottom)
                    .setSelectList(strMale, strFemale)
                    .setDismissOutside(true)
                    .setOnStringItemClickListener(new QDAlertView.OnStringItemClickListener() {
                        @Override
                        public void onItemClick(String str, int position) {
                            Map<String, String> map = new HashMap<>();
                            if (position == 0) {
                                map.put("sex", "1");
                            } else {
                                map.put("sex", "2");
                            }
                            QDClient.getInstance().updateSelfInfo(map, QDSelfDetailActivity.this);
                        }
                    }).build();
            alertView1.show();

        } else if (i == R.id.view_self_birthday) {
            customDatePicker1.show(date);

        } else if (i == R.id.view_self_dept) {
        } else if (i == R.id.view_self_mobile) {
            if (QDUtil.isHaveSelfAce(profileEdit, QDRolAce.ACE_PE_BAN_PROPERTY)) {
//                    QDUtil.showToast(context, strAceProperty);
                return;
            }
            toModifyActivity(strMobile, REQUEST_MOBILE, user.getMobile());

        } else if (i == R.id.view_self_ophone) {
            if (QDUtil.isHaveSelfAce(profileEdit, QDRolAce.ACE_PE_BAN_PROPERTY)) {
//                    QDUtil.showToast(context, strAceProperty);
                return;
            }
            toModifyActivity(strOphone, REQUEST_OPHONE, user.getOphone());

        } else if (i == R.id.view_self_email) {
            toModifyActivity(strEmail, REQUEST_EMAIL, user.getEmail());

        }
    }

    private void toModifyActivity(String name, int requestCode, String info) {
        Intent intent = new Intent(context, QDModifyInfoActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, name);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, info);
        startActivityForResult(intent, requestCode);
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        String birthday = user.getBirthday();
        if (TextUtils.isEmpty(birthday)) {
            date = "1980-01-01";
        } else {
            date = birthday;
        }

        customDatePicker1 = new QDDateSelectView(this, new QDDateSelectView.ResultHandler() {
            @Override
            public void handle(Date date) { // 回调接口，获得选中的时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String time = sdf.format(date);
                Map<String, String> map = new HashMap<>();
                map.put("birthday", time);
                QDClient.getInstance().updateSelfInfo(map, QDSelfDetailActivity.this);
            }
        }, "1950-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String info = "";
            if (data != null) {
                info = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO);
            }
            Map<String, String> map = new HashMap<>();
            switch (requestCode) {
                case REQUEST_NAME:
                    map.put("name", info);
                    QDClient.getInstance().updateSelfInfo(map, this);
                    break;
                case REQUEST_JOB:
                    map.put("title", info);
                    QDClient.getInstance().updateSelfInfo(map, this);
                    break;
                case REQUEST_MOBILE:
                    map.put("mobile", info);
                    QDClient.getInstance().updateSelfInfo(map, this);
                    break;
                case REQUEST_OPHONE:
                    map.put("phone", info);
                    QDClient.getInstance().updateSelfInfo(map, this);
                    break;
                case REQUEST_EMAIL:
                    map.put("email", info);
                    QDClient.getInstance().updateSelfInfo(map, this);
                    break;
                case REQUEST_SELECT_PHOTO:
                    String filePath = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH);
                    tempPath = QDUtil.startPhotoZoom(QDSelfDetailActivity.this, new File(filePath), REQUEST_CLIPPING);
                    break;
                case REQUEST_CAMERA:
                    tempPath = QDUtil.startPhotoZoom(QDSelfDetailActivity.this, cameraFile, REQUEST_CLIPPING);
                    break;
                case REQUEST_CLIPPING:
                    if (cameraFile != null)
                        cameraFile.deleteOnExit();
                    uploadFile(tempPath, callBack);
                    break;
            }
        }
    }

    private void uploadFile(String path, QDFileCallBack callBack) {
        File file = new File(path);
        QDClient.getInstance().uploadFile(file, "avatar", callBack);
    }

    @Override
    public void onError(String errorMsg) {
        QDUtil.showToast(context, strModifyError + errorMsg);
    }

    @Override
    public void onSuccess(Boolean b) {
        user = QDUserHelper.getUserById(QDLanderInfo.getInstance().getId());
        QDBitmapUtil.getInstance().createAvatar(context, user.getId(), user.getName(), user.getPic(), ivSelfIcon);

        QDLanderInfo.getInstance().setName(user.getName());
        QDLanderInfo.getInstance().setPic(user.getPic());
        QDLanderInfo.getInstance().setAccount(user.getAccount());
        QDLanderInfo.getInstance().save();
        initName();
        initSex();
        initMobile();
        initOphone();
        initEmail();
        initBirthday();
        initJob();
    }

    private QDAlertView.OnStringItemClickListener listener = new QDAlertView.OnStringItemClickListener() {
        @Override
        public void onItemClick(String str, int position) {
            if (position == 1) {
                Intent intent = new Intent(context, QDSelectPhotoActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_SINGLE, true);
                startActivityForResult(intent, REQUEST_SELECT_PHOTO);
            } else {
                if (AndPermission.hasPermissions(context, Permission.Group.CAMERA)) {
                    String path = QDStorePath.IMG_PATH + System.currentTimeMillis() + ".png";
                    cameraFile = new File(path);
                    if (cameraFile.exists()) {
                        cameraFile.delete();
                    }
                    QDUtil.startTakePhoto(QDSelfDetailActivity.this, REQUEST_CAMERA, cameraFile);
                } else {
                    QDUtil.getPermission(context, Permission.Group.CAMERA);
                }
            }
        }
    };

    private QDAlertView alertView;

    private void initAlert() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strCamera, strAlbum)
                .setOnStringItemClickListener(listener)
                .setDismissOutside(true)
                .build();
    }
}
