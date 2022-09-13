package io.framecore.Frame;

 
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public abstract class ViewStore {
	
	Map<String,Object> map = new HashMap<String, Object>();
	
	public void fill(Map<String,Object> map)
	{
		this.map = map;
	}
	
	public Map<String,Object> export()
	{
		return this.map;
	}
	
	public void mergeForNull(Map<String,Object> newMap)
	{
		
		for (String key : newMap.keySet()) {
			
			if(newMap.get(key)==null)
			{
				continue;
			}
			
			map.put(key, newMap.get(key));
			
		}
		
	}
	
	public Object get(String key)
	{
		if(!map.containsKey(key))
		{
			return null;
		}
		return map.get(key);
	}
	
	public void set(String key,Object value)
	{
		map.put(key, value);
	}
	
	
	public String toFormatString()
	{
		
		Method[] methods = this.getClass().getDeclaredMethods();
		
		String res="";
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			JsonProperty jp =method.getAnnotation(JsonProperty.class);
			
			if(jp==null)
			{
				continue;
			}
			String key = jp.value();
			 
			Note note =method.getAnnotation(Note.class);
			if(note==null )
			{
				continue;
			}
			
			res=res+String.format("%s:%s, ", note.value(),this.get(key)==null?"":this.get(key).toString());
			 
		}
				
		return "["+res+"]";
		
	}
	
	
	public String toUpdateString(ViewStore orgin)
	{
		
		Method[] methods = this.getClass().getDeclaredMethods();
		
		String res="";
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			JsonProperty jp =method.getAnnotation(JsonProperty.class);
			
			if(jp==null)
			{
				continue;
			}
			String key = jp.value();
			
			String val = this.get(key)==null?"":this.get(key).toString();
			String orginVal = orgin.get(key)==null?"":orgin.get(key).toString();
			
			
			if(val.equals(orginVal))
			{
				continue;
			}
			 
			Note note =method.getAnnotation(Note.class);
			if(note==null )
			{
				continue;
			}
		
			res=res+String.format("%s:%s-->%s, ", note.value(),orginVal,val);
			 
		}
				
		return "["+res+"]";
		
	}
	
	
	public String getNote(String key)
	{
		Method[] methods = this.getClass().getDeclaredMethods();
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			
			JsonProperty jp =method.getAnnotation(JsonProperty.class);
			Note note =method.getAnnotation(Note.class);
			
			if(jp!=null && key.equals(jp.value()) && note!=null)
			{
				return note.value();
			}
			 
		}
		
		return null;
		
	}
	
	public List<String> getCols()
	{
		List<String> cols = new ArrayList<String>();
		
		Method[] methods = this.getClass().getDeclaredMethods();
		
		String res="";
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			JsonProperty jp =method.getAnnotation(JsonProperty.class);
			
			if(jp==null)
			{
				continue;
			}
			String key = jp.value();
			if(cols.contains(key)) 
			{
				continue;
			}
			cols.add(key);
		 
			 
		}
		return  cols;
		
	}

	
	
}
