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
	*��ʾդ���ͼ�������������ſؼ�������MapController���Ƶ�ͼ�����ĵ㼰Zoom����
	*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.setBuiltInZoomControls(true);  //�����������õ����ſؼ�
		mMapController = mMapView.getController();  // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		point = new GeoPoint((int) (39.982378 * 1E6),
				(int) (116.304923 * 1E6));  //�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);  //���õ�ͼ���ĵ�
		mMapController.setZoom(12);    //���õ�ͼzoom����
				
		mMapView.registerMapViewListener(new MapViewListener() {
            @Override
            public void onMapMoveFinish() {
                //v1.4.1 ���� ��ͼ�ƶ���ִ�еĻص�������
                Toast.makeText(GridMapView.this, "Map Moved !", Toast.LENGTH_SHORT).show();
            }
        });
		
		Toast.makeText(this, "��ǰSDK�汾Ϊ��" + mMapView.getReleaseVersion(), Toast.LENGTH_SHORT).show();
	}
}
