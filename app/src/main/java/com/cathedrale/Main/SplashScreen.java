package com.cathedrale.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.cathedrale.Constants.AppConstants;
import com.cathedrale.R;
import com.cathedrale.Utils.SharedPref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SplashScreen extends Activity {

    private final int INTERVEL = 0000;
    private SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        sharedPref = new SharedPref(this);
        chekDates();


    }
    private void chekDates(){
        String CurrentDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());

//        Log.e("CURRENTWEEK","@@@@@"+sharedPref.getString(AppConstants.CURRENTWEEK));
//        Log.e("PREVIOUSDATE","@@@@@"+sharedPref.getString(AppConstants.PREVIOUSDATE));
//        Log.e("CURRENTDATE","@@@@@"+sharedPref.getString(AppConstants.CURRENTDATE));

        if(sharedPref.getString(AppConstants.FirstTimeOpeneddate)!=null){

            String FirstTimeOpeneddate = sharedPref.getString(AppConstants.FirstTimeOpeneddate);
            SimpleDateFormat dates = new SimpleDateFormat("MM-dd-yyyy");
            //Setting dates
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = dates.parse(CurrentDate);
                date2 = dates.parse(FirstTimeOpeneddate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Comparing dates
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            int dayDifference = (int) differenceDates;
            dayDifference = dayDifference+1;
            //Convert long to String
//            Log.e("differenceDates", "differenceDates : " + dayDifference);

            if(dayDifference>=7){

                int diff =dayDifference/7;
//                Log.e("diff",""+diff);
                if ((diff % 2) == 0) {
                    // number is even
//                    Log.e("ODD","ODD WEEK");
                    if(!sharedPref.getString(AppConstants.CURRENTWEEK).equals(AppConstants.ODDWEEK)){
                        sharedPref.putString(AppConstants.CURRENTWEEK, AppConstants.ODDWEEK);
                        sharedPref.putString(AppConstants.COMPLETED_QUESTIONS_FORthisWEEK, AppConstants.NOTCOMPLETED);
                    }
                }
                else {
                    // number is odd
//                    Log.e("EVEN","EVEN WEEK");
                    if(!sharedPref.getString(AppConstants.CURRENTWEEK).equals(AppConstants.EVENWEEK)){
                        sharedPref.putString(AppConstants.CURRENTWEEK, AppConstants.EVENWEEK);
                        sharedPref.putString(AppConstants.COMPLETED_QUESTIONS_FORthisWEEK, AppConstants.NOTCOMPLETED);
                    }
                }
            }
        }else{
//            Log.e("FirstTime", "FirstTime : " +"NO CURRENTWEEK" );
            sharedPref.putString(AppConstants.CURRENTWEEK, AppConstants.ODDWEEK);
            sharedPref.putString(AppConstants.COMPLETED_QUESTIONS_FORthisWEEK, AppConstants.NOTCOMPLETED);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,FacebookLoginActivity.class));
                finish();
            }
        },INTERVEL);
    }
}
