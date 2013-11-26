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
 * �ø�������������ʵ���������룬�����õ��ĵ�����Toast��ӡ�ڵ�ͼ��
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
		mMapView.setBuiltInZoomControls(true); // �����������õ����ſؼ�
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
				getLatlon("������");
			}

		});
		progDialog = new ProgressDialog(this);
	}

	// ��������
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
		progDialog.setMessage("���ڻ�ȡ��ַ");
		progDialog.show();
		t.start();
	}

	// �������
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
								+ addres.getFeatureName() + "����";
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
		progDialog.setMessage("���ڻ�ȡ��ַ");
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
				Toast.makeText(getApplicationContext(), "�������������Ƿ���ȷ?",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
}
