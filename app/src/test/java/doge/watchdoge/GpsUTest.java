package doge.watchdoge;
import android.support.v4.util.Pair;

import org.junit.Before;
import org.junit.Test;

import doge.watchdoge.gpsgetter.DummyGpsCoordinates;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Pat on 13.10.16.
 */

public class GpsUTest {
    private Pair<Double, Double> pair;
    private DummyGpsCoordinates dum;

    @Before
    public void initilizer() throws Exception{
        DummyGpsCoordinates dum = new DummyGpsCoordinates(null);
        pair = dum.getGPS();
    }

    @Test
    public void dummyConstructorTest() throws Exception{
        DummyGpsCoordinates dumt = new DummyGpsCoordinates(null);
        assertEquals(true, dumt!=null);
    }

    @Test
    public void dummyPairClassTest() throws Exception{
        assertNotNull(pair);
        double t = 1;
        Pair<Double,Double> testPair = new Pair<Double,Double>(t,t);
        assertEquals(pair.getClass().toString(), testPair.getClass().toString());
    }

    @Test
    public void dummyPairFirstValueTest() throws Exception{
        assertNotNull(pair.first);
        double first = pair.first;
        assertEquals((double) 60.462518, first);
    }

    @Test
    public void dummyPairSecondValueTest() throws Exception{
        assertNotNull(pair.second);
        double second = pair.second.doubleValue();
        assertEquals((double) 22.288918, second);
    }
}
