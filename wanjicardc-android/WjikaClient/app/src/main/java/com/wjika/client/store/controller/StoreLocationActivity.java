package com.wjika.client.store.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.common.viewinject.annotation.ViewInject;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.ui.BaseActivity;
import com.wjika.client.utils.ViewInjectUtils;

/**
 * 地图
 * Created by Leo_Zhang on 2016/6/2.
 */
public class StoreLocationActivity extends BaseActivity implements View.OnClickListener {

	@ViewInject(R.id.left_button)
	private ImageView leftButton;
	@ViewInject(R.id.bmapView)
	private MapView mapView;

	public MyLocationListenner myListener = new MyLocationListenner();
	private double latitude;
	private double longitude;
	private BaiduMap baiduMap;
	private LocationClient locationClient;
	boolean isFirstLoc = true; // 是否首次定位

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_location_act);
		ViewInjectUtils.inject(this);
		latitude = getIntent().getDoubleExtra("latitude", 0);
		longitude = getIntent().getDoubleExtra("longitude", 0);
		initMapView();
		leftButton.setOnClickListener(this);
	}

	private void initMapView() {
		baiduMap = mapView.getMap();
		baiduMap.setMyLocationEnabled(true);
		locationClient = new LocationClient(this);
		locationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		BaiduMapOptions baiduMapOptions = new BaiduMapOptions();
		baiduMapOptions.compassEnabled(true);
		option.setOpenGps(true);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		locationClient.setLocOption(option);
		locationClient.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.left_button:
				finish();
				break;
		}
	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(latitude, longitude);
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(ll).zoom(18.0f);
				baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
				//构建markeroption,用于在地图上添加marker
				OverlayOptions options = new MarkerOptions().position(ll).icon(bitmapDescriptor);
				baiduMap.addOverlay(options);
			}
		}

	}

	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// 退出时销毁定位
		locationClient.stop();
		// 关闭定位图层
		baiduMap.setMyLocationEnabled(false);
		mapView.onDestroy();
		mapView = null;
		super.onDestroy();
	}
}
