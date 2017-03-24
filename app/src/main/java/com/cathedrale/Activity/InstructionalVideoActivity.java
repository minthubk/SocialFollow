package com.cathedrale.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.cathedrale.Constants.AppConstants;
import com.cathedrale.R;


public class InstructionalVideoActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_instructional_video);

        init();
    }

    private void init(){
        findViewById(R.id.no_layout).setOnClickListener(this);
        findViewById(R.id.yes_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.yes_layout:
                Toast.makeText(InstructionalVideoActivity.this,"Loading video Please wait.....",Toast.LENGTH_LONG).show();
                watchYoutubeVideo(AppConstants.YOUTUBECHANNELURL);
                break;
            case R.id.no_layout:
                Intent intent=new Intent(InstructionalVideoActivity.this,ExperienceDryEyes.class);
                startActivity(intent);
                break;
        }
    }

    private void watchYoutubeVideo(String url){

        Intent intent =new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.google.android.youtube");
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
