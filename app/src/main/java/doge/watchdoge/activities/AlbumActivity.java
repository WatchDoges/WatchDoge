package doge.watchdoge.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import doge.watchdoge.R;

public class AlbumActivity extends AppCompatActivity
        implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity_layout);
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


    public void backToMainButtonClick(View v){
        finish();
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        if(velocityX > 50){
            Toast t = Toast.makeText(this.getApplicationContext(), "Flinging right!", Toast.LENGTH_SHORT);
            t.show();
        } else if (velocityX < -50){
            Toast t = Toast.makeText(this.getApplicationContext(), "Flinging left!", Toast.LENGTH_SHORT);
            t.show();
        }
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

}
