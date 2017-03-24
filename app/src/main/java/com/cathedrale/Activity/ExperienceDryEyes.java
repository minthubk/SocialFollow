package com.cathedrale.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cathedrale.R;


public class ExperienceDryEyes extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_experience_dry_eyes);

        init();
    }

    private void init() {
        findViewById(R.id.sadlayout).setOnClickListener(this);
        findViewById(R.id.happylayout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sadlayout:
                Intent intent = new Intent(ExperienceDryEyes.this, QuestioneriesNewActivity.class);
                startActivity(intent);
                break;
            case R.id.happylayout:
                break;
        }
    }
}
