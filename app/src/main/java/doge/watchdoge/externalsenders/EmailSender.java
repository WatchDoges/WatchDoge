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

    @Override
    public boolean send(){
        return false;
        //System.out.println("Attempting to send email...");
        //if(parentView==null || information==null){
        //    System.out.println("Failed to send email. Information and/or ParentView has not been provided.");
        //    return false;
        //}
        //Intent i = new Intent(Intent.ACTION_SEND);
        //i.setType("message/rfc822");
        //for(String receiver : this.receivers) {
        //    i.putExtra(Intent.EXTRA_EMAIL, new String[]{receiver});
        //}
        //i.putExtra(Intent.EXTRA_SUBJECT, "Auto-Generated Title");
        //i.putExtra(Intent.EXTRA_TEXT, "This is a test message");
        //for(File file : this.attachments) {
         //   Uri uri = Uri.fromFile(file);
        //    i.putExtra(Intent.EXTRA_STREAM, uri);
        //}

        //try {
        //    System.out.println("Trying to start activity...");
        //    //startActivity(Intent.createChooser(i, "Send mail..."));
        //    System.out.println("Succeeded in switching activity?");
        //    return true;
        //} catch (Exception ex) {
        //    ex.printStackTrace();
        //    System.out.println("ERROR when sending email!");
        //    return false;
        //}
    }

    public static Intent getIntent(HashMap<String, Object> info){
        System.out.println("Creating Intent i..");
        if(info==null){
            System.out.println("Info is null");
            return null;
        }
        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
        i.setType("message/rfc822");

        if(info.containsKey("title")){
            i.putExtra(Intent.EXTRA_SUBJECT, info.get("title").toString());
        } else {
            i.putExtra(Intent.EXTRA_SUBJECT, "PLACEHOLDER TITLE");
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

        if(info.containsKey("message")){
            String msg = info.get("message").toString();
            i.putExtra(Intent.EXTRA_TEXT, msg);
        } else {
            i.putExtra(Intent.EXTRA_TEXT, "PLACEHOLDER MESSAGE");
        }

        ArrayList<Uri> uris = new ArrayList<>();
        if(info.containsKey("attachments")){
            ArrayList<String> attachments = (ArrayList<String>)info.get("attachments");
            for(String filename : attachments) {
                File path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File filelocation = new File(path1, filename);
                Uri path = Uri.fromFile(filelocation);
                uris.add(path);
                //Uri uri = Uri.fromFile(file);
                //i.putExtra(Intent.EXTRA_STREAM, path);
            }
        }

        i.putExtra(Intent.EXTRA_STREAM, uris);
        return i;
    }
}