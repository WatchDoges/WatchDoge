package doge.watchdoge.gpsgetter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;

import java.util.Calendar;

import static java.lang.Thread.sleep;

public class GpsCoordinates {
    private static LocationManager locationManager;
    private static LocationListener locationListener;

    private static Pair<Double, Double> gpsCoords;
    private static float gpsAccuracy = 0;
    private long gpsAge = 0;

    public GpsCoordinates(Context context){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Use GPS location data
        String locationProvider = LocationManager.GPS_PROVIDER;

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

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        //selfcheck if has permission
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Looper.prepare();
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        }
        else if(permissionCheck == PackageManager.PERMISSION_DENIED){
            throw new SecurityException("No permission to use GPS");
        }
    }

    public static Pair<Double, Double> getGPS() {
        while(gpsAccuracy==(float)0.0 || gpsAccuracy>(float)20.0){
            try{
                sleep(100);
            }catch(InterruptedException ie){

            }
        }
        // Remove the listener added
        locationManager.removeUpdates(locationListener);
        return gpsCoords;
    }
}
