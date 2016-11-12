package doge.watchdoge.converters;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Gorbad on 17/10/2016.
 */

public class ImageConverters {

    public static Uri bitmapToPNG(Bitmap bm, String filename){

        FileOutputStream out = null;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
}
