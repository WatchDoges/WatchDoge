package doge.watchdoge.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import doge.watchdoge.R;
import doge.watchdoge.converters.ImageConverters;
import doge.watchdoge.creategpspicture.createGPSPicture;
import doge.watchdoge.externalsenders.EmailSender;
import doge.watchdoge.externalsenders.ISender;
import doge.watchdoge.gpsgetter.DummyGpsCoordinates;
import doge.watchdoge.gpsgetter.GpsCoordinates;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final int requestGranted = 1;
    private DummyGpsCoordinates dummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        // Check for permissions and request as necessary
        requestPermission();
        dummy = new DummyGpsCoordinates(this);

        final Button camBtn = (Button) findViewById(R.id.camera_button);
        camBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // DEBUG
                Toast t = Toast.makeText(v.getContext(),"Fetching GPS data", Toast.LENGTH_SHORT);
                t.show();
                // DEBUG END
                gpsPicture();
            }
        });

        final Button button = (Button) findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("title","Email Title, custom.");
                hm.put("message","Email message comes here. Very nice indeed.");

                ArrayList<String> list = new ArrayList<>();
                list.add("miroeklu@abo.fi");
                list.add("miroeklu@gmail.com");
                hm.put("receivers",list);

                ArrayList<Uri> uris = new ArrayList<>();
                File path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File filelocation = new File(path1, "gpspicture.png");
                Uri path = Uri.fromFile(filelocation);
                uris.add(path);
                hm.put("attachments",uris);

                Intent i = EmailSender.getIntent(hm);
                startActivity(Intent.createChooser(i, "Send mail..."));

            }
        });

        //example of how to use CreateGPSPicture

    }

    private void gpsPicture(){
        try {
            Bitmap tmp = createGPSPicture.CreateGPSPictue(dummy);
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(tmp);
            String newName = ImageConverters.bitmapToPNG(tmp, "gpspicture");
        }
        catch(Exception e){

        }
    }

    private void requestPermission(){
        // Make an array for all the permissions that may be needed
        String perm[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        // Set size to how many permissions you want to request
        // The result of permission requests listed in perm[] is stored here
        int permissionCheckResult[] = new int[perm.length];
        int denied = 0;
        for(int i=0; i<perm.length;i++){
            permissionCheckResult[i] = ContextCompat.checkSelfPermission(this.getApplicationContext(),perm[i]);
            if(permissionCheckResult[i]== PackageManager.PERMISSION_DENIED) denied++;
        }

        if(denied!=0) {
            // The permissions that actually need a request is stored here
            String requesting[] = new String[denied];
            denied = 0;
            for (int i = 0; i < perm.length; i++) {
                if (permissionCheckResult[i] == PackageManager.PERMISSION_DENIED) {
                    requesting[denied] = perm[i];
                    denied++;
                }
            }

            if (requesting.length > 0) {
                ActivityCompat.requestPermissions(this, requesting, requestGranted);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case requestGranted: {
                // If request is cancelled, the result arrays are empty.

                // DEBUG
                // Flash toast whether the permissions have been granted or not
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast t = Toast.makeText(this.getApplicationContext(),"Permission granted for location data",Toast.LENGTH_LONG);
                    t.show();
                }
                else {
                    Toast t = Toast.makeText(this.getApplicationContext(),"Permission denied for location data",Toast.LENGTH_LONG);
                    t.show();
                }
                // DEBUG END
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}