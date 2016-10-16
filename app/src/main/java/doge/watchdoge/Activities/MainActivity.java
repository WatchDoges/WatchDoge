package doge.watchdoge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import doge.watchdoge.R;
import doge.watchdoge.externalsenders.EmailSender;
import doge.watchdoge.externalsenders.ISender;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

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

    }


}
