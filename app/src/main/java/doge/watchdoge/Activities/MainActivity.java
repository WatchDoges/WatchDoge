package doge.watchdoge.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

import doge.watchdoge.R;
import doge.watchdoge.externalsenders.EmailSender;
import doge.watchdoge.externalsenders.IEmailSender;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
    }
}
