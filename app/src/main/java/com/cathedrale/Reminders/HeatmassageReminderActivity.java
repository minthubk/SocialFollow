package com.cathedrale.Reminders;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cathedrale.Activity.HomeScreenActivity;
import com.cathedrale.Constants.AppConstants;
import com.cathedrale.R;
import com.cathedrale.Receiver.AlertDialogReceiver;
import com.cathedrale.Services.NotifyService;
import com.cathedrale.Utils.SharedPref;

import java.util.Calendar;

public class HeatmassageReminderActivity extends Activity implements View.OnClickListener{
    EditText timeedit;
    int randno;
    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_heatmassage_reminder);

        init();
    }
    private void init(){
        sharedPref = new SharedPref(this);
        timeedit = (EditText) findViewById(R.id.timeedit);
        if (sharedPref.getString(AppConstants.SetHeatmassageReminderTime) != null) {
            timeedit.setText("" + sharedPref.getString(AppConstants.SetHeatmassageReminderTime));
        }
        timeedit.setOnClickListener(this);
        findViewById(R.id.donereminder).setOnClickListener(this);
        findViewById(R.id.cancelreminder).setOnClickListener(this);
        generaterandomnumber();
    }

    private void generaterandomnumber() {
        randno = 4;
//        randno = (int) (Math.random() * 9000) + 1000;
        Log.e("randno", "" + randno);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeedit:
                openclocktosettime();
                break;
            case R.id.donereminder:
                finalbuttonclick();
                break;
            case R.id.cancelreminder:
                cancelreminder();
                break;
        }
    }
    private void openclocktosettime() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(HeatmassageReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                String hour, min;
                if (String.valueOf(selectedHour).length() == 1) {
                    hour = "0" + String.valueOf(selectedHour);
                } else {
                    hour = String.valueOf(selectedHour);
                }
                if (String.valueOf(selectedMinute).length() == 1) {
                    min = "0" + String.valueOf(selectedMinute);
                } else {
                    min = String.valueOf(selectedMinute);
                }
                if (selectedHour >= 12) {
                    timeedit.setText(hour + ":" + min);
                } else {
                    timeedit.setText(hour + ":" + min);
                }
//                setdailyremainderforeyepeace(selectedHour,selectedMinute);

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
    private void setdailyremainderforeyepeace(int hour, int min) {
        Intent myIntent = new Intent(HeatmassageReminderActivity.this, NotifyService.class);
        myIntent.putExtra("alarmresponse", "Heat massage Reminder");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), randno, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
        finish();
    }

    private void cancelreminder() {
        Log.e("time","@@@@@@"+sharedPref.getString(AppConstants.SetHeatmassageReminderTime));
        if(sharedPref.getString(AppConstants.SetHeatmassageReminderTime)!=null){
            showAlertDialog(HeatmassageReminderActivity.this, "Cancel Remainder", "Do you want to cancel Reminder");
        }else{
            Toast.makeText(HeatmassageReminderActivity.this, "Please set Reminder first.....", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HeatmassageReminderActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                timeedit.setText("");

                sharedPref.putString(AppConstants.SetHeatmassageSnoozeTime, null);
                sharedPref.putString(AppConstants.SetHeatmassageReminderTime, null);

                Intent alarmIntent = new Intent(HeatmassageReminderActivity.this, NotifyService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(HeatmassageReminderActivity.this, randno, alarmIntent, 0);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);

                Intent alarmIntents = new Intent(HeatmassageReminderActivity.this, NotifyService.class);
                PendingIntent pendingIntents = PendingIntent.getBroadcast(HeatmassageReminderActivity.this, 7, alarmIntents, 0);
                AlarmManager managers = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                managers.cancel(pendingIntents);

                Toast.makeText(HeatmassageReminderActivity.this, "Reminder Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
    private void snoozealarm(PendingIntent pendingIntent, int hour, int min, String snoozetime) {
        long interval = 0;
        String[] split_snooze = snoozetime.trim().split(" ");
        if (split_snooze[0].toString().equals("5")) {
            interval += 1000 * 60 * 05;
        } else if (split_snooze[0].toString().equals("30")) {
            interval += AlarmManager.INTERVAL_HALF_HOUR;
        } else if (split_snooze[0].toString().equals("1")) {
            interval += AlarmManager.INTERVAL_HOUR;
        } else if (split_snooze[0].toString().equals("2")) {
            interval += 7200000; // 2 hours converted to milliseconds
        } else if (split_snooze[0].toString().equals("3")) {
            interval += 10800000;  // 3 hours converted to milliseconds
        } else if (split_snooze[0].toString().equals("Day")) {
            interval =  24 * 60 * 60 * 1000;
        }
        Log.e("interval", "" + interval);
        if (interval == 0) {
        } else {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            /* Repeating on every 20 minutes interval */
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval, pendingIntent);
        }
    }


    private void finalbuttonclick() {
        if (timeedit.length() == 0 ) {
            Toast.makeText(HeatmassageReminderActivity.this, "Enter Reminder time", Toast.LENGTH_SHORT).show();
        } else {
            sharedPref.putString(AppConstants.SetHeatmassageSnoozeTime, null);
            sendalarmtimeandsnoozetime(timeedit.getText().toString());
        }
    }

    private void sendalarmtimeandsnoozetime(String alarmtime) {
        String[] time_split = alarmtime.split(":");
        Log.e("alarm", alarmtime);
        setdailyremainderforeyepeace(Integer.parseInt(time_split[0]), Integer.parseInt(time_split[1]));
        sharedPref.putString(AppConstants.SetHeatmassageReminderTime, timeedit.getText().toString());
        Toast.makeText(HeatmassageReminderActivity.this, "Reminder set succesfully  "+"\n"+"Reminder time : " + alarmtime, Toast.LENGTH_LONG).show();
    }

    private void homealertdialog(PendingIntent pendingIntent, int hour, int min,String snoozetime){
        long interval = 0;
        String[] split_snooze = snoozetime.trim().split(" ");
        if (split_snooze[0].toString().equals("5")) {
            interval += 1000 * 60 * 05;
        } else if (split_snooze[0].toString().equals("30")) {
            interval += AlarmManager.INTERVAL_HALF_HOUR;
        } else if (split_snooze[0].toString().equals("1")) {
            interval += AlarmManager.INTERVAL_HOUR;
        } else if (split_snooze[0].toString().equals("2")) {
            interval += 7200000; // 2 hours converted to milliseconds
        } else if (split_snooze[0].toString().equals("3")) {
            interval += 10800000; // 3 hours converted to milliseconds
        } else if (split_snooze[0].toString().equals("Day")) {
            interval =  24 * 60 * 60 * 1000;
        }
//        long timestamp = 10000;
        long timestamp = 0;
        Intent intent = new Intent(getApplicationContext(), AlertDialogReceiver.class);
        intent.putExtra("alarmname", AppConstants.HeatmassageReminder);
        PendingIntent mAlarmSender = PendingIntent.getBroadcast(getApplicationContext(),
                4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        AlarmManager amgr = (AlarmManager) getApplicationContext()
                .getSystemService(getApplicationContext().ALARM_SERVICE);
//        amgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timestamp,
//                mAlarmSender);
        amgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                interval, mAlarmSender);
    }
}
