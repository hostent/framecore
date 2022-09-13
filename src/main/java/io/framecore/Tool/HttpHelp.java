package io.framecore.Tool;

import java.io.IOException;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import io.framecore.Frame.JFrameException;
import okhttp3.*;
import okhttp3.Request.Builder;
import okio.BufferedSink;

public class HttpHelp {

	public static final MediaType Form = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
	
	public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
	


	public static String post(String url, HashMap<String, Object> head, HashMap<String, Object> body) throws JFrameException {

		FormBody.Builder build = new FormBody.Builder();

		for (Entry<String, Object> entry : body.entrySet()) {

			build.add(entry.getKey(), entry.getValue().toString());
		}

		RequestBody requestBody = build.build();

		Request.Builder reqBuild = new Request.Builder();
		
		reqBuild.addHeader("content-type", "application/x-www-form-urlencoded;charset:utf-8");

		if (head != null && (!head.isEmpty())) {
			for (Entry<String, Object> entry : head.entrySet()) {
				reqBuild.addHeader(entry.getKey(), entry.getValue().toString());
			}
		}

		Request requestPost = reqBuild.url(url).post(requestBody).build();

		OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS).build();

		okhttp3.Response response;
		String str="";
		try {
			response = client.newCall(requestPost).execute();

			str = response.body().string();
			
			return str;
			
		} catch (IOException e) {
			 throw  JFrameException.apiCallError(requestPost.method(), str, e);
		}

		

	}
	
	
	public static String get(String url) throws JFrameException {

 
		Request requestPost = new Request.Builder().url(url).get().build();

		OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS).build();

		okhttp3.Response response;
		String str="";
		try {
			response = client.newCall(requestPost).execute();

			str = response.body().string();
			
			return str;
			
		} catch (IOException e) {
			 throw  JFrameException.apiCallError(requestPost.method(), str, e);
		}

		

	}
	
	public static String get(HashMap<String, Object> head,String url) throws JFrameException {

		 
 

		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS).build();

		okhttp3.Response response;
		String str="";
			
		okhttp3.Headers.Builder headers = new Headers.Builder();		 
	    
	    if (head != null && (!head.isEmpty())) {
			for (Entry<String, Object> entry : head.entrySet()) {
				headers.add(entry.getKey(), entry.getValue().toString());
			}
		}
	    
	    
	    Request request = new Request.Builder().url(url).get().headers(headers.build()).build();
	    
		try {
			response = client.newCall(request).execute();

			str = response.body().string();
			
			return str;
			
		} catch (IOException e) {
			 throw  JFrameException.apiCallError(request.method(), str, e);
		}

		

	}
	

	public static String get(String userName, String psw, HashMap<String, Object> head,String url) throws JFrameException {

		 
 

		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.addInterceptor(new BasicAuthInterceptor(userName, psw))
				.build();

		okhttp3.Response response;
		String str="";
			
		okhttp3.Headers.Builder headers = new Headers.Builder();		 
	    
	    if (head != null && (!head.isEmpty())) {
			for (Entry<String, Object> entry : head.entrySet()) {
				headers.add(entry.getKey(), entry.getValue().toString());
			}
		}
	    
	    
	    Request request = new Request.Builder().url(url).get().headers(headers.build()).build();
	    
		try {
			response = client.newCall(request).execute();

			str = response.body().string();
			
			return str;
			
		} catch (IOException e) {
			 throw  JFrameException.apiCallError(request.method(), str, e);
		}

		

	}
	
	
	
	public static String post(HashMap<String, Object> head,HashMap<String, Object> body,String url) throws JFrameException {


		OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS).build();

		okhttp3.Response response;
		String str="";
			
		okhttp3.Headers.Builder headers = new Headers.Builder();		 
	    
	    if (head != null && (!head.isEmpty())) {
			for (Entry<String, Object> entry : head.entrySet()) {
				headers.add(entry.getKey(), entry.getValue().toString());
			}
		}	    
	    
	    okhttp3.FormBody.Builder bodyBuild = new FormBody.Builder();
	    
	    if (body != null && (!body.isEmpty())) {
			for (Entry<String, Object> entry : body.entrySet()) {
				bodyBuild.add(entry.getKey(), entry.getValue().toString());
			}
		}
	    

	    Request request = new Request.Builder().url(url).headers(headers.build()).post(bodyBuild.build()).build();
	    
		try {
			response = client.newCall(request).execute();

			str = response.body().string();
			
			return str;
			
		} catch (IOException e) {
			 throw  JFrameException.apiCallError(request.method(), str, e);
		}

		

	}
	
	
	public static String postJson(String url,String json) {
	    
	    OkHttpClient okHttpClient = new OkHttpClient();
	    
	    RequestBody requestBody = RequestBody.create(JSON, json);
	    
	    Request request = new Request.Builder()
	            .url(url)
	            .post(requestBody)
	            .build();
	    
	    request.newBuilder().addHeader("content-type", "application/json;charset=utf-8");
	     
	    try {
	    Response response=okHttpClient.newCall(request).execute();
	    
	    return response.body().string();
	       
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "";
	    }

	}
	
	public static String postJson(String url,HashMap<String, Object> head,String json) {
	    
	    OkHttpClient okHttpClient = new OkHttpClient();
	    
	    RequestBody requestBody = RequestBody.create(JSON, json);
 
		 
			
		okhttp3.Headers.Builder headers = new Headers.Builder();		 
	    
	    if (head != null && (!head.isEmpty())) {
			for (Entry<String, Object> entry : head.entrySet()) {
				headers.add(entry.getKey(), entry.getValue().toString());
			}
		}
	    
	    headers.add("content-type", "application/json;charset=utf-8");
	    
	    
	    
	    
	    Request request = new Request.Builder().url(url).headers(headers.build()).post(requestBody).build();
	    
	    
	    try {
	    	Response response= okHttpClient.newCall(request).execute();
	    
	    	return response.body().string();
	       
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "";
	    }

	}
 
	
	public static String postJson(String userName, String psw ,String url,HashMap<String, Object> head,String json) {
	    

		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.addInterceptor(new BasicAuthInterceptor(userName, psw))
				.build();
	    
	    RequestBody requestBody = RequestBody.create(JSON, json);
 
		 
			
		okhttp3.Headers.Builder headers = new Headers.Builder();		 
	    
	    if (head != null && (!head.isEmpty())) {
			for (Entry<String, Object> entry : head.entrySet()) {
				headers.add(entry.getKey(), entry.getValue().toString());
			}
		}
	    
	    headers.add("content-type", "application/json;charset=utf-8");
	    
	    
	    
	    
	    Request request = new Request.Builder().url(url).headers(headers.build()).post(requestBody).build();
	    
	    
	    try {
	    	Response response= client.newCall(request).execute();
	    
	    	return response.body().string();
	       
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "";
	    }

	}
 
}
