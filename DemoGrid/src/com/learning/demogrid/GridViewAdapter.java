package com.learning.demogrid;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {

	private Context mContext = null;
	private ArrayList<Integer> srcImags;
	private GridController mData = null;
	
	public GridViewAdapter(Context context,
			int resource, ArrayList<Integer> to, GridController common) {
		super();

        srcImags = to;
		mContext = context;
		mData = common;
	}
	
	@Override
	public int getCount() {
		return srcImags.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return srcImags.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView v = (ImageView) convertView;

		// if convertView's not recycled, initialize some attributes
		if (v == null) {
			v = new ImageView(mContext);
			v.setLayoutParams(new GridView.LayoutParams(0, 0));
			v.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            v.setBackgroundColor(Color.BLACK);
            v.setImageBitmap(null);
		}
		
        String image = srcImags.get(position).toString();
        mData.startLoadImage(image, v);
		return v;
	}
}

