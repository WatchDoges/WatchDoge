package doge.watchdoge.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private GpsCoordinates dummy;
    public static HashMap<String, Uri> uris = new HashMap<String, Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        // Check for permissions and request as necessary
        requestPermission();
    }

    private void gpsPicture(){
        try {
            Bitmap tmp = createGPSPicture.CreateGPSPictue(dummy);
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(tmp);
            String gpspicname = "gpspicture";
            Uri newName = ImageConverters.bitmapToPNG(tmp, gpspicname);
            updateUrisHashmap(gpspicname, newName);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Creating GPS Picture failed.");
        }
    }

    private void updateUrisHashmap(String key, Uri value){
        //If a GPS Picture already exists, remove it first.
        if (MainActivity.uris.containsKey(key))
            MainActivity.uris.remove(key);
        //Then, place the current gps picture in the HashMap<String, Uri> uris.
        MainActivity.uris.put(key,value);
    }


    public void sendButtonClick(View v){
        HashMap<String, Object> hm = createInformationHashMap();
        Intent i = EmailSender.getIntent(hm);
        startActivity(Intent.createChooser(i, "Send mail..."));
    }

    public void cameraButtonClick(View v) {
        //DEBUG START
        Toast t = Toast.makeText(v.getContext(),"Fetching GPS data", Toast.LENGTH_SHORT);
        t.show();
        // DEBUG END
        gpsPicture();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String probpicname = "problempicture";
            Uri probpicuri = ImageConverters.bitmapToPNG(imageBitmap, probpicname);
            updateUrisHashmap(probpicname, probpicuri);
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(imageBitmap);
        }
    }

    private HashMap<String, Object> createInformationHashMap(){
        HashMap<String, Object> hm = new HashMap<String, Object>();
        CharSequence reporttype = findReportTypeFromRadioGroup();
        CharSequence title = ((EditText)findViewById(R.id.title_field)).getText();
        CharSequence description = ((EditText)findViewById(R.id.desc_field)).getText();

        if (reporttype==null || reporttype=="") hm.put("report_type","Unspecified Report Type");
        else hm.put("report_type", reporttype);

        if (title==null || title=="") hm.put("title", "Unspecified Title");
        else hm.put("title", title);

        if (description==null || description=="") hm.put("description", "Unspecified Description");
        else hm.put("description", description);

        ArrayList<String> list = new ArrayList<>();
        list.add("PLACEHOLDER_NOSPAM@wudifuqq.fi");
        hm.put("receivers",list);

        ArrayList<Uri> onlyUris = new ArrayList<>(MainActivity.uris.values());
        hm.put("attachments", onlyUris);
        return hm;
    }

    private CharSequence findReportTypeFromRadioGroup() {
        Object possibleRadioGroupObject = findViewById(R.id.radioGroup1);
        if (possibleRadioGroupObject != null){
            RadioGroup privatePublicRadioGroup = (RadioGroup) possibleRadioGroupObject;
            Integer possibleID = privatePublicRadioGroup.getCheckedRadioButtonId();

            if (possibleID != null && possibleID != -1) {
                Object possibleRadioButtonObject = findViewById(possibleID);
                if (possibleRadioButtonObject != null) {
                    RadioButton privatePublicButton = (RadioButton) possibleRadioButtonObject;
                    CharSequence text = privatePublicButton.getText();
                    if (text != null)
                        return text;
                }
            }
        }
        return "";
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
        else{
            dummy = new GpsCoordinates(this);
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
                    dummy = new GpsCoordinates(this);
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