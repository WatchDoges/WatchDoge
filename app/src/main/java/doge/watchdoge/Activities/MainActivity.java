package doge.watchdoge.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;

import doge.watchdoge.R;
import doge.watchdoge.converters.ImageConverters;
import doge.watchdoge.creategpspicture.createGPSPicture;
import doge.watchdoge.externalsenders.EmailSender;
import doge.watchdoge.gpsgetter.GpsCoordinates;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    private final int requestGranted = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private GpsCoordinates dummy;
    public static HashMap<String, Uri> uris = new HashMap<String, Uri>();
    private static Pair<Double, Double> gpscoordinates;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 5; // 10 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_menu_tys);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Check for permissions and request as necessary
        requestPermission();

        if (checkGoogleAPI()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        } else {
            togglePeriodicLocationUpdates();
            dummy = new GpsCoordinates(this);
        }

    }

    private void updateUrisHashmap(String key, Uri value) {
        //If a GPS Picture already exists, remove it first.
        if (MainActivity.uris.containsKey(key))
            MainActivity.uris.remove(key);
        //Then, place the current gps picture in the HashMap<String, Uri> uris.
        MainActivity.uris.put(key, value);
    }


    public void sendButtonClick(View v) {
        HashMap<String, Object> hm = createInformationHashMap();
        Intent i = EmailSender.getIntent(hm);
        startActivity(Intent.createChooser(i, "Send mail..."));
    }

    public void cameraButtonClick(View v) {
        //DEBUG START
        //Toast t = Toast.makeText(v.getContext(), "Fetching GPS data", Toast.LENGTH_SHORT);
        //t.show();
        // DEBUG END


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
            gpsPicture();
        }
    }

    private HashMap<String, Object> createInformationHashMap() {
        HashMap<String, Object> hm = new HashMap<String, Object>();
        CharSequence reporttype = findReportTypeFromRadioGroup();
        CharSequence title = ((EditText) findViewById(R.id.title_field)).getText();
        CharSequence description = ((EditText) findViewById(R.id.desc_field)).getText();

        if (reporttype == null || reporttype == "")
            hm.put("report_type", "Unspecified Report Type");
        else hm.put("report_type", reporttype);

        if (title == null || title == "") hm.put("title", "Unspecified Title");
        else hm.put("title", title);

        if (description == null || description == "")
            hm.put("description", "Unspecified Description");
        else hm.put("description", description);

        ArrayList<String> list = new ArrayList<>();
        list.add("PLACEHOLDER_NOSPAM@wudifuqq.fi");
        hm.put("receivers", list);

        ArrayList<Uri> onlyUris = new ArrayList<>(MainActivity.uris.values());
        hm.put("attachments", onlyUris);
        return hm;
    }

    private CharSequence findReportTypeFromRadioGroup() {
        Object possibleRadioGroupObject = findViewById(R.id.radioGroup1);
        if (possibleRadioGroupObject != null) {
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

    private void requestPermission() {
        // Make an array for all the permissions that may be needed
        String perm[] = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_CONTACTS,
        };

        // Set size to how many permissions you want to request
        // The result of permission requests listed in perm[] is stored here
        int permissionCheckResult[] = new int[perm.length];
        int denied = 0;
        for (int i = 0; i < perm.length; i++) {
            permissionCheckResult[i] = ContextCompat.checkSelfPermission(this.getApplicationContext(), perm[i]);
            if (permissionCheckResult[i] == PackageManager.PERMISSION_DENIED) denied++;
        }

        if (denied != 0) {
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
                    //Toast t = Toast.makeText(this.getApplicationContext(), "Permission granted for location data", Toast.LENGTH_LONG);
                    //t.show();
                    startLocationUpdates();
                    updateLocation();
                } else {
                    //Toast t = Toast.makeText(this.getApplicationContext(), "Permission denied for location data", Toast.LENGTH_LONG);
                    //t.show();
                }
                // DEBUG END
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void gpsPicture() {
        try {
            Bitmap tmp;
            if (!mRequestingLocationUpdates) {
                tmp = createGPSPicture.CreateGPSPictue(gpscoordinates);
            } else {
                tmp = createGPSPicture.CreateGPSPictue(null);
            }

            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(tmp);
            String gpspicname = "gpspicture";
            Uri newName = ImageConverters.bitmapToPNG(tmp, gpspicname);
            updateUrisHashmap(gpspicname, newName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Creating GPS Picture failed.");
        }
    }

    private void updateLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            gpscoordinates = new Pair<>(latitude, longitude);
            //Toast t = Toast.makeText(this.getApplicationContext(), "new location found, accuracy: " + mLastLocation.getAccuracy(), Toast.LENGTH_LONG);
            //t.show();

        } else {
            Toast t = Toast.makeText(this.getApplicationContext(), "mLastLocation was null", Toast.LENGTH_LONG);
            t.show();
        }
    }

    /**
     * Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {

            mRequestingLocationUpdates = true;

            // Starting the location updates
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) startLocationUpdates();

        } else {
            mRequestingLocationUpdates = false;
            // Stopping the location updates
            stopLocationUpdates();
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkGoogleAPI() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int CODE = googleAPI.isGooglePlayServicesAvailable(this);
        //if returned code is SUCCESS, the right version is installed, else a dialog is shown
        if (CODE != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(CODE)) {
                googleAPI.getErrorDialog(this, CODE, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                return false;
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        updateLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        //Toast.makeText(getApplicationContext(), "Location changed!",
        //        Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        updateLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkGoogleAPI();

        // Resuming the periodic location updates
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
    }
}