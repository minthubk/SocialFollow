package com.cathedrale.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Created by aspire on 2/16/2015.
 */
public class Alert {

    private Context context;
    public Alert(Context context){

        this.context = context;
    }
    public void alertMessage(String  status, int message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(status);
        if (message>0) {
            builder.setMessage(message);
		}
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context. startActivity(startMain);
            }
        });
        builder.create().show();
    }
}
