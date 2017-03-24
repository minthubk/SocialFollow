package com.cathedrale.Reminders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cathedrale.R;
import com.cathedrale.cview.TypefaceTextview;

public class ReminderListAcitivty extends Activity implements View.OnClickListener {
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reminder_list_acitivty);

        init();
    }

    private void init() {
        fa = this;
        TypefaceTextview toolbar_text = (TypefaceTextview) findViewById(R.id.tool_bar_text);
        toolbar_text.setText("Set Reminder");
        findViewById(R.id.eyepeace_button).setOnClickListener(this);
        findViewById(R.id.eyenutritionbutton).setOnClickListener(this);
        findViewById(R.id.heatmassagebutton).setOnClickListener(this);
        findViewById(R.id.eyedropsbutton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eyepeace_button:
                startActivity(new Intent(ReminderListAcitivty.this, EyepeaceReminderAactivity.class));
                break;
            case R.id.eyenutritionbutton:
                startActivity(new Intent(ReminderListAcitivty.this, EyenutritionReminderActivity.class));
                break;
            case R.id.heatmassagebutton:
                startActivity(new Intent(ReminderListAcitivty.this, HeatmassageReminderActivity.class));
                break;
            case R.id.eyedropsbutton:
                startActivity(new Intent(ReminderListAcitivty.this, EyedropsReminderActivity.class));
                break;
        }
    }
}
