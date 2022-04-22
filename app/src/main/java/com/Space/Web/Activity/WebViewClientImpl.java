package com.Space.Web.Activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.Space.Web.R;
import com.Space.Web.Utilities.RemoteConfigUtils;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class WebViewClientImpl extends WebViewClient {

    private Activity activity = null;
    CustomProgress progressDialog = CustomProgress.getInstance();
    String url ;


    public WebViewClientImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();


        if (isConnected(view)) {



            if (url == null) {
                return false;
            }


            if (url.startsWith(activity.getString(R.string.facebook_url))) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }

            if (url.contains(activity.getString(R.string.instagram_url))) {

                Uri uri = Uri.parse("http://instagram.com/");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage(activity.getString(R.string.instagram_package));

                try {
                    view.getContext().startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/")));
                }

                return true;
            }


            if (url.startsWith("market://")) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            if (url.startsWith("https://api")) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            if (url.startsWith("mailto:")) {

                try {
                    List<String> to = new ArrayList<String>();
                    List<String> cc = new ArrayList<String>();
                    List<String> bcc = new ArrayList<String>();
                    String subject = null;
                    String body = null;

                    url = url.replaceFirst("mailto:", "");

                    String[] urlSections = url.split("&");
                    if (urlSections.length >= 2) {

                        to.addAll(Arrays.asList(urlSections[0].split(",")));

                        for (int i = 1; i < urlSections.length; i++) {
                            String urlSection = urlSections[i];
                            String[] keyValue = urlSection.split("=");

                            if (keyValue.length == 2) {
                                String key = keyValue[0];
                                String value = keyValue[1];

                                value = URLDecoder.decode(value, "UTF-8");

                                if (key.equals("cc")) {
                                    cc.addAll(Arrays.asList(url.split(",")));
                                } else if (key.equals("bcc")) {
                                    bcc.addAll(Arrays.asList(url.split(",")));
                                } else if (key.equals("subject")) {
                                    subject = value;
                                } else if (key.equals("body")) {
                                    body = value;
                                }
                            }
                        }
                    } else {
                        to.addAll(Arrays.asList(url.split(",")));
                    }

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("message/rfc822");

                    String[] dummyStringArray = new String[0]; // For list to array conversion
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, to.toArray(dummyStringArray));
                    if (cc.size() > 0) {
                        emailIntent.putExtra(Intent.EXTRA_CC, cc.toArray(dummyStringArray));
                    }
                    if (bcc.size() > 0) {
                        emailIntent.putExtra(Intent.EXTRA_BCC, bcc.toArray(dummyStringArray));
                    }
                    if (subject != null) {
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    }
                    if (body != null) {
                        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                    }
                    view.getContext().startActivity(emailIntent);

                    return true;
                } catch (UnsupportedEncodingException e) {
                    /* Won't happen*/
                }

            } else {
                view.loadUrl(url);
                return true;
            }
            return false;
        }else {

            tryAgainPage(view,url);
        }
        return false;
    }

        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon){

            super.onPageStarted(view, url, favicon);
            if (RemoteConfigUtils.enableLoader()){

                progressDialog.showProgress(view.getContext(), true);

            }

            // edit this

        }

        @Override
        public void onPageFinished (WebView view, String url){
            progressDialog.hideProgress();
        }

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest (WebView view, String url){

            return super.shouldInterceptRequest(view, url);

        }

        private boolean isConnected (View view){
            ConnectivityManager connMgr = (ConnectivityManager) view.getContext().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connMgr != null) {
                networkInfo = connMgr.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isConnected())
                return true;
            else
                return false;
        }


        void tryAgainPage(View view,String url){

            Intent intent = new Intent(view.getContext(), NoInternetConnectionActivity.class);
            intent.putExtra("url",url);
            view.getContext().startActivity(intent);
            activity.finish();

        }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);

        Log.i(TAG, "onReceivedSslError: "+error.toString());
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.i(TAG, "onReceivedError: "+description);

}
}