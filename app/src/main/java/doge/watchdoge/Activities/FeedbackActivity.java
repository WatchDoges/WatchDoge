package doge.watchdoge.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.util.HashMap;

import doge.watchdoge.R;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_menu_tys);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public void resendButtonClick(View v){
        //PLACEHOLDER
    }


    /** Input: The current view, provided automatically.
     *  Output: None
     *  Effect: Return to the main menu of your android device.
     *  Intended effect: To be implemented! Should close entire app and return to android device
     *  home screen.
     */
    public void closeButtonClick(View v){
        deleteOldFiles(MainActivity.uris);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    /** Input: The current view. Provided automatically.
     *  Output: None.
     *  Effect: Should delete all old pictures and restart the MainActiity with cleared fields.
     *  Currently only deletes old report picture files.
     */
    public void newReportButtonClick(View v){
        deleteOldFiles(MainActivity.uris);
    }

    /** Input: The hasmap with all uris for the picutres taken
     *  Output: None.
     *  Effect: Removes all the files in the uris hashmap.
     *  Intended use: When starting a new report or closing app, call this to clear
     *  the external directory of pictures taken by this app.
     */
    private void deleteOldFiles(HashMap<String, Uri> uris){
        System.out.println("Running on destroy...");
        for(Uri uri : MainActivity.uris.values()){
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
    }
}
