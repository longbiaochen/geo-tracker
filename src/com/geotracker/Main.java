package com.geotracker;

import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Main extends MapActivity {
	final long[] VIBRATE_PATTERN = { 500, 300 };
	final int GPS_INTERVAL = 10000;// GPS awake interval in million seconds
	final float ACCURACY_THRESHOLD = 33;// gps accuracy threshold in meters
	MapView mapView;
	TextView textView;
	MapController mapController;
	List<Overlay> mapOverlays;
	Context context;
	LocationManager locationManager;
	NotificationManager notificationManager;
	PositionOverlay positionOverlay;
	Location lastLocation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialization
		setContentView(R.layout.mapview);
		initView();
		initPosition();

		// listen to locaton update
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		context = this;
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				GPS_INTERVAL, 0, new LocationListener() {
					private float accuracy;
					private float lastAccuracy = 200; // accuracy threshold

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// pick up a best location when GPS goes asleep
						if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
							Log.e("Accuracy: ", accuracy + " meters");
							if (accuracy < ACCURACY_THRESHOLD) {
								// pick up only location in accuracy threshold
								markLocation(lastLocation);
								textView.setText("Marker Accuracy: " + accuracy
										+ " meters.");
								// trigger viberate
								Util.vibrate(vibrator, VIBRATE_PATTERN);

							}
							lastAccuracy = 200;
							textView.setText("Init Accuracy: " + lastAccuracy
									+ " meters.");

						}

					}

					@Override
					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProviderDisabled(String provider) {
						// TODO Auto-generated method stub
						Util.alert(context, "GPS disabled.");

					}

					@Override
					public void onLocationChanged(Location location) {
						accuracy = location.getAccuracy();
						if (accuracy < lastAccuracy) {
							// decrease toast accuracy to narrow down
							lastLocation = location;
							lastAccuracy = accuracy;
							textView.setText("Changing Accuracy: " + accuracy
									+ " meters.");

						}

					}

				});

	}

	// show the mapview
	private void initView() {
		// MapView
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setKeepScreenOn(true);
		mapView.getZoomButtonsController().setAutoDismissed(false);
		mapView.setSatellite(false);
		mapController = mapView.getController();
		mapController.setZoom(17);
		mapOverlays = mapView.getOverlays();
		// TextView
		textView = (TextView) findViewById(R.id.textview);
		textView.setTextSize(20);
	}

	// setup initial location
	private void initPosition() {
		// setup position layer
		Drawable positionMarker = this.getResources().getDrawable(
				R.drawable.position);
		positionOverlay = new PositionOverlay(positionMarker, getBaseContext());
		// get last location
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		lastLocation = locationManager.getLastKnownLocation(locationManager
				.getBestProvider(new Criteria(), true));
		if (null != lastLocation) {
			// set text
			textView.setText("Init Accuracy: " + lastLocation.getAccuracy()
					+ " meters.");
			markLocation(lastLocation);
		}

	}

	// mark location on the mapview
	private void markLocation(Location location) {
		// fix location offset in mapview, do not apply for satellite view
		// location = Util.fixLocation(location);
		GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		mapController.animateTo(point);
		positionOverlay.addOverlay(new OverlayItem(point, "Current Position",
				""));
		// Log.e("MapOverlay Size: ", "" + mapOverlays.size());
		if (positionOverlay.id >= 0) {
			// positionOverlay is already added to the mapOverlays
			mapOverlays.set(positionOverlay.id, positionOverlay);
		} else {
			mapOverlays.add(positionOverlay);
			positionOverlay.id = mapOverlays.size() - 1;
		}
		mapView.invalidate();

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}