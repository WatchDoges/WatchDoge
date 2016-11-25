package doge.watchdoge.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.IOException;
import doge.watchdoge.R;
import doge.watchdoge.imagehandlers.ImageHandlers;

public class AlbumActivity extends AppCompatActivity
        implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final int RIGHT = 1;
    private static final int LEFT = -1;
    private static final int DEFAULT = 0;
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private ImageSwitcher imageSwitcher;
    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity_layout);
        initImageSwitcher();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_menu_tys);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
    }

    private void initImageSwitcher(){
        System.out.println("Initing image switcher.");
        imageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher1);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                return myView;
            }
        });
        Uri req = getRequestedUri(DEFAULT);
        updateImageView(req);
    }

    public void backToMainButtonClick(View v){
        state = 0;
        finish();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velX, float velY) {
        Uri requestedUri = null;
        if(velX > 50) requestedUri = getRequestedUri(LEFT); //direction is right, going left.
        else if (velX < -50) requestedUri = getRequestedUri(RIGHT); //direction is left, going right.
        updateImageView(requestedUri);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) { return true; }

    @Override
    public void onLongPress(MotionEvent event) {}

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX,  float distanceY) { return true; }

    @Override
    public void onShowPress(MotionEvent event) {}

    @Override
    public boolean onSingleTapUp(MotionEvent event) { return true; }

    @Override
    public boolean onDoubleTap(MotionEvent event) { return true; }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) { return true; }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) { return true; }

    private Uri getRequestedUri(int direction){
        System.out.println("Requesting uri");
        try {
            Object[] uris = (MainActivity.uris.values()).toArray();
            int length = uris.length;
            if(length==0) return null;
            else {
                int targetIndex = newState(direction, length);
                Uri target = (Uri) uris[targetIndex];
                System.out.println("Returning actual target");
                return target;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Returning null target");
            return null;
        }
    }

    private String getRequestedKey(int direction){
        System.out.println("Requesting uri");
        try {
            Object[] keys = (MainActivity.uris.keySet()).toArray();
            int length = keys.length;
            if(length==0) return null;
            else {
                int targetIndex = newState(direction, length);
                String target = (String) keys[targetIndex];
                System.out.println("Returning actual target");
                return target;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Returning null target");
            return null;
        }
    }

    private int newState(int direction, int maxLength){
        System.out.println("Setting state. MaxLength: " + maxLength);
        if(maxLength==0) return (updateState(0));
        else {
            if(direction==RIGHT){
                if(state==(maxLength-1)) return (updateState(0));
                else return (updateState(state + 1));
            } else if(direction==LEFT){//going left
                if(state==0) return (updateState(maxLength-1));
                else return (updateState(state - 1));
            } else {
                return state;
            }
        }
    }

    private int updateState(int newState){
        state = newState;
        return state;
    }

    private void updateImageView(Uri uri){
        if(uri!=null) imageSwitcher.setImageURI(uri);
    }

    public void deletePictureClick(View v){
        String key = getRequestedKey(DEFAULT);
        imageSwitcher.setImageURI(null);
        if(key!=null) ImageHandlers.deleteFileByKey(key);
        state = 0;
        Uri uri2 = getRequestedUri(DEFAULT);
        imageSwitcher.setImageURI(uri2);
        Toast t = Toast.makeText(this.getApplicationContext(), "Image deleted", Toast.LENGTH_LONG);
        t.show();
    }
}
