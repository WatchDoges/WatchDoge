package doge.watchdoge.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import doge.watchdoge.R;
import doge.watchdoge.exitHelpClass.ExitHelper;
import doge.watchdoge.externalsenders.EmailSender;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_menu_tys);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        HashMap<String, Object> hm = EmailSender.hashMap;
        if(hm != null){
            TextView type = (TextView)findViewById(R.id.report_type_text_view);
            type.setText((CharSequence) hm.get("report_type"));
            TextView title = (TextView)findViewById(R.id.title_text_view);
            title.setText((CharSequence) hm.get("title"));
            TextView desc = (TextView)findViewById(R.id.desc_text_view);
            desc.setText((CharSequence) hm.get("description"));
        }
    }

    public void resendButtonClick(View v){
        HashMap<String, Object> hm = EmailSender.hashMap;
        if(hm != null){
            Intent i = EmailSender.getIntent(hm);
            startActivity(Intent.createChooser(i, "Send mail..."));
        }
    }


    /** Input: The current view, provided automatically.
     *  Output: None
     *  Effect: Return to the home screen of your android device by using the finish()-method
     *      on all activities created.
     */
    public void closeButtonClick(View v){
        deleteOldFiles(MainActivity.uris, MainActivity.pictureList);
        ExitHelper.isExitFlagRaised = true;
        finish();
    }

    /** Input: The current view. Provided automatically.
     *  Output: None.
     *  Effect: Deletes all old pictures and restarts the MainActivity with cleared fields.
     */
    public void newReportButtonClick(View v){
        deleteOldFiles(MainActivity.uris, MainActivity.pictureList);
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    /** Input: The hasmap with all uris for the picutres taken
     *  Output: None.
     *  Effect: Removes all the files in the uris hashmap.
     *  Intended use: When starting a new report or closing app, call this to clear
     *  the external directory of pictures taken by this app.
     */
    private void deleteOldFiles(HashMap<String, Uri> uris, ArrayList<File> tempImages){
        System.out.println("Running on destroy...");
        for(Uri uri : uris.values()){
        //Destroy the file references in the uri.
            File pictureFile = new File(uri.getPath());
            if (pictureFile!=null) {
                System.out.println("Deleting picture " + pictureFile.getName() + "...");
                pictureFile.delete();
                System.out.println("Delete succeeded!");
            } else {
                System.out.println("Picture was null!");
            }
        }
        //deleting all temporary images
        for(File file : tempImages){
            file.delete();
        }
    }
}
