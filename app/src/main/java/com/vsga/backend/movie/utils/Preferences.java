package com.vsga.backend.movie.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.vsga.backend.movie.R;

/**
 * Created by oiDutS on 04/12/2017.
 */

public class Preferences {

    public static String readSharedPreferences(Context context, String key, String defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );

        return sharedPref.getString(key, defaultValue);
    }

    public static void writeSharedPreferences(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getUserRole(Context context, String key, String defaultValue) {
        String role = readSharedPreferences(context,"ROLE","Guest");
        return role;
    }
}
