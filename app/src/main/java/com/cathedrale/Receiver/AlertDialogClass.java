package com.cathedrale.Receiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cathedrale.Constants.AppConstants;
import com.cathedrale.R;
import com.cathedrale.Services.NotifyService;
import com.cathedrale.Utils.SharedPref;

/**
 * Created by Aspire on 1/8/2016.
 */
public class AlertDialogClass extends Activity {
    AlertDialog.Builder mAlertDlgBuilder;
    AlertDialog mAlertDialog;
    View mDialogView = null;
    Button mOKBtn, mCancelBtn;
    TextView alertText;
    String message = null;
    int alarmrandno;
    private RadioGroup radioGroup1, radioGroup2, radioGroup3;
    private RadioButton fivemin_radiobtn, thirteemin_radiobtn, onehr_radiobtn, twohr_radiobtn, threehr_radiobtn, nextday_button;
    String snoozetime, reminder_type;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        // Build the dialog
        mAlertDlgBuilder = new AlertDialog.Builder(this);
        mDialogView = inflater.inflate(R.layout.dialog_layout, null);

        init();
    }

    private void init(){
        message = getIntent().getExtras().getString("alarm_message");
        Log.e("message", "@@@@@@@" + message);
        mOKBtn = (Button) mDialogView.findViewById(R.id.ID_Ok);
        mCancelBtn = (Button) mDialogView.findViewById(R.id.ID_Cancel);
        alertText = (TextView) mDialogView.findViewById(R.id.alertText);
        radioGroup1 = (RadioGroup) mDialogView.findViewById(R.id.dialogradioGroup1);
        radioGroup2 = (RadioGroup) mDialogView.findViewById(R.id.dialogradioGroup2);
        radioGroup3 = (RadioGroup) mDialogView.findViewById(R.id.dialogradioGroup3);
        fivemin_radiobtn = (RadioButton) mDialogView.findViewById(R.id.fivemin);
        thirteemin_radiobtn = (RadioButton) mDialogView.findViewById(R.id.thirteemin);
        onehr_radiobtn = (RadioButton) mDialogView.findViewById(R.id.onehr);
        twohr_radiobtn = (RadioButton) mDialogView.findViewById(R.id.twohour);
        threehr_radiobtn = (RadioButton) mDialogView.findViewById(R.id.threehr);
        nextday_button = (RadioButton) mDialogView.findViewById(R.id.nextday);

        sharedPref = new SharedPref(this);
        radioGroup1.clearCheck();
        radioGroup2.clearCheck();
        radioGroup3.clearCheck();
        RadiobuttonChangeListener();
        getsharedprefdata();
        getmessageetails();

//        alertText.setText("This is "+message+"."+"\n"+"Set snooze time / change reminder time");
        mOKBtn.setOnClickListener(mDialogbuttonClickListener);
        mCancelBtn.setOnClickListener(mDialogbuttonClickListener);
        mAlertDlgBuilder.setCancelable(false);
        mAlertDlgBuilder.setInverseBackgroundForced(true);
        mAlertDlgBuilder.setView(mDialogView);
        mAlertDialog = mAlertDlgBuilder.create();
        mAlertDialog.show();

    }

    View.OnClickListener mDialogbuttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ID_Ok) {
                if (message.equals(AppConstants.EyepeaceReminder)) {
                    setsnoozeoption(AppConstants.EyepeaceReminder);
                    finish();
                } else if (message.equals(AppConstants.HeatmassageReminder)) {
                    setsnoozeoption(AppConstants.HeatmassageReminder);
                    finish();
                } else if (message.equals(AppConstants.EyenutritionReminder)) {
                    finish();
                } else if (message.equals(AppConstants.EyedropsReminder)) {
                    finish();
                } else {
                    Log.e("message :", message);
                    finish();
                }
                mAlertDialog.dismiss();
            } else if (v.getId() == R.id.ID_Cancel) {
                cancelreminder();
                mAlertDialog.dismiss();
                finish();
            }
        }
    };

    private void getmessageetails() {
        if (message.equals(AppConstants.EyenutritionReminder)) {
            alertText.setText(AppConstants.EyenutritionMessage);
            mOKBtn.setText("OK");
            mCancelBtn.setVisibility(View.GONE);
            mDialogView.findViewById(R.id.snoozelayout).setVisibility(View.GONE);
        }else if (message.equals(AppConstants.EyedropsReminder)) {
            alertText.setText(AppConstants.EyedropsnMessage);
            mOKBtn.setText("OK");
            mCancelBtn.setVisibility(View.GONE);
            mDialogView.findViewById(R.id.snoozelayout).setVisibility(View.GONE);
        } else if (message.equals(AppConstants.EyepeaceReminder)) {
            alertText.setText(AppConstants.EyepeaceMessage);
        } else if (message.equals(AppConstants.HeatmassageReminder)) {
            alertText.setText(AppConstants.HeatmassageMessage);
        } else {
            alertText.setText("This is " + message);
        }
    }

    private void setsnoozeoption(String remindertype_message) {
        try {
            if (remindertype_message.equals(AppConstants.EyepeaceReminder)) {
                alarmrandno = 6;
                String alarmtime = sharedPref.getString(AppConstants.SetEyepeaceReminderTime);
                String[] alarmsplit = alarmtime.split(":");
                String hourst = alarmsplit[0];
                String minst = alarmsplit[1];
                reminder_type = "Eyepeace";
                sendalarmtimeandsnoozetime(Integer.parseInt(hourst), Integer.parseInt(minst), snoozetime, remindertype_message);
            } else if (remindertype_message.equals(AppConstants.HeatmassageReminder)) {
                alarmrandno = 7;
                String alarmtime = sharedPref.getString(AppConstants.SetHeatmassageReminderTime);
                String[] alarmsplit = alarmtime.split(":");
                String hourst = alarmsplit[0];
                String minst = alarmsplit[1];
                reminder_type = "Heatmassage";
                sendalarmtimeandsnoozetime(Integer.parseInt(hourst), Integer.parseInt(minst), snoozetime, remindertype_message);
            } else {

            }
        }catch (Exception e){

        }
    }

    private void cancelreminder() {
        if (message.equals(AppConstants.EyepeaceReminder)) {
            alarmcancel();
        } else if (message.equals(AppConstants.HeatmassageReminder)) {
            alarmcancel();
        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void alarmcancel() {
        Intent alarmIntent = new Intent(AlertDialogClass.this, NotifyService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlertDialogClass.this, alarmrandno, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
//        Toast.makeText(AlertDialogClass.this, "Snooze Canceled", Toast.LENGTH_LONG).show();
    }

    private void getsharedprefdata() {
        if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime) != null) {
            if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("5 min")) {
                fivemin_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("30 min")) {
                thirteemin_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("1 hr")) {
                onehr_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("2 hr")) {
                twohr_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("3 hr")) {
                threehr_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetEyepeaceSnoozeTime).equals("Next Day")) {
                nextday_button.setChecked(true);
            } else {
                radioGroup1.clearCheck();
                radioGroup2.clearCheck();
                radioGroup3.clearCheck();
            }
        } else if (sharedPref.getString(AppConstants.SetHeatmassageReminderTime) != null) {
            if (sharedPref.getString(AppConstants.SetHeatmassageReminderTime).equals("5 min")) {
                fivemin_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetHeatmassageReminderTime).equals("30 min")) {
                thirteemin_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetHeatmassageReminderTime).equals("1 hr")) {
                onehr_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetHeatmassageReminderTime).equals("2 hr")) {
                twohr_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetHeatmassageReminderTime).equals("3 hr")) {
                threehr_radiobtn.setChecked(true);
            } else if (sharedPref.getString(AppConstants.SetHeatmassageReminderTime).equals("Next Day")) {
                nextday_button.setChecked(true);
            } else {
                radioGroup1.clearCheck();
                radioGroup2.clearCheck();
                radioGroup3.clearCheck();
            }
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
        radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioGroup3(group, checkedId);
            }
        });
    }

    private void radioGroup1(RadioGroup group, int checkedId) {
        RadioButton rb = (RadioButton) group.findViewById(checkedId);
        if (null != rb && checkedId > -1) {
            radioGroup2.clearCheck();
            radioGroup3.clearCheck();
            radioGroup1.check(checkedId);
            if (checkedId == R.id.fivemin || checkedId == R.id.thirteemin) {
                snoozetime = rb.getText().toString();
            }
        }
    }

    private void radioGroup2(RadioGroup group, int checkedId) {
        RadioButton rb = (RadioButton) group.findViewById(checkedId);
        if (null != rb && checkedId > -1) {
            radioGroup1.clearCheck();
            radioGroup3.clearCheck();
            radioGroup2.check(checkedId);
            if (checkedId == R.id.onehr || checkedId == R.id.twohour) {
                snoozetime = rb.getText().toString();
            }
        }
    }

    private void radioGroup3(RadioGroup group, int checkedId) {
        RadioButton rb = (RadioButton) group.findViewById(checkedId);
        if (null != rb && checkedId > -1) {
            radioGroup1.clearCheck();
            radioGroup2.clearCheck();
            radioGroup3.check(checkedId);
            if (checkedId == R.id.threehr || checkedId == R.id.nextday) {
                snoozetime = rb.getText().toString();
            }
        }
    }

    private void sendalarmtimeandsnoozetime(int hour, int min, String snoozetime, String remindertype_message) {
        Log.e("alarm", "" + hour + ":" + min);
        Log.e("snoozetime", "" + snoozetime);
        Log.e("alarmrandno", "" + alarmrandno);
        snoozealarm(hour, min, snoozetime, remindertype_message);
        if (message.equals(AppConstants.EyepeaceReminder)) {
            sharedPref.putString(AppConstants.SetEyepeaceSnoozeTime, snoozetime);
        } else if (message.equals(AppConstants.HeatmassageReminder)) {
            sharedPref.putString(AppConstants.SetHeatmassageSnoozeTime, snoozetime);
        }
        Toast.makeText(AlertDialogClass.this, "Snoozed for next " + snoozetime, Toast.LENGTH_LONG).show();
    }

    private void snoozealarm(int hour, int min, String snoozetime, String remindertype_message) {
        long interval = 0;
        String[] split_snooze = snoozetime.trim().split(" ");
        if (split_snooze[0].toString().equals("5")) {
            interval += 300000;
        } else if (split_snooze[0].toString().equals("30")) {
            interval += 1800000;
        } else if (split_snooze[0].toString().equals("1")) {
            interval += 3600000;
        } else if (split_snooze[0].toString().equals("2")) {
            interval += 7200000; // 2 hours converted to milliseconds
        } else if (split_snooze[0].toString().equals("3")) {
            interval += 10800000; // 3 hours converted to milliseconds
        } else if (split_snooze[0].toString().equals("Next")) {
            interval = 24 * 60 * 60 * 1000;
        } else {
            interval = 0;
        }
        Log.e("interval", "@@@@@" + interval);
        Log.e("alarmrandno", "@@@@@" + alarmrandno);
        if (interval == 0) {
        } else {
            Intent intent = new Intent(this, NotifyService.class);
            intent.putExtra("alarmresponse", remindertype_message);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmrandno, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (interval), pendingIntent);
        }
    }
}
