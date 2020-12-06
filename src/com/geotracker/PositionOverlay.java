package com.geotracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class PositionOverlay extends ItemizedOverlay<OverlayItem> {
	final int OFFSET_X = 1;
	final int OFFSET_Y = 7;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private Paint paint;
	private Path path;
	private Projection projection;
	private Point itemPoint;
	private boolean isFirstElement = true;
	int id = -1;

	public PositionOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));

		mContext = context;
		paint = new Paint();
		paint.setColor(Color.rgb(127, 125, 237));
		paint.setAlpha(100);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(8);
		path = new Path();

	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		Log.e("Index: ", "" + index);
		OverlayItem item = mOverlays.get(index);
		Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT);
		return true;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
		// draw path
		path.reset();
		projection = mapView.getProjection();
		isFirstElement = true;
		for (OverlayItem item : mOverlays) {
			itemPoint = projection.toPixels(item.getPoint(), null);
			if (isFirstElement) {
				path.moveTo(itemPoint.x - OFFSET_X, itemPoint.y - OFFSET_Y);
				isFirstElement = false;
			} else {
				path.lineTo(itemPoint.x - OFFSET_X, itemPoint.y - OFFSET_Y);
			}
		}
		canvas.drawPath(path, paint);

	}

}
