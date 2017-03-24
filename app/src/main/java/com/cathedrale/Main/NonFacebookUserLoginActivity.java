package com.cathedrale.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cathedrale.Activity.HomeScreenActivity;
import com.cathedrale.Constants.AppConstants;
import com.cathedrale.GcmNotification.GcmRegister;
import com.cathedrale.Network.ConnectionDetector;
import com.cathedrale.R;
import com.cathedrale.Utils.NetworkUtils;
import com.cathedrale.Utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class NonFacebookUserLoginActivity extends Activity implements View.OnClickListener {
    private SharedPref sharedPref;
    EditText register_email;
    String responseString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_user_login);

        init();
    }

    private void init() {
        sharedPref = new SharedPref(this);
        register_email = (EditText) findViewById(R.id.register_email);

        findViewById(R.id.register_button).setOnClickListener(this);

//        Log.e("@@@@@@@@@@@@@@@@@@@@", "NONFB_EMAIL" + sharedPref.getString(AppConstants.NONFB_EMAIL));
        if (sharedPref.getString(AppConstants.NONFB_EMAIL) != null) {

        } else {
            findViewById(R.id.registerlayout).setVisibility(View.VISIBLE);
            TextView toolbar_text = (TextView) findViewById(R.id.tool_bar_text);
            toolbar_text.setText(" Login");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                try {
                    register_user();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void register_user() throws IOException, JSONException {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            new GcmRegister(NonFacebookUserLoginActivity.this);

            if (register_email.length() == 0) {
                register_email.setError("Enter Email");
                showAlertDialog(NonFacebookUserLoginActivity.this, "Details", "Enter Email", 0);
            } else {
                String emailAddressString = register_email.getText().toString().trim();
                if (AppConstants.isValidEmail(emailAddressString)) {
                    if (sharedPref.getString("Gcm_ID") != null) {
                        CustomAsyncTask customAsyncTask = new CustomAsyncTask();
                        customAsyncTask.execute();
                    }
                } else {
                    /*Toast.makeText(NonFacebookUserLoginActivity.this, "Invalid EmailId", Toast.LENGTH_LONG).show();
                    register_email.setError("Invalid EmailId");*/
                    showAlertDialog(NonFacebookUserLoginActivity.this, "Details", "Invalid EmailId", 0);
                }
            }
        } else {
            showAlertDialog(NonFacebookUserLoginActivity.this, "No Internet Connection", "Connect to Internet and try again", 0);
        }
    }

    private class CustomAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(NonFacebookUserLoginActivity.this, "Please wait", "Registering...");
        }

        @Override
        protected String doInBackground(String... urls) {
            return doregister();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
//                Log.e("result :", result);
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }

                if (result != null) {
                    if (result.contains("ContactId")) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String ContactId = jsonObject.optString("ContactId");
                            sharedPref.putString(AppConstants.ContactId, ContactId);
                            sharedPref.putString(AppConstants.NONFB_EMAIL, register_email.getText().toString());
                            Toast.makeText(NonFacebookUserLoginActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(NonFacebookUserLoginActivity.this, HomeScreenActivity.class));
                            finish();
//                            Log.e("ContactId", ContactId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (result.contains("NO_PATIENT_FOUND")) {
                        showAlertDialog(NonFacebookUserLoginActivity.this, "Details", AppConstants.No_Patioent_found_message, 0);
                    } else {
                        showAlertDialog(NonFacebookUserLoginActivity.this, "Details", AppConstants.No_Patioent_found_message, 0);
                    }
                }
            }
        }
    }

    public String doregister() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String mobileOsVersion = Build.VERSION.RELEASE;
        String mobilePlatformType = "Android";
        String brand = Build.BRAND;
        String DEVICE_MODEL = android.os.Build.MODEL;
        String gcmId = sharedPref.getString("Gcm_ID");
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_UNIQUE_ID = telephonyManager.getDeviceId();

        sharedPref.putString(AppConstants.DEVICE_MODEL, brand);
        sharedPref.putString(AppConstants.DEVICE_TYPE, mobilePlatformType);
        sharedPref.putString(AppConstants.AndroidVersion, mobileOsVersion);
        sharedPref.putString(AppConstants.DEVICE_UNIQUE_ID, DEVICE_UNIQUE_ID);

        /*Log.e("DEVICE_MODEL", brand);
        Log.e("DEVICE_TYPE", mobilePlatformType);
        Log.e("AndroidVersion", mobileOsVersion);
        Log.e("DEVICE_UNIQUE_ID", DEVICE_UNIQUE_ID);*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (gcmId != null) {
            if (!gcmId.equalsIgnoreCase("null") && !gcmId.equalsIgnoreCase("")) {
                paramMap.put("FACEBOOK_EMAIL", null);
                paramMap.put("OTHER_EMAIL", "" + register_email.getText().toString());
                paramMap.put("PUSH_NOTIFICATION_ID", gcmId);
                paramMap.put("DEVICE_MODEL", brand);
                paramMap.put("DEVICE_TYPE", mobilePlatformType);
                paramMap.put("OS_INFORMATION", mobileOsVersion);
                paramMap.put("DEVICE_UNIQUE_ID", sharedPref.getString(AppConstants.DEVICE_UNIQUE_ID));
                paramMap.put("IsPatient", "1");
                paramMap.put("FIRSTNAME", null);
                paramMap.put("LASTNAME", null);
                try {
                    responseString = NetworkUtils.makePOSTRequest(paramMap, AppConstants.LOGIN_URL, NonFacebookUserLoginActivity.this);
//                    Log.e("responseString", responseString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return responseString;
    }

    public void showAlertDialog(Context context, String title, String message, int num) {
        if (num == 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setCancelable(false);
            alertDialog.setMessage(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            // Showing Alert Message
            alertDialog.show();
        } else if (num == 1) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setCancelable(false);
            alertDialog.setMessage(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(NonFacebookUserLoginActivity.this, HomeScreenActivity.class));
                    finish();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
    }
}
