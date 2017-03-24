package com.cathedrale.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cathedrale.R;
import com.cathedrale.Reminders.EyedropsReminderActivity;
import com.cathedrale.Reminders.EyenutritionReminderActivity;
import com.cathedrale.Reminders.EyepeaceReminderAactivity;
import com.cathedrale.Reminders.HeatmassageReminderActivity;

public class ReportsActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reports);

        init();
    }

    private void init() {
        TextView toolbar_text = (TextView) findViewById(R.id.tool_bar_text);
        toolbar_text.setText(" Reports");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
