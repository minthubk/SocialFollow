package com.cathedrale.webservices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpGetHandler {


    public String jsonUrl(String url) {

        String responce = null;
        try {
            HttpGet request = new HttpGet(url);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            responce = EntityUtils.toString(httpEntity);
//            Log.e(" Rest Log", "servicevalue:" + responce);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responce;

    }
}
