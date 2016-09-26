package com.ariefianzy.plantplaces.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class ImeiManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "PlantPlaces";
    public static final String KEY_IMEI = "imei";
    private static final String IS_LOGIN = "IsLoggedIn";

    public ImeiManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveImei(String imei){
        editor.putString(KEY_IMEI, imei);
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public boolean checkImei(){
        if(!this.isLoggedIn()){
           return false;
        }else {
            return true;
        }
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN,false);
    }

    public HashMap<String, String> getEmei(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_IMEI,pref.getString(KEY_IMEI,null));
        return user;
    }

    public void logout() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }
}