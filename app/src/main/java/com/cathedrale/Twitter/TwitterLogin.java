package com.cathedrale.Twitter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cathedrale.Constants.AppConstants;
import com.cathedrale.Utils.SharedPref;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;


/**
 * Created by Aspire on 2/15/2016.
 */
public class TwitterLogin {

    public SocialAuthAdapter twittweAdapter;
    private String UserName;
    private Context context;
    SharedPref sharedPref;

    public TwitterLogin(Context context) {
        // TODO Auto-generated constructor stub
        this.context=context;
        sharedPref = new SharedPref(context);
    }

    public void calltwitterLogin(){
        Log.e("Custom-UI", "calltwitterLogin");
        twittweAdapter = new SocialAuthAdapter(new TwitterResponsListener());
        twittweAdapter.addCallBack(Provider.TWITTER,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
        try {
            twittweAdapter.addConfig(SocialAuthAdapter.Provider.TWITTER, "7eoaYLOm6TbZ4NfELobrZE75D","h5bFofYriktzoumrrKvvDuLuOsLZ5k1iA5todNDVFHOsPSSiGd", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        twittweAdapter.authorize(context, Provider.TWITTER);
    }
    public final class TwitterResponsListener implements DialogListener {
        @Override
        public void onBack() {
            // TODO Auto-generated method stub
            Profile t =null;
            ((OnLoginComplete)context).twitterCompleted(t);

        }
        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            Profile t =null;
            ((OnLoginComplete)context).twitterCompleted(t);
        }
        @Override
        public void onComplete(Bundle bndle) {
            // TODO Auto-generated method stub
            twittweAdapter.getUserProfileAsync(new ProfileDataListener());
        }

        @Override
        public void onError(SocialAuthError arg0) {
            // TODO Auto-generated method stub
        }
    }
    public final class ProfileDataListener implements
            SocialAuthListener<Profile> {
        @Override
        public void onExecute(String provider, Profile t) {

            String UserSocialId = t.getValidatedId();
            String token =twittweAdapter.getCurrentProvider()
                    .getAccessGrant().getKey();
            String secret =twittweAdapter.getCurrentProvider()
                    .getAccessGrant().getSecret();
            try{
                if(UserSocialId!=null){
                    UserName=t.getFullName();
                    Log.e("UserName",UserName);
                    sharedPref.putString("UserSocialId", UserSocialId);
                    sharedPref.putString("TwitterToken", token);
                    sharedPref.putString("TwitterSecret", secret);
                    sharedPref.putString(AppConstants.TWITTER_USERNAME,UserName);
                    sharedPref.putString("twitterImageURL",t.getProfileImageURL());
                    sharedPref.putString("screenName",t.getDisplayName());
                }
                ((OnLoginComplete)context).twitterCompleted(t);
            }catch (Exception exception){

            }
        }
        @Override
        public void onError(SocialAuthError e) {
        }
    }

    public  class MessageListener implements SocialAuthListener<Integer> {
        @SuppressWarnings("deprecation")
        @Override
        public void onExecute(String provider, Integer t) {
            Integer status = t;
            Log.e("status",""+status);
            if (status.intValue() == 200 || status.intValue() == 201
                    || status.intValue() == 204) {
                final AlertDialog alertDialog = new AlertDialog.Builder(
                        context).create();
                alertDialog.setTitle("Sucessfully posted");
                alertDialog.setMessage("Message posted on " + provider);
                alertDialog.setButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                alertDialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                Toast.makeText(context,
                        "Message not posted on " + provider, Toast.LENGTH_LONG)
                        .show();
            }
        }
        @Override
        public void onError(SocialAuthError e) {

        }
    }


}

