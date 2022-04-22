package com.Space.Web.Activity;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;

import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.Space.Web.R;
import com.Space.Web.Utilities.RemoteConfigUtils;
import com.Space.Web.Utilities.SaveSharedPreference;
import com.Space.Web.Utilities.VideoEnabledWebChromeClient;
import com.Space.Web.Utilities.VideoEnabledWebView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressLint("NewApi")
public class WebViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, ViewTreeObserver.OnScrollChangedListener {

    AdView mAdView;
    private InterstitialAd mInterstitialAd;
    DrawerLayout drawer;
    ImageView forward, backward, refresh;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILE_CHOOSER_RESULT_CODE = 1;
    private static final String TAG = WebViewActivity.class.getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final String[] PERMISSIONS_REQ = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private PermissionRequest myRequest;
    private String url;
    SwitchCompat nightModeButton;
    NavigationView navigationView;
    Toolbar toolbar;
    static int REQUEST_PHONE_CALL = 1;
    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    Dialog dialog;
    TextView toolBarTitle;
    int colorFromToolbar;
    int colorFromTextColor;
    SwipeRefreshLayout mySwipeRefreshLayout;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    static String USER_AGENT_POSTFIX = "SPWAndroid"; // useful for identifying traffic, e.g. in Google Analytics
    static String CUSTOM_USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";    // custom user-agent


    @SuppressLint({"ResourceType", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent in = getIntent();
        Uri data = in.getData();

        setContentView(R.layout.activity_webview);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolBarTitle = findViewById(R.id.toolbar_title);
        this.webView = findViewById(R.id.webView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.backward);
        refresh = findViewById(R.id.refresh);
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);


        if (data == null) {
            setUpWebViewDefaults(RemoteConfigUtils.getBaseUrl());
        } else {
            setUpWebViewDefaults(Objects.requireNonNull(data).toString());
        }
        setSupportActionBar(toolbar);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (RemoteConfigUtils.isRTL()) {

            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        }


        if (!RemoteConfigUtils.enableSwipeRefresh()) {

            mySwipeRefreshLayout.setRefreshing(false);
            mySwipeRefreshLayout.setEnabled(false);
        } else {

            mySwipeRefreshLayout.setOnRefreshListener(this);
            mySwipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(this);
        }

        Menu menuView = navigationView.getMenu();
        MenuItem darkModeItem = menuView.findItem(R.id.dark_switch);
        MenuItem shareItem = menuView.findItem(R.id.share);
        MenuItem rateItem = menuView.findItem(R.id.rate);
        MenuItem contactUsItem = menuView.findItem(R.id.contact_us);

        nightModeButton = darkModeItem.getActionView().findViewById(R.id.nightModeButton);
        final int colorFrom = getResources().getColor(R.color.colorWhite);
        final int colorTo = getResources().getColor(R.color.dark_color);
        final int colorToTextColor = getResources().getColor(R.color.colorPrimaryDark);
        final int colorToToolbar = getResources().getColor(R.color.dark_color);

        colorFromToolbar = getResources().getColor(R.color.colorPrimary);
        colorFromTextColor = getResources().getColor(R.color.colorPrimary);


        if (!RemoteConfigUtils.enableDarkMode()) {

            darkModeItem.setVisible(false);
        }

        if (!RemoteConfigUtils.enableShare()) {

            shareItem.setVisible(false);
        }

        if (!RemoteConfigUtils.enableRate()) {

            rateItem.setVisible(false);
        }

        if (!RemoteConfigUtils.enableContactUs()) {

            contactUsItem.setVisible(false);
        }

        if (RemoteConfigUtils.enableCameraPermission()) {

            if (!checkPermissions()) {
                requestPermissions();
            }

        }


        if (SaveSharedPreference.getIsDarkMode(getApplicationContext()).equals("true")) {


            animateNavDrawerBackground(colorFrom, colorTo, colorFromTextColor);
            animateToolBarBackground(colorFromToolbar, colorToToolbar);
            statusBarNightColor();
            nightModeButton.setChecked(true);
            forward.setImageResource(R.drawable.white_forward);
            backward.setImageResource(R.drawable.white_backward);
            refresh.setImageResource(R.drawable.refresh_white);
            toolBarTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));


        } else {

            animateNavDrawerBackground(colorTo, colorFrom, colorToTextColor);
            animateToolBarBackground(colorToToolbar, colorFromToolbar);
            statusBarNormalColor();

            if (isValidColor(RemoteConfigUtils.getToolbarTextColor())) {

                toolBarTitle.setTextColor(Color.parseColor(RemoteConfigUtils.getToolbarTextColor()));
            }

            if (RemoteConfigUtils.isToolBarIcons_Lite()) {

                forward.setImageResource(R.drawable.white_forward);
                backward.setImageResource(R.drawable.white_backward);
                refresh.setImageResource(R.drawable.refresh_white);
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));

            } else {

                forward.setImageResource(R.drawable.dark_forward);
                backward.setImageResource(R.drawable.dark_backward);
                refresh.setImageResource(R.drawable.refresh_dark);
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.dark_color));

            }

        }


        nightModeButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                SaveSharedPreference.setIsDarkMode(getApplicationContext(), "true");
                animateNavDrawerBackground(colorFrom, colorTo, colorFromTextColor);
                animateToolBarBackground(colorFromToolbar, colorToToolbar);
                statusBarNightColor();
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
                toolBarTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));

                forward.setImageResource(R.drawable.white_forward);
                backward.setImageResource(R.drawable.white_backward);
                refresh.setImageResource(R.drawable.refresh_white);


            } else {
                SaveSharedPreference.setIsDarkMode(getApplicationContext(), "false");
                animateNavDrawerBackground(colorTo, colorFrom, colorToTextColor);
                animateToolBarBackground(colorToToolbar, colorFromToolbar);
                statusBarNormalColor();
                if (isValidColor(RemoteConfigUtils.getToolbarTextColor())) {
                    toolBarTitle.setTextColor(Color.parseColor(RemoteConfigUtils.getToolbarTextColor()));
                }

                if (RemoteConfigUtils.isToolBarIcons_Lite()) {

                    forward.setImageResource(R.drawable.white_forward);
                    backward.setImageResource(R.drawable.white_backward);
                    refresh.setImageResource(R.drawable.refresh_white);
                    toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));

                } else {

                    forward.setImageResource(R.drawable.dark_forward);
                    backward.setImageResource(R.drawable.dark_backward);
                    refresh.setImageResource(R.drawable.refresh_dark);
                    toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.dark_color));

                }

            }
        });


        navigationView.setNavigationItemSelectedListener(this);
        url = getIntent().getStringExtra("url");
        MobileAds.initialize(this, initializationStatus -> {
        });
        LinearLayout adBannerLayout = findViewById(R.id.layout_banner_holder);
        View header = navigationView.getHeaderView(0);

        LinearLayout nav_layout = header.findViewById(R.id.nav_layout);
        TextView description = header.findViewById(R.id.description);
        TextView title = header.findViewById(R.id.title);


        nav_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        if (isValidColor(RemoteConfigUtils.getToolbarTextColor())) {
            title.setTextColor(Color.parseColor(RemoteConfigUtils.getToolbarTextColor()));
            description.setTextColor(Color.parseColor(RemoteConfigUtils.getToolbarTextColor()));

        }


        mAdView = new AdView(this);
        mAdView.setAdUnitId(RemoteConfigUtils.getBannerAdId());
        mAdView.setAdSize(AdSize.BANNER);
        adBannerLayout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();

        if (!RemoteConfigUtils.isHideBannerAd()) {

            adBannerLayout.setVisibility(View.VISIBLE);
            mAdView.loadAd(adRequest);
        }

        if (RemoteConfigUtils.isHideNavigationView()) {

            toolbar.setNavigationIcon(null);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


        if (RemoteConfigUtils.isHideToolBar()) {

            toolbar.setVisibility(View.GONE);
        }


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(RemoteConfigUtils.getInterstitialAdId());

        if (!RemoteConfigUtils.isHideInterstitialAd()) {

            mInterstitialAd.loadAd(new AdRequest.Builder().build());

        }


        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });


        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                if (verifyPermissions(WebViewActivity.this)) {
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url.replace("blob:", "")));
                    request.setMimeType(mimeType);
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                            mimeType));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                    url, contentDisposition, mimeType));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File",
                            Toast.LENGTH_LONG).show();
                } //prompt user for permission


            }
        });


        backward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isConnected()) {

                    if (webView.canGoBack()) {
                        webView.goBack();
                    }
                } else {

                    tryAgainPage(url);
                }


            }
        });


        forward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isConnected()) {

                    if (webView.canGoForward()) {
                        webView.goForward();
                    }

                } else {

                    tryAgainPage(url);
                }


            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webView.reload();
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView.canGoBack()) {

                    if (isConnected()) {

                        if (webChromeClient.isVideoFullscreen()) {

                            webChromeClient.onHideCustomView();
                        } else {

                            webView.goBack();

                        }


                    } else {

                        tryAgainPage(url);
                    }

                } else {

                    if (doubleBackToExitPressedOnce) {
                        finish();
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getString(R.string.please_click_back), Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (isConnected()) {


            if (id == R.id.home) {

                webView.loadUrl(RemoteConfigUtils.getBaseUrl());


            }

            if (id == R.id.about) {

                setUpWebViewDefaults(getString(R.string.about_website_link));

            }


            if (id == R.id.portfolio) {

                setUpWebViewDefaults(getString(R.string.portfolio_website_link));

            }

            if (id == R.id.share) {

                shareApp();

            }

            if (id == R.id.rate) {

                rateApp();

            }


            if (id == R.id.logout) {

                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }

            if (id == R.id.contact_us) {

                ContactUsPopup();
            }

        } else {

            tryAgainPage(url);
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void shareApp() {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String link = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, link);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)));
        } catch (Exception ignored) {


        }
    }

    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebViewDefaults(String webUrl) {


        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = findViewById(R.id.videoLayout); // Your own view, read class comments
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new Callback());
        webView.setWebChromeClient(new ChromeClient());
        WebSettings webSettings = webView.getSettings();

        // setting custom user agent
        String userAgent = webSettings.getUserAgentString();
        userAgent = CUSTOM_USER_AGENT;

        userAgent = userAgent + " " + USER_AGENT_POSTFIX;

        webSettings.setUserAgentString(userAgent);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setSupportZoom(false);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);

        webView.setHapticFeedbackEnabled(false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setGeolocationDatabasePath(this.getFilesDir().getPath());
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {

            private File createImageFile() throws IOException {

                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat(getString(R.string.data_formate)).format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File imageFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                return imageFile;

            }

            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
                // Double check that we don't have any existing callbacks
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePath;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                // Create AndroidExampleFolder at sdcard
                // Create AndroidExampleFolder at sdcard
                File imageStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES)
                        , "AndroidExampleFolder");
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }
                // Create camera captured image file path and name
                File file = new File(
                        imageStorageDir + File.separator + "IMG_"
                                + System.currentTimeMillis()
                                + ".jpg");
                mCapturedImageURI = Uri.fromFile(file);
                // Camera capture image intent
                final Intent captureIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                        , new Parcelable[]{captureIntent});
                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE);
            }


            public void onPermissionRequest(final PermissionRequest request) {
                myRequest = request;

                for (String permission : request.getResources()) {
                    if ("android.webkit.resource.AUDIO_CAPTURE".equals(permission)) {
                        askForPermission(request.getOrigin().toString(), Manifest.permission.RECORD_AUDIO, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    }
                }
            }

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                checkLocationPermission();


            }


            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {

            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                if (fullscreen) {
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    //noinspection all
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
                } else {
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
        // Call private class InsideWebViewClient
        webView.setWebViewClient(new WebViewClientImpl(WebViewActivity.this));

        if (isConnected()) {
            if (url != null) {
                webView.loadUrl(url);
            } else {
                webView.loadUrl(webUrl);
            }
        } else {

            tryAgainPage(webUrl);
        }

    }


    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILE_CHOOSER_RESULT_CODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri result = null;
            try {
                if (resultCode != RESULT_OK) {
                } else {
                    // retrieve from the private variable if the intent is null
                    result = data == null ? mCapturedImageURI : data.getData();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "activity :" + e,
                        Toast.LENGTH_LONG).show();
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }


    }


    @Override
    public void onRefresh() {

        if (isConnected()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                    mySwipeRefreshLayout.setRefreshing(false);
                }

            }, 2000);


        } else {

            tryAgainPage(url);
        }
    }

    @Override
    public void onScrollChanged() {

        mySwipeRefreshLayout.setEnabled(webView.getScrollY() == 0);

    }


    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean verifyPermissions(Activity activity) {
        // Check if we have write permission
        int WritePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (WritePermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_REQ,
                    REQUEST_CODE_PERMISSION
            );
            return false;
        } else {
            return true;
        }
    }

    public void askForPermission(String origin, String permission, int requestCode) {

        Log.d("WebView", "inside askForPermission for" + origin + "with" + permission);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(WebViewActivity.this,
                    permission)) {

                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(WebViewActivity.this,
                        new String[]{permission},
                        requestCode);
            }
        } else {
            myRequest.grant(myRequest.getResources());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                Log.d("WebView", "PERMISSION FOR AUDIO");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    myRequest.grant(myRequest.getResources());

                }  // permission denied, boo! Disable the


            }

            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                    }

                }  // permission denied, boo! Disable the
                // functionality that depends on this permission.

            }

        }


    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }  // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        }
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    void tryAgainPage(String url) {
        Intent intent = new Intent(getApplicationContext(), NoInternetConnectionActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
        finish();

    }

    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public void ContactUsPopup() {

        dialog = new Dialog(WebViewActivity.this, R.style.ThemeUserDialog);
        dialog.setContentView(R.layout.contact_us_popup);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.00);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85);

        final int colorFrom = getResources().getColor(R.color.colorPrimary);
        final int colorTo = getResources().getColor(R.color.dark_color);

        Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;

        LinearLayout pop_up_Layout = dialog.findViewById(R.id.pop_up_Layout);
        TextView aboutText = dialog.findViewById(R.id.aboutText);
        TextView versionText = dialog.findViewById(R.id.versionText);
        TextView followUs = dialog.findViewById(R.id.followUs);
        ImageView close = dialog.findViewById(R.id.close);


        if (SaveSharedPreference.getIsDarkMode(getApplicationContext()).equals("true")) {

            animateContactPopupBackground(colorFrom, colorTo, pop_up_Layout, aboutText, versionText, followUs);

        } else {

            animateContactPopupBackground(colorTo, colorFrom, pop_up_Layout, aboutText, versionText, followUs);

        }

        ImageView facebook = dialog.findViewById(R.id.faceBook);
        ImageView instgram = dialog.findViewById(R.id.instgram);
        ImageView phone = dialog.findViewById(R.id.phone);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        instgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_url)));
                startActivity(browserIntent);
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_url)));
                startActivity(browserIntent);
            }
        });


        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    MakePhoneCall(getString(R.string.phone_number));
                }
            }
        });


        dialog.show();


    }

    @SuppressLint("MissingPermission")
    public void MakePhoneCall(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(WebViewActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    public void animateNavDrawerBackground(int colorFrom, int colorTo, final int textColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                navigationView.setBackgroundColor((int) animator.getAnimatedValue());
                navigationView.setItemTextColor(ColorStateList.valueOf(textColor));
                navigationView.setItemIconTintList(ColorStateList.valueOf(textColor));


            }

        });
        colorAnimation.start();
    }

    public void animateToolBarBackground(int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                toolbar.setBackgroundColor((int) animator.getAnimatedValue());


            }

        });
        colorAnimation.start();
    }

    public void statusBarNightColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_color, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_color));
        }

    }

    public void statusBarNormalColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }


    public void animateContactPopupBackground(final int colorFrom, int colorTo, final LinearLayout linearLayout, final TextView aboutText, final TextView versionText, final TextView followUs) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                if (dialog != null) {

                    linearLayout.setBackgroundColor((int) animator.getAnimatedValue());
                    aboutText.setTextColor(Color.parseColor(RemoteConfigUtils.getToolbarTextColor()));
                    versionText.setTextColor(Color.parseColor(RemoteConfigUtils.getToolbarTextColor()));
                    followUs.setTextColor(Color.parseColor(RemoteConfigUtils.getToolbarTextColor()));

                }


            }

        });
        colorAnimation.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mySwipeRefreshLayout.getViewTreeObserver().removeOnScrollChangedListener(this);

    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * this method request to permission asked.
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
        } else {
            Log.i(TAG, "Requesting permission");
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(WebViewActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    private boolean isValidColor(String color) {

        Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8}|[0-9A-F]{3}|[0-9A-F]{6}|[0-9A-F]{8})");
        Matcher m = colorPattern.matcher(color);
        return m.matches();
    }

}

