package com.cnnet.otc.health.bluetooth;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.MyBlueToothDevice;

public class DeviceListAdapter extends BaseAdapter {
	private ArrayList<MyBlueToothDevice> list;
	private LayoutInflater mInflater;

	public DeviceListAdapter(Context context, ArrayList<MyBlueToothDevice> list2) {
		this.list = list2;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		MyBlueToothDevice item = list.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			viewHolder = new ViewHolder(
					convertView.findViewById(R.id.list_child),
					(TextView) convertView.findViewById(R.id.chat_msg));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.child.setBackgroundResource(R.drawable.list_selector);
		viewHolder.msg.setText(item.getDevice_name() + "\n" + item.getAddress());

		
		return convertView;
	}

	class ViewHolder {
		protected View child;
		protected TextView msg;

		public ViewHolder(View child, TextView msg) {
			this.child = child;
			this.msg = msg;
		}
	}
}
