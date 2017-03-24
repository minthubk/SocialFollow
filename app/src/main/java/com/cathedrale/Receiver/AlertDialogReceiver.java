package com.cathedrale.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cathedrale.Activity.ReminderActivity;

/**
 * Created by Aspire on 1/8/2016.
 */
public class AlertDialogReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String message = bundle.getString("alarmname");

        Intent alarmIntent = new Intent("android.intent.action.MAIN");
        alarmIntent.setClass(context, AlertDialogClass.class);
        alarmIntent.putExtra("alarm_message",message);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start the popup activity
        context.startActivity(alarmIntent);
    }
}
