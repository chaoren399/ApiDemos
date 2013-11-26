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
 * ʹ��MyLocationOverlayʵ�ֵ�ͼ�Զ���λ
 * ʵ�ֳ��ζ�λʹ��λ���������ʾ
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
				(int) (116.397428 * 1E6));  //�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6) --39947306,116210764
		mMapController.setCenter(point);  //���õ�ͼ���ĵ�
		mMapController.setZoom(12);   
		mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
		//ʵ�ֳ��ζ�λʹ��λ���������ʾ
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