package com.cathedrale.Graph;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cathedrale.Activity.OtherReportsActivity;
import com.cathedrale.Constants.AppConstants;
import com.cathedrale.R;
import com.cathedrale.Twitter.OnLoginComplete;
import com.cathedrale.Twitter.TwitterLogin;
import com.cathedrale.Utils.Alert;
import com.cathedrale.Utils.NetworkConnection;
import com.cathedrale.Utils.SharedPref;
import com.cathedrale.database.Database;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.brickred.socialauth.Profile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BarGraphActivity extends Activity implements View.OnClickListener, OnLoginComplete {
    BarChart chart;
    CallbackManager callbackManager;
    LoginManager manager;
    private SharedPref sharedPref;
    Database db;
    private TwitterLogin twitterLogin;
    ArrayList<HashMap<String, String>> databaseSymptom_Score = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> databaseTreatment_Score = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> databaseTotal_Score = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bar_graph);

        init();
    }

    private void init() {
        db = new Database(this);
        db.open();
        sharedPref = new SharedPref(this);
        chart = (BarChart) findViewById(R.id.chart);
        TextView toolbar_text = (TextView) findViewById(R.id.tool_bar_text);
        toolbar_text.setText(" Weekly Score Graph");
        findViewById(R.id.facebook).setOnClickListener(this);
        findViewById(R.id.twitter).setOnClickListener(this);
        findViewById(R.id.others).setOnClickListener(this);
        findViewById(R.id.linearfacebook).setOnClickListener(this);

        showBarchart();
    }


    private void showBarchart() {
        databaseSymptom_Score = db.getScore();
        databaseTreatment_Score = db.getTreatmentScore();
        if (databaseSymptom_Score.size() == 0 && databaseTreatment_Score.size() == 0) {
            Toast.makeText(BarGraphActivity.this, "No points , please answer to questions to show graph", Toast.LENGTH_LONG).show();
            findViewById(R.id.reportslayout).setVisibility(View.GONE);
        }
        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("Cathedral eye clinic");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        databaseSymptom_Score = db.getScore();
        databaseTreatment_Score = db.getTreatmentScore();
//      Log.e("database_Score", "" + database_Score);

        int j = 0;
        for (int i = 0; i < databaseSymptom_Score.size(); i++) {
            String score = databaseSymptom_Score.get(i).get("score");
            String week = databaseSymptom_Score.get(i).get("week");
            j = j + 1;
            BarEntry v1e1 = new BarEntry(Integer.parseInt(score), j - 1); // Jan
            valueSet1.add(v1e1);
        }

        int k = 0;
        for (int i = 0; i < databaseTreatment_Score.size(); i++) {
            String score = databaseTreatment_Score.get(i).get("score");
            String week = databaseTreatment_Score.get(i).get("week");
            k = k + 1;
            BarEntry v1e2 = new BarEntry(Integer.parseInt(score), k - 1); // Jan
            valueSet2.add(v1e2);
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Symptom");
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Treatment");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        barDataSet2.setColor(Color.rgb(192, 192, 192));
        //    barDataSet1.setColor(Color.rgb(0,191, 255));
//        barDataSet1.setColor(Color.rgb(30,144, 255));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        databaseTotal_Score = db.getTotalScore();
        ArrayList<String> xAxis = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < databaseTotal_Score.size(); i++) {
            String score = databaseTotal_Score.get(i).get("score");
            String week = databaseTotal_Score.get(i).get("week");
            j = j + 1;
            xAxis.add(j - 1 + "W");
        }

        if (xAxis.size() <= 3) {
            for (int i = 0; i <= 3; i++) {
                xAxis.add(xAxis.size() + 1 - 1 + "W");
            }
        }
        return xAxis;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook:
                showdialog("Post to Facebook", 0);
                break;
            case R.id.twitter:
                showdialog("Post to Twitter", 1);
                break;
            case R.id.others:
                startActivity(new Intent(BarGraphActivity.this, OtherReportsActivity.class));
                break;
            case R.id.linearfacebook:
                TakescreenshotandShare(0);
        }
    }

    private void showdialog(String message, final int num) {
        AlertDialog.Builder shareDialog = new AlertDialog.Builder(this);
        shareDialog.setTitle("Share Screenshot");
//        shareDialog.setMessage(message);
        shareDialog.setPositiveButton(message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (num == 1) {
                    sharetoTwitter();
                } else if (num == 0) {
                    TakescreenshotandShare(0);
                }
                dialog.cancel();
            }
        });
        shareDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        shareDialog.show();
    }

    private void sharetoTwitter() {
        twitterLogin = new TwitterLogin(this);
        if (sharedPref.getString(AppConstants.TWITTER_USERNAME) != null) {
            if (twitterLogin.twittweAdapter != null) {
                twitterLogin.twittweAdapter.updateStatus("hello", twitterLogin.new MessageListener(), true);
            } else {
                twitterLogin.calltwitterLogin();
            }
        } else {
            twitterLogin.calltwitterLogin();
        }
    }

    private void sharefacebookImage(final Bitmap bitmap) {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, permissionNeeds);
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("loginResult :", "onSuccess");
                sharePhotoToFacebook(bitmap);
            }

            @Override
            public void onCancel() {
                Log.e("error :", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
                Log.e("error :", exception.getMessage());
            }
        });
    }

    private void sharePhotoToFacebook(Bitmap bitmap) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
//                .setCaption("Cathedral eye clinic score weekly graph")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, null);
        alertdialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    private void alertdialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                BarGraphActivity.this).create();
        alertDialog.setTitle("Sucessfully posted");
        alertDialog.setMessage("Message posted on Facebook");
        alertDialog.setButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void TakescreenshotandShare(final int num) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            File myDirectory = new File(Environment.getExternalStorageDirectory(), "Cathedral");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            String mPath = Environment.getExternalStorageDirectory() + "/Cathedral/" + "CathedralWeekGraph" + ".jpg";
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            if (num == 0) {
                sharefacebookImage(bitmap);
//                sharetofacebook(mPath);
            } else if (num == 1) {
                sharetwitterImage(bitmap, mPath);
            }

        } catch (Throwable e) {
            e.printStackTrace();
//            Log.e("error :", e.getMessage());
        }
    }

    private void sharetofacebook(String path){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "text");
        shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(path)));
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "send"));
    }

    private void sharetwitterImage(Bitmap bitmap, String mPath) {
        try {
            Toast.makeText(BarGraphActivity.this, "Please wait", Toast.LENGTH_LONG).show();
            twitterLogin.twittweAdapter.uploadImageAsync("Cathedral Report", mPath, bitmap, 100, twitterLogin.new MessageListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            checkConnection();
        }
    };

    private void checkConnection() {
        if (!new NetworkConnection(BarGraphActivity.this).isConnectionAvailable()) {
            new Alert(BarGraphActivity.this).alertMessage("Connection lost", R.string.check_connection);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void twitterCompleted(Profile result) {
//        Log.e("Completed", "Completed");
        TakescreenshotandShare(1);
    }
}
