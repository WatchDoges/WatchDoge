package doge.watchdoge.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import doge.watchdoge.R;
import doge.watchdoge.converters.ImageConverters;
import doge.watchdoge.creategpspicture.createGPSPicture;
import doge.watchdoge.externalsenders.EmailSender;
import doge.watchdoge.externalsenders.ISender;
import doge.watchdoge.gpsgetter.DummyGpsCoordinates;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        DummyGpsCoordinates dummy = new DummyGpsCoordinates(this);
        Bitmap tmp = createGPSPicture.CreateGPSPictue(dummy);
        ImageView img = (ImageView)findViewById(R.id.imageView);
        img.setImageBitmap(tmp);
        ImageConverters.bitmapToPNG(tmp, "gpspicture");

        final Button button = (Button) findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("title","Email Title, custom.");
                hm.put("message","Email message comes here. Very nice indeed.");
                ArrayList<String> list = new ArrayList<String>();
                list.add("miroeklu@abo.fi");
                list.add("miroeklu@gmail.com");
                hm.put("receivers",list);
                Intent i = EmailSender.getIntent(hm);
                startActivity(Intent.createChooser(i, "Send mail..."));
            }
        });

        //example of how to use CreateGPSPicture

    }
}