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

public class EyenutritionReminderActivity extends Activity implements View.OnClickListener {

    EditText timeedit1, timeedit2;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_eyenutrition_reminder);

        init();
    }

    private void init() {
        sharedPref = new SharedPref(this);
        timeedit1 = (EditText) findViewById(R.id.timeedit1);
        timeedit2 = (EditText) findViewById(R.id.timeedit2);

        timeedit1.setOnClickListener(this);
        timeedit2.setOnClickListener(this);
        findViewById(R.id.donereminder).setOnClickListener(this);
        findViewById(R.id.cancelreminder).setOnClickListener(this);

        timeedit1.setText(sharedPref.getString(AppConstants.SetEyenutritionRemindertime1));
        timeedit2.setText(sharedPref.getString(AppConstants.SetEyenutritionRemindertime2));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeedit1:
                openclocktosettime1();
                break;
            case R.id.timeedit2:
                openclocktosettime2();
                break;
            case R.id.donereminder:
                setreminder();
                break;
            case R.id.cancelreminder:
                cancelreminder();
                break;
        }
    }

    private void openclocktosettime1() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EyenutritionReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                    timeedit1.setText(hour + ":" + min);
                } else {
                    timeedit1.setText(hour + ":" + min);
                }
//                setdailyremainderforeyepeace(selectedHour,selectedMinute);

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void openclocktosettime2() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EyenutritionReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                    timeedit2.setText(hour + ":" + min);
                } else {
                    timeedit2.setText(hour + ":" + min);
                }
//                setdailyremainderforeyepeace(selectedHour,selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void setreminder() {
        if (timeedit1.length() == 0) {
            Toast.makeText(EyenutritionReminderActivity.this, "Enter First Reminder Time", Toast.LENGTH_LONG).show();
        } else if (timeedit2.length() == 0) {
            Toast.makeText(EyenutritionReminderActivity.this, "Enter Second Reminder Time", Toast.LENGTH_LONG).show();
        } else {
            String time1 = timeedit1.getText().toString();
            String time2 = timeedit2.getText().toString();
            setalarmtimeandsnoozetime(time1, time2);
        }
    }

    private void setalarmtimeandsnoozetime(String time1, String time2) {

        String[] time_split1 = time1.split(":");
        Log.e("alarm", time1);
        setdailyremainderforeyenutrition(Integer.parseInt(time_split1[0]), Integer.parseInt(time_split1[1]), 2);

        String[] time_split2 = time2.split(":");
        Log.e("alarm", time2);
        setdailyremainderforeyenutrition(Integer.parseInt(time_split2[0]), Integer.parseInt(time_split2[1]), 3);

        sharedPref.putString(AppConstants.SetEyenutritionRemindertime1, time1);
        sharedPref.putString(AppConstants.SetEyenutritionRemindertime2, time2);
        Toast.makeText(EyenutritionReminderActivity.this, "Reminder set succesfully  " + "\n" + "Reminder time : " + time1 + "\n" + "Reminder time  : " + time2, Toast.LENGTH_LONG).show();
        finish();
    }

    private void setdailyremainderforeyenutrition(int hour, int min, int randno) {
        Intent myIntent = new Intent(EyenutritionReminderActivity.this, NotifyService.class);
        myIntent.putExtra("alarmresponse", "Eyenutrition Reminder");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), randno, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
//        homealertdialog(pendingIntent,hour,min,randno);
    }
    private void setdailyremainderforeyenutrition2(int hour, int min, int randno) {

        Intent myIntent = new Intent(EyenutritionReminderActivity.this, NotifyService.class);
        myIntent.putExtra("alarmresponse", "Eyenutrition Reminder.");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), randno, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
//        homealertdialog(pendingIntent,hour,min,randno);
    }
    private void cancelreminder() {
        Log.e("time", "@@@@@@" + sharedPref.getString(AppConstants.SetEyenutritionRemindertime1));
        if (sharedPref.getString(AppConstants.SetEyenutritionRemindertime1) != null && sharedPref.getString(AppConstants.SetEyenutritionRemindertime2) != null) {
            showAlertDialog(EyenutritionReminderActivity.this, "Cancel Remainder", "Do you want to cancel Reminder");
        } else {
            Toast.makeText(EyenutritionReminderActivity.this, "Please set Reminder first.....", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EyenutritionReminderActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                timeedit1.setText("");
                timeedit2.setText("");
                sharedPref.putString(AppConstants.SetEyenutritionRemindertime1, null);
                sharedPref.putString(AppConstants.SetEyenutritionRemindertime2, null);
                Intent alarmIntent = new Intent(EyenutritionReminderActivity.this, NotifyService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(EyenutritionReminderActivity.this, 2, alarmIntent, 0);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                Toast.makeText(EyenutritionReminderActivity.this, "Reminder Canceled", Toast.LENGTH_SHORT).show();
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
    private void homealertdialog(PendingIntent pendingIntent, int hour, int min,int randno){
        Intent intent = new Intent(getApplicationContext(), AlertDialogReceiver.class);
        intent.putExtra("alarmname", AppConstants.EyenutritionReminder);
        PendingIntent mAlarmSender = PendingIntent.getBroadcast(getApplicationContext(),
                randno, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        AlarmManager amgr = (AlarmManager) getApplicationContext()
                .getSystemService(getApplicationContext().ALARM_SERVICE);
        amgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, mAlarmSender);
    }
}
