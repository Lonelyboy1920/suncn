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

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.model.QDLocationBean;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDAlertView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/1/8 下午4:03
 */
public class QDGaodeLocationInfoActivity extends QDBaseActivity {

    @BindView(R2.id.view_bd_title)
    View viewTitle;
    @BindView(R2.id.view_map)
    MapView mapView;
    @BindView(R2.id.tv_detail_info)
    TextView tvDetailName;
    @BindView(R2.id.tv_simple_info)
    TextView tvSimplerName;
    @BindView(R2.id.iv_navi)
    ImageView ivNavi;
    @BindView(R2.id.view_place)
    View viePlace;

    private String[] strings;
    private QDLocationBean item;
    private AMap aMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_gaode_location_info);
        ButterKnife.bind(this);
        viePlace.setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
        tvTitleName.setText("位置");
        item = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_LOCATION);
        tvSimplerName.setText(item.getSampleLocationInfo());
        tvDetailName.setText(item.getDetailLocationInfo());
        initPopWindow();
        addMark();
        ivNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });
        mapView.onCreate(savedInstanceState);
    }

    private void addMark() {
        aMap = mapView.getMap();
        LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka)));
    }


    private void initPopWindow() {
        String strBaidu = "", strGaode = "";

        if (isAvilible(getApplicationContext(), "com.baidu.BaiduMap")) {
            strBaidu = "百度地图";
        }

        if (isAvilible(getApplicationContext(), "com.autonavi.minimap")) {
            strGaode = "高德地图";
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
        QDAlertView alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strings)
                .setOnStringItemClickListener(new QDAlertView.OnStringItemClickListener() {
                    @Override
                    public void onItemClick(String str, int position) {
                        if (position == 0) {
                            if (strings[0].equalsIgnoreCase("百度地图")) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }
}
