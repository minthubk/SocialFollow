package com.cathedrale.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cathedrale.Activity.HomeScreenActivity;
import com.cathedrale.Constants.AppConstants;
import com.cathedrale.R;
import com.cathedrale.Receiver.AlertDialogClass;

import java.util.Calendar;

/**
 * Created by Aspire on 12/21/2015.
 */
public class NotifyService extends BroadcastReceiver {
    NotificationManager notificationManager;
    public static  Notification myNotification;
    String message,randno;
    int alarmrandno;
    Intent myIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

//        Toast.makeText(context,"Alarm recieved",Toast.LENGTH_LONG).show();
        Bundle bundle = intent.getExtras();
        message = bundle.getString("alarmresponse");
        Log.e("message :",message);

        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int hours = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);

        if(message.equals(AppConstants.EyepeaceReminder)){
             myIntent = new Intent(context, HomeScreenActivity.class);
        }else if(message.equals(AppConstants.EyenutritionReminder)){
            myIntent = new Intent(context, HomeScreenActivity.class);
        }else if(message.equals(AppConstants.HeatmassageReminder)){
             myIntent = new Intent(context, HomeScreenActivity.class);
        }else if(message.equals(AppConstants.EyedropsReminder)){
             myIntent = new Intent(context, HomeScreenActivity.class);
        }else{
             myIntent = new Intent(context, HomeScreenActivity.class);
        }

        PendingIntent  pendingIntent = PendingIntent.getActivity(context, 0,myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        myNotification = new NotificationCompat.Builder(context)
                .setContentTitle("Cathedral Notification!")
                .setContentText(message).setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
                .setSmallIcon(R.drawable.logonotification).build();

//        myNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        final int MY_NOTIFICATION_ID = 1;
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(ts), myNotification);


        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(context, alert);
        mp.setVolume(100, 100);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        Intent alarmIntent = new Intent(context,AlertDialogClass.class);
        alarmIntent.putExtra("alarm_message",message);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }

}