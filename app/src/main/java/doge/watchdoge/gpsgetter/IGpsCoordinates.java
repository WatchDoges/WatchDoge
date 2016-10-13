package doge.watchdoge.gpsgetter;

import android.content.Context;
import android.util.Pair;

public interface IGpsCoordinates {
    public Pair<Double, Double> getGPS();
}
