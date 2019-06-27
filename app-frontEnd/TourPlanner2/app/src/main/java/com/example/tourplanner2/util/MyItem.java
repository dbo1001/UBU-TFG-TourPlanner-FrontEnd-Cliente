package com.example.tourplanner2.util;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

public class MyItem extends ItemizedOverlay {
	private int size = 0;

	@Override
	protected OverlayItem createItem(int i) {
		return null;
	}

	@Override
	public int size() {
		return size;
	}

	public MyItem(Drawable pDefaultMarker) {
		super(pDefaultMarker);
	}

	@Override
	public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
		return false;
	}

	@Override
	public Drawable boundToHotspot(final Drawable marker, OverlayItem.HotspotPlace hotspot){
		if (hotspot == null) {
			hotspot = OverlayItem.HotspotPlace.BOTTOM_CENTER;
		}
		final int markerWidth = marker.getIntrinsicWidth();
		final int markerHeight = marker.getIntrinsicHeight();
		final int offsetX;
		final int offsetY;
		switch(hotspot) {
			default:
			case NONE:
			case LEFT_CENTER:
			case UPPER_LEFT_CORNER:
			case LOWER_LEFT_CORNER:
				offsetX = 0;
				break;
			case CENTER:
			case BOTTOM_CENTER:
			case TOP_CENTER:
				offsetX = -markerWidth / 2;
				break;
			case RIGHT_CENTER:
			case UPPER_RIGHT_CORNER:
			case LOWER_RIGHT_CORNER:
				offsetX = -markerWidth;
				break;
		}
		switch (hotspot) {
			default:
			case NONE:
			case TOP_CENTER:
			case UPPER_LEFT_CORNER:
			case UPPER_RIGHT_CORNER:
				offsetY = 0;
				break;
			case CENTER:
			case RIGHT_CENTER:
			case LEFT_CENTER:
				offsetY = -markerHeight / 2;
				break;
			case BOTTOM_CENTER:
			case LOWER_RIGHT_CORNER:
			case LOWER_LEFT_CORNER:
				offsetY = -markerHeight;
				break;
		}
		marker.setBounds(offsetX, offsetY, offsetX + markerWidth, offsetY + markerHeight);
		return marker;
	}
}
