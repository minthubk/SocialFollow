package com.cathedrale.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cathedrale.Constants.AppConstants;
import com.cathedrale.Graph.BarGraphActivity;
import com.cathedrale.R;
import com.cathedrale.Reminders.ReminderListAcitivty;
import com.cathedrale.Utils.Alert;
import com.cathedrale.Utils.NetworkConnection;
import com.cathedrale.Utils.SharedPref;
import com.cathedrale.volley.AppController;
import com.cathedrale.webservices.HttpGetHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class HomeScreenActivity extends Activity implements View.OnClickListener {

    private ImageLoader imageLoader;
    private static ViewPager pager;
    public static ArrayList<String> myImageurls = new ArrayList<String>();
    private static int numberofImages;
    DetailOnPageChangeListener listener;
    private static Handler handler;
    private static int position;
    private static int dealyTimeMills = 3000;
    Switch mySwitch;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_screen);


        init();
    }


    private void init() {
        imageLoader = AppController.getInstance().getImageLoader();
        handler = new Handler();
        sharedPref = new SharedPref(this);
        myImageurls.clear();

        pager = (ViewPager) findViewById(R.id.pager);
        findViewById(R.id.dryeyeslayout).setOnClickListener(this);
        findViewById(R.id.reminderslayout).setOnClickListener(this);
        findViewById(R.id.contactuslayout).setOnClickListener(this);
        findViewById(R.id.eyenutritionlayout).setOnClickListener(this);
        findViewById(R.id.files).setOnClickListener(this);
        findViewById(R.id.remainder).setOnClickListener(this);
        findViewById(R.id.location).setOnClickListener(this);
        findViewById(R.id.buy).setOnClickListener(this);
        findViewById(R.id.questions_text).setOnClickListener(this);

        mySwitch = (Switch) findViewById(R.id.mySwitch);
        switchforquestions();

        slideimagesfromurl();


    }

    private void switchforquestions() {
        try {
            if (sharedPref.getString(AppConstants.SWITCHCASE).equals(AppConstants.TRUE)) {
                mySwitch.setChecked(true);
                checkedTrue();
            } else if (sharedPref.getString(AppConstants.SWITCHCASE).equals(AppConstants.FALSE)) {
                mySwitch.setChecked(false);
                checkedFalse();
            }
        } catch (Exception e) {
            mySwitch.setChecked(false);
            checkedFalse();
        }

        TextView mTextView = (TextView) findViewById(R.id.questions_text);
        mTextView.setPaintFlags(mTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    checkedTrue();
                } else {
                    checkedFalse();
                }
            }
        });

    }

    private void checkedTrue() {
        mySwitch.setText("YES");
        sharedPref.putString(AppConstants.SWITCHCASE, AppConstants.TRUE);
        findViewById(R.id.questions_text).setVisibility(View.VISIBLE);
    }

    private void checkedFalse() {
        mySwitch.setText("NO");
        sharedPref.putString(AppConstants.SWITCHCASE, AppConstants.FALSE);
        findViewById(R.id.questions_text).setVisibility(View.GONE);
    }

    private void slideimagesfromurl() {
        noimagesfoundin_database();
    }

    private void noimagesfoundin_database() {
        String url = AppConstants.ADVERT_URL;
        String post = url.replaceAll(" ", "%20");
        URI sourceUrl = null;
        try {
            sourceUrl = new URI(post);
            getimageurls(sourceUrl.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dryeyeslayout:
                break;
            case R.id.reminderslayout:
                startActivity(new Intent(HomeScreenActivity.this, ReminderListAcitivty.class));
                break;
            case R.id.contactuslayout:
                contactdialog();
                break;
            case R.id.eyenutritionlayout:
                startActivity(new Intent(HomeScreenActivity.this, InstructionalVideoActivity.class));
                break;
            case R.id.files:
                startActivity(new Intent(HomeScreenActivity.this, BarGraphActivity.class));
                break;
            case R.id.remainder:
                break;
            case R.id.location:
                startActivity(new Intent(HomeScreenActivity.this, LocationWebviewActivity.class));
                break;
            case R.id.buy:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.CATHEDRALWEBSITE)));
                break;
            case R.id.questions_text:
                startActivity(new Intent(HomeScreenActivity.this, QuestioneriesNewActivity.class));
                break;
        }
    }

    private void getimageurls(final String url) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        new AsyncTask<Void, Void, Void>() {
            String responce = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                responce = new HttpGetHandler().jsonUrl(url);
//                Log.e("response", "@@@" + responce);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (responce != null) {
                    try {
                        myImageurls.clear();
                        JSONArray jsonarray = new JSONArray(responce);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject obj = jsonarray.getJSONObject(i);
                            String id = obj.getString("Id");
                            String imageurl = obj.getString("Advert_URL__c");
                            myImageurls.add(imageurl);
                        }

                        if (myImageurls.size() > 0) {
                            numberofImages = myImageurls.size();
                            pager.setAdapter(new ImagePagerAdapter(myImageurls));
                            listener = new DetailOnPageChangeListener();
                            pager.setOnPageChangeListener(listener);
                            handler.postDelayed(runnable, 3000);
                        } else {
                            Toast.makeText(HomeScreenActivity.this, "No images found", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.execute();
    }

    private void contactdialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.contactdialog);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialog.findViewById(R.id.call_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialnumber();
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.findViewById(R.id.email_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        sendemail();
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    private void sendemail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "info@cathedraleye.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Cathedraleye Mobile App");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void dialnumber() {
        try {
            /*Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.android.dialer");
            startActivity(LaunchIntent);*/
            Intent intent = new Intent(Intent.ACTION_DIAL);
            startActivity(intent);
        }catch (Exception e){

        }
    }


    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public class DetailOnPageChangeListener extends
            ViewPager.SimpleOnPageChangeListener {
        private int currentPage;

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
        }

        public int getCurrentPage() {
            return currentPage;
        }
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            if (position >= numberofImages) {
                position = 0;
                dealyTimeMills = 0;
                pager.setCurrentItem(position, false);
            } else {
                position = pager.getCurrentItem();
                position = position + 1;
                dealyTimeMills = 3000;
                pager.setCurrentItem(position, true);
            }
            handler.postDelayed(runnable, dealyTimeMills);
        }
    };

    private class ImagePagerAdapter extends PagerAdapter {

        private ArrayList<String> myImageurls;
        private LayoutInflater inflater;

        ImagePagerAdapter(ArrayList<String> myImageurls) {
            this.myImageurls = myImageurls;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return myImageurls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(View view, int position) {
            final View imageLayout = inflater.inflate(R.layout.item_pager_image, null);
            final NetworkImageView imageView = (NetworkImageView) imageLayout.findViewById(R.id.imageView);
            imageView.setImageUrl(myImageurls.get(position), imageLoader);
            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            checkConnection();
        }
    };

    private void checkConnection() {
        if (!new NetworkConnection(HomeScreenActivity.this).isConnectionAvailable()) {
            new Alert(HomeScreenActivity.this).alertMessage("Connection lost", R.string.check_connection);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(message);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(message,
                new IntentFilter("send"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver message = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

        }
    };


}
