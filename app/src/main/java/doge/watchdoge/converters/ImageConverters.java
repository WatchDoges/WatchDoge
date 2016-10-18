package doge.watchdoge.converters;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Gorbad on 17/10/2016.
 */

public class ImageConverters {

    public static String bitmapToPNG(Bitmap bm, String filename){
        FileOutputStream out = null;
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, filename + ".png");
            out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    return filename+".png";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void bitmapToJPEG(Bitmap bm, String filename){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
