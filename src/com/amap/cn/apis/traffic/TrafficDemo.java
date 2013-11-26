package com.amap.cn.apis.traffic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.amap.cn.apis.util.Constants;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.cn.apis.R;

/**
 * 目前支持实时路况的城市有：北京 上海 广州 深圳 成都 南京 沈阳 武汉 宁波 重庆 青岛 杭州
 * 实时路况的图符块不会被缓存，且每5分钟左右更新一次。
 * 矢量地图暂时还不支持矢量地图，请期待。
 */
public class TrafficDemo extends MapActivity{

	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private ImageButton trafficLayer;
	private boolean isTraffic = false;//处理实时路况

	@Override
	/**
	*显示栅格地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
	*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic);
		mMapView = (MapView) findViewById(R.id.traffic_mapView);
		mMapView.setBuiltInZoomControls(true);  //设置启用内置的缩放控件
		mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		point = new GeoPoint((int) (39.90923 * 1E6),
				(int) (116.397428 * 1E6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);  //设置地图中心点
		mMapController.setZoom(12);    //设置地图zoom级别
		trafficLayer=(ImageButton) findViewById(R.id.ImageButtonTraffic);
		
		trafficLayer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(Constants.DIALOG_LAYER);	
			}
		});
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.DIALOG_LAYER:
			String[] traffic = { getResources().getString(
					R.string.real_time_traffic) };
			boolean[] traffic_falg = new boolean[] { isTraffic };
			return new AlertDialog.Builder(TrafficDemo.this)
					.setTitle(R.string.choose_layer)
					.setMultiChoiceItems(traffic, traffic_falg,
							new DialogInterface.OnMultiChoiceClickListener() {

								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {

									if (which == 0) {
										if (isChecked) {
											mMapView.setTraffic(true);// 显示实时路况
										} else {
											mMapView.setTraffic(false);// 关闭实时路况
										}
										isTraffic = isChecked;
									}
									mMapView.postInvalidate();
									dismissDialog(Constants.DIALOG_LAYER);
								}
							})
					.setPositiveButton(R.string.alert_dialog_cancel,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dismissDialog(Constants.DIALOG_LAYER);
								}
							}).create();
		}
		return super.onCreateDialog(id);
	}
}
