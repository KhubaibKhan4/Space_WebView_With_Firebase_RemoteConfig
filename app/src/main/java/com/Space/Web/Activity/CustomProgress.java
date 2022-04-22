package com.Space.Web.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Window;
import android.widget.ProgressBar;

import com.Space.Web.R;
import com.Space.Web.Utilities.RemoteConfigUtils;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.github.ybq.android.spinkit.style.Wave;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomProgress {

    public static CustomProgress customProgress = null;
    private Dialog mDialog;

    public static CustomProgress getInstance() {
        if (customProgress == null) {
            customProgress = new CustomProgress();
        }
        return customProgress;
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void showProgress(Context context, boolean cancelable) {
        mDialog = new Dialog(context, R.style.CustomDialogTheme);
        // no tile for the dialog

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.prograss_bar_dialog);
        ProgressBar mProgressBar = mDialog.findViewById(R.id.progress_bar);



        if (RemoteConfigUtils.getLoaderStyle().equals("Wave")) {
            Wave wave = new Wave();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  wave.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(wave);

        } else if (RemoteConfigUtils.getLoaderStyle().equals("RotatingPlane")) {

            RotatingPlane rotatingPlane = new RotatingPlane();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  rotatingPlane.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(rotatingPlane);

        } else if (RemoteConfigUtils.getLoaderStyle().equals("DoubleBounce")) {

            DoubleBounce doubleBounce = new DoubleBounce();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  doubleBounce.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(doubleBounce);

        } else if (RemoteConfigUtils.getLoaderStyle().equals("WanderingCubes")) {

            WanderingCubes wanderingCubes = new WanderingCubes();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  wanderingCubes.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(wanderingCubes);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("Pulse")) {

            Pulse pulse = new Pulse();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  pulse.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(pulse);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("ChasingDots")) {

            ChasingDots chasingDots = new ChasingDots();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  chasingDots.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(chasingDots);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("ThreeBounce")) {

            ThreeBounce threeBounce = new ThreeBounce();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  threeBounce.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(threeBounce);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("Circle")) {

            Circle circle = new Circle();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  circle.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(circle);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("CubeGrid")) {

            CubeGrid cubeGrid = new CubeGrid();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  cubeGrid.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(cubeGrid);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("CubeGrid")) {

            CubeGrid cubeGrid = new CubeGrid();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  cubeGrid.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(cubeGrid);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("FadingCircle")) {

            FadingCircle fadingCircle = new FadingCircle();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  fadingCircle.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(fadingCircle);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("FoldingCube")) {

            FoldingCube foldingCube = new FoldingCube();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  foldingCube.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(foldingCube);

        }else if (RemoteConfigUtils.getLoaderStyle().equals("RotatingCircle")) {

            RotatingCircle rotatingCircle = new RotatingCircle();
            if (isValidColor(RemoteConfigUtils.getLoaderColor())){  rotatingCircle.setColor(Color.parseColor(RemoteConfigUtils.getLoaderColor())); }
            mProgressBar.setIndeterminateDrawable(rotatingCircle);
        }else {

            Wave wave = new Wave();
            mProgressBar.setIndeterminateDrawable(wave);
        }


        mProgressBar.setIndeterminate(true);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private boolean isValidColor(String color){

        Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8}|[0-9A-F]{3}|[0-9A-F]{6}|[0-9A-F]{8})");
        Matcher m = colorPattern.matcher(color);
        return m.matches();
    }
}