package doge.watchdoge.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import doge.watchdoge.R;
import doge.watchdoge.applicationcleaup.CleanupHelper;
import doge.watchdoge.externalsenders.EmailSender;
import doge.watchdoge.gpsgetter.GpsCoordinates;
import doge.watchdoge.imagehandlers.GpsPictureCreationThread;
import doge.watchdoge.imagehandlers.ImageHandlers;
import doge.watchdoge.imagehandlers.PictureCreationThread;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    private final int requestGranted = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private GpsCoordinates coordinates;
    public static ArrayList<File> pictureList = new ArrayList<File>();
    public static HashMap<String, Uri> uris = new HashMap<String, Uri>();
    private static Pair<Double, Double> gpscoordinates;
    public static final String gpspicbasename = "gpspicture";
    public static final String probpicbasename = "problempicture";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 4000; // 4 sec
    private static int FATEST_INTERVAL = 2000; // 2 sec
    private static int DISPLACEMENT = 5; // 5 meters displacement triggers locationupdate

    // XML component declarations
    private static EditText titleField;
    private static EditText descField;
    private static RadioGroup radioGroup1;
    private static RadioButton privateButton;
    private static RadioButton publicButton;
    private static ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageHandlers.initializeDirectories();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_menu_tys);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Check for permissions and request as necessary
        requestPermission();

        /** If there is a saved bitmap in the state, reset it. Will prevent image from disappearing
         when changing orientation of the device*/
//        if (savedInstanceState != null) {
//            ImageView view = (ImageView) findViewById(R.id.imageView); // Get the current bitmap
//            // Restore the the bitmap to the imageView
//            view.setImageBitmap((Bitmap) savedInstanceState.getParcelable("problemPicture"));
//        }

        if (checkGoogleAPI()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        } else {
            togglePeriodicLocationUpdates();
            coordinates = new GpsCoordinates(this);
        }

        sendButtonListenSetUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CleanupHelper.isExitFlagRaised) {
            CleanupHelper.isExitFlagRaised = false;
            this.finish();
        }
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkGoogleAPI();

        // Resuming the periodic location updates
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates)
                startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
    }

    /**
     * Sets up listeners on the title field, description field and on the radio buttons to
     * enable/disable the send button when there is text in the fields and a button is selected.
     */
    private void sendButtonListenSetUp() {
        titleField = (EditText) findViewById(R.id.title_field);
        descField = (EditText) findViewById(R.id.desc_field);
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        privateButton = (RadioButton) findViewById(R.id.private_button);
        publicButton = (RadioButton) findViewById(R.id.public_button);
        sendButton = (ImageButton) findViewById(R.id.send_button);

        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                toggleSendButton();
            }
        });

        descField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                toggleSendButton();
            }
        });

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup1, int checkedId) {
                boolean isChecked = privateButton.isChecked() || publicButton.isChecked();
                if (isChecked) toggleSendButton();
            }
        });
        toggleSendButton();
    }

    /**
     * Enables/disables and greys out the send button.
     */
    private static void toggleSendButton() {
        boolean enable = titleField.getText().toString().length() > 0
                && descField.getText().toString().length() > 0
                && radioGroup1.getCheckedRadioButtonId() != -1;
                //&& !uris.isEmpty();
        sendButton.setEnabled(enable);
        sendButton.setClickable(enable);
        if (enable) sendButton.setAlpha(1f);
        else sendButton.setAlpha(0.5f);
    }


    /**
     * Override the onSaveInstanceState to save the bitmap of the problem picture. By saving the
     * bitmap to the state it can later be restored in onCreate. This is needed when the runtime
     * changes, for example, when the devices orientation changes.
     *
     * @param state The state in which the bitmap is changed
     */
//    @Override
//    public void onSaveInstanceState(Bundle state) {
//        super.onSaveInstanceState(state);
//        ImageView view = (ImageView) findViewById(R.id.imageView); // Get the imageView
//        try {
//            // Get current bitmap
//            Bitmap problemPicture = ((BitmapDrawable) view.getDrawable()).getBitmap();
//            // Save the bitmap in the state
//            state.putParcelable("problemPicture", problemPicture);
//        } catch (ClassCastException CCE) {
//            CCE.printStackTrace();
//            System.err.print("Class cast exception");
//        }
//
//    }


    /**
     * Input: the new name of a key value to be added to the pictures hashmap called uris
     * Output: Nothing.
     * Effect: Sets the provided URI object, that should reference an active picture, into the
     * uris hashmap.
     * Purpose: Used to track problem pictures and the gps picture of a report.
     */
    public static void updateUrisHashmap(String key, Uri value) {
        //If a GPS Picture already exists, remove it and the actual picture first.
        if (key == MainActivity.gpspicbasename && MainActivity.uris.containsKey(key)) {
            Uri oldPic = MainActivity.uris.get(key);
            if (!oldPic.getPath().equals(value.getPath()))
                ImageHandlers.deleteFileByUri(oldPic, key);
            MainActivity.uris.remove(key);
        }
        //Regular problem pictures are automatically added.
        MainActivity.uris.put(key, value);
    }

    /**
     * Input: The current view. Provided automatically.
     * Ouput: None.
     * Effect: Starts a new send activity (currently email) with provided information.
     * Also takes the app to FeedbackActivity.
     */
    public void sendButtonClick(View v) {
        HashMap<String, Object> hm = createInformationHashMap();
        Intent i = EmailSender.getIntent(hm);
        Intent feedbackActivityIntent = new Intent(this, FeedbackActivity.class);
        startActivity(feedbackActivityIntent);
        startActivity(Intent.createChooser(i, "Send mail..."));
    }

    /**
     * Input: The current view. Provided automatically.
     * Output: None.
     * Effect: Takes the user to the android device's own camera application.
     * Returns to MainActivity upon taking a picture.
     */
    public void cameraButtonClick(View v) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String probpicname = findFreshName(MainActivity.probpicbasename, MainActivity.uris);
        File file = new File(Environment.getExternalStorageDirectory()+File.separator + probpicname);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * The album button functionality
     */
    public void  albumButtonClick(View v) {

        Intent albumActivityIntent = new Intent(this, AlbumActivity.class);
        startActivity(albumActivityIntent);
    }

    /**
     * Display help text for the title
     */
    public void titleHelpClick(View v) {
        Toast t = Toast.makeText(this.getApplicationContext(), R.string.popup_title_text, Toast.LENGTH_LONG);
        t.show();
    }

    /**
     * Display help text for the description
     */
    public void descHelpClick(View v) {
        Toast t = Toast.makeText(this.getApplicationContext(), R.string.popup_help_text, Toast.LENGTH_LONG);
        t.show();
    }

    /** Input: request and result code of the caller (camera), data containing image thumbnail from camera
     *  Output: None.
     *  Effect: Creates a full-sized picture from the camera, stores it with a unique name
     *  Returns to MainActivity upon making a problem picture and initiate the creation of gpspicture.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String newProblemPicName = findFreshName(MainActivity.probpicbasename, MainActivity.uris);

        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE) {
            //Get our saved file into a bitmap object:
            System.out.println("Doing the capture-image-fullsize in MainActivity.");
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + newProblemPicName);
            Object[] DataTransfer = new Object[2];
            DataTransfer[0] = file;
            DataTransfer[1] = newProblemPicName;

            Uri probPicUri = null;

            //thread is created that processes the problem picture
            PictureCreationThread thread = new PictureCreationThread(DataTransfer, probPicUri);
            thread.start();
            gpsPicture();
        }
    }

    /**
     * Input: The picture name (key) that might exist in the HashMap provided.
     * Output: A new unique name for a problem picture.
     * Used to to find a fresh problempicture name before converting the BitMap to a PNG.
     */
    private String findFreshName(String attemptedName, HashMap<String, Uri> uris) {
        Set<String> keys = uris.keySet();
        Integer newID = 0;
        //If the keys are, for example. key+0, key+1 and key+2, this for-loop will push newID
        //to 3 and return key+3. If the keys are only key+1, then key+0 will be returned since
        //it wasn't in the set of keys.
        for (String k : keys) {
            String possibleNewName = attemptedName + newID.toString();
            if (keys.contains(possibleNewName)) newID++;
            else return possibleNewName;
        }
        return attemptedName + newID.toString();
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
        list.add("watchdoge.app@gmail.com");
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
                    CharSequence text = privatePublicButton.getContentDescription();
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
                //Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
                //Manifest.permission.WRITE_CONTACTS,
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


    public void gpsPicture() {
//        try {
//            Bitmap tmp;
//            if (!mRequestingLocationUpdates) {
//                tmp = createGPSPicture.CreateGPSPicture(gpscoordinates);
//            } else {
//                tmp = createGPSPicture.CreateGPSPicture(null);
//            }
//
//            //ImageView img = (ImageView) findViewById(R.id.imageView);
//            //img.setImageBitmap(tmp);
//            String newGpsPictureName = MainActivity.gpspicbasename;
//            if(mLastLocation != null){
//                //int accuracy = (int)mLastLocation.getAccuracy();
//                float accuracy = mLastLocation.getAccuracy();
//                newGpsPictureName += "_"+accuracy+"m";
//            } else newGpsPictureName += "_m";
//
//            Uri newName = ImageHandlers.bitmapToPNG(tmp, newGpsPictureName);
//            if (newName != null)
//                updateUrisHashmap(MainActivity.gpspicbasename, newName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Creating GPS Picture failed.");
//        }
        //threaded behaviour of gpsPicture
        GpsPictureCreationThread thread = new GpsPictureCreationThread(mRequestingLocationUpdates,gpscoordinates,mLastLocation);
        thread.start();
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
}