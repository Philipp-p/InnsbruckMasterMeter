package org.apptirol.innsbruckmastermeter;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.renderer.MapWorker;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.reader.MapDatabase;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MasterMeterApplication extends Application {

	public static final String SETTING_DEBUG_TIMING = "debug_timing";
	public static final String SETTING_SCALE = "scale";
	public static final String SETTING_TEXTWIDTH = "textwidth";
	public static final String SETTING_WAYFILTERING = "wayfiltering";
	public static final String SETTING_WAYFILTERING_DISTANCE = "wayfiltering_distance";
	public static final String SETTING_TILECACHE_THREADING = "tilecache_threading";
	public static final String SETTING_TILECACHE_QUEUESIZE = "tilecache_queuesize";
	public static final String TAG = "SAMPLES APP";

	@Override
	public void onCreate() {
		super.onCreate();
		AndroidGraphicFactory.createInstance(this);
		Log.e(TAG,
				"Device scale factor "
						+ Float.toString(DisplayModel.getDeviceScaleFactor()));
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		float fs = Float.valueOf(preferences.getString(SETTING_SCALE,
				Float.toString(DisplayModel.getDefaultUserScaleFactor())));
		Log.e(TAG, "User ScaleFactor " + Float.toString(fs));
		if (fs != DisplayModel.getDefaultUserScaleFactor()) {
			DisplayModel.setDefaultUserScaleFactor(fs);
		}

		MapDatabase.wayFilterEnabled = preferences.getBoolean(SETTING_WAYFILTERING, true);
		if (MapDatabase.wayFilterEnabled) {
			MapDatabase.wayFilterDistance = Integer.parseInt(preferences.getString(SETTING_WAYFILTERING_DISTANCE, "20"));
		}
		MapWorker.DEBUG_TIMING = preferences.getBoolean(SETTING_DEBUG_TIMING, false);
	}
}