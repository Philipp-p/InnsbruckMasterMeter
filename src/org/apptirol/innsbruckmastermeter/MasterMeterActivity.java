package org.apptirol.innsbruckmastermeter;

import java.io.IOException;

import android.os.Bundle;

import org.apptirol.innsbruckmastermeter.model.Manager;
import org.apptirol.innsbruckmastermeter.model.Point;
import org.apptirol.innsbruckmastermeter.view.RenderTheme4;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.layer.overlay.Marker;

import android.graphics.drawable.Drawable;

/**
 * MapViewer that shows current position. In the data directory of the Samples
 * project is the file berlin.gpx that can be loaded in the Android Monitor to
 * simulate location data in the center of Berlin.
 */
public class MasterMeterActivity extends RenderTheme4 {
	private MyLocationOverlay myLocationOverlay;
	private Manager manager;
	@Override
	public void onPause() {
		myLocationOverlay.disableMyLocation();
		super.onPause();
	}

	public void onResume() {
		super.onResume();
		this.myLocationOverlay.enableMyLocation(true);
	}
	public void onCreate(Bundle savedInstanceState, android.os.PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
	};
	@Override
	protected void createLayers() {
		super.createLayers();
		manager = new Manager();
		try {
			manager.fillTrees(getAssets().open("map/parkscheinautomaten.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// a marker to show at the position 
		Drawable pos_i = getResources().getDrawable(R.drawable.position);
		Drawable p180_i = getResources().getDrawable(R.drawable.p180min);
		Drawable p90_i = getResources().getDrawable(R.drawable.p90min);
		Drawable parkw_i = getResources().getDrawable(R.drawable.parkw);
		Drawable parkt_i = getResources().getDrawable(R.drawable.parkt);
		Bitmap bpos_i = AndroidGraphicFactory.convertToBitmap(pos_i);
		Bitmap bp180_i = AndroidGraphicFactory.convertToBitmap(p180_i);
		Bitmap bp90_i = AndroidGraphicFactory.convertToBitmap(p90_i);
		Bitmap bparkw_i = AndroidGraphicFactory.convertToBitmap(parkw_i);
		Bitmap bparkt_i = AndroidGraphicFactory.convertToBitmap(parkt_i);
		
		// create the overlay and tell it to follow the location
		this.myLocationOverlay = new MyLocationOverlay(this,
				this.mapView.getModel().mapViewPosition, bpos_i);
		// add every mastermetericon
		for(Point pos : manager.get90MasterMeters())
			mapView.getLayerManager().getLayers().add(new Marker(new LatLong(pos.getY(), pos.getX()), bp90_i, 0, 0));
		for(Point pos : manager.get180MasterMeters())
			mapView.getLayerManager().getLayers().add(new Marker(new LatLong(pos.getY(), pos.getX()), bp180_i, 0, 0));
		for(Point pos : manager.getParkWMasterMeters())
			mapView.getLayerManager().getLayers().add(new Marker(new LatLong(pos.getY(), pos.getX()), bparkw_i, 0, 0));
		for(Point pos : manager.getParkTMasterMeters())
			mapView.getLayerManager().getLayers().add(new Marker(new LatLong(pos.getY(), pos.getX()), bparkt_i, 0, 0));
		this.myLocationOverlay.setSnapToLocationEnabled(true);
		mapView.getLayerManager().getLayers().add(this.myLocationOverlay);
	}
}