package doge.watchdoge;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.util.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;

import doge.watchdoge.gpsgetter.GpsCoordinates;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

/**
 * Created by Pat on 13.10.16.
 */

@RunWith(AndroidJUnit4.class)
public class GpsITest {
    private Pair<Double, Double> pair;
    private GpsCoordinates gpsCoordinates;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("doge.watchdoge", appContext.getPackageName());
    }

    @Test
    public void runGpsGetter(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        gpsCoordinates = new GpsCoordinates(appContext);
        assertNotNull(gpsCoordinates);

        Pair<Double, Double> pair = gpsCoordinates.getGPS();
        assertNotNull(pair);
    }
}
