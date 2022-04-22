package com.Space.Web.Utilities;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.Space.Web.BuildConfig;
import com.Space.Web.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

public class RemoteConfigUtils {

    @SuppressLint("StaticFieldLeak")
    private static FirebaseRemoteConfig firebaseRemoteConfig;
    private static final String BASE_URL_KEY = "base_url";
    private static final String HIDE_INTERSTITIAL_Ad_KEY = "HideInterstitialAd";
    private static final String HIDE_BANNER_AD_KEY = "HideBannerAd";
    private static final String BANNER_AD_ID_KEY = "ad_unitId_BANNER";
    private static final String INTERSTITIAL_Ad_ID_KEY = "ad_unitId_interstitialAd";
    private static final String HIDE_NAVIGATION_VIEW = "HideNavigationView";
    private static final String HIDE_TOOLBAR = "HideToolBar";
    private static final String TOOLBAR_TEXT_COLOR = "ToolBar_text_color";
    private static final String IS_TOOLBAR_ICONS_LITE = "isToolBarIcons_Lite";
    private static final String ENABLE_LOADER = "Enable_Loader";
    private static final String LOADER_STYLE = "Loader_style";
    private static final String LOADER_COLOR = "Loader_color";
    private static final String ENABLE_SWIPE_REFRESH = "Enable_SwipeRefresh";
    private static final String HIDE_APP_INTRO = "HideAppIntro";
    private static final String APP_INTRO_TEXT_COLOR = "AppIntroTextColor";
    private static final String IS_RTL = "Is_RTL";
    private static final String ENABLE_DARK_MODE = "enable_DarkMode";
    private static final String ENABLE_SHARE = "enable_share";
    private static final String ENABLE_RATE = "enable_rate";
    private static final String ENABLE_CONTACT_US = "enable_contact_us";
    private static final String ENABLE_CAMERA_PERMISSION = "enable_camera_permission";


    public static void init() {

        firebaseRemoteConfig = getFirebaseRemoteConfig();

    }

    private static FirebaseRemoteConfig getFirebaseRemoteConfig() {


        int minimumFetchIntervalInSeconds;

        if (BuildConfig.DEBUG) {
            minimumFetchIntervalInSeconds = 0;// Kept 0 for quick debug

        } else {
            minimumFetchIntervalInSeconds = 10; // Change this based on your requirement

        }


        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(minimumFetchIntervalInSeconds)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        firebaseRemoteConfig.fetchAndActivate();


        return firebaseRemoteConfig;
    }

    public static String getBaseUrl() {

        return firebaseRemoteConfig.getString(BASE_URL_KEY);
    }

    public static boolean isHideInterstitialAd() {

        return firebaseRemoteConfig.getBoolean(HIDE_INTERSTITIAL_Ad_KEY);
    }

    public static boolean isHideToolBar() {

        return firebaseRemoteConfig.getBoolean(HIDE_TOOLBAR);
    }

    public static boolean isHideBannerAd() {

        return firebaseRemoteConfig.getBoolean(HIDE_BANNER_AD_KEY);
    }

    public static boolean isHideNavigationView() {

        return firebaseRemoteConfig.getBoolean(HIDE_NAVIGATION_VIEW);
    }

    public static String getBannerAdId() {

        return firebaseRemoteConfig.getString(BANNER_AD_ID_KEY);
    }

    public static String getInterstitialAdId() {

        return firebaseRemoteConfig.getString(INTERSTITIAL_Ad_ID_KEY);
    }


    public static String getToolbarTextColor() {

        return firebaseRemoteConfig.getString(TOOLBAR_TEXT_COLOR);
    }


    public static boolean isToolBarIcons_Lite() {

        return firebaseRemoteConfig.getBoolean(IS_TOOLBAR_ICONS_LITE);
    }

    public static boolean enableLoader() {

        return firebaseRemoteConfig.getBoolean(ENABLE_LOADER);
    }

    public static String getLoaderStyle() {

        return firebaseRemoteConfig.getString(LOADER_STYLE);
    }

    public static String getLoaderColor() {

        return firebaseRemoteConfig.getString(LOADER_COLOR);
    }

    public static String getAppIntroTextColor() {

        return firebaseRemoteConfig.getString(APP_INTRO_TEXT_COLOR);
    }

    public static boolean enableSwipeRefresh() {

        return firebaseRemoteConfig.getBoolean(ENABLE_SWIPE_REFRESH);
    }

    public static boolean isHideAppIntro() {

        return firebaseRemoteConfig.getBoolean(HIDE_APP_INTRO);
    }

    public static boolean isRTL() {

        return firebaseRemoteConfig.getBoolean(IS_RTL);
    }

    public static boolean enableDarkMode() {

        return firebaseRemoteConfig.getBoolean(ENABLE_DARK_MODE);
    }


    public static boolean enableShare() {

        return firebaseRemoteConfig.getBoolean(ENABLE_SHARE);
    }

    public static boolean enableRate() {

        return firebaseRemoteConfig.getBoolean(ENABLE_RATE);
    }

    public static boolean enableContactUs() {

        return firebaseRemoteConfig.getBoolean(ENABLE_CONTACT_US);
    }

    public static boolean enableCameraPermission() {

        return firebaseRemoteConfig.getBoolean(ENABLE_CAMERA_PERMISSION);
    }

}


