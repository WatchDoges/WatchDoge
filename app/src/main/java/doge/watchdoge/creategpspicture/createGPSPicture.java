package doge.watchdoge.creategpspicture;

import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import doge.watchdoge.gpsgetter.GpsCoordinates;

import java.util.concurrent.ExecutionException;

/**
 * Created by Frey on 13.10.2016.
 */

public class createGPSPicture {
    public static Bitmap CreateGPSPictue(){
        //Pair<Double, Double> coordinates = Pair.create(60.457027 ,2022.283202);
        Pair<Double, Double> coordinates = GpsCoordinates.getGPS();
        String imageUrl = "http://maps.googleapis.com/maps/api/staticmap?&size=600x600&markers=color:blue|" +
                coordinates.first + ",%" +coordinates.second;

        Bitmap picture = null;
        try {
            picture = new GPSPicture().execute(imageUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return picture;

    }
}
