package org.apptirol.innsbruckmastermeter;

import Model.Manager;
import Model.Point;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
//
//
//public class MasterMeterActivity extends Activity {
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_master_meter);
//	}
//}
/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2013-2014 Ludwig M Brinckmann
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
//package org.apptirol.innsbruckmastermeter;






import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.layer.Layer;
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

		// a marker to show at the position 
		Drawable pos_i = getResources().getDrawable(R.drawable.position);
		Drawable p180_i = getResources().getDrawable(R.drawable.p180min_s);
		Drawable p90_i = getResources().getDrawable(R.drawable.p90min_s);
		Drawable parkw_i = getResources().getDrawable(R.drawable.parkw_s);
		Drawable parkt_i = getResources().getDrawable(R.drawable.parkt_s);
		Bitmap bpos_i = AndroidGraphicFactory.convertToBitmap(pos_i);
		Bitmap bp180_i = AndroidGraphicFactory.convertToBitmap(p180_i);
		Bitmap bp90_i = AndroidGraphicFactory.convertToBitmap(p90_i);
		Bitmap bparkw_i = AndroidGraphicFactory.convertToBitmap(parkw_i);
		Bitmap bparkt_i = AndroidGraphicFactory.convertToBitmap(parkt_i);
		
		// create the overlay and tell it to follow the location
		this.myLocationOverlay = new MyLocationOverlay(this,
				this.mapView.getModel().mapViewPosition, bpos_i);
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