package io.framecore.Tool;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.framecore.Frame.Log;
import io.framecore.Frame.Result;

public class JsonHelp {
	
	static ObjectMapper objectMapper=null;
	
	public static ObjectMapper getJack()
	{
		if(objectMapper!=null)
		{
			return objectMapper;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(dateFormat);
		
		return objectMapper;
		
	}
	
	
	public static Object toObject(String json,Type returnType,Class<?>  elementClasses)
	{
		ObjectMapper objectMapper = JsonHelp.getJack();
		
		Object  obj=null;
		if(returnType.equals(java.util.List.class) ||returnType.toString().startsWith("java.util.List"))
		 {
			if(elementClasses==null)
			{
				elementClasses = (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0];
			}
						
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, elementClasses);
			
			try {
				obj =  objectMapper.readValue(json, javaType);
			} catch (IOException e) {
				Log.logError(e);
			}
						
		 }
		else if(returnType.equals(Result.class) && elementClasses!=null)
		{
			 
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Result.class, elementClasses);
			
			try {
				obj =  objectMapper.readValue(json, javaType);
			} catch (IOException e) {
				Log.logError(e);
			}
		}
		else if (returnType.getTypeName().startsWith("java.util.HashMap"))
		{
			try {
				obj =  objectMapper.readValue(json, HashMap.class);
			} catch (IOException e) {
				Log.logError(e);
			}
		}
		else
		{
			try {
				obj = objectMapper.readValue(json, (Class<?>)returnType);
			} catch (IOException e) {
				Log.logError(e);
			}
		}
		
		return obj;
	}
	
	
	public static String toJson(Object obj)
	{
		ObjectMapper objectMapper = JsonHelp.getJack();
		
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			 
			Log.logError(e);
		}
		return null;
	}

}

