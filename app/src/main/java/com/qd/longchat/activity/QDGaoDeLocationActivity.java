package com.qd.longchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDLocationAdapter;
import com.qd.longchat.model.QDLocationBean;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDXEditText;
import com.qd.longchat.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/1/8 下午2:12
 */
public class QDGaoDeLocationActivity extends QDBaseActivity {

    @BindView(R2.id.view_bd_title)
    View viewTitle;
    @BindView(R2.id.et_search_location)
    QDXEditText etSearch;
    @BindView(R2.id.view_list)
    RecyclerView recyclerView;
    @BindView(R2.id.view_map)
    MapView mapView;
    @BindView(R2.id.view_place)
    View viePlace;

    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private double latitude, longitude;
    private GeocodeSearch geocodeSearch;
    private List<QDLocationBean> locationBeanList;
    private QDLocationBean locationBean;
    private QDLocationAdapter locationAdapter;
    private String city;
    private PoiSearch poiSearch;

    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    city = aMapLocation.getCity();
                    longitude = aMapLocation.getLongitude();
                    latitude = aMapLocation.getLatitude();
                    QDLog.e("QDGaoDeLocationActivity", "the longtitude is:" + longitude + ", the latitude is:" + latitude);
                    addMark();
                    LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
                    RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 1000, GeocodeSearch.AMAP);
                    geocodeSearch.getFromLocationAsyn(query);
                } else {
                    QDUtil.showToast(context, "定位失败");
                }
            }
        }
    };

    private GeocodeSearch.OnGeocodeSearchListener geocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            if (i == 1000) {
                List<PoiItem> list = regeocodeResult.getRegeocodeAddress().getPois();
                setLocationList(list);
            } else {
                QDUtil.showToast(context, "获取周边位置失败");
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        }
    };

    private QDLocationAdapter.OnItemClickListener onItemClickListener = new QDLocationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            QDLocationBean info = locationBeanList.get(position);
            latitude = info.getLatitude();
            longitude = info.getLongitude();
            addMark();
            locationBean = locationBeanList.get(position);
            locationAdapter.setSelectPosition(position);
            locationAdapter.notifyDataSetChanged();
        }
    };

    private PoiSearch.OnPoiSearchListener searchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {
            if (i == 1000) {
                List<PoiItem> list = poiResult.getPois();
                setLocationList(list);
            } else {
                QDUtil.showToast(context, "搜索位置失败");
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_gaode_location);
        ButterKnife.bind(this);
        viePlace.setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
        tvTitleName.setText(getString(R.string.location_search));
        tvTitleRight.setText(R.string.chat_send);
        tvTitleRight.setVisibility(View.VISIBLE);
        locationBeanList = new ArrayList<>();

        mapView.onCreate(savedInstanceState);
        initGeocode();
        initMapView();
        initRecycleView();

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchTxt = v.getText().toString();
                    PoiSearch.Query query = new PoiSearch.Query(searchTxt, "", city);
                    query.setPageSize(10);
                    poiSearch = new PoiSearch(context, query);
                    poiSearch.setOnPoiSearchListener(searchListener);
                    poiSearch.searchPOIAsyn();
                }
                QDUtil.closeKeybord(etSearch, context);

                return false;
            }
        });

        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_LOCATION, locationBean);
                setResult(Activity.RESULT_OK, intent);
                QDUtil.closeKeybord(etSearch, context);
                finish();
            }
        });
    }

    private void initGeocode() {
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(geocodeSearchListener);
    }

    private void initMapView() {
        aMap = mapView.getMap();

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void initRecycleView() {
        recyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayout.HORIZONTAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationBeanList = new ArrayList<>();
        locationAdapter = new QDLocationAdapter(locationBeanList);
        recyclerView.setAdapter(locationAdapter);
        locationAdapter.setOnItemClickListener(onItemClickListener);
    }

    private void addMark() {
        aMap.clear();
        LatLng latLng = new LatLng(latitude, longitude);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka)));
    }

    private void setLocationList(List<PoiItem> itemList) {
        locationBeanList.clear();
        for (PoiItem item : itemList) {
            QDLocationBean locationBean = new QDLocationBean();
            locationBean.setLatitude(item.getLatLonPoint().getLatitude());
            locationBean.setLongitude(item.getLatLonPoint().getLongitude());
            locationBean.setSampleLocationInfo(item.getTitle());
            locationBean.setDetailLocationInfo(item.getSnippet());
            locationBeanList.add(locationBean);
        }
        locationBean = locationBeanList.get(0);
        locationAdapter.setSelectPosition(0);
        locationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
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
