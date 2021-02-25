package com.weatherlive.darkskyweather.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.weatherlive.darkskyweather.R;


public class ProgressDialog {

    public static ProgressDialog progressDialog;
    public Dialog mDialog;


    public ProgressDialog() {
        super();
    }

    public static ProgressDialog getInstance() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog();
        }
        return progressDialog;
    }

    public void showProgress(Context mContext) {
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_progress_layout);
        mDialog.findViewById(R.id.progress_circular).setVisibility(View.VISIBLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    public void hideProgress() {

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
