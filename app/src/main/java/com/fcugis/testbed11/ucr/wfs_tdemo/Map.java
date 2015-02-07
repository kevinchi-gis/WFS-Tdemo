package com.fcugis.testbed11.ucr.wfs_tdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.fcugis.testbed11.ucr.wfs_tdemo.util.SystemUiHider;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Map extends Activity {

    private MapView mapView;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        createMapView(savedInstanceState);

    }

    private void createMapView(Bundle savedState) {
        try {
            mapView = (MapView)findViewById(R.id.mapView);
            mapView.onCreate(savedState);
            mapView.onResume();;
            MapsInitializer.initialize(getApplicationContext());
            map = mapView.getMap();
            map.setMyLocationEnabled(true);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } catch (NullPointerException e) {
            Log.e("Map", e.toString());
        }
    }
}
