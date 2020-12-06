package com.geotracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Vibrator;

public class Util {
	final static double LAT_FIX = -0.002300;
	final static double LNG_FIX = 0.004710;

	public static void alert(Context context, String msg) {
		new AlertDialog.Builder(context).setTitle("Notice").setMessage(msg).setPositiveButton("OK", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).setCancelable(false).create().show();
	}

	public static void vibrate(Vibrator vibrator, long[] pattern) {
		// vibrate once for the specific pattern
		vibrator.vibrate(pattern, 1);

	}

	public static Location fixLocation(Location location) {
		location.setLatitude(location.getLatitude() + LAT_FIX);
		location.setLongitude(location.getLongitude() + LNG_FIX);
		return location;

	}
}
