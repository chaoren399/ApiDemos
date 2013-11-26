package com.amap.cn.apis.location;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.amap.cn.apis.R;
import com.amap.cn.apis.util.Constants;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.MyLocationOverlay;

/**
 * 使用MyLocationOverlay实现地图自动定位
 * 实现初次定位使定位结果居中显示
 */
public class MyLocation extends MapActivity {
	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private MyLocationOverlay mLocationOverlay;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.setBuiltInZoomControls(true);  
		mMapController = mMapView.getController();  
		point = new GeoPoint((int) (39.90923 * 1E6),
				(int) (116.397428 * 1E6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6) --39947306,116210764
		mMapController.setCenter(point);  //设置地图中心点
		mMapController.setZoom(12);   
		mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
		//实现初次定位使定位结果居中显示
		mLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	handler.sendMessage(Message.obtain(handler, Constants.FIRST_LOCATION));
            }
        });
		
    }
    
    @Override
	protected void onPause() {
    	this.mLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onResume() {
		this.mLocationOverlay.enableMyLocation();
		super.onResume();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) { 
			if (msg.what == Constants.FIRST_LOCATION) {
				mMapController.animateTo(mLocationOverlay.getMyLocation());
				System.out.println("----------------------"+mLocationOverlay.getMyLocation().toString());
			}
		}
    };
}