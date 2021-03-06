package org.apptirol.innsbruckmastermeter.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.apptirol.innsbruckmastermeter.R;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.renderer.MapWorker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.scalebar.ImperialUnitAdapter;
import org.mapsforge.map.scalebar.MetricUnitAdapter;
import org.mapsforge.map.scalebar.NauticalUnitAdapter;

/**
 * Code common to most activities in the Samples app.
 */
public abstract class MapViewBaseActivity extends MapViewerTemplate implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String SETTING_SCALEBAR = "scalebar";
	public static final String SETTING_SCALEBAR_METRIC = "metric";
	public static final String SETTING_SCALEBAR_IMPERIAL = "imperial";
	public static final String SETTING_SCALEBAR_NAUTICAL = "nautical";
	public static final String SETTING_SCALEBAR_BOTH = "both";
	public static final String SETTING_SCALEBAR_NONE = "none";
	
	public static final String SETTING_DEBUG_TIMING = "debug_timing";
	public static final String SETTING_SCALE = "scale";
	public static final String SETTING_TEXTWIDTH = "textwidth";
	public static final String SETTING_WAYFILTERING = "wayfiltering";
	public static final String SETTING_WAYFILTERING_DISTANCE = "wayfiltering_distance";
	public static final String SETTING_TILECACHE_THREADING = "tilecache_threading";
	public static final String SETTING_TILECACHE_QUEUESIZE = "tilecache_queuesize";
	public static final String TAG = "SAMPLES APP";
	
	protected static final int DIALOG_ENTER_COORDINATES = 2923878;
	protected static final int LEGEND = 2923878+77;
	protected SharedPreferences sharedPreferences;
	TileDownloadLayer downloadLayer;
	@Override
	protected int getLayoutId() {
		return R.layout.mapviewer;
	}

	@Override
	protected int getMapViewId() 
	{
		return R.id.mapView;
	}

	@Override
	protected MapPosition getDefaultInitialPosition() {
		return new MapPosition(new LatLong(47.263534,11.393943), getZoomLevelDefault());
	}

	@Override
	protected void createLayers() {
		if(checkMapFile()) {
			TileRendererLayer tileRendererLayer = AndroidUtil.createTileRendererLayer(this.tileCaches.get(0),
					mapView.getModel().mapViewPosition, getMapFile(), getRenderTheme(), false, true);
			this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

			// needed only for samples to hook into Settings.
			setMaxTextWidthFactor();
		} else {
			downloadLayer = new TileDownloadLayer(this.tileCaches.get(0),
					this.mapView.getModel().mapViewPosition, OpenStreetMapMapnik.INSTANCE,
					AndroidGraphicFactory.INSTANCE);
			mapView.getLayerManager().getLayers().add(downloadLayer);
		}
	}

	@Override
	protected void createControls() {
		setMapScaleBar();
	}

	protected void createTileCaches() {
		boolean threaded = sharedPreferences.getBoolean(SETTING_TILECACHE_THREADING, true);
		int queueSize = Integer.parseInt(sharedPreferences.getString(SETTING_TILECACHE_QUEUESIZE, "4"));

		this.tileCaches.add(AndroidUtil.createTileCache(this, getPersistableId(),
				this.mapView.getModel().displayModel.getTileSize(), this.getScreenRatio(),
				this.mapView.getModel().frameBufferModel.getOverdrawFactor(),
				threaded, queueSize
		));
	}

	/**
	 * @return the map file name to be used
	 */
	protected String getMapFileName() {
		return "Innsbruck.map";
	}

	protected void onDestroy() {
		super.onDestroy();
		this.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}
	@Override
	public void onPause() {
		super.onPause();
		if(downloadLayer != null)
			downloadLayer.onPause();
		
	}

	@Override
	public void onResume() {
		super.onResume();
		if(downloadLayer != null)
			downloadLayer.onResume();
	}
	/*
	 * Settings related methods.
	 */
	@Override
	protected void createSharedPreferences() {
		super.createSharedPreferences();

		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		// problem that the first call to getAll() returns nothing, apparently the
		// following two calls have to be made to read all the values correctly
		// http://stackoverflow.com/questions/9310479/how-to-iterate-through-all-keys-of-shared-preferences
		this.sharedPreferences.edit().clear();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

		this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Deprecated
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View view;
		switch (id) {
			case DIALOG_ENTER_COORDINATES:
				builder.setIcon(android.R.drawable.ic_menu_mylocation);
				builder.setTitle(R.string.dialog_location_title);
				view = factory.inflate(R.layout.dialog_enter_coordinates, null);
				builder.setView(view);
				builder.setPositiveButton(R.string.okbutton, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						double lat = Double.parseDouble(((EditText) view.findViewById(R.id.latitude)).getText()
								.toString());
						double lon = Double.parseDouble(((EditText) view.findViewById(R.id.longitude)).getText()
								.toString());
						byte zoomLevel = (byte) ((((SeekBar) view.findViewById(R.id.zoomlevel)).getProgress()) +
								MapViewBaseActivity.this.mapView.getModel().mapViewPosition.getZoomLevelMin());

						MapViewBaseActivity.this.mapView.getModel().mapViewPosition.setMapPosition(
								new MapPosition(new LatLong(lat, lon), zoomLevel));
					}
				});
				builder.setNegativeButton(R.string.cancelbutton, null);
				return builder.create();
			case LEGEND:
				builder.setTitle(R.string.menu_legend);
				view = factory.inflate(R.layout.dialog_legend, null);
				builder.setView(view);
				return builder.create();
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_position_enter_coordinates:
				showDialog(DIALOG_ENTER_COORDINATES);
				break;
			case R.id.menu_legend:
				showDialog(LEGEND);
				
		}
		return false;
	}

	@Deprecated
	@Override
	protected void onPrepareDialog(int id, final Dialog dialog) {
		if (id == this.DIALOG_ENTER_COORDINATES) {
			MapViewPosition currentPosition = MapViewBaseActivity.this.mapView.getModel().mapViewPosition;
			LatLong currentCenter = currentPosition.getCenter();
			EditText editText = (EditText) dialog.findViewById(R.id.latitude);
			editText.setText(Double.toString(currentCenter.latitude));
			editText = (EditText) dialog.findViewById(R.id.longitude);
			editText.setText(Double.toString(currentCenter.longitude));
			SeekBar zoomlevel = (SeekBar) dialog.findViewById(R.id.zoomlevel);
			zoomlevel.setMax(currentPosition.getZoomLevelMax() - currentPosition.getZoomLevelMin());
			zoomlevel.setProgress(MapViewBaseActivity.this.mapView.getModel().mapViewPosition.getZoomLevel()
					- currentPosition.getZoomLevelMin());
			final TextView textView = (TextView) dialog.findViewById(R.id.zoomlevelValue);
			textView.setText(String.valueOf(zoomlevel.getProgress()));
			zoomlevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					textView.setText(String.valueOf(progress));
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// nothing
				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// nothing
				}
			});
		} else {
			super.onPrepareDialog(id, dialog);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (SETTING_SCALE.equals(key)) {
			this.mapView.getModel().displayModel.setUserScaleFactor(DisplayModel.getDefaultUserScaleFactor());
			Log.d(TAG, "Tilesize now " + this.mapView.getModel().displayModel.getTileSize());
			AndroidUtil.restartActivity(this);
		}
		if (SETTING_TEXTWIDTH.equals(key)) {
			AndroidUtil.restartActivity(this);
		}
		if (SETTING_TILECACHE_QUEUESIZE.equals(key) || SETTING_TILECACHE_THREADING.equals(key)) {
			AndroidUtil.restartActivity(this);
		}
		if (SETTING_SCALEBAR.equals(key)) {
			setMapScaleBar();
		}
		if (SETTING_DEBUG_TIMING.equals(key)) {
			MapWorker.DEBUG_TIMING = preferences.getBoolean(SETTING_DEBUG_TIMING, false);
		}
		if (SETTING_WAYFILTERING_DISTANCE.equals(key) ||
				SETTING_WAYFILTERING.equals(key)) {
			MapDatabase.wayFilterEnabled = preferences.getBoolean(SETTING_WAYFILTERING, true);
			if (MapDatabase.wayFilterEnabled) {
				MapDatabase.wayFilterDistance = Integer.parseInt(preferences.getString(SETTING_WAYFILTERING_DISTANCE, "20"));
			}
		}
	}

	/**
	 * Sets the scale bar from preferences.
	 */
	protected void setMapScaleBar() {
		String value = this.sharedPreferences.getString(SETTING_SCALEBAR, SETTING_SCALEBAR_BOTH);

		if (SETTING_SCALEBAR_NONE.equals(value)) {
			AndroidUtil.setMapScaleBar(this.mapView, null, null);
		} else {
			if (SETTING_SCALEBAR_BOTH.equals(value)) {
				AndroidUtil.setMapScaleBar(this.mapView, MetricUnitAdapter.INSTANCE, ImperialUnitAdapter.INSTANCE);
			} else if (SETTING_SCALEBAR_METRIC.equals(value)) {
				AndroidUtil.setMapScaleBar(this.mapView, MetricUnitAdapter.INSTANCE, null);
			} else if (SETTING_SCALEBAR_IMPERIAL.equals(value)) {
				AndroidUtil.setMapScaleBar(this.mapView, ImperialUnitAdapter.INSTANCE, null);
			} else if (SETTING_SCALEBAR_NAUTICAL.equals(value)) {
				AndroidUtil.setMapScaleBar(this.mapView, NauticalUnitAdapter.INSTANCE, null);
			}
		}
	}

	/**
	 * sets the value for breaking line text in labels.
	 */
	protected void setMaxTextWidthFactor() {
		mapView.getModel().displayModel.setMaxTextWidthFactor(Float.valueOf(sharedPreferences.getString(SETTING_TEXTWIDTH, "0.7")));
	}

}