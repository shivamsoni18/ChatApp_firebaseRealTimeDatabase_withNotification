package com.example.firebasechat.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shivam on 06-04-2019.
 */

public class PrefManager {

    private Context context;
    public static String prefUSERNAME = "Pusername";
    public static String prefPASSWORD = "Ppassword";


    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "FireChatApp";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";



    // User name (make variable public to access from outside)


    public PrefManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(){
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
        editor.apply();
    }
    public void deletePrefData(){
        editor.clear();
        editor.commit();
        editor.apply();
    }

    public void savePrefValue(String key, String value){
        editor.putString(key,value);
        editor.commit();
        editor.apply();

    }

    public String getPrefValue(String key){
        String returnValue = "";
        try {
            returnValue = pref.getString(key, "");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return returnValue;
    }

    public boolean checkLogin(){
        if(pref.getBoolean(PrefManager.IS_LOGIN,false)){
            return true;
        }else{
            return false;
        }
    }
}