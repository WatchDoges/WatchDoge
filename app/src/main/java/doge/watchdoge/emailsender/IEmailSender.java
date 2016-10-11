package doge.watchdoge.emailsender;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gorbad on 11/10/2016.
 */

public interface IEmailSender {

    public boolean send();

    public void changeMessage(String message);
    public void addToMessage(String message);
    public String getMessage();

    public void addReceiver(String receiver);
    public ArrayList<String> getReceivers();

    public void addAttachment(File attachment);
    public ArrayList<File> getAttachments();
}
