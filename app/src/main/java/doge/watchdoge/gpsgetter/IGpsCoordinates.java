package doge.watchdoge.gpsgetter;

import android.content.Context;
import android.util.Pair;

public interface IGpsCoordinates {
    public void GpsCoordinates(Context context);
    public Pair<Double, Double> getGPS();
}
