package com.Space.Web.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.Space.Web.R;
import com.Space.Web.Utilities.RemoteConfigUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoInternetConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet_connection);

        Button tryAgain = findViewById(R.id.tryAgainBtn);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected()) {
                    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                    intent.putExtra("url", getIntent().getStringExtra("url"));
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });

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

    private boolean isValidColor(String color){

        Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8}|[0-9A-F]{3}|[0-9A-F]{6}|[0-9A-F]{8})");
        Matcher m = colorPattern.matcher(color);
        return m.matches();
    }
}