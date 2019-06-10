package com.example.tourplanner2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView myOpenMapView;
    private MapController myMapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GeoPoint madrid = new GeoPoint(40.416775, -3.70379);

        myOpenMapView = (MapView) findViewById(R.id.openmapview);
        myOpenMapView.setBuiltInZoomControls(true);
        myMapController = (MapController) myOpenMapView.getController();
        myMapController.setCenter(madrid);
        myMapController.setZoom(6);

        myOpenMapView.setMultiTouchControls(true);
    }
}
