package com.cathedrale.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.cathedrale.Activity.HomeScreenActivity;
import com.cathedrale.Constants.AppConstants;
import com.cathedrale.GcmNotification.GcmRegister;
import com.cathedrale.Network.ConnectionDetector;
import com.cathedrale.R;
import com.cathedrale.Utils.Alert;
import com.cathedrale.Utils.NetworkConnection;
import com.cathedrale.Utils.NetworkUtils;
import com.cathedrale.Utils.SharedPref;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class FacebookLoginActivity extends Activity {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    Boolean isInternetPresent = false;
    private SharedPref sharedPref;
    String responseString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_registration);

        init();

    }

    private void init() {
        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        sharedPref = new SharedPref(this);
        Checkforinternet_and_facebooklogin();

        findViewById(R.id.newuserlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FacebookLoginActivity.this, NonFacebookUserLoginActivity.class));
            }
        });
    }

    private void Checkforinternet_and_facebooklogin() {
       /* Log.e("@@@@@@@@@@@@@@@@@@@@", "FB_ID" + sharedPref.getString(AppConstants.FBId));
        Log.e("@@@@@@@@@@@@@@@@@@@@", "GCMID:" + sharedPref.getString("Gcm_ID"));
        Log.e("@@@@@@@@@@@@@@@@@@@@", "NONFB_EMAIL:" + sharedPref.getString(AppConstants.NONFB_EMAIL));*/
        if (sharedPref.getString(AppConstants.Login_Success) == null) {
            sharedPref.putString(AppConstants.Login_Success, "NotSuccess");
        }
        if (sharedPref.getString(AppConstants.Login_Success).equals("Success") || sharedPref.getString(AppConstants.NONFB_EMAIL) != null) {
            startActivity(new Intent(FacebookLoginActivity.this, HomeScreenActivity.class));
            finish();
        } else {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();
            // check for Internet status
            if (isInternetPresent) {
                new GcmRegister(FacebookLoginActivity.this);
                getfacebookhashkey();
                facebooklogin();
            } else {
//                showAlertDialog(FacebookLoginActivity.this, "No Internet Connection", "Connect to Internet and try again",0);
            }
        }
    }

    public void showAlertDialog(Context context, String title, String message, final int num) {
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
    }

    private void facebooklogin() {
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            fbLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
            fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        final JSONObject object,
                                        GraphResponse response) {
                                    // Application code
                                    Log.e("LoginActivity", response.toString());

                                    if (response != null) {
                                        String id = null;
                                        try {
                                            id = object.getString("id");
                                            String name = object.getString("name");
                                            String email = object.getString("email");
                                            String gender = object.getString("gender");
//                                            String birthday = object.getString("birthday");

//                                            Profile profile = Profile.getCurrentProfile();
//                                            String firstname = profile.getFirstName();
//                                            String lastname = profile.getLastName();
                                            if (name != null) {
                                                String[] namesplit = name.split(" ");
                                                sharedPref.putString(AppConstants.FBFIRSTName, namesplit[0]);
                                                sharedPref.putString(AppConstants.FBLASTName, namesplit[1]);

                                                sharedPref.putString(AppConstants.FBName, name);
                                                sharedPref.putString(AppConstants.FBId, id);
                                                sharedPref.putString(AppConstants.FBEMAIL, email);
                                            }

                                            try {
                                                CustomAsyncTask customAsyncTask = new CustomAsyncTask();
                                                customAsyncTask.execute();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            loginerror();
                                        }
                                    } else {
                                        loginerror();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    // App code
//                    Log.e("LoginActivity", "cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.e("LoginActivity", exception.getCause().toString());
                }
            });
        } else {
            showAlertDialog(FacebookLoginActivity.this, "No Internet Connection",
                    "Connect to Internet and try again", 0);
        }
    }


    private class CustomAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FacebookLoginActivity.this, "Please wait", "Registering...");
        }

        @Override
        protected String doInBackground(String... urls) {
            return doregister();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
//                Log.e("result :",result);
                if (progressDialog != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }

                if (result != null) {
                    if (result.contains("ContactId")) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            String ContactId = jsonObject.optString("ContactId");
                            sharedPref.putString(AppConstants.ContactId, ContactId);
                            sharedPref.putString(AppConstants.Login_Success, "Success");
                            Toast.makeText(FacebookLoginActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(FacebookLoginActivity.this, HomeScreenActivity.class));
                            finish();
//                            Log.e("ContactId", ContactId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (result.contains("NO_PATIENT_FOUND")) {
//                        Toast.makeText(FacebookLoginActivity.this, AppConstants.No_Patioent_found_message, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(FacebookLoginActivity.this, NonFacebookUserLoginActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(FacebookLoginActivity.this, NonFacebookUserLoginActivity.class));
                        finish();
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

       /* Log.e("DEVICE_MODEL", brand);
        Log.e("DEVICE_TYPE", mobilePlatformType);
        Log.e("AndroidVersion", mobileOsVersion);
        Log.e("DEVICE_UNIQUE_ID", DEVICE_UNIQUE_ID);*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (gcmId != null) {
            if (!gcmId.equalsIgnoreCase("null") && !gcmId.equalsIgnoreCase("")) {
                paramMap.put("FACEBOOK_EMAIL", "" + sharedPref.getString(AppConstants.FBEMAIL));
                paramMap.put("OTHER_EMAIL", "" + sharedPref.getString(AppConstants.NONFB_EMAIL));
                paramMap.put("PUSH_NOTIFICATION_ID", gcmId);
                paramMap.put("DEVICE_MODEL", brand);
                paramMap.put("DEVICE_TYPE", mobilePlatformType);
                paramMap.put("OS_INFORMATION", mobileOsVersion);
                paramMap.put("DEVICE_UNIQUE_ID", sharedPref.getString(AppConstants.DEVICE_UNIQUE_ID));
                paramMap.put("IsPatient", "0");
                paramMap.put("FIRSTNAME", sharedPref.getString(AppConstants.FBFIRSTName));
                paramMap.put("LASTNAME", sharedPref.getString(AppConstants.FBLASTName));

                try {
                    responseString = NetworkUtils.makePOSTRequest(paramMap, AppConstants.LOGIN_URL, FacebookLoginActivity.this);
//                    Log.e("responseString", responseString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return responseString;
    }

    private void loginerror() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(FacebookLoginActivity.this);
        alert.setCancelable(false);
        alert.setTitle("Login Error");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    private void getfacebookhashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.cathedrale",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent i) {
        callbackManager.onActivityResult(reqCode, resCode, i);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            checkConnection();
        }
    };

    private void checkConnection() {
        if (!new NetworkConnection(FacebookLoginActivity.this).isConnectionAvailable()) {
            new Alert(FacebookLoginActivity.this).alertMessage("Connection lost", R.string.check_connection);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, intentFilter);
    }
}
