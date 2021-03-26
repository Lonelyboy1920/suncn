package com.qd.longchat.sign;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDUser;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.manager.QDSignManager;
import com.longchat.base.model.gd.QDFileModel;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.activity.QDContactActivity;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/24 上午9:42
 */
public class QDSignActivity extends QDBaseActivity {

    private final static int REQUEST_FOR_SELECT_USER = 1000;
    private final static int REQUEST_CAMERA = 1001;
    private final static int REQUEST_SELECT_ADDRESS = 1002;
    public final static int DEFAULT_OFFSET_MS = 10 * 60 * 1000; //十分钟

    @BindView(R2.id.view_lr_title)
    View viewTile;
    @BindView(R2.id.tv_lr_time)
    TextView tvTime;
    @BindView(R2.id.tv_lr_location)
    TextView tvLocation;
    @BindView(R2.id.view_map)
    MapView mapView;
    @BindView(R2.id.tv_lr_select)
    TextView tvSelectUser;
    @BindView(R2.id.iv_lr_img)
    ImageView ivAddUser;
    @BindView(R2.id.et_lr_edit)
    EditText etRemark;
    @BindView(R2.id.tv_limit)
    TextView tvLimit;
    @BindView(R2.id.fl_img)
    FrameLayout flLayout;
    @BindView(R2.id.iv_rl_img)
    ImageView ivPic;
    @BindView(R2.id.iv_rl_del)
    ImageView ivDel;
    @BindView(R2.id.iv_lr_photo)
    ImageView ivPhoto;
    @BindView(R2.id.rl_sign)
    LinearLayout llSignLayout;
    @BindView(R2.id.tv_sign)
    TextView tvSign;
    @BindView(R2.id.tv_sign_time)
    TextView tvSignTime;

    @BindString(R2.string.sign_title)
    String strTitle;
    @BindString(R2.string.sign_sign)
    String strSign;
    @BindString(R2.string.sign_locationing)
    String strLocationing;
    @BindString(R2.string.text_camera)
    String strCamera;
    @BindString(R2.string.text_album)
    String strAlbum;

    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient = null;
    private String address;
    private double latitude, longitude;
    private List<Map<String, String>> selectedUserList;
    private QDAlertView alertView;
    private String cameraPath;
    private String attachId;
    private boolean isRegister;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                if (tvSignTime != null) {
                    tvSignTime.setText(QDDateUtil.dateToString(new Date(System.currentTimeMillis()), QDDateUtil.MSG_FORMAT1));
                }
            }
        }
    };

    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            address = bdLocation.getAddrStr();
            mBaiduMap.setMyLocationEnabled(true);

            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();

            setLocation(latitude, longitude, address);
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 25) {
                etRemark.removeTextChangedListener(textWatcher);
                String text = s.toString().substring(0, 25);
                etRemark.setText(text);
                etRemark.setSelection(text.length());
                tvLimit.setText(text.length() + "/25");
                HideSoftInput();
                QDUtil.showToast(context, "你输入的字数已经超过了限制！");
                etRemark.addTextChangedListener(textWatcher);
            } else {
                tvLimit.setText(s.length() + "/25");
            }
        }
    };

    private QDAlertView.OnStringItemClickListener listener = new QDAlertView.OnStringItemClickListener() {
        @Override
        public void onItemClick(String str, int position) {
            if (AndPermission.hasPermissions(context, Permission.Group.CAMERA)) {
                cameraPath = QDStorePath.IMG_PATH + System.currentTimeMillis() + ".png";
                File file = new File(cameraPath);
                if (file.exists()) {
                    file.delete();
                }
                QDUtil.startTakePhoto((Activity) context, REQUEST_CAMERA, file);
            } else {
                QDUtil.getPermission(context, Permission.Group.CAMERA);
            }
        }
    };

    private QDResultCallBack callBack = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, "记录失败");
        }

        @Override
        public void onSuccess(Object o) {
            Intent intent = new Intent(context, QDSignSuccessActivity.class);
            intent.putExtra(QDSignSuccessActivity.KEY_OF_ADDRESS, tvLocation.getText().toString());
            intent.putExtra(QDSignSuccessActivity.KEY_OF_TIME, System.currentTimeMillis());
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);

        initTitleView(viewTile);
        tvTitleName.setText(strTitle);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText(strSign);
        selectedUserList = new ArrayList<>();
        initViewValue();
        initMapView();
        initAlertView();
        initSelectedUser();
        register();
        etRemark.addTextChangedListener(textWatcher);
    }

    /**
     * 初始化数据
     */
    private void initViewValue() {
        tvLocation.setText(strLocationing);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String strDay = "";
        switch (day) {
            case 1:
                strDay = "周日";
                break;
            case 2:
                strDay = "周一";
                break;
            case 3:
                strDay = "周二";
                break;
            case 4:
                strDay = "周三";
                break;
            case 5:
                strDay = "周四";
                break;
            case 6:
                strDay = "周五";
                break;
            case 7:
                strDay = "周六";
                break;
        }
        StringBuilder sb = new StringBuilder();
        sb.append((calendar.get(Calendar.MONTH) + 1)).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日 ").append(strDay);
        tvTime.setText(sb.toString());
        tvSignTime.setText(QDDateUtil.dateToString(new Date(), QDDateUtil.MSG_FORMAT1));
    }

    /**
     * 定位
     */
    private void initMapView() {
        mBaiduMap = mapView.getMap();

        mapView.showZoomControls(false);  //去除放大缩小按钮
        mBaiduMap.getUiSettings().setScrollGesturesEnabled(false); //禁止地图拖动

        //开始定位
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setScanSpan(0);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private void initSelectedUser() {
        String info = QDUtil.getUserInfo(context);
        if (!TextUtils.isEmpty(info)) {
            String[] infos = info.split("\\|");
            StringBuilder sb = new StringBuilder();
            for (String str : infos) {
                String[] userInfo = str.split(":");
                String userId = userInfo[0];
                String userName = userInfo[1];
                sb.append(userName).append(",");
                Map<String, String> map = new HashMap<>();
                map.put("user_id", userId);
                map.put("name", userName);
                selectedUserList.add(map);
            }
            tvSelectUser.setText(sb.toString().substring(0, sb.toString().length() - 1));
        }
    }

    /**
     * 设置地址
     *
     * @param lat
     * @param log
     * @param adr
     */
    private void setLocation(double lat, double log, final String adr) {
        mBaiduMap.clear();
        MyLocationData data;
        LatLng ll;
        data = new MyLocationData.Builder()
                .latitude(lat)
                .longitude(log).build();
        ll = new LatLng(lat, log);

        mBaiduMap.setMyLocationData(data);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka));
        mBaiduMap.setMyLocationConfiguration(config);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(adr)) {
                    tvLocation.setText(address);
                } else {
                    tvLocation.setText(adr);
                }
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent locationIntent = new Intent(context, QDLocationActivity.class);
                locationIntent.putExtra(QDLocationActivity.KEY_OF_LONGITUDE, longitude);
                locationIntent.putExtra(QDLocationActivity.KEY_OF_LATITUDE, latitude);
                locationIntent.putExtra(QDLocationActivity.KEY_OF_ADDRESS, address);
                startActivityForResult(locationIntent, REQUEST_SELECT_ADDRESS);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    @OnClick({R2.id.tv_lr_select, R2.id.iv_lr_img, R2.id.iv_lr_photo, R2.id.iv_rl_img, R2.id.iv_rl_del,
            R2.id.view_map, R2.id.tv_lr_location, R2.id.rl_sign, R2.id.tv_title_right})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_lr_img || i == R.id.tv_lr_select) {
            List<String> excludedIdList = new ArrayList<>(1);
            excludedIdList.add(QDLanderInfo.getInstance().getId());
            Intent intent = new Intent(context, QDContactActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_MULTI);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_TYPE, QDContact.TYPE_CONTACT);
            intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST, (ArrayList<String>) excludedIdList);
            if (selectedUserList.size() != 0) {
                List<String> idList = new ArrayList<>();
                List<QDUser> userList = new ArrayList<>();
                for (Map<String, String> map : selectedUserList) {
                    String userId = map.get("user_id");
                    String userName = map.get("name");
                    idList.add(userId);
                    QDUser user = new QDUser();
                    user.setId(userId);
                    user.setName(userName);
                    userList.add(user);
                }
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) idList);
                intent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) userList);
            }
            startActivityForResult(intent, REQUEST_FOR_SELECT_USER);

        } else if (i == R.id.iv_lr_photo) {
            alertView.show();

        } else if (i == R.id.iv_rl_img) {
            List<String> pathList = new ArrayList<>(1);
            pathList.add(cameraPath);
            Intent picIntent = new Intent(context, QDPicActivity.class);
            picIntent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) pathList);
            startActivity(picIntent);

        } else if (i == R.id.iv_rl_del) {
            flLayout.setVisibility(View.GONE);
            attachId = "";

        } else if (i == R.id.tv_lr_location) {
            Intent locationIntent = new Intent(context, QDLocationActivity.class);
            locationIntent.putExtra(QDLocationActivity.KEY_OF_LONGITUDE, longitude);
            locationIntent.putExtra(QDLocationActivity.KEY_OF_LATITUDE, latitude);
            locationIntent.putExtra(QDLocationActivity.KEY_OF_ADDRESS, address);
            startActivityForResult(locationIntent, REQUEST_SELECT_ADDRESS);

        } else if (i == R.id.rl_sign) {
            QDUtil.addUserInfo(context, "");
            doSign();

        } else if (i == R.id.tv_title_right) {
            Intent reportIntent = new Intent(context, QDSignListActivity.class);
            startActivity(reportIntent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FOR_SELECT_USER:
                    List<QDUser> userList = data.getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
                    addSelectedUser(userList);
                    break;
                case REQUEST_CAMERA:
                    getWarningDailog().show();
                    File file = new File(cameraPath);
                    if (TextUtils.isEmpty(cameraPath) || !file.exists()) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, "路径为空！");
                    } else {
                        QDUtil.createBitmapFromCamera(cameraPath);
                        drawTextOnDrawable();
                        doUploadPhoto();
                    }
                    break;
                case REQUEST_SELECT_ADDRESS:
                    latitude = data.getDoubleExtra(QDLocationActivity.KEY_OF_LATITUDE, 0);
                    longitude = data.getDoubleExtra(QDLocationActivity.KEY_OF_LONGITUDE, 0);
                    address = data.getStringExtra(QDLocationActivity.KEY_OF_ADDRESS);
                    setLocation(latitude, longitude, address);
                    flLayout.setVisibility(View.GONE);
                    attachId = "";
                    break;
            }
        }
    }

    private void addSelectedUser(List<QDUser> userList) {
        if (selectedUserList.size() != 0) {
            selectedUserList.clear();
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        for (QDUser user : userList) {
            Map<String, String> map = new HashMap<>();
            map.put("user_id", user.getId());
            map.put("name", user.getName());
            selectedUserList.add(map);
            sb.append(user.getName()).append(",");
            sb1.append(user.getId()).append(":").append(user.getName()).append("|");
        }
        QDUtil.addUserInfo(context, sb1.toString());
        tvSelectUser.setText(sb.toString().substring(0, sb.toString().length() - 1));
    }

    private void drawTextOnDrawable() {
        Bitmap bitmap = BitmapFactory.decodeFile(cameraPath);
        Bitmap tBitmap = QDBitmapUtil.drawTextToBitmap(context, bitmap, tvLocation.getText().toString(),
                QDUtil.getServerTime(),
                QDLanderInfo.getInstance().getName());
        QDUtil.savePhotoToSD(tBitmap, cameraPath);
    }

    private void doUploadPhoto() {
        File file = new File(cameraPath);
        QDFileManager.getInstance().uploadFile(file, "addons", new QDFileCallBack<QDFileModel>() {
            @Override
            public void onUploading(String path, int per) {

            }

            @Override
            public void onUploadFailed(String errorMsg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, "上传图片失败：" + errorMsg);
            }

            @Override
            public void onUploadSuccess(QDFileModel model) {
                getWarningDailog().dismiss();
                attachId = model.getId();
                flLayout.setVisibility(View.VISIBLE);
                Glide.with(context).load(cameraPath).into(ivPic);
            }
        });
    }

    private void initAlertView() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strCamera)
                .setOnStringItemClickListener(listener)
                .setDismissOutside(true)
                .build();
    }

    private void doSign() {
        if (TextUtils.isEmpty(tvLocation.getText().toString()) || tvLocation.getText().toString().equals("正在定位...")) {
            QDUtil.showToast(context, "定位失败");
            return;
        }
        if (selectedUserList.size() == 0) {
            QDUtil.showToast(context, "请先选择知会人");
            return;
        }
        if (TextUtils.isEmpty(attachId)) {
            QDUtil.showToast(context, "请先上传现场照片");
            return;
        }
        long offset = QDClient.getInstance().getLoginInfo().getSTimeOffset();
        if (Math.abs(offset) >= DEFAULT_OFFSET_MS) {
            QDUtil.showToast(context, "手机时间和服务器时间不一致，当前北京时间" + QDUtil.getServerTime());
        }
        tvSign.setText("正在记录...");

        String users = QDGson.getGson().toJson(selectedUserList);

        Map<String, String> map = new HashMap<>();
        map.put("type", "3");
        map.put("note", etRemark.getText().toString());
        map.put("latitude", String.valueOf(latitude));
        map.put("longitude", String.valueOf(longitude));
        map.put("area", address);
        map.put("attachs", attachId);
        map.put("users", users);
        QDSignManager.getInstance().addSign(map, callBack);
    }

    private void register() {
        if (!isRegister) {
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            registerReceiver(receiver, filter);
        }
    }

    private void unRegister() {
        if (isRegister) {
            isRegister = false;
            unregisterReceiver(receiver);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.setMapCustomEnable(false);
        mapView.onDestroy();
        unRegister();
        mapView = null;
    }
}
