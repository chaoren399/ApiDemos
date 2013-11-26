package com.amap.cn.apis.offlinemap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.cn.apis.R;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.offlinemap.OfflineMapManager;
import com.amap.mapapi.offlinemap.MOfflineMapStatus;
import com.amap.mapapi.offlinemap.OfflineMapManager.OfflineMapDownloadListener;

public class OfflineMapDemo extends MapActivity implements OfflineMapDownloadListener {
	private OfflineMapManager mOffline = null;
	private EditText mEditCityName;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offlinemap);  
        mOffline = new OfflineMapManager(this,this); 
        mEditCityName = (EditText)findViewById(R.id.city);   
        Button btn = (Button)findViewById(R.id.start);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				mOffline.downloadByCityName(mEditCityName.getText().toString());
			}
		});
        
        btn = (Button)findViewById(R.id.stop);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				mOffline.stop();
			}
		}); 
        
        
        btn = (Button)findViewById(R.id.del);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				mOffline.remove(mEditCityName.getText().toString());
			}
		}); 
        
     
        btn = (Button)findViewById(R.id.pause);
        btn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				mOffline.pause();
			}
		}); 

	}
	



	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onDownload(int status, int completeCode) {
		// TODO Auto-generated method stub
		switch(status)
		{
		case MOfflineMapStatus.LOADING:
			Toast.makeText(this,
					"正在下载,"+ "已完成：" + completeCode+"%",
					Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(this,
					"status:" + status + ",comlete:" + completeCode,
					Toast.LENGTH_SHORT).show();
			break;
		}
	}
}

