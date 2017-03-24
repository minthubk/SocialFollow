package com.cathedrale.Utils;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aspire on 12/28/2015.
 */
public class NetworkUtils {

    public static String makePOSTRequest(HashMap<String, String> paramMap, String Url, Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpClient httpClient = new DefaultHttpClient();
        String responseString = null;
        HttpPost httpPost = new HttpPost(Url);
//        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        httpPost.setHeader("Content-type", "multipart/form-data");
//        httpPost.setHeader("Content-type", "application/json; charset=utf8");
//        httpPost.setHeader("Content-type", "application/json");

        HttpEntity httpEntity = null;
        int paramSize = paramMap.size();
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(paramSize);
        for (String currentParamName : paramMap.keySet()) {
//            Log.e("parametername", currentParamName);
            nameValuePair.add(new BasicNameValuePair(currentParamName, paramMap.get(currentParamName)));
        }
//        Log.e("nameValuePair ", "" + nameValuePair);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // writing error to Log
//            System.out.println(e);
//            Log.e("e", e.getMessage());
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            // writing response to log
            httpEntity = response.getEntity();
            int responseCode = response.getStatusLine().getStatusCode();
            Log.e("Http responseCode:", "" + responseCode);
            if (responseCode >= 200 && responseCode < 300) {
                responseString = EntityUtils.toString(httpEntity);
//                Log.e("Http Response:", responseString);
            } else {
                responseString = EntityUtils.toString(httpEntity);
//                Log.e("Http Response:", responseString);
            }
        } catch (Exception e) {
            // writing exception to log
//            System.out.println(e);
//            Log.e("es", e.getMessage());
        }
        return responseString;
    }
}
