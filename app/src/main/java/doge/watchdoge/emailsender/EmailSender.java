package doge.watchdoge.emailsender;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gorbad on 11/10/2016.
 */

public class EmailSender implements IEmailSender {

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
        return false;
    }
}
