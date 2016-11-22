package doge.watchdoge.imagehandlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import doge.watchdoge.activities.MainActivity;

/**
 * Created by Gorbad on 17/10/2016.
 */

public class ImageHandlers {
    public static final String MAIN_DIR_NAME = "watchdoge";
    public static final String PROBLEM_DIR_NAME = "problempictures";
    public static final String GPS_DIR_NAME = "gpspictures";

    public static File getMainDir(){
        File f = new File(Environment.getExternalStorageDirectory(), MAIN_DIR_NAME);
        if (!f.exists()) {
            f.mkdirs();
            System.out.println("Main directory created.");
        } else {
            System.out.println("Main directory already existed.");
        }
        return f;
    }

    public static File getProblemDir(){
        File main_dir = getMainDir();
        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + MAIN_DIR_NAME,
                PROBLEM_DIR_NAME);
        if (!f1.exists()){
            f1.mkdirs();
            System.out.println("Problem directory created.");
        } else {
            System.out.println("Problem directory already existed.");
        }
        return f1;
    }

    public static File getGPSDir(){
        File main_dir = getMainDir();
        File f2 = new File(Environment.getExternalStorageDirectory() + "/" + MAIN_DIR_NAME,
                GPS_DIR_NAME);
        if (!f2.exists()){
            f2.mkdirs();
            System.out.println("GPS directory created.");
        } else {
            System.out.println("GPS directory already existed.");
        }
        return f2;
    }

    public static void initializeDirectories(){
        getMainDir();
        getProblemDir();
        getGPSDir();
    }

    public static Uri bitmapToPNG(Bitmap bm, String filename){

        FileOutputStream out = null;

        File path = null;
        if(filename.contains(MainActivity.probpicbasename)){
            path = getProblemDir();
        } else if (filename.contains(MainActivity.gpspicbasename)){
            path = getGPSDir();
        } else {
            path = getMainDir();
        }
        String newFileName = filename + ".png";
        try {
            File file = new File(path, newFileName);
            out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    try {
                        File filelocation = new File(path, newFileName);
                        return Uri.fromFile(filelocation);
                    } catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /** Input: path to image to be converted, preferred width of image, preferred height of image
     *  Output: Bitmap from problem picture
     *  Effect: Decodes a file from the camera to a Bitmap with the best match to the desired resolution
     *  Returns to caller when Bitmap conversion is finished
     */
    public static Uri decodeSampledBitmapFromFile(String path, String pictureName, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;


        return bitmapToPNG(BitmapFactory.decodeFile(path, options),pictureName);
    }

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
        MainActivity.uris.clear();
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
