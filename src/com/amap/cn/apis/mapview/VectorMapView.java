package com.amap.cn.apis.mapview;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.cn.apis.R;

public class VectorMapView extends MapActivity{

	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private EditText angleText;
	private Button btn;

	@Override
	/**
	*显示矢量地图，将libminimapv320.so复制到工程目录下的libs\armeabi。
	*启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
	*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vmapview);
		mMapView = (MapView) findViewById(R.id.vmapView);
		mMapView.setVectorMap(true);//设置地图为矢量模式
		mMapView.setBuiltInZoomControls(true);  //设置启用内置的缩放控件
		mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		point = new GeoPoint((int) (39.90923 * 1E6),
				(int) (116.397428 * 1E6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);  //设置地图中心点
		mMapController.setZoom(12);    //设置地图zoom级别
		
		angleText = (EditText)findViewById(R.id.angleEditText);
		angleText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		btn=(Button)this.findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int angle = Integer.parseInt(angleText.getText().toString());
				mMapView.setMapAngle(angle);
			}
		});
	}
	
}
