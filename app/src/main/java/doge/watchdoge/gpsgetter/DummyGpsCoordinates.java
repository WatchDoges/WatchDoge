package doge.watchdoge.gpsgetter;

import android.content.Context;
import android.util.Pair;

public class DummyGpsCoordinates {
    private static Pair<Double, Double> gpsCoords;
    private float gpsAccuracy = 0;
    private long gpsAge = 0;

    public void DummyGpsCoordinates(Context context) {
        gpsCoords = new Pair<Double, Double>(60.462518, 22.288918);
        gpsAccuracy = (float) 5.0;
        gpsAge = (long) 2;
    }

    public static Pair<Double, Double> getGPS() {
        return gpsCoords;
    }
}
