package com.cathedrale.GcmNotification;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cathedrale.Utils.SharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class GcmRegister {

    private GoogleCloudMessaging gcm;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String SENDER_ID = "1019144578894";//829014106511
    private String regid;
    private Context context;
    private SharedPref sharedPref;

    public GcmRegister(Context context){
        this.context = context;
        sharedPref = new SharedPref(context);
        if(sharedPref.getString("Gcm_ID")!=null){
           Log.e("@@@@@@@@@@@@@@@@@@@@", "GCMID:" + sharedPref.getString("Gcm_ID"));
        }else{
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(context);
                new RegisterDeviceWithGCM().execute();
            } else {
                Toast.makeText(context, "No valid Google Play Services found.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
               // Log.e("@@@@@@@@@@@@@@@@@@@@", "This device is not supported.");
            }
            return false;
        }
        return true;
    }
    public class RegisterDeviceWithGCM extends AsyncTask< String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
               // Log.e("@@@@@@@@@@  regid", regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
               // Log.e("@@@@@@@@@@  regid", msg);

            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(regid!=null){
                sharedPref.putString("Gcm_ID", regid);
            }

        }
    }
}
