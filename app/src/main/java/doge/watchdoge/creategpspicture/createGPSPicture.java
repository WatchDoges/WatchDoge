package doge.watchdoge.creategpspicture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import doge.watchdoge.gpsgetter.DummyGpsCoordinates;
import doge.watchdoge.gpsgetter.GpsCoordinates;

/**
 * Created by Frey on 13.10.2016.
 */

public class createGPSPicture {

    /**
     * Creates a bitmap, size 1280x1280 from provided coordinates
     *  returns finished bitmap or null in case no picture can be created
     */
    public static Bitmap CreateGPSPicture(Pair<Double, Double> coordinates) {

        String size = "640x640";
        //String marker = "http://i.imgur.com/kPtp25C.png"; Round variant
        String marker = "http://i.imgur.com/S0yy7o3.png";   // shaped as marker
        String imageUrl;
        if (coordinates == null || coordinates.first == null || coordinates.second == null) {
            System.out.println("Using Android.location coordinates (googleAPI not supported or disabled!)");
            Pair<Double, Double> androidLocation = GpsCoordinates.getGPS();
            if (androidLocation != null) {
                imageUrl = "http://maps.googleapis.com/maps/api/staticmap?&size=" + size + "&scale=2&markers=&markers=icon:" + marker + "|" +
                        androidLocation.first + ",%20" + androidLocation.second;
            } else {
                System.out.println("Using default dummy coordinates (googleAPI and android.Location not supported or disabled!)");
                Pair<Double, Double> dummy = DummyGpsCoordinates.getGPS();
                imageUrl = "http://maps.googleapis.com/maps/api/staticmap?&size=" + size + "&scale=2&markers=icon:" + marker + "|" +
                        dummy.first + ",%20" + dummy.second;

            }

        } else {
            imageUrl = "http://maps.googleapis.com/maps/api/staticmap?&size=" + size + "&scale=2&markers=icon:" + marker + "|" +
                    coordinates.first + ",%20" + coordinates.second;
        }
        //picture creation, google api is contacted and inputstream decoded
        try {

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000); //set timeout to 10 seconds
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        }
        //no responce form server, image cannot be created
        catch (java.net.SocketTimeoutException e) {
            return null;
        }
        //invalid connection
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
