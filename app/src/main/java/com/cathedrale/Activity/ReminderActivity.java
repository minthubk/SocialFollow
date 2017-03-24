package com.cathedrale.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cathedrale.Constants.AppConstants;
import com.cathedrale.R;
import com.cathedrale.Receiver.AlertDialogReceiver;
import com.cathedrale.Services.NotifyService;
import com.cathedrale.Utils.SharedPref;

import junit.framework.Test;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ReminderActivity extends Activity implements View.OnClickListener {
    EditText timeedit;
    int randno;
    SharedPref sharedPref;
    private RadioGroup radioGroup1, radioGroup2;
    private RadioButton  fivemin_radiobtn,thirteemin_radiobtn, onehr_radiobtn, twohr_radiobtn, threehr_radiobtn, nextday_button;
    String snoozetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reminder);

        init();

    }

    private void init() {
        sharedPref = new SharedPref(this);
        timeedit = (EditText) findViewById(R.id.timeedit);
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        fivemin_radiobtn = (RadioButton) findViewById(R.id.fivemin);
        thirteemin_radiobtn = (RadioButton) findViewById(R.id.thirteemin);
        onehr_radiobtn = (RadioButton) findViewById(R.id.onehr);
        twohr_radiobtn = (RadioButton) findViewById(R.id.twohour);
        threehr_radiobtn = (RadioButton) findViewById(R.id.threehr);
        nextday_button = (RadioButton) findViewById(R.id.nextday);

        radioGroup1.clearCheck();
        radioGroup2.clearCheck();
        RadiobuttonChangeListener();

        timeedit.setOnClickListener(this);
        findViewById(R.id.donereminder).setOnClickListener(this);
        findViewById(R.id.cancelreminder).setOnClickListener(this);
        generaterandomnumber();
        getsharedprefdata();
    }

    private void getsharedprefdata() {
        if (sharedPref.getString(AppConstants.SetEyepeaceReminderTime) != null) {
            timeedit.setText("" + sharedPref.getString(AppConstants.SetEyepeaceReminderTime));
        }
        Log.e("snoozetime", "@@@@@@" + sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime));

        if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime) != null) {
            if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("5 min")) {
                fivemin_radiobtn.setChecked(true);
            } else
            if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("30 min")) {
                thirteemin_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("1 hr")) {
                onehr_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("2 hr")) {
                twohr_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("3 hr")) {
                threehr_radiobtn.setChecked(true);
            }else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("Next Day")) {
                nextday_button.setChecked(true);
            }
        }
    }

    private void generaterandomnumber() {
        randno = 1;
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
        mTimePicker = new TimePickerDialog(ReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    private void setdailyremainderforeyepeace(int hour, int min, String snoozetime) {
        Intent myIntent = new Intent(ReminderActivity.this, NotifyService.class);
        myIntent.putExtra("alarmresponse", "Eyepeace : Your snooze time is "+snoozetime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), randno, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
        snoozealarm(pendingIntent, hour, min, snoozetime);
        homealertdialog(pendingIntent,hour,min,snoozetime);
    }

    private void cancelreminder() {
        Log.e("time","@@@@@@"+sharedPref.getString(AppConstants.SetEyepeaceReminderTime));
        if(sharedPref.getString(AppConstants.SetEyepeaceReminderTime)!=null){
            showAlertDialog(ReminderActivity.this, "Cancel Remainder", "Do you want to cancel Reminder");
        }else{
            Toast.makeText(ReminderActivity.this, "Please set Reminder first.....", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReminderActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                timeedit.setText("");
                radioGroup1.clearCheck();
                radioGroup2.clearCheck();
                sharedPref.putString(AppConstants.SetEyepeaceSnoozeTime, null);
                sharedPref.putString(AppConstants.SetEyepeaceReminderTime, null);

                Intent alarmIntent = new Intent(ReminderActivity.this, NotifyService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, randno, alarmIntent, 0);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);

                Intent alertdialogIntent = new Intent(ReminderActivity.this, AlertDialogReceiver.class);
                PendingIntent alertdialogpendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, randno, alertdialogIntent, 0);
                AlarmManager alertmanager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alertmanager.cancel(alertdialogpendingIntent);

                Toast.makeText(ReminderActivity.this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
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
            interval += 10800000; // 3 hours converted to milliseconds
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

    private void RadiobuttonChangeListener() {
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioGroup1(group, checkedId);
            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioGroup2(group, checkedId);
            }
        });
    }

    private void radioGroup1(RadioGroup group, int checkedId) {
        RadioButton rb = (RadioButton) group.findViewById(checkedId);
        if (null != rb && checkedId > -1) {
            radioGroup2.clearCheck();
            radioGroup1.check(checkedId);
            if (checkedId == R.id.fivemin ||checkedId == R.id.thirteemin || checkedId == R.id.onehr) {
                snoozetime = rb.getText().toString();
            }
        }
    }

    private void radioGroup2(RadioGroup group, int checkedId) {
        RadioButton rb = (RadioButton) group.findViewById(checkedId);
        if (null != rb && checkedId > -1) {
            radioGroup1.clearCheck();
            radioGroup2.check(checkedId);
            if (checkedId == R.id.twohour|| checkedId == R.id.threehr || checkedId == R.id.nextday) {
                snoozetime = rb.getText().toString();
            }
        }
    }

    private void finalbuttonclick() {
        if (timeedit.length() == 0 || snoozetime == null) {
            Toast.makeText(ReminderActivity.this, "Enter Alarm time and Snooze time", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(ReminderActivity.this, "Enter Alarm time "+timeedit.getText().toString()+"\n"+ " and Snooze time "+snoozetime, Toast.LENGTH_SHORT).show();
            sendalarmtimeandsnoozetime(timeedit.getText().toString(), snoozetime);
        }
    }

    private void sendalarmtimeandsnoozetime(String alarmtime, String snoozetime) {
        String[] time_split = alarmtime.split(":");
        Log.e("alarm", alarmtime);
        setdailyremainderforeyepeace(Integer.parseInt(time_split[0]), Integer.parseInt(time_split[1]), snoozetime);
        sharedPref.putString(AppConstants.SetEyepeaceReminderTime, timeedit.getText().toString());
        sharedPref.putString(AppConstants.SetEyepeaceSnoozeTime, snoozetime);
        Toast.makeText(ReminderActivity.this, "Your Reminder has set succesfully  "+"\n"+"Alarm time : " + alarmtime + "\n" + "snooze time : " + snoozetime, Toast.LENGTH_LONG).show();
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
            interval += 120 * 60 * 60 * 1000; // 2 hours converted to milliseconds
        } else if (split_snooze[0].toString().equals("3")) {
            interval += 180 * 60 * 60 * 1000; // 3 hours converted to milliseconds
        } else if (split_snooze[0].toString().equals("Day")) {
            interval = 0;
        }
//        long timestamp = 10000;
        long timestamp = 0;
        Intent intent = new Intent(getApplicationContext(), AlertDialogReceiver.class);
        intent.putExtra("alarmname",AppConstants.EyepeaceReminder);
        PendingIntent mAlarmSender = PendingIntent.getBroadcast(getApplicationContext(),
                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
