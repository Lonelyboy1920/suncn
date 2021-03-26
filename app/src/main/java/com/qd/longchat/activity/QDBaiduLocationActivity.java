package com.qd.longchat.activity;

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

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDLocationAdapter;
import com.qd.longchat.model.QDLocationBean;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDXEditText;
import com.qd.longchat.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2017/12/13 下午6:38
 */

public class QDBaiduLocationActivity extends QDBaseActivity {

    private MapView mapView;
    private RecyclerView recyclerView;
    private BaiduMap mBaiduMap;
    private QDXEditText etSearch;

    private double latitude;
    private double longitude;
    private QDLocationAdapter locationAdapter;
    private List<QDLocationBean> locationBeanList;
    private QDLocationBean locationBean;
    private String city;

    private PoiSearch mPoiSearch;
    private LocationClient mLocationClient = null;
    private GeoCoder mGeoCoder;

    //定位监听
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            city = bdLocation.getCity();
            mBaiduMap.setMyLocationEnabled(true);
            latitude = bdLocation.getLatitude();
            longitude = bdLocation.getLongitude();
            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude, longitude)));
            setLocation();
        }

    };

    //搜索监听
    private OnGetPoiSearchResultListener searchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult.error.equals(SearchResult.ERRORNO.NO_ERROR)) {
                List<PoiInfo> poiInfos = poiResult.getAllPoi();
                if (poiInfos.size() != 0) {
                    setLocationBeanList(poiInfos);
                } else {
                    QDUtil.showToast(QDBaiduLocationActivity.this, context.getResources().getString(R.string.location_cannot_be_searched));
                }
            } else {
                QDUtil.showToast(QDBaiduLocationActivity.this, context.getResources().getString(R.string.location_cannot_be_searched));
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    //地理编码监听
    private OnGetGeoCoderResultListener geoListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            //地理编码结果能获取当前的位置
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            //反地理编码结果能获取附近的poi信息
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            List<PoiInfo> poiInfos = result.getPoiList();
            setLocationBeanList(poiInfos);
        }
    };

    private QDLocationAdapter.OnItemClickListener onItemClickListener = new QDLocationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            QDLocationBean info = locationBeanList.get(position);
            latitude = info.getLatitude();
            longitude = info.getLongitude();
            setLocation();
            locationBean = locationBeanList.get(position);
            locationAdapter.setSelectPosition(position);
            locationAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_activity_baidu_location);

        initTitleView(findViewById(R.id.view_bd_title));
        tvTitleName.setText(getString(R.string.location_search));
        tvTitleRight.setText(R.string.chat_send);
        tvTitleRight.setVisibility(View.VISIBLE);
        etSearch = findViewById(R.id.et_search_location);

        initGeoCoder(); //地理编码
        initMapView();
        initRecycleView();
        initSearchLocation();

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchTxt = v.getText().toString();
                    mPoiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword(searchTxt).pageNum(10));
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
                setResult(RESULT_OK, intent);
                QDUtil.closeKeybord(etSearch, context);
                finish();
            }
        });
    }

    private void initGeoCoder() {
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(geoListener);
    }

    private void initMapView() {
        mapView = findViewById(R.id.view_map);
        mBaiduMap = mapView.getMap();

//        mapView.showZoomControls(false);  //去除放大缩小按钮
//        mBaiduMap.getUiSettings().setScrollGesturesEnabled(false); //禁止地图拖动

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

    private void initRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.view_list);
        recyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayout.HORIZONTAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationBeanList = new ArrayList<>();
        locationAdapter = new QDLocationAdapter(locationBeanList);
        recyclerView.setAdapter(locationAdapter);
        locationAdapter.setOnItemClickListener(onItemClickListener);
    }

    private void initSearchLocation() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(searchListener);
    }

    private void setLocation() {
        mBaiduMap.clear();
        MyLocationData data = new MyLocationData.Builder()
                .latitude(latitude)
                .longitude(longitude).build();
        mBaiduMap.setMyLocationData(data);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka));
        mBaiduMap.setMyLocationConfiguration(config);
        LatLng ll = new LatLng(latitude, longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    private void setLocationBeanList(List<PoiInfo> poiInfos) {
        locationBeanList.clear();
        for (PoiInfo poiInfo: poiInfos) {
            QDLocationBean locationBean = new QDLocationBean();
            locationBean.setLatitude(poiInfo.location.latitude);
            locationBean.setLongitude(poiInfo.location.longitude);
            locationBean.setDetailLocationInfo(poiInfo.address);
            locationBean.setSampleLocationInfo(poiInfo.name);
            locationBeanList.add(locationBean);
        }
        locationBean = locationBeanList.get(0);
        locationAdapter.setSelectPosition(0);
        locationAdapter.notifyDataSetChanged();
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
        mPoiSearch.destroy();
        mGeoCoder.destroy();
        mapView = null;
    }
}
