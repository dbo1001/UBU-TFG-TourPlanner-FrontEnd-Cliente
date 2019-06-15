package com.example.tourplanner2;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class OSMUpdateLocation implements LocationListener {

    private MainActivity actividad;

    public OSMUpdateLocation(MainActivity actividad) {
        this.actividad = actividad;
    }

    @Override
    public void onLocationChanged(Location location) {
        actividad.onRealTimeLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
