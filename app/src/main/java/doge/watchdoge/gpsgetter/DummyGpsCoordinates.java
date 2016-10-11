package doge.watchdoge.gpsgetter;

import android.content.Context;
import android.util.Pair;

public class DummyGpsCoordinates implements IGpsCoordinates {
    private Pair<Double, Double> gpsCoords;
    private float gpsAccuracy = 0;
    private long gpsAge = 0;

    @Override
    public void GpsCoordinates(Context context) {
        gpsCoords = new Pair<Double, Double>(60.462518, 22.288918);
        gpsAccuracy = (float) 5.0;
        gpsAge = (long) 2;
    }

    @Override
    public Pair<Double, Double> getGPS() {
        return gpsCoords;
    }
}
