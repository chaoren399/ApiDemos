package com.amap.cn.apis.route;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.cn.apis.R;
import com.amap.cn.apis.route.RouteSearchPoiDialog.OnListItemClick;
import com.amap.cn.apis.util.Constants;
import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.amap.mapapi.map.RouteMessageHandler;
import com.amap.mapapi.map.RouteOverlay;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiSearch.Query;
import com.amap.mapapi.poisearch.PoiTypeDef;
import com.amap.mapapi.route.Route;

public class RouteDemo extends MapActivity implements RouteMessageHandler {

	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private Button drivingButton;
	private Button transitButton;
	private Button walkButton;
	private ImageButton startImageButton;
	private ImageButton endImageButton;
	private ImageButton routeSearchImagebtn;
	private AutoCompleteTextView startTextView;
	private AutoCompleteTextView endTextView;
	private int mode = Route.BusDefault;
	private ProgressDialog progDialog;
	private PoiPagedResult startSearchResult;
	private PoiPagedResult endSearchResult;
	private String strStart;
	private String strEnd;
	private GeoPoint startPoint = null;
	private GeoPoint endPoint = null;
	private MapPointOverlay overlay;
	private String poiType;
	private List<Route> routeResult;
	private RouteOverlay ol;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route);
		mMapView = (MapView) findViewById(R.id.route_MapView);
		mMapView.setBuiltInZoomControls(true); // �����������õ����ſؼ�
		mMapController = mMapView.getController(); // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		point = new GeoPoint((int) (39.90923 * 1E6), (int) (116.397428 * 1E6)); // �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢��
																				// (��
																				// *
																				// 1E6)
		mMapController.setCenter(point); // ���õ�ͼ���ĵ�
		mMapController.setZoom(12); // ���õ�ͼzoom����
		startTextView = (AutoCompleteTextView) findViewById(R.id.autotextview_roadsearch_start);
		startTextView.setSelectAllOnFocus(true);
		endTextView = (AutoCompleteTextView) findViewById(R.id.autotextview_roadsearch_goals);
		endTextView.setSelectAllOnFocus(true);
		drivingButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_driving);
		transitButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_transit);
		walkButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_walk);
		overlay = new MapPointOverlay(this);
		drivingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mode = Route.DrivingDefault;
				drivingButton.setBackgroundResource(R.drawable.mode_driving_on);
				transitButton
						.setBackgroundResource(R.drawable.mode_transit_off);
			}
		});
		transitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mode = Route.BusDefault;
				drivingButton
						.setBackgroundResource(R.drawable.mode_driving_off);
				transitButton.setBackgroundResource(R.drawable.mode_transit_on);

			}
		});

		walkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToast("�ݲ�֧�ֲ��й滮");
			}
		});

		startImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_startoption);
		startImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToast("�ڵ�ͼ�ϵ���������");
				poiType = "startPoint";
				mMapView.getOverlays().add(overlay);
			}
		});
		endImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_goalsoption);
		endImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToast("�ڵ�ͼ�ϵ�������յ�");
				poiType = "endPoint";
				mMapView.getOverlays().add(overlay);
			}
		});

		routeSearchImagebtn = (ImageButton) findViewById(R.id.imagebtn_roadsearch_search);
		routeSearchImagebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strStart = startTextView.getText().toString().trim();
				strEnd = endTextView.getText().toString().trim();
				if (strStart == null || strStart.length() == 0) {
					Toast.makeText(RouteDemo.this, "��ѡ�����", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (strEnd == null || strEnd.length() == 0) {
					Toast.makeText(RouteDemo.this, "��ѡ���յ�", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				startSearchResult();
			}
		});

	}

	public void showToast(String showString) {
		Toast.makeText(getApplicationContext(), showString, Toast.LENGTH_SHORT)
				.show();
	}

	// ��ѯ·���滮���
	public void startSearchResult() {
		strStart = startTextView.getText().toString().trim();
		if (startPoint != null && strStart.equals("��ͼ�ϵĵ�")) {
			endSearchResult();
		} else {
			final Query startQuery = new Query(strStart, PoiTypeDef.All, "010");
			progDialog = ProgressDialog.show(RouteDemo.this, null,
					"����������������Ϣ...", true, true);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// ��������POI����
					PoiSearch poiSearch = new PoiSearch(RouteDemo.this,
							startQuery); // ���������ַ���
					try {
						startSearchResult = poiSearch.searchPOI();
						if (progDialog.isShowing()) {
							routeHandler.sendMessage(Message.obtain(
									routeHandler, Constants.ROUTE_START_SEARCH));
						}
					} catch (AMapException e) {
						Message msg = new Message();
						msg.what = Constants.ROUTE_SEARCH_ERROR;
						msg.obj = e.getErrorMessage();
						routeHandler.sendMessage(msg);
					}
				}

			});
			t.start();
		}
	}

	// ��ѯ·���滮�յ�
	public void endSearchResult() {

		strEnd = endTextView.getText().toString().trim();
		if (endPoint != null && strEnd.equals("��ͼ�ϵĵ�")) {
			searchRouteResult(startPoint, endPoint);
		} else {
			final Query endQuery = new Query(strEnd, PoiTypeDef.All, "010");
			progDialog = ProgressDialog.show(RouteDemo.this, null,
					"����������������Ϣ...", true, false);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					PoiSearch poiSearch = new PoiSearch(RouteDemo.this,
							endQuery); // ���������ַ���
					try {
						endSearchResult = poiSearch.searchPOI();
						if (progDialog.isShowing()) {
							routeHandler.sendMessage(Message.obtain(
									routeHandler, Constants.ROUTE_END_SEARCH));
						}
					} catch (AMapException e) {
						Message msg = new Message();
						msg.what = Constants.ROUTE_SEARCH_ERROR;
						msg.obj = e.getErrorMessage();
						routeHandler.sendMessage(msg);
					}
				}

			});
			t.start();
		}
	}

	public void searchRouteResult(GeoPoint startPoint, GeoPoint endPoint) {
		progDialog = ProgressDialog.show(RouteDemo.this, null, "���ڻ�ȡ��·", true,
				true);
		final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint,
				endPoint);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					routeResult = Route.calculateRoute(RouteDemo.this,
							fromAndTo, mode);
					if (progDialog.isShowing()) {
						if (routeResult != null || routeResult.size() > 0)
							routeHandler.sendMessage(Message
									.obtain(routeHandler,
											Constants.ROUTE_SEARCH_RESULT));
					}
				} catch (AMapException e) {
					Message msg = new Message();
					msg.what = Constants.ROUTE_SEARCH_ERROR;
					msg.obj = e.getErrorMessage();
					routeHandler.sendMessage(msg);
				}
			}
		});
		t.start();

	}

	private Handler routeHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.ROUTE_START_SEARCH) {
				progDialog.dismiss();
				try {
					List<PoiItem> poiItems;
					if (startSearchResult != null
							&& (poiItems = startSearchResult.getPage(1)) != null
							&& poiItems.size() > 0) {
						RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
								RouteDemo.this, poiItems);

						dialog.setTitle("��Ҫ�ҵ������:");
						dialog.show();
						dialog.setOnListClickListener(new OnListItemClick() {
							@Override
							public void onListItemClick(
									RouteSearchPoiDialog dialog,
									PoiItem startpoiItem) {
								startPoint = startpoiItem.getPoint();
								strStart = startpoiItem.getTitle();
								startTextView.setText(strStart);
								endSearchResult();
							}

						});
					} else {
						showToast("�����������,���������趨...");
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}

			} else if (msg.what == Constants.ROUTE_END_SEARCH) {
				progDialog.dismiss();
				try {
					List<PoiItem> poiItems;
					if (endSearchResult != null
							&& (poiItems = endSearchResult.getPage(1)) != null
							&& poiItems.size() > 0) {
						RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
								RouteDemo.this, poiItems);
						dialog.setTitle("��Ҫ�ҵ��յ���:");
						dialog.show();
						dialog.setOnListClickListener(new OnListItemClick() {
							@Override
							public void onListItemClick(
									RouteSearchPoiDialog dialog,
									PoiItem endpoiItem) {
								// TODO Auto-generated method stub
								endPoint = endpoiItem.getPoint();
								strEnd = endpoiItem.getTitle();
								endTextView.setText(strEnd);
								searchRouteResult(startPoint, endPoint);
							}

						});
					} else {
						showToast("�����������,���������趨...");
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}

			} else if (msg.what == Constants.ROUTE_SEARCH_RESULT) {
				progDialog.dismiss();
				if (routeResult != null && routeResult.size() > 0) {
					Route route = routeResult.get(0);
					if (route != null) {
						if (ol != null) {
							ol.removeFromMap(mMapView);
						}
						ol = new RouteOverlay(RouteDemo.this, route);
						ol.registerRouteMessage(RouteDemo.this); // ע����Ϣ������
						ol.addToMap(mMapView); // ���뵽��ͼ
						ArrayList<GeoPoint> pts = new ArrayList<GeoPoint>();
						pts.add(route.getLowerLeftPoint());
						pts.add(route.getUpperRightPoint());
						mMapView.getController().setFitView(pts);// ������ͼ��ʾ��Χ
						mMapView.invalidate();
					}
				}
			} else if (msg.what == Constants.ROUTE_SEARCH_ERROR) {
				progDialog.dismiss();
				showToast((String) msg.obj);
			}
		}
	};

	// RouteOverlay�϶������д���
	@Override
	public void onDrag(MapView arg0, RouteOverlay arg1, int arg2, GeoPoint arg3) {
		// TODO Auto-generated method stub

	}

	// RouteOverlay�϶���ʼʱ����
	@Override
	public void onDragBegin(MapView arg0, RouteOverlay arg1, int arg2,
			GeoPoint arg3) {
		// TODO Auto-generated method stub

	}

	// RouteOverlay�϶���ɴ���
	@Override
	public void onDragEnd(MapView mapView, RouteOverlay overlay, int index,
			GeoPoint pos) {
		// TODO Auto-generated method stub

		try {
			startPoint = overlay.getStartPos();
			endPoint = overlay.getEndPos();
			searchRouteResult(startPoint, endPoint);
		} catch (IllegalArgumentException e) {
			ol.restoreOverlay(mMapView);
			overlayToBack(ol, mMapView);
		} catch (Exception e1) {
			overlay.restoreOverlay(mMapView);
			overlayToBack(ol, mMapView);
		}
	}

	private void overlayToBack(RouteOverlay overlay, MapView mapView) {
		startPoint = overlay.getStartPos();
		endPoint = overlay.getEndPos();
	}

	@Override
	public boolean onRouteEvent(MapView arg0, RouteOverlay arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public class MapPointOverlay extends Overlay {
		private LayoutInflater inflater;
		private View popUpView;

		public MapPointOverlay(Context context) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);
		}

		@Override
		public boolean onTap(final GeoPoint point, final MapView view) {
			if (popUpView != null) {
				view.removeView(popUpView);
			}
			// Projection�ӿ�������Ļ���ص�����ϵͳ�͵�����澭γ�ȵ�����ϵͳ֮��ı任
			popUpView = inflater.inflate(R.layout.popup, null);
			TextView textView = (TextView) popUpView.findViewById(R.id.PoiName);
			textView.setText("�������ѡ��˵�");
			MapView.LayoutParams lp;
			lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, point, 0, 0,
					MapView.LayoutParams.BOTTOM_CENTER);
			view.addView(popUpView, lp);
			popUpView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (poiType.equals("startPoint")) {
						startTextView.setText("��ͼ�ϵĵ�");
						startTextView.selectAll();
						startPoint = point;
					}

					if (poiType.equals("endPoint")) {
						endTextView.setText("��ͼ�ϵĵ�");
						endTextView.selectAll();
						endPoint = point;
					}

					view.removeView(popUpView);
					view.getOverlays().remove(overlay);
				}
			});
			return super.onTap(point, view);
		}
	}
}
