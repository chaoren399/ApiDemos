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
 * Ŀǰ֧��ʵʱ·���ĳ����У����� �Ϻ� ���� ���� �ɶ� �Ͼ� ���� �人 ���� ���� �ൺ ����
 * ʵʱ·����ͼ���鲻�ᱻ���棬��ÿ5�������Ҹ���һ�Ρ�
 * ʸ����ͼ��ʱ����֧��ʸ����ͼ�����ڴ���
 */
public class TrafficDemo extends MapActivity{

	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private ImageButton trafficLayer;
	private boolean isTraffic = false;//����ʵʱ·��

	@Override
	/**
	*��ʾդ���ͼ�������������ſؼ�������MapController���Ƶ�ͼ�����ĵ㼰Zoom����
	*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic);
		mMapView = (MapView) findViewById(R.id.traffic_mapView);
		mMapView.setBuiltInZoomControls(true);  //�����������õ����ſؼ�
		mMapController = mMapView.getController();  // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		point = new GeoPoint((int) (39.90923 * 1E6),
				(int) (116.397428 * 1E6));  //�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);  //���õ�ͼ���ĵ�
		mMapController.setZoom(12);    //���õ�ͼzoom����
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
											mMapView.setTraffic(true);// ��ʾʵʱ·��
										} else {
											mMapView.setTraffic(false);// �ر�ʵʱ·��
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
