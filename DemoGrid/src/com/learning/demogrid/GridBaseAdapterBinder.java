package com.learning.demogrid;

import java.util.ArrayList;

import android.content.Context;
import android.widget.GridView;

public class GridBaseAdapterBinder extends GridAdapterInterface {
	private GridViewAdapter mAdapter = null;

	public GridBaseAdapterBinder() {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object bind(GridView view, Context cntx, Object srcData,
			GridController data) {
		mAdapter = new GridViewAdapter(cntx,
                0,
                (ArrayList<Integer>)srcData,
                data);
		view.setAdapter(mAdapter);
		return mAdapter;
	}

}
