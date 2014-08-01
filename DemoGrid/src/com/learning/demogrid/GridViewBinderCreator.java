package com.learning.demogrid;

public class GridViewBinderCreator {
		
	public static enum GridViewAdapterType {
		SIMPLE, BASE
	}
	
	public static GridAdapterInterface createGridAdapter(GridViewAdapterType type) {
		if(type == GridViewAdapterType.BASE) {
			return new GridBaseAdapterBinder();
		} else if (type == GridViewAdapterType.SIMPLE) {
			return new GridSimpleAdapterBinder();
		}
		return null;
	}
}
