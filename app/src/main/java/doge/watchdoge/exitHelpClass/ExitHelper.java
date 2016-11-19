package doge.watchdoge.exitHelpClass;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pat on 19.11.16.
 */
public class ExitHelper {
    public static boolean isExitFlagRaised = false;

    /** Input: The hashmap with all uris for the pictures taken
     *  Output: None.
     *  Effect: Removes all the files in the uris hashmap.
     *  Intended use: When starting a new report or closing app, call this to clear
     *  the external directory of pictures taken by this app.
     */
    public static void deleteOldFiles(HashMap<String, Uri> uris, ArrayList<File> tempImages){
        System.out.println("Running on destroy...");
        for(Uri uri : uris.values()){
            //Destroy the file references in the uri.
            deleteFileByUri(uri);
        }
        //deleting all temporary images
        for(File file : tempImages){
            file.delete();
        }
    }

    /**
     * Given an uri, removes the corresponding file, reports it into the console
     * @param uri
     * @return boolean success or fail
     */
    public static boolean deleteFileByUri(Uri uri){
        File pictureFile = new File(uri.getPath());
        if (pictureFile!=null) {
            System.out.println("Deleting picture " + pictureFile.getName() + "...");
            pictureFile.delete();
            System.out.println("Delete succeeded!");
            return true;
        } else {
            System.out.println("Picture was null!");
            return false;
        }
    }
}
