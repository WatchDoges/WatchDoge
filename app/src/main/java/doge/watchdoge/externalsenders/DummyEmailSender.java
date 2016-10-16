package doge.watchdoge.externalsenders;

import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gorbad on 11/10/2016.
 */

public class DummyEmailSender extends GeneralSender{

    //@Override
    //public void provideInformation(HashMap<String, Object> information, View parentView){
    //    super.provideInformation(information, parentView);
    //}

    @Override
    public boolean send(){
        System.out.println("Should send..");
        return false;
    }
}
