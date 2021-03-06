package doge.watchdoge.imagehandlers;

import android.net.Uri;

import java.io.File;

import doge.watchdoge.activities.MainActivity;


/**
 * Created by Frey on 20.11.2016.
 */

/**
 * Thread that creates bitmap and uri from the camera picture
 */
public class PictureCreationThread extends Thread {
    Object[] DataTransfer;
    Uri probPicUri = null;

    public PictureCreationThread(Object[] data) {
        DataTransfer = data;
    }

    public void run() {
        File file = (File)DataTransfer[0];
        //debug time taken creating picture
        long startTime = System.currentTimeMillis();
        probPicUri = ImageHandlers.decodeSampledBitmapFromFile(file.getAbsolutePath(), (String)DataTransfer[1]);

        //debug time taken creating picture
        long difference = System.currentTimeMillis() - startTime;
        System.out.println(difference + " milliseconds to make picture");

        if (probPicUri != null)
            MainActivity.updateUrisHashmap((String)DataTransfer[1], probPicUri);
        if (file != null)
            file.delete();
    }

}



