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
	*��ʾʸ����ͼ����libminimapv320.so���Ƶ�����Ŀ¼�µ�libs\armeabi��
	*�����������ſؼ�������MapController���Ƶ�ͼ�����ĵ㼰Zoom����
	*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vmapview);
		mMapView = (MapView) findViewById(R.id.vmapView);
		mMapView.setVectorMap(true);//���õ�ͼΪʸ��ģʽ
		mMapView.setBuiltInZoomControls(true);  //�����������õ����ſؼ�
		mMapController = mMapView.getController();  // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		point = new GeoPoint((int) (39.90923 * 1E6),
				(int) (116.397428 * 1E6));  //�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);  //���õ�ͼ���ĵ�
		mMapController.setZoom(12);    //���õ�ͼzoom����
		
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
