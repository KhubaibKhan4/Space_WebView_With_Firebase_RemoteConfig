package com.Space.Web.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.Space.Web.R;
import com.Space.Web.Utilities.RemoteConfigUtils;
import com.Space.Web.Utilities.SaveSharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Audio.AudioColumns.TITLE_KEY;


public class Splash extends AppCompatActivity {

    Handler handler = new Handler();
    ConstraintLayout splashBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashBackground = findViewById(R.id.splashBackground);


        if (SaveSharedPreference.getIsDarkMode(getApplicationContext()).equals("true")) {

            splashBackground.setBackgroundColor(getResources().getColor(R.color.dark_color));
            statusBarNightColor();
        } else {



            splashBackground.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            statusBarNormalColor();

        }

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        int SPLASH_DISPLAY_LENGTH = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (RemoteConfigUtils.isHideAppIntro()) {

                    Intent mainIntent = new Intent(Splash.this, WebViewActivity.class);
                    Splash.this.startActivity(mainIntent);

                } else {

                    if (SaveSharedPreference.getShowIntro(getApplicationContext()).equals("true")) {

                        Intent mainIntent = new Intent(Splash.this, WebViewActivity.class);
                        Splash.this.startActivity(mainIntent);

                    } else {

                        Intent mainIntent = new Intent(Splash.this, AppIntroActivity.class);
                        Splash.this.startActivity(mainIntent);
                    }


                }


                Splash.this.finish();



                /* Create an Intent that will start the Menu-Activity. */

            }
        }, SPLASH_DISPLAY_LENGTH);

    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void statusBarNormalColor() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }

    }

    public void statusBarNightColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_color, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_color));
        }

    }

}