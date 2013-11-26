package com.amap.cn.apis.geocoder;

import java.util.List;

import android.app.ProgressDialog;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.amap.cn.apis.R;
import com.amap.cn.apis.util.Constants;
import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.geocoder.Geocoder;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapView;

/**
 * 用给定的坐标数据实现逆地理编码，并将得到的地名用Toast打印在地图上
 */
public class GeocoderDemo extends MapActivity {
	private MapView mMapView;
	private Button btn;
	private Button resBtn;
	private ProgressDialog progDialog = null;
	private Geocoder coder;
	private double mLat = 39.982402;
	private double mLon = 116.305304;
	private String addressName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geocoder);
		btn = (Button) this.findViewById(R.id.geobtn);
		mMapView = ((MapView) findViewById(R.id.geocode_MapView));
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		coder = new Geocoder(this);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getAddress(mLat, mLon);
			}
		});
		resBtn = (Button) this.findViewById(R.id.resgeobtn);
		resBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getLatlon("王府井");
			}

		});
		progDialog = new ProgressDialog(this);
	}

	// 逆地理编码
	public void getLatlon(final String name) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					List<Address> address = coder.getFromLocationName(name, 3);
					if (address != null && address.size() > 0) {
						Address addres = address.get(0);
						addressName = addres.getLatitude() + ","
								+ addres.getLongitude();
						handler.sendMessage(Message.obtain(handler,
								Constants.REOCODER_RESULT));

					}
				} catch (AMapException e) {
					handler.sendMessage(Message
							.obtain(handler, Constants.ERROR));
				}

			}
		});

		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在获取地址");
		progDialog.show();
		t.start();
	}

	// 地理编码
	public void getAddress(final double mlat, final double mLon) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					List<Address> address = coder
							.getFromLocation(mlat, mLon, 3);
					if (address != null && address.size() > 0) {
						Address addres = address.get(0);
						addressName = addres.getAdminArea()
								+ addres.getSubLocality()
								+ addres.getFeatureName() + "附近";
						handler.sendMessage(Message.obtain(handler,
								Constants.REOCODER_RESULT));

					}
				} catch (AMapException e) {
					// TODO Auto-generated catch block
					handler.sendMessage(Message
							.obtain(handler, Constants.ERROR));
				}

			}
		});

		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在获取地址");
		progDialog.show();
		t.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.REOCODER_RESULT) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), addressName,
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == Constants.ERROR) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), "请检查网络连接是否正确?",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
}
