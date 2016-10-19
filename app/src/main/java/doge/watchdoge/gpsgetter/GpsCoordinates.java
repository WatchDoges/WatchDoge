package doge.watchdoge.gpsgetter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;

import java.util.Calendar;

import static java.lang.Thread.sleep;

public class GpsCoordinates {
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static String locationProvider;

    private static Pair<Double, Double> gpsCoords;
    private static float gpsAccuracy = 0;
    private long gpsAge = 0;

    public GpsCoordinates(Context context){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Use GPS location data
        locationProvider = LocationManager.GPS_PROVIDER;

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // execute if accuracy of new location is not 0.0
                // and less(/better) than current accuracy
                // or if age of coordinates is more than 5s
                if((location.getAccuracy()>(float)0.0 && location.getAccuracy()<gpsAccuracy)
                        || ((Calendar.getInstance().getTimeInMillis()-gpsAge)>5000)){
                    gpsCoords = new Pair<Double, Double>(location.getLatitude(),location.getLongitude());
                    gpsAccuracy = location.getAccuracy();
                    gpsAge = location.getTime();
                }
            }

            //TODO remove used listener and create new with the best available provider
            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (provider.equals(locationManager.GPS_PROVIDER)){
                    if(status == LocationProvider.AVAILABLE && locationProvider.equals(locationManager.NETWORK_PROVIDER)){
                        locationProvider = provider;
                    }
                    else if((status == LocationProvider.TEMPORARILY_UNAVAILABLE || status == LocationProvider.OUT_OF_SERVICE) && locationProvider.equals(locationManager.GPS_PROVIDER)){
                        locationProvider = locationManager.NETWORK_PROVIDER;
                    }
                }
                else if (provider.equals(locationManager.NETWORK_PROVIDER)){
                    if(status == LocationProvider.AVAILABLE && locationProvider.equals(locationManager.GPS_PROVIDER)){
                    }
                    else if((status == LocationProvider.TEMPORARILY_UNAVAILABLE || status == LocationProvider.OUT_OF_SERVICE) && locationProvider.equals(locationManager.NETWORK_PROVIDER)){
                        locationProvider = locationManager.GPS_PROVIDER;
                    }
                }
            }

            //TODO remove used listener and create new with the best available provider
            public void onProviderEnabled(String provider) {
                if(provider.equals(locationManager.GPS_PROVIDER) && locationProvider.equals(locationManager.NETWORK_PROVIDER)){
                    locationProvider = locationManager.GPS_PROVIDER;
                }
                else if(provider.equals(locationManager.NETWORK_PROVIDER) && locationProvider.equals(locationManager.GPS_PROVIDER)){
                    // Nothing to do, better accuracy is already used
                }
            }

            //TODO remove used listener and create new with the best available provider
            public void onProviderDisabled(String provider) {
                if(provider.equals(locationManager.GPS_PROVIDER)){
                    locationProvider = locationManager.NETWORK_PROVIDER;
                }
                else if(provider.equals(locationManager.NETWORK_PROVIDER)){
                    locationProvider = locationManager.GPS_PROVIDER;
                }
            }
        };

        //selfcheck if has permission
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            //Looper.prepare();
            locationManager.requestLocationUpdates(locationProvider, 1000, 5, locationListener);
            //(locationProvider, 0, 0, locationListener);
        }
        else if(permissionCheck == PackageManager.PERMISSION_DENIED){
            throw new SecurityException("No permission to use GPS");
        }
        else throw new SecurityException("Failure at permission request");
    }

    public static Pair<Double, Double> getGPS() {
        //DEBUG ************
        boolean debug = true;
        if(debug){
            try{
                sleep(1000);
            }
            catch(InterruptedException ie) {
            }
        }
        //DEBUG END *********
        else {
            //TODO fix gpsAccuracy being constantly 0.0
            while (gpsAccuracy == (float) 0.0 || gpsAccuracy > (float) 20.0) {
                try{
                    sleep(100);
                    System.out.println("gpsCoords is null: "+gpsCoords==null);
                    return gpsCoords;
                }
                catch(InterruptedException ie) {
                }
            }
        }
        // Remove the listener added
        locationManager.removeUpdates(locationListener);
        return gpsCoords;
    }
}
