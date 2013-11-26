package com.amap.cn.apis.overlay;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Bundle;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.amap.cn.apis.R;


public class OverlayDemo extends MapActivity{

	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mMapView = (MapView) findViewById(R.id.main_mapView);
		mMapView.setBuiltInZoomControls(true);  //设置启用内置的缩放控件
		mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		point = new GeoPoint((int) (39.982378 * 1E6),
				(int) (116.304923 * 1E6));  //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);  //设置地图中心点
		mMapController.setZoom(15);    //设置地图zoom级别
	    mMapView.getOverlays().add(new MyOverlay());        

	}
	/**
	 * 
	 *自定义Overlay可以实现点，线，面的叠加。
	 */
	public class MyOverlay extends Overlay{

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			 super.draw(canvas, mapView, shadow);
			 Point screenPts = new Point();
	         mapView.getProjection().toPixels(point, screenPts);
	         //---add the marker---
	         Bitmap bmp = BitmapFactory.decodeResource(
	                getResources(), R.drawable.da_marker_red);            
	         canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);  
	         Paint paintText = new Paint();
	         paintText.setTextSize(18);
	         paintText.setColor(Color.BLACK);
		     canvas.drawText("AMap", screenPts.x, screenPts.y, paintText); // 绘制文本
			 Paint mCirclePaint = new Paint();
			 mCirclePaint.setAntiAlias(true);
			 mCirclePaint.setColor(Color.BLUE);
			 mCirclePaint.setAlpha(50);
			 mCirclePaint.setStyle(Style.FILL);
			 canvas.drawCircle(screenPts.x+150, screenPts.y, 50, mCirclePaint);
			 Paint paintLine = new Paint();
			 paintLine.setColor(Color.RED);
			 paintLine.setStrokeWidth(3.0f);
			 paintLine.setStyle(Paint.Style.STROKE);
			 canvas.drawLine(screenPts.x-100, screenPts.y, screenPts.x, screenPts.y-200, paintLine);
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			return super.onTap(arg0, arg1);
		}
	}


	
}

