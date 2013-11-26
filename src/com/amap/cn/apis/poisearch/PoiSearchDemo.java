package com.amap.cn.apis.poisearch;

import java.util.List;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.cn.apis.R;
import com.amap.cn.apis.util.Constants;
import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.PoiOverlay;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiSearch.SearchBound;
import com.amap.mapapi.poisearch.PoiTypeDef;
public class PoiSearchDemo extends MapActivity{
	
	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private TextView searchTextView;
	private String query = null;
	private PoiPagedResult result;
	private ProgressDialog progDialog = null;
	private PoiOverlay poiOverlay;
	private Button btn;
	private int curpage = 1;
	private int cnt = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poisearch);
		mMapView = (MapView) findViewById(R.id.poisearch_MapView);
		
		mMapView.setBuiltInZoomControls(true);  //�����������õ����ſؼ�

		mMapController = mMapView.getController();  // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		point = new GeoPoint((int) (39.90923 * 1E6),
				(int) (116.397428 * 1E6));  //�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);  //���õ�ͼ���ĵ�
		mMapController.setZoom(12);    //���õ�ͼzoom����
		
		searchTextView = (TextView) findViewById(R.id.TextViewSearch);
		searchTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearchRequested();
			}

		});
		progDialog=new ProgressDialog(this);
		btn = (Button)findViewById(R.id.next);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(cnt >= curpage) {
					handler.sendMessage(Message
						.obtain(handler, Constants.POISEARCH_NEXT));
				}
			}
			
		});
	}
	
	@Override
	protected void onNewIntent(final Intent newIntent) {
		super.onNewIntent(newIntent);
		String ac = newIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(ac)) {
			doSearchQuery(newIntent);
		}
	}
	
	protected void doSearchQuery(Intent intent) {
		query = intent.getStringExtra(SearchManager.QUERY);
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
				PoiSearchDemo.this, MySuggestionProvider.AUTHORITY,
				MySuggestionProvider.MODE);
		suggestions.saveRecentQuery(query, null);
		curpage = 1;
		cnt = 0;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					PoiSearch poiSearch = new PoiSearch(PoiSearchDemo.this,
							new PoiSearch.Query(query, PoiTypeDef.All, "010")); // ���������ַ�����"010Ϊ��������"
					poiSearch.setBound(new SearchBound(mMapView));//�ڵ�ǰ��ͼ��ʾ��Χ�ڲ���
					poiSearch.setPageSize(10);//��������ÿ����෵�ؽ����
					result = poiSearch.searchPOI();
					if(result != null) {
						cnt = result.getPageCount();
					}

					handler.sendMessage(Message.obtain(handler,
							Constants.POISEARCH));
				} catch (AMapException e) {
					handler.sendMessage(Message.obtain(handler,
							Constants.ERROR));
					e.printStackTrace();
				}
			}
		});
		if(progDialog==null)
			progDialog=new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("��������:\n" + query);
		progDialog.show();
		t.start();
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.POISEARCH) {
				progDialog.dismiss();
				try {
					if (result != null) {
						List<PoiItem> poiItems = result.getPage(1);
						if (poiItems != null && poiItems.size() > 0) {
							mMapController.setZoom(13);
							mMapController
									.animateTo(poiItems.get(0).getPoint());
							if (poiOverlay != null) {
								poiOverlay.removeFromMap();
							}
							Drawable drawable = getResources().getDrawable(
									R.drawable.da_marker_red);
//							poiOverlay = new MyPoiOverlay(PoiSearchDemo.this,
//									drawable, poiItems); // ������ĵ�һҳ��ӵ�PoiOverlay
							poiOverlay = new PoiOverlay(drawable, poiItems);
							poiOverlay.addToMap(mMapView); // ��poiOverlay��ע�ڵ�ͼ��
							poiOverlay.showPopupWindow(0);
							return;
						}
					}
					Toast.makeText(getApplicationContext(), "����ؽ����",
							Toast.LENGTH_SHORT).show();
				} catch (AMapException e) {
					Toast.makeText(getApplicationContext(), "�������Ӵ���",
							Toast.LENGTH_SHORT).show();
				}
			} else if (msg.what == Constants.ERROR) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), "����ʧ��,�����������ӣ�",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == Constants.POISEARCH_NEXT) {
				curpage++;
				try {
					List<PoiItem> poiItems = result.getPage(curpage);
					if (poiItems != null && poiItems.size() > 0) {
						mMapController.setZoom(13);
						mMapController.animateTo(poiItems.get(0).getPoint());
						if (poiOverlay != null) {
							poiOverlay.removeFromMap();
						}
						Drawable drawable = getResources().getDrawable(
								R.drawable.da_marker_red);
//						poiOverlay = new MyPoiOverlay(PoiSearchDemo.this,
//								drawable, poiItems); // ������ĵ�һҳ��ӵ�PoiOverlay
						poiOverlay = new PoiOverlay(drawable, poiItems);
						poiOverlay.addToMap(mMapView); // ��poiOverlay��ע�ڵ�ͼ��
						poiOverlay.showPopupWindow(0);
						mMapView.invalidate();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
}
