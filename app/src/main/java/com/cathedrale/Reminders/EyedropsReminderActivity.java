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
import android.widget.TimePicker;
import android.widget.Toast;

import com.cathedrale.Constants.AppConstants;
import com.cathedrale.R;
import com.cathedrale.Receiver.AlertDialogReceiver;
import com.cathedrale.Services.NotifyService;
import com.cathedrale.Utils.SharedPref;

import java.util.Calendar;

public class EyedropsReminderActivity extends Activity implements View.OnClickListener{

    EditText timeedit;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_eyedrops_reminder);

        init();
    }

    private void init(){
        sharedPref = new SharedPref(this);
        timeedit = (EditText) findViewById(R.id.timeedit1);
        findViewById(R.id.donereminder).setOnClickListener(this);
        findViewById(R.id.cancelreminder).setOnClickListener(this);

        timeedit.setText(sharedPref.getString(AppConstants.SetEyedropsReminderTime));
        timeedit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeedit1:
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
        mTimePicker = new TimePickerDialog(EyedropsReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    private void finalbuttonclick() {
        if (timeedit.length() == 0 ) {
            Toast.makeText(EyedropsReminderActivity.this, "Enter Reminder time", Toast.LENGTH_SHORT).show();
        } else {
            setalarmtime(timeedit.getText().toString());
        }
    }
    private void setalarmtime(String alarmtime){
        String[] time_split = alarmtime.split(":");
        Log.e("alarm", alarmtime);
        setdailyremainderforeyepeace(Integer.parseInt(time_split[0]), Integer.parseInt(time_split[1]));
        sharedPref.putString(AppConstants.SetEyedropsReminderTime, alarmtime);
        Toast.makeText(EyedropsReminderActivity.this, "Reminder set succesfully  "+"\n"+"Reminder time : " + alarmtime , Toast.LENGTH_LONG).show();
        finish();
    }
    private void setdailyremainderforeyepeace(int hour, int min) {
        Intent myIntent = new Intent(EyedropsReminderActivity.this, NotifyService.class);
        myIntent.putExtra("alarmresponse", "Eyedrops Reminder");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 5, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
//        homealertdialog(pendingIntent,hour,min);
    }
    private void cancelreminder(){
        Log.e("time","@@@@@@"+sharedPref.getString(AppConstants.SetEyedropsReminderTime));
        if(sharedPref.getString(AppConstants.SetEyedropsReminderTime)!=null){
            showAlertDialog(EyedropsReminderActivity.this, "Cancel Remainder", "Do you want to cancel Reminder");
        }else{
            Toast.makeText(EyedropsReminderActivity.this, "Please set Reminder first.....", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EyedropsReminderActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                timeedit.setText("");

                sharedPref.putString(AppConstants.SetEyedropsReminderTime, null);
                Intent alarmIntent = new Intent(EyedropsReminderActivity.this, NotifyService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(EyedropsReminderActivity.this, 5, alarmIntent, 0);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                Toast.makeText(EyedropsReminderActivity.this, "Reminder Canceled", Toast.LENGTH_SHORT).show();
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
    private void homealertdialog(PendingIntent pendingIntent, int hour, int min){
        Intent intent = new Intent(getApplicationContext(), AlertDialogReceiver.class);
        intent.putExtra("alarmname", AppConstants.EyedropsReminder);
        PendingIntent mAlarmSender = PendingIntent.getBroadcast(getApplicationContext(),
                5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        AlarmManager amgr = (AlarmManager) getApplicationContext()
                .getSystemService(getApplicationContext().ALARM_SERVICE);
        amgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, mAlarmSender);
    }

}
