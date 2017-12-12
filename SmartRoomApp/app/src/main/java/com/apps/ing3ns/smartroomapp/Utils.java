package com.apps.ing3ns.smartroomapp;

import android.content.SharedPreferences;

/**
 * Created by JuanDa on 15/08/2017.
 */

public class Utils {

    public static Boolean getGPSSharedPreferences(SharedPreferences preferences) {
        return preferences.getBoolean("gps",false);
    }

    public static String getDomiciliarioSharedPreferences(SharedPreferences preferences) {
        return preferences.getString("domiciliario","");
    }

    public static void removeGPSandDomSharedPreferences(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("gps");
        editor.remove("domiciliario");
        editor.apply();
    }

}
