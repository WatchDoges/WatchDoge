package doge.watchdoge.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import doge.watchdoge.R;
import doge.watchdoge.creategpspicture.createGPSPicture;
import doge.watchdoge.externalsenders.EmailSender;
import doge.watchdoge.externalsenders.IEmailSender;
import doge.watchdoge.gpsgetter.DummyGpsCoordinates;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        //example of how to use CreateGPSPicture
        DummyGpsCoordinates dummy = new DummyGpsCoordinates(this);
        Bitmap tmp = createGPSPicture.CreateGPSPictue(dummy);
        ImageView img = (ImageView)findViewById(R.id.imageView);
        img.setImageBitmap(tmp);
    }
}
