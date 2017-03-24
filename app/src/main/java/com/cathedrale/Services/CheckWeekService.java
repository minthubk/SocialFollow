package com.cathedrale.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cathedrale.Constants.AppConstants;
import com.cathedrale.Utils.SharedPref;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aspire on 1/29/2016.
 */
public class CheckWeekService extends BroadcastReceiver {
    private SharedPref sharedPref;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPref = new SharedPref(context);
        Bundle bundle = intent.getExtras();
        String message = bundle.getString("Messageresponse");
        Log.e("message service :", message);

//        Toast.makeText(context, "Alaram Recieved", Toast.LENGTH_LONG).show();

        if (message.equals("CheckDay")) {
            String CurrentDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
            String PREVIOUSDATE = sharedPref.getString(AppConstants.PREVIOUSDATE);

            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            Calendar now = Calendar.getInstance();
            try {
                now.setTime(df.parse(PREVIOUSDATE));
                Log.e("Current date service : ", "" + CurrentDate);
                // add days to current date using Calendar.add method
                now.add(Calendar.DATE, 7);

                int month = now.get(Calendar.MONTH) + 1;
                String m = String.valueOf(month);
                String d = "" + now.get(Calendar.DATE);
                if (m.length() == 1) {
                    m = "0" + m;
                }
                if (d.length() == 1) {
                    d = "0" + d;
                }
                Log.e("date after seven day service : ", "" + m + "-" + d + "-" + now.get(Calendar.YEAR));
                String date = "" + m + "-" + d + "-" + now.get(Calendar.YEAR);
                if (CurrentDate.equals(date)) {

                    Log.e("Updated_Database_Week service :", sharedPref.getString(AppConstants.CURRENTWEEK));
                    String Database_Week = sharedPref.getString(AppConstants.CURRENTWEEK);
                    if (Database_Week.equals(AppConstants.ODDWEEK)) {
                        sharedPref.putString(AppConstants.CURRENTWEEK, AppConstants.EVENWEEK);
                    } else if (Database_Week.equals(AppConstants.EVENWEEK)) {
                        sharedPref.putString(AppConstants.CURRENTWEEK, AppConstants.ODDWEEK);
                    }else{
                        sharedPref.putString(AppConstants.CURRENTWEEK, AppConstants.ODDWEEK);
                    }

                    now.setTime(df.parse(CurrentDate));
                    Log.e("Current date service : ", "" + CurrentDate);
                    // add days to current date using Calendar.add method
                    now.add(Calendar.DATE, 7);

                    int Updated_month = now.get(Calendar.MONTH) + 1;
                    String Updated_m = String.valueOf(Updated_month);
                    String Updated_d = "" + now.get(Calendar.DATE);
                    if (Updated_m.length() == 1) {
                        Updated_m = "0" + Updated_m;
                    }
                    if (Updated_d.length() == 1) {
                        Updated_d = "0" + Updated_d;
                    }
                    Log.e("date after seven day service : ", "" + Updated_m + "-" + Updated_d + "-" + now.get(Calendar.YEAR));
                    String Updated_date = "" + Updated_m + "-" + Updated_d + "-" + now.get(Calendar.YEAR);
                    sharedPref.putString(AppConstants.PREVIOUSDATE, Updated_date);


                    Log.e("Updated_PREVIOUSDATE service :", sharedPref.getString(AppConstants.PREVIOUSDATE));
                    Log.e("Updated_CURRENTWEEK service :", sharedPref.getString(AppConstants.CURRENTWEEK));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


}
