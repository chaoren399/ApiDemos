package com.amap.cn.apis.route;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.mapapi.core.PoiItem;
import com.amap.cn.apis.R;

public class RouteSearchAdapter extends BaseAdapter {
	private List<PoiItem> poiItems = null;
	private LayoutInflater mInflater;

	public RouteSearchAdapter(Context context, List<PoiItem> poiItems) {
		this.poiItems = poiItems;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return poiItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.poi_result_list, null);
		}

		TextView PoiName = ((TextView) convertView.findViewById(R.id.poiName));
		TextView poiAddress = (TextView) convertView
				.findViewById(R.id.poiAddress);
		PoiName.setText(poiItems.get(position).getTitle());
		String address = null;
		if (poiItems.get(position).getSnippet() != null) {
			address = poiItems.get(position).getSnippet();
		} else {
			address = "ÖÐ¹ú";
		}
		poiAddress.setText("µØÖ·:" + address);
		return convertView;
	}

}
