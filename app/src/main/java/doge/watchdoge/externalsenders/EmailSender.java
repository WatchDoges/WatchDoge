package doge.watchdoge.externalsenders;

import java.io.File;
import java.util.ArrayList;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;

/**
 * Created by Gorbad on 11/10/2016.
 */

//Button button = (Button)findViewById(R.id.private_activity_button);
//        button.setOnClickListener(new Button.OnClickListener() {
//public void onClick(View v) {
//        doStuff();
//        }
//        });

//private void doStuff(){
        //ArrayList<File> att = new ArrayList<>();
        //ArrayList<String> rec = new ArrayList<>();
        //rec.add("miroeklu@abo.fi");
        //rec.add("miroeklu@gmail.com");
        //String message = "This is just a test.";

        //IEmailSender sender = new EmailSender(att, rec, message);
        //sender.send();
//}

public class EmailSender extends AppCompatActivity implements IEmailSender{

    private ArrayList<File> attachments = new ArrayList<>();
    private ArrayList<String> receivers = new ArrayList<>();
    private String message = "NOT GIVEN";

    public EmailSender(ArrayList<File> attachments, ArrayList<String> receivers, String message){
        this.attachments = attachments;
        this.receivers = receivers;
        this.message = message;
    }

    @Override
    public void changeMessage(String message){
        this.message = message;
    }

    @Override
    public void addToMessage(String message){
        this.message += message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }

    @Override
    public void addReceiver(String receiver){
        this.receivers.add(receiver);
    }

    @Override
    public ArrayList<String> getReceivers(){
        return this.receivers;
    }

    @Override
    public void addAttachment(File attachment){
        this.attachments.add(attachment);
    }

    @Override
    public ArrayList<File> getAttachments(){
        return this.attachments;
    }

    @Override
    public boolean send(){
        System.out.println("You have: " + (this.attachments.size()) + " attachments, " +
                (this.receivers).size() + " receiverse and your message is: " + this.message);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        for(String receiver : this.receivers) {
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{receiver});
        }
        i.putExtra(Intent.EXTRA_SUBJECT, "Auto-Generated Title");
        i.putExtra(Intent.EXTRA_TEXT, this.message);
        for(File file : this.attachments) {
            Uri uri = Uri.fromFile(file);
            i.putExtra(Intent.EXTRA_STREAM, uri);
        }

        try {
            System.out.println("Trying to start activity...");
            //startActivity(Intent.createChooser(i, "Send mail..."));
            System.out.println("Succeeded in switching activity?");
        } catch (Exception ex) {
            System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return true;
    }
}