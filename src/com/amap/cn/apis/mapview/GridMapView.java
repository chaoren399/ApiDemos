package com.amap.cn.apis.mapview;

import android.os.Bundle;
import android.widget.Toast;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.MapViewListener;
import com.amap.cn.apis.R;

public class GridMapView extends MapActivity{

	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;

	@Override
	/**
	*显示栅格地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
	*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.setBuiltInZoomControls(true);  //设置启用内置的缩放控件
		mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		point = new GeoPoint((int) (39.982378 * 1E6),
				(int) (116.304923 * 1E6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);  //设置地图中心点
		mMapController.setZoom(12);    //设置地图zoom级别
				
		mMapView.registerMapViewListener(new MapViewListener() {
            @Override
            public void onMapMoveFinish() {
                //v1.4.1 新增 地图移动后执行的回调函数。
                Toast.makeText(GridMapView.this, "Map Moved !", Toast.LENGTH_SHORT).show();
            }
        });
		
		Toast.makeText(this, "当前SDK版本为：" + mMapView.getReleaseVersion(), Toast.LENGTH_SHORT).show();
	}
}
