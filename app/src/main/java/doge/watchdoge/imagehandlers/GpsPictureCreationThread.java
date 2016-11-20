package doge.watchdoge.imagehandlers;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.v4.util.Pair;

import java.io.File;

import doge.watchdoge.activities.MainActivity;
import doge.watchdoge.creategpspicture.createGPSPicture;

import static doge.watchdoge.activities.MainActivity.updateUrisHashmap;

/**
 * Created by Frey on 20.11.2016.
 */

public class GpsPictureCreationThread extends Thread {


    private boolean mRequestingLocationUpdates;
    private Pair<Double, Double> gpscoordinates;
    private Location mLastLocation;

    public GpsPictureCreationThread(boolean mRequestingLocationUpdates, Pair<Double, Double> gpscoordinates, Location mLastLocation) {
        this.mRequestingLocationUpdates = mRequestingLocationUpdates;
        this.gpscoordinates = gpscoordinates;
        this.mLastLocation = mLastLocation;

    }

    public void run() {
        try {
            Bitmap tmp;
            if (!mRequestingLocationUpdates) {
                tmp = createGPSPicture.CreateGPSPicture(gpscoordinates);
            } else {
                tmp = createGPSPicture.CreateGPSPicture(null);
            }

            //ImageView img = (ImageView) findViewById(R.id.imageView);
            //img.setImageBitmap(tmp);
            String newGpsPictureName = MainActivity.gpspicbasename;
            if (mLastLocation != null) {
                //int accuracy = (int)mLastLocation.getAccuracy();
                float accuracy = mLastLocation.getAccuracy();
                newGpsPictureName += "_" + accuracy + "m";
            } else newGpsPictureName += "_m";

            Uri newName = ImageHandlers.bitmapToPNG(tmp, newGpsPictureName);
            if (newName != null)
                updateUrisHashmap(MainActivity.gpspicbasename, newName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Creating GPS Picture failed.");
        }
    }
}
