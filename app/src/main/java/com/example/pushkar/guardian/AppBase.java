package com.example.pushkar.guardian;

import android.app.Application;

/**
 * Created by pushkar on 1/21/16.
 */
public class AppBase extends Application {

    private String userID;

    public String getUserID(){
        return userID;
    }
    public void setUserID(String s){
        userID = s;
    }

}
