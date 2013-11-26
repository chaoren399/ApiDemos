package com.amap.cn.apis.overlay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.amap.cn.apis.location.MyLocationOverlayProxy;
import com.amap.cn.apis.util.Constants;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.cn.apis.R;

public class MyLocationOverlay extends MapActivity{
	
	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private MyLocationOverlayProxy mLocationOverlay;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.setMapMode(MAP_MODE_VECTOR);//设置地图为矢量模式
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		mMapView = (MapView) findViewById(R.id.main_mapView);
		mMapView.setBuiltInZoomControls(true);  
		mMapController = mMapView.getController();  
		point = new GeoPoint((int) (39.90923 * 1E6),
				(int) (116.397428 * 1E6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);  //设置地图中心点
		mMapController.setZoom(12);   
		mLocationOverlay = new MyLocationOverlayProxy(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
		mLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	handler.sendMessage(Message.obtain(handler, Constants.FIRST_LOCATION));
            }
        });
		
    }
    
    
    
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
    	this.mLocationOverlay.disableMyLocation();
    	this.mLocationOverlay.disableCompass();
		super.onPause();
	}




	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		this.mLocationOverlay.enableMyLocation();
		this.mLocationOverlay.enableCompass();
		super.onResume();
		
	}




	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.FIRST_LOCATION) {
				mMapController.animateTo(mLocationOverlay.getMyLocation());
			}
		}
    };

}
