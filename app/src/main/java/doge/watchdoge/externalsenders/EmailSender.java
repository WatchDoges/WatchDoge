package doge.watchdoge.externalsenders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Gorbad on 11/10/2016.
 */

public class EmailSender extends GeneralSender{

    public static HashMap<String, Object> hashMap;

    @Override
    public boolean send(){
        return false;
    }

    public static Intent getIntent(HashMap<String, Object> info){
        System.out.println("Creating Intent i..");
        if(info==null){
            System.out.println("Info is null");
            return null;
        }
        hashMap = info;
        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
        i.setType("message/rfc822");

        String reportType = "";
        if(info.containsKey("report_type")){
            reportType = (String)info.get("report_type");
        }

        if(info.containsKey("title")){
            i.putExtra(Intent.EXTRA_SUBJECT, "(" + reportType + ") " + info.get("title").toString());
        } else {
            i.putExtra(Intent.EXTRA_SUBJECT, "(" + reportType + ") " + "PLACEHOLDER TITLE");
        }

        if(info.containsKey("receivers")){
            ArrayList<String> rescvrs = (ArrayList<String>)info.get("receivers");
            String[] targets = new String[rescvrs.size()];
            for(int z = 0; z < rescvrs.size(); z++){
                targets[z] = rescvrs.get(z);
            }
            i.putExtra(Intent.EXTRA_EMAIL, targets);
        } else {
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{});
        }

        if(info.containsKey("description")){
            String msg = info.get("description").toString();
            i.putExtra(Intent.EXTRA_TEXT, msg);
        } else {
            i.putExtra(Intent.EXTRA_TEXT, "Placeholder");
        }

        ArrayList<Uri> uris = new ArrayList<>();
        if(info.containsKey("attachments")){
            ArrayList<Uri> attachments = (ArrayList<Uri>)info.get("attachments");
            for(Uri uri : attachments) {
                uris.add(uri);
            }
        }

        i.putExtra(Intent.EXTRA_STREAM, uris);
        return i;
    }
}