package com.amap.cn.apis.busline;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.cn.apis.R;
import com.amap.cn.apis.util.Constants;
import com.amap.mapapi.busline.BusLineItem;
import com.amap.mapapi.busline.BusPagedResult;
import com.amap.mapapi.busline.BusQuery;
import com.amap.mapapi.busline.BusSearch;
import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapView;

public class BusLineSearchDemo extends MapActivity implements
		OnItemSelectedListener, OnClickListener,
		BusLineOverlay.BusLineMsgHandler {
	private MapView mMapView = null;
	private Button searchbynameBtn;
	private Spinner selectCity;
	private EditText searchName;
	private String[] itemCitys = { "北京-010", "上海-021", "西安-029" };
	private String cityCode;
	private EditText pageSizeText;
	private Button searchbystationBtn;
	private ProgressDialog progDialog = null;
	private BusPagedResult result = null;
	private BusLineOverlay overlay = null;
	private int curPage = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bussearch);
		mMapView = ((MapView) findViewById(R.id.buslinesearchmapview));
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		searchbynameBtn = (Button) this.findViewById(R.id.searchbyname);
		searchbynameBtn.setOnClickListener(this);
		selectCity = (Spinner) findViewById(R.id.cityName);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, itemCitys);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectCity.setAdapter(adapter);
		selectCity.setPrompt("请选择城市：");
		selectCity.setOnItemSelectedListener(this);
		searchName = (EditText) findViewById(R.id.busName);
		pageSizeText = (EditText) findViewById(R.id.pageSize);
		searchbystationBtn = (Button) findViewById(R.id.searchbystation);
		searchbystationBtn.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		if (overlay != null) {
			overlay.removeFromMap(mMapView);
		}
		super.onDestroy();
	}

	private void drawBusLine(BusLineItem busLine) {
		if (overlay != null) {
			overlay.removeFromMap(mMapView);
		}
		overlay = new BusLineOverlay(this, busLine);
		overlay.registerBusLineMessage(BusLineSearchDemo.this);
		overlay.addToMap(mMapView);
		ArrayList<GeoPoint> pts = new ArrayList<GeoPoint>();
		pts.add(busLine.getLowerLeftPoint());
		pts.add(busLine.getUpperRightPoint());
		mMapView.getController().setFitView(pts);// 调整地图显示范围
		mMapView.invalidate();
	}

	private void showResultList(List<BusLineItem> list) {
		BusSearchDialog dialog = new BusSearchDialog(BusLineSearchDemo.this,
				list);

		dialog.setTitle("搜索结果:");
		dialog.setOnListClickListener(new OnListItemClick() {
			@Override
			public void onListItemClick(BusSearchDialog dialog,
					final BusLineItem busLineItem) {
				progDialog = ProgressDialog.show(BusLineSearchDemo.this, null,
						"正在搜索...", true, false);
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						String lineId = busLineItem.getmLineId();
						BusSearch busSearch = new BusSearch(
								BusLineSearchDemo.this, new BusQuery(lineId,
										BusQuery.SearchType.BY_ID, cityCode)); // 设置搜索字符串
						try {
							result = busSearch.searchBusLine();
							buslineHandler
									.sendEmptyMessage(Constants.BUSLINE_DETAIL_RESULT);
						} catch (AMapException e) {
							Message msg = new Message();
							msg.what = Constants.BUSLINE_ERROR_RESULT;
							msg.obj = e.getErrorMessage();
							buslineHandler.sendMessage(msg);
						}
					}

				});
				t.start();
			}

		});
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		final Button btn = (Button) v;
		progDialog = ProgressDialog.show(BusLineSearchDemo.this, null,
				"正在搜索...", true, false);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String search = searchName.getText().toString().trim();
				BusQuery.SearchType type = BusQuery.SearchType.BY_LINE_NAME;
				if (searchbynameBtn.equals(btn)) {
					if ("".equals(search)) {
						search = "101";
					}
				} else if (searchbystationBtn.equals(btn)) {
					if ("".equals(search)) {
						search = "阜通东大街";
					}
					type = BusQuery.SearchType.BY_STATION_NAME;
				}
				try {
					curPage = 1;
					BusSearch busSearch = new BusSearch(BusLineSearchDemo.this,
							new BusQuery(search, type, cityCode)); // 设置搜索字符串
					busSearch.setPageSize(4);
					String text = pageSizeText.getText().toString();
					if (text.length() > 0)
						busSearch.setPageSize(Integer.parseInt(text));
					result = busSearch.searchBusLine();
					Log.d("AMAP POI search", "poi search page count = "
							+ result.getPageCount());
					buslineHandler.sendEmptyMessage(Constants.BUSLINE_RESULT);
				} catch (AMapException e) {
					Message msg = new Message();
					msg.what = Constants.BUSLINE_ERROR_RESULT;
					msg.obj = e.getErrorMessage();
					buslineHandler.sendMessage(msg);
				}
			}

		});
		t.start();
	}

	private Handler buslineHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.BUSLINE_RESULT) {
				progDialog.dismiss();
				if (overlay != null) {
					overlay.removeFromMap(mMapView);
				}
				List<BusLineItem> items;
				try {
					if (result == null
							|| (items = result.getPage(curPage)) == null
							|| items.size() == 0) {
						Toast.makeText(getApplicationContext(), "没有找到！",
								Toast.LENGTH_SHORT).show();
					} else {
						Log.d("AMAP busline search",
								"item number of 1st page = " + items.size());
						Log.d("AMAP busline search", items.toString());

						showResultList(items);
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}
			} else if (msg.what == Constants.BUSLINE_DETAIL_RESULT) {
				progDialog.dismiss();
				List<BusLineItem> list;
				try {
					if (result != null) {
						list = result.getPage(1);
						if (list != null && list.size() > 0) {
							drawBusLine(list.get(0));
						}
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}
			} else if (msg.what == Constants.BUSLINE_ERROR_RESULT) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), (String) msg.obj,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String cityString = itemCitys[position];
		cityCode = cityString.substring(cityString.indexOf("-") + 1);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// do nothing
	}

	interface OnListItemClick {
		/**
		 * This method will be invoked when the dialog is canceled.
		 * 
		 * @param dialog
		 *            The dialog that was canceled will be passed into the
		 *            method.
		 */
		public void onListItemClick(BusSearchDialog dialog, BusLineItem item);
	}

	public class BusSearchDialog extends Dialog implements OnItemClickListener,
			OnItemSelectedListener {
		private List<BusLineItem> busLineItems;
		private BusSearchAdapter adapter;
		protected OnListItemClick mOnClickListener;
		private Button preButton, nextButton;

		public BusSearchDialog(Context context) {
			this(context, android.R.style.Theme_Dialog);
		}

		public BusSearchDialog(Context context, int theme) {
			super(context, theme);
		}

		public BusSearchDialog(Context context, List<BusLineItem> busLineItems) {
			this(context, android.R.style.Theme_Dialog);
			this.busLineItems = busLineItems;
			adapter = new BusSearchAdapter(context, busLineItems);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.navsearch_list_busline);
			ListView listView = (ListView) findViewById(R.id.ListView_busline);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					dismiss();
					mOnClickListener.onListItemClick(BusSearchDialog.this,
							busLineItems.get(position));
				}
			});

			onButtonClick listener = new onButtonClick();
			preButton = (Button) findViewById(R.id.preButton);
			if (curPage <= 1) {
				preButton.setEnabled(false);
			}
			preButton.setOnClickListener(listener);
			nextButton = (Button) findViewById(R.id.nextButton);
			if (curPage >= result.getPageCount()) {
				nextButton.setEnabled(false);
			}
			nextButton.setOnClickListener(listener);
		}

		class onButtonClick implements View.OnClickListener {

			@Override
			public void onClick(View v) {
				BusSearchDialog.this.dismiss();
				if (v.equals(preButton)) {
					curPage--;
				} else if (v.equals(nextButton)) {
					curPage++;
				}

				progDialog = ProgressDialog.show(BusLineSearchDemo.this, null,
						"正在搜索...", true, false);
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							result.getPage(curPage);
							buslineHandler
									.sendEmptyMessage(Constants.BUSLINE_RESULT);
						} catch (AMapException e) {
							Message msg = new Message();
							msg.what = Constants.BUSLINE_ERROR_RESULT;
							msg.obj = e.getErrorMessage();
							buslineHandler.sendMessage(msg);
						}
					}

				});
				t.start();
			}
		}

		@Override
		public void onItemClick(AdapterView<?> view, View view1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

		public void setOnListClickListener(OnListItemClick l) {
			mOnClickListener = l;
		}
	}

	// Dialog list view adapter
	public class BusSearchAdapter extends BaseAdapter {
		private List<BusLineItem> busLineItems = null;
		private LayoutInflater mInflater;

		public BusSearchAdapter(Context context, List<BusLineItem> busLineItems) {
			this.busLineItems = busLineItems;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return busLineItems.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.bus_result_list, null);
			}

			TextView PoiName = ((TextView) convertView
					.findViewById(R.id.buslineName));
			TextView poiAddress = (TextView) convertView
					.findViewById(R.id.buslineLength);
			PoiName.setText(busLineItems.get(position).getmName());
			float length = busLineItems.get(position).getmLength();
			poiAddress.setText("全长:" + length + "公里");
			return convertView;
		}
	}

	@Override
	public boolean onStationClickEvent(MapView mapView, BusLineOverlay overlay,
			int index) {
		return false;
	}
}
