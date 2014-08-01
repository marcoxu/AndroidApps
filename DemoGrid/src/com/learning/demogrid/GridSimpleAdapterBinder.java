package com.learning.demogrid;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.widget.GridView;

public class GridSimpleAdapterBinder extends GridAdapterInterface {
	private GridViewSimpleAdapter mSimple = null;

	@SuppressWarnings("unchecked")
	@Override
	public Object bind(GridView view, Context cntx, Object srcData,
			GridController data) {
        ArrayList<HashMap<String, String>> src = data.updateSrcTable((ArrayList<Integer>)srcData);
		mSimple = new GridViewSimpleAdapter(cntx,
				src,
                R.layout.activity_demo_grid_view,
                new String[] { "image_file" },
                new int[] { R.id.image_item },
                data);
		view.setAdapter(mSimple);
		return mSimple;
	}

}
