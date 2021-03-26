package com.qd.longchat.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.qd.longchat.R;
import com.qd.longchat.model.QDLocationBean;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDAlertView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2017/12/14 上午11:48
 */

public class QDBaiduLocationInfoActivity extends QDBaseActivity {

    private MapView mapView;
    private BaiduMap mBaiduMap;
    private TextView tvDetailName;
    private TextView tvSimplerName;
    private ImageView ivNavi;
    private QDLocationBean item;

    private QDAlertView alertView;
    private String[] strings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_activity_baidu_location_info);

        initTitleView(findViewById(R.id.view_bd_title));
        tvTitleName.setText(R.string.str_location);

        mapView = findViewById(R.id.view_map);
        tvDetailName = findViewById(R.id.tv_detail_info);
        tvSimplerName = findViewById(R.id.tv_simple_info);
        ivNavi = findViewById(R.id.iv_navi);

        initPopWindow();
        initMapView();

        ivNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });

    }

    private void initMapView() {
        mBaiduMap = mapView.getMap();
        item = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_LOCATION);
        tvSimplerName.setText(item.getSampleLocationInfo());
        tvDetailName.setText(item.getDetailLocationInfo());
        setLocation();
    }

    private void setLocation() {
        mBaiduMap.clear();
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData data = new MyLocationData.Builder()
                .latitude(item.getLatitude())
                .longitude(item.getLongitude()).build();
        mBaiduMap.setMyLocationData(data);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka));
        mBaiduMap.setMyLocationConfiguration(config);
        LatLng ll = new LatLng(item.getLatitude(), item.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    private void initPopWindow() {
        String strBaidu = "", strGaode = "";

        if (isAvilible(getApplicationContext(), "com.baidu.BaiduMap")) {
            strBaidu = context.getResources().getString(R.string.baidu_map);
        }

        if (isAvilible(getApplicationContext(), "com.autonavi.minimap")) {
            strGaode = context.getResources().getString(R.string.gaode_map);
        }

        if (TextUtils.isEmpty(strBaidu) && TextUtils.isEmpty(strGaode)) {
            return;
        } else if (!TextUtils.isEmpty(strBaidu) && !TextUtils.isEmpty(strGaode)) {
            strings = new String[]{strBaidu, strGaode};
        } else {
            if (TextUtils.isEmpty(strBaidu)) {
                strings = new String[]{strGaode};
            } else {
                strings = new String[]{strBaidu};
            }

        }
    }

    private void showAlert() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strings)
                .setOnStringItemClickListener(new QDAlertView.OnStringItemClickListener() {
                    @Override
                    public void onItemClick(String str, int position) {
                        if (position == 0) {
                            if (strings[0].equalsIgnoreCase(context.getResources().getString(R.string.baidu_map))) {
                                Intent i = new Intent();
                                i.setData(Uri.parse("baidumap://map/navi?location=" + item.getLatitude() + "," + item.getLongitude()));
                                startActivity(i);
                            } else {
                                Intent intent;
                                try {
                                    intent = Intent.getIntent("androidamap://navi?sourceApplication=慧医&poiname=我的目的地&lat=" + item.getLatitude() + "&lon=" + item.getLongitude() + "&dev=0");
                                    startActivity(intent);
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Intent intent;
                            try {
                                intent = Intent.getIntent("androidamap://navi?sourceApplication=慧医&poiname=我的目的地&lat=" + item.getLatitude() + "&lon=" + item.getLongitude() + "&dev=0");
                                startActivity(intent);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .build();
        alertView.show();
    }


    /**
     * 判断是否安装了某个程序
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        return packageNames.contains(packageName);
    }

}
