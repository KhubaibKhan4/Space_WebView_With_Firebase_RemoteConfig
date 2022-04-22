package com.Space.Web.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    private static final String SHOW_INTRO = "showIntro";
    private static final String IS_DARK_MODE = "darkMode";




    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setShowIntro(Context ctx, String show) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(SHOW_INTRO, show);
        editor.apply();
    }

    public static String getShowIntro(Context ctx) {
        return getSharedPreferences(ctx).getString(SHOW_INTRO, "");
    }

    public static void setIsDarkMode(Context ctx, String darkMode) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(IS_DARK_MODE, darkMode);
        editor.apply();
    }

    public static String getIsDarkMode(Context ctx) {
        return getSharedPreferences(ctx).getString(IS_DARK_MODE, "");
    }


}
