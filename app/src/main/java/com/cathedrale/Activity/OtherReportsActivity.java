package com.cathedrale.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cathedrale.R;
import com.cathedrale.Seekbar.CustomSeekBar;
import com.cathedrale.Seekbar.ProgressItem;
import com.cathedrale.Twitter.OnLoginComplete;
import com.cathedrale.Twitter.TwitterLogin;
import com.cathedrale.Utils.SharedPref;
import com.cathedrale.cview.TypefaceTextview;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OtherReportsActivity extends Activity implements View.OnClickListener, OnLoginComplete {


    private SharedPref sharedPref;
    Database db;
    BarChart chart;
    String Lastweekanswers, problem, status;
    ListView answerslist;
    ArrayList<HashMap<String, String>> databaseSymptom_Score = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> databaseTreatment_Score = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> databaseTotal_Score = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> databaseWeekly_Score = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> AnswersList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> arrayList = new ArrayList<>();
    String score1 = null, score2 = null;
    private CustomSeekBar seekbar;
    TwitterLogin twitterLogin;
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_other_reports);

        init();
    }

    private void init() {
        db = new Database(this);
        db.open();
        findViewById(R.id.convert).setOnClickListener(this);
        sharedPref = new SharedPref(this);
        chart = (BarChart) findViewById(R.id.chart);
        answerslist = (ListView) findViewById(R.id.answerslist);
        String s = "<b>Dre Eye</b>," + "\n" + "Questionnaire" + "\n" + "<b>Results</b>";

        findViewById(R.id.facebook).setOnClickListener(this);
        findViewById(R.id.twitter).setOnClickListener(this);
        findViewById(R.id.others).setOnClickListener(this);

        TypefaceTextview resulttext = (TypefaceTextview) findViewById(R.id.resulttext);
        resulttext.setText(Html.fromHtml(s));

        showBarchart();
    }

    private void showBarchart() {
        databaseSymptom_Score = db.getScore();
        databaseTreatment_Score = db.getTreatmentScore();

        if (databaseSymptom_Score.size() == 0 && databaseTreatment_Score.size() == 0) {
            Toast.makeText(OtherReportsActivity.this, "No points , please answer to questionnaire to show graph", Toast.LENGTH_LONG).show();
        } else {
            BarData data = new BarData(getXAxisValues(), getDataSet());
            chart.setData(data);
            chart.setDescription("Cathedral eye clinic");
            chart.animateXY(2000, 2000);
            chart.invalidate();
        }
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();

        databaseSymptom_Score = db.getScore();
        databaseTreatment_Score = db.getTreatmentScore();
        databaseTotal_Score = db.getTotalScore();

        for (int i = 0; i < databaseSymptom_Score.size(); i++) {
            score1 = databaseSymptom_Score.get(i).get("score");
            String week = databaseSymptom_Score.get(i).get("week");
        }

        for (int i = 0; i < databaseTreatment_Score.size(); i++) {
            score2 = databaseTreatment_Score.get(i).get("score");
            String week = databaseTreatment_Score.get(i).get("week");
        }

        getanswers();

        BarEntry v1e1 = new BarEntry((Integer.parseInt(score1) / arrayList.size()), 0); // Jan
        valueSet1.add(v1e1);

        BarEntry v1e2 = new BarEntry(Integer.parseInt(score2), 0); // Jan
        valueSet2.add(v1e2);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Symptom");
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Treatment");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        barDataSet2.setColor(Color.rgb(192, 192, 192));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {

        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("");
        return xAxis;
    }

    private void showscoreansresult(Double div, String score1, String score2) {

        int sum = (Integer.parseInt(score1) + Integer.parseInt(score2));
        int Treatmentscore = Integer.parseInt(score2);

        String doublevalue = String.format("%.2f", div);
        Double divide = Double.parseDouble(doublevalue);
        Log.e("divide", "" + divide);

        Double d = new Double(divide);
        int i = d.intValue();

        setseekbar(i);

        if (divide >= 0 && divide <= 0.99) {
            problem = "Normal";

        } else if (divide >= 1 && divide <= 1.99) {
            problem = "Mild";

        } else if (divide >= 2 && divide <= 2.99) {
            problem = "Moderate";

        } else if (divide >= 3 && divide <= 3.99) {
            problem = "Severe ";

        } else if (divide >= 4) {
            problem = "Worse";
        }

        if (Treatmentscore == 0) {
            status = "Poor";
        } else if (Treatmentscore == 1) {
            status = "Poor";
        } else if (Treatmentscore == 2) {
            status = "fair";
        } else if (Treatmentscore == 3) {
            status = "fair";
        } else if (Treatmentscore == 4) {
            status = "good";
        }

        TypefaceTextview resultscoretext = (TypefaceTextview) findViewById(R.id.resultscoretext);
        TextView scorevalue = (TextView) findViewById(R.id.scorevalue);
        resultscoretext.setText("Your score indicates that you may have " + problem + " Dry Eye Symptoms and your adherence to treatment has been " + status + " .Share this report with your eye doctor regarding your Dry Eye symptoms.");
        scorevalue.setText("" + sum + "\n" + problem);
    }


    private void TakeScreenshot(int num) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm", now);
        Log.e("_hh:mm:ss",now.toString());
        try {
            File myDirectory = new File(Environment.getExternalStorageDirectory(), "Cathedral");
            if(!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            String mPath = Environment.getExternalStorageDirectory() + "/Cathedral/" + "CathedralReport" +".jpg";
            Log.e("mPath", mPath);
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
                openScreenshot(imageFile);
            } else if (num == 1) {
                SharetoTwitter(bitmap, mPath);
            } else if (num == 2) {
                sharefacebookImage(bitmap);
            }

        } catch (Throwable e) {
            e.printStackTrace();
//            Log.e("error",e.getMessage());
        }

    }

    private void SharetoTwitter(Bitmap bitmap, String mPath) {
        try {
            Toast.makeText(OtherReportsActivity.this, "Please wait", Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.convert:
                findViewById(R.id.convert).setVisibility(View.GONE);
                TakeScreenshot(0);
                break;
            case R.id.facebook:
                showdialog("Facebook", "Post to Facebook", 0);
                break;
            case R.id.twitter:
                showdialog("Twitter", "Post to Twitter", 1);
                break;
            case R.id.others:
                showdialog("Save Report", "Save as JPEG", 2);
                break;
        }
    }

    private void showdialog(String title, String message, final int num) {
        AlertDialog.Builder shareDialog = new AlertDialog.Builder(this);
        shareDialog.setTitle(title);
//        shareDialog.setIcon(R.drawable.facebookcircle);
//        shareDialog.setMessage(message);
        shareDialog.setPositiveButton(message, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (num == 1) {
                    sharetoTwitter();
                } else if (num == 0) {
                    TakeScreenshot(2);
                } else if (num == 2) {
                    TakeScreenshot(0);
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

    private void sharefacebookImage(final Bitmap bitmap) {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(this, permissionNeeds);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                sharePhotoToFacebook(bitmap);
            }

            @Override
            public void onCancel()
            {
                Log.e("error :", "onCancel");
            }

            @Override
            public void onError(FacebookException exception)
            {
                System.out.println("onError");
            }
        });
    }

    private void sharePhotoToFacebook(Bitmap bitmap) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption("Cathedral eye clinic Report")
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
                OtherReportsActivity.this).create();
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

    private void sharetoTwitter() {
        twitterLogin = new TwitterLogin(this);
        twitterLogin.calltwitterLogin();
    }

    private void getanswers() {
        databaseWeekly_Score = db.getWeeklyAnswers();
        String questionId = null, questions = null, answers = null;
        for (int i = 0; i < databaseWeekly_Score.size(); i++) {
            Lastweekanswers = databaseWeekly_Score.get(i).get("answers");
            String week = databaseWeekly_Score.get(i).get("week");
        }
        try {
            JSONArray jsonarray = new JSONArray(Lastweekanswers);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject obj = jsonarray.getJSONObject(i);
                questionId = obj.getString("questionId");
                questions = obj.getString("questions");
                answers = obj.getString("asnwer");

                HashMap<String, String> hashmaplist = new HashMap<String, String>();
                hashmaplist.put("questionId", questionId);
                hashmaplist.put("questions", questions);
                hashmaplist.put("answers", answers);

                arrayList.add(questions);

                AnswersList.add(hashmaplist);
            }

            if (AnswersList.size() > 0) {
                ListAdapter adapter = new SimpleAdapter(
                        OtherReportsActivity.this, AnswersList,
                        R.layout.list_item, new String[]{"questions", "answers",}, new int[]
                        {R.id.questions, R.id.answers});
                answerslist.setAdapter(adapter);
            }

            double div = ((Double.parseDouble(score1) + Double.parseDouble(score2)) / arrayList.size());
            showscoreansresult(div, score1, score2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void twitterCompleted(Profile result) {
        Log.e("Completed", "Completed");
        TakeScreenshot(1);
    }

    private void setseekbar(int i) {
        Log.e("i", "" + i);
        float totalSpan = 5;
        float normalSpan = 1;
        float MildSpan = 1;
        float ModerateSpan = 1;
        float SevereSpan = 1;
        float WorseSpan = 1;
        float darkGreySpan = 0;

        setseekbarprogress(i, totalSpan, normalSpan, MildSpan, ModerateSpan, SevereSpan, WorseSpan, darkGreySpan);

    }

    private void setseekbarprogress(int i, float totalSpan, float normalSpan, float MildSpan, float ModerateSpan, float SevereSpan, float WorseSpan, float darkGreySpan) {
        i = i + 1;
        if (i == 1) {
            ArrayList<ProgressItem> progressItemList;
            ProgressItem mProgressItem;

            seekbar = ((CustomSeekBar) findViewById(R.id.seekBar0));
            progressItemList = new ArrayList<ProgressItem>();
            // green span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = ((normalSpan / totalSpan) * 100);
            mProgressItem.color = R.color.bluedark;
            progressItemList.add(mProgressItem);
            // red span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);
            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);
            // orange span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);
            // bluedark
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            seekbar.initData(progressItemList);
            seekbar.invalidate();
            seekbar.setEnabled(false);
            seekbar.setMax(5);
            seekbar.setProgress(i);
        } else if (i == 2) {
            ArrayList<ProgressItem> progressItemList;
            ProgressItem mProgressItem;

            seekbar = ((CustomSeekBar) findViewById(R.id.seekBar0));
            progressItemList = new ArrayList<ProgressItem>();
            // green span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = ((normalSpan / totalSpan) * 100);
            mProgressItem.color = R.color.bluedark;
            progressItemList.add(mProgressItem);
            // red span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (MildSpan / totalSpan) * 100;
            mProgressItem.color = R.color.orange;
            progressItemList.add(mProgressItem);
            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);
            // orange span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);
            // bluedark
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            seekbar.initData(progressItemList);
            seekbar.invalidate();
            seekbar.setEnabled(false);
            seekbar.setMax(5);
            seekbar.setProgress(i);
        } else if (i == 3) {
            ArrayList<ProgressItem> progressItemList;
            ProgressItem mProgressItem;

            seekbar = ((CustomSeekBar) findViewById(R.id.seekBar0));
            progressItemList = new ArrayList<ProgressItem>();
            // green span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = ((normalSpan / totalSpan) * 100);
            mProgressItem.color = R.color.bluedark;
            progressItemList.add(mProgressItem);
            // red span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (MildSpan / totalSpan) * 100;
            mProgressItem.color = R.color.orange;
            progressItemList.add(mProgressItem);
            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (ModerateSpan / totalSpan) * 100;
            mProgressItem.color = R.color.blue;
            progressItemList.add(mProgressItem);
            // orange span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);
            // bluedark
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            seekbar.initData(progressItemList);
            seekbar.invalidate();
            seekbar.setEnabled(false);
            seekbar.setMax(5);
            seekbar.setProgress(i);
        } else if (i == 4) {
            ArrayList<ProgressItem> progressItemList;
            ProgressItem mProgressItem;

            seekbar = ((CustomSeekBar) findViewById(R.id.seekBar0));
            progressItemList = new ArrayList<ProgressItem>();
            // green span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = ((normalSpan / totalSpan) * 100);
            mProgressItem.color = R.color.bluedark;
            progressItemList.add(mProgressItem);
            // red span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (MildSpan / totalSpan) * 100;
            mProgressItem.color = R.color.orange;
            progressItemList.add(mProgressItem);
            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (ModerateSpan / totalSpan) * 100;
            mProgressItem.color = R.color.blue;
            progressItemList.add(mProgressItem);
            // orange span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (SevereSpan / totalSpan) * 100;
            mProgressItem.color = R.color.red;
            progressItemList.add(mProgressItem);
            // bluedark
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            seekbar.initData(progressItemList);
            seekbar.invalidate();
            seekbar.setEnabled(false);
            seekbar.setMax(5);
            seekbar.setProgress(i);
        } else if (i == 5) {
            ArrayList<ProgressItem> progressItemList;
            ProgressItem mProgressItem;

            seekbar = ((CustomSeekBar) findViewById(R.id.seekBar0));
            progressItemList = new ArrayList<ProgressItem>();
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = ((normalSpan / totalSpan) * 100);
            mProgressItem.color = R.color.bluedark;
            progressItemList.add(mProgressItem);
            // red span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (MildSpan / totalSpan) * 100;
            mProgressItem.color = R.color.redcolor;
            progressItemList.add(mProgressItem);
            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (ModerateSpan / totalSpan) * 100;
            mProgressItem.color = R.color.blue;
            progressItemList.add(mProgressItem);
            // orange span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (SevereSpan / totalSpan) * 100;
            mProgressItem.color = R.color.orange;
            progressItemList.add(mProgressItem);
            // bluedark
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (WorseSpan / totalSpan) * 100;
            mProgressItem.color = R.color.green;
            progressItemList.add(mProgressItem);

            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (darkGreySpan / totalSpan) * 100;
            mProgressItem.color = R.color.grey;
            progressItemList.add(mProgressItem);

            seekbar.initData(progressItemList);
            seekbar.invalidate();
            seekbar.setEnabled(false);
            seekbar.setMax(5);
            seekbar.setProgress(i);
        }
    }
}
