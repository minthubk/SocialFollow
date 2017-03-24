package com.cathedrale.webservices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONStringer;

public class HttpPostHandler {
	
	public  String jsonUrl(String url,JSONStringer stringer){

		String responce=null;
		try {
			// make web service connection
			HttpPost request = new HttpPost(url);
			//request.setHeader("Accept", "application/json");
			//request.setHeader("Content-type", "application/json");
			if (stringer!=null){
				StringEntity entity = new StringEntity(stringer.toString());
				request.setEntity(entity);
			}
			//Log.e("****Parameter Input****", "Testing:" + stringer +"..."+request);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(request);
			HttpEntity httpEntity =response.getEntity();
			responce = EntityUtils.toString(httpEntity);
//			Log.e(" Rest Log", "servicevalue:" + responce);

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return responce;

	}
}
