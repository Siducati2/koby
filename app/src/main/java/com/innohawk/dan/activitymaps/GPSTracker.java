package com.innohawk.dan.activitymaps;

/**
 * Created by innohawk on 20/10/17.
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.appconfig.AppConfig;

public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    double old_lat = 0.0; //es para guardar y enviar al servidor si se cambian las coordenadas
    double old_lng = 0.0;

    String isGuest; //Lo fijaremos en un principio!

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 1 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;


    public GPSTracker(Context context,String guest) {
        this.mContext = context;
        isGuest = guest;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        Log.d("GPSTRACKER", "Coordenadas enviadas : ");
        //double lat_server = String.valueOf(location.getLatitude());
        //double lng_server = String.valueOf(location.getLongitude());

        double lat_server = location.getLatitude();
        double lng_server = location.getLongitude();

        if(old_lat != lat_server && old_lng != lng_server){ //Esto es que se han iniciado!!! entonces se deberá cambiar la variable
            old_lat = lat_server;
            old_lng = lng_server;
            //Transformamos a String
            String lat_serverConvert = String.valueOf(location.getLatitude());
            String lng_serverConvert = String.valueOf(location.getLongitude());
//ConnectorMaps(String sUrl, String sUsername, String sPwd, int iMemberId)
            Connector o = Main.getConnector();
            Object[] aParams = {
                    o.getUsername(),
                    o.getPassword(),
                    lat_serverConvert,
                    lng_serverConvert,
                    isGuest
            };
            //OJO!!! AÑADIDO PARA EVITAR EL "LOAD"...
            //Y hemos creado un connector nuevo para no liarlo con el otro!!!!!!
            ConnectorMaps oo = new ConnectorMaps(AppConfig.URL_METHODS, o.getUsername(),o.getPassword(), o.getMemberId());
            oo.execAsyncMethodMyCoordenates("ih.coord", aParams, new ConnectorMaps.Callback() {
                @SuppressWarnings("unchecked")
                public void callFinished(Object result) {
                    Log.d("GPSTRACKER", "Coordenadas enviadas : ");
                }
            }, this);

            //Y asi evitamos que siempre se envien! solo cuando se cambia
        }


    }
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
