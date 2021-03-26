package com.qd.longchat.sign;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

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
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.adapter.QDLocationAdapter;
import com.qd.longchat.model.QDLocationBean;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;


/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2017/12/8 下午6:55
 */

public class QDLocationActivity extends QDBaseActivity {

    public final static String KEY_OF_LATITUDE = "latitude";
    public final static String KEY_OF_LONGITUDE = "longitude";
    public final static String KEY_OF_ADDRESS = "address";

    public final static String[] locationNames = {"公司企业", "房地产", "交通设施", "美食", "旅游景点"
            , "金融", "购物", "医疗", "文化传媒", "教育培训", "酒店", "生活服务", "休闲娱乐", "医疗", "政府机构"};
//            {"银行", "宾馆", "酒店", "公司", "园区", "景点", "大厦",
//            "商场", "小区", "车站", "机场", "码头", "公园", "广场"};
    private int index;
    private int length;
    private LatLng ll;

    private PoiSearch mPoiSearch;
    private double lantitude, longitude;
    private List<PoiInfo> poiInfoList;
    private BaiduMap mBaiduMap;
    private MapView mapView;
    private RecyclerView listView;
    private QDLocationAdapter locationAdapter;
    private List<QDLocationBean> locationBeanList;
    private String address;
    private GeoCoder mSearch;

    private OnGetPoiSearchResultListener myListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult.error.equals(SearchResult.ERRORNO.NO_ERROR)) {
                List<PoiInfo> list = poiResult.getAllPoi();
                if (list.size() !=0 ) {
                    poiInfoList.addAll(list);
                }
                StringBuilder sb = new StringBuilder();
                for (int i=0; i<list.size(); i++) {
                    PoiInfo info = list.get(i);
                    sb.append(info.name).append(", ");
                }
                QDLog.e("1111", "==========\n" + sb.toString() + "\n========");
            }
            if (index < length) {
                searchLocation(locationNames[index]);
            } else {
                setLocationData();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };


    private OnGetGeoCoderResultListener mSearchListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                QDUtil.showToast(QDLocationActivity.this, "没有搜索到位置信息");
                return;
            }
            poiInfoList.addAll(result.getPoiList());
            setLocationData();
        }
    };

    private QDLocationAdapter.OnItemClickListener onItemClickListener = new QDLocationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            PoiInfo info = poiInfoList.get(position);
            lantitude = info.location.latitude;
            longitude = info.location.longitude;
            address = info.name;
            setLocation();
            locationAdapter.setSelectPosition(position);
            locationAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_activity_location);

        initTitleView(findViewById(R.id.view_ltitle));
        tvTitleName.setText("地点微调");
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("确定");
        poiInfoList = new ArrayList<>();
        initUI();
        initData();
        initListView();

        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(KEY_OF_LATITUDE, lantitude);
                intent.putExtra(KEY_OF_LONGITUDE, longitude);
                intent.putExtra(KEY_OF_ADDRESS, address);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initUI() {
        mapView = (MapView) findViewById(R.id.view_lmap);
        listView = (RecyclerView) findViewById(R.id.view_llist);
        mBaiduMap = mapView.getMap();
        mapView.showZoomControls(false);  //去除放大缩小按钮
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void initData() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(myListener);
        lantitude = getIntent().getDoubleExtra(KEY_OF_LATITUDE, 0);
        longitude = getIntent().getDoubleExtra(KEY_OF_LONGITUDE, 0);
        address = getIntent().getStringExtra(KEY_OF_ADDRESS);
        ll = new LatLng(lantitude, longitude);
        index = 1;
        length = locationNames.length;
        //searchLocation(locationNames[index]);
        initGeoSearch();
        setLocation();
    }

    private void initListView() {
        listView = (RecyclerView) findViewById(R.id.view_llist);
        listView.addItemDecoration(new RecycleViewDivider(this, LinearLayout.HORIZONTAL));
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        locationBeanList = new ArrayList<>();
        locationAdapter = new QDLocationAdapter(locationBeanList);
        locationAdapter.setSelectPosition(-1);
        listView.setAdapter(locationAdapter);
        locationAdapter.setOnItemClickListener(onItemClickListener);
    }

    private void initGeoSearch() {
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(mSearchListener);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
    }

    private void setLocationData() {
        for (PoiInfo poiInfo : poiInfoList) {
            QDLocationBean locationBean = new QDLocationBean();
            locationBean.setLatitude(poiInfo.location.latitude);
            locationBean.setLongitude(poiInfo.location.longitude);
            locationBean.setSampleLocationInfo(poiInfo.name);
            locationBean.setDetailLocationInfo(poiInfo.address);
            locationBeanList.add(locationBean);
        }
        if (locationBeanList != null) {
            locationAdapter.notifyDataSetChanged();
        } else {
            QDUtil.showToast(this, "没有搜索到结果");
        }
    }

    private void searchLocation(String keyword) {
        index++;
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .keyword(keyword)
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(ll)
                .radius(100)
                .pageNum(1)
                .pageCapacity(10));
    }

    private void setLocation() {
        mBaiduMap.clear();
        MyLocationData data = new MyLocationData.Builder()
                .latitude(lantitude)
                .longitude(longitude).build();
        mBaiduMap.setMyLocationData(data);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka));
        mBaiduMap.setMyLocationConfiguration(config);
        LatLng ll = new LatLng(lantitude, longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
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
        mPoiSearch.destroy();
        mSearch.destroy();
        mapView.setMapCustomEnable(false);
        mapView.onDestroy();
        mapView = null;
    }
}
