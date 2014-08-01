package com.learning.demogrid;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class GridViewSimpleAdapter extends SimpleAdapter {

	private GridController mData = null;
	
	public GridViewSimpleAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to, GridController common) {
		super(context, data, resource, from, to);

		mData = common;
	}
	
    public void setViewImage(ImageView v, String image) {
        mData.startLoadImage(image, v);
	}
}
