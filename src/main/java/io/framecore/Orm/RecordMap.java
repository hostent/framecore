package io.framecore.Orm;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.framecore.Frame.ViewStore;
import io.framecore.Tool.DataConverter;

public class RecordMap {

	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(Class<?> clazz, ResultSet rs) throws SQLException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException {
		if(rs==null)
		{
			return null;
		}
		List<T> list = new ArrayList<T>();

		while (rs.next()) {

			Map<String, Object> map = new HashMap<String, Object>();
			
			
			Method[] methods =clazz.getDeclaredMethods();
			for (Method method : methods) {
				if(method.getName().startsWith("set"))
				{
					continue;
				}
				Type paramType = method.getGenericReturnType();
				JsonProperty jp =method.getAnnotation(JsonProperty.class);
				if(jp==null)
				{
					continue;
				}
				
				String key = jp.value();
				 
				try
				{
					rs.findColumn(key); //列不存在，就异常
				}catch (Exception e) {
					continue;
				}
				
				 
				Object value = DataConverter.parse(paramType, rs.getObject(key));
				map.put(key, value);
				
			}		 

			T t = (T) clazz.newInstance();

			((ViewStore) t).fill(map);

			list.add(t);
		}
		if (!rs.isClosed()) {
			rs.close();
		}

		return list;
	}

	public static <T> T toEntity(Class<?> clazz, ResultSet rs) throws SQLException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException {
		if(rs==null)
		{
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
	 
		if(!rs.next())
		{
			return null;
		}
		
		Method[] methods =clazz.getDeclaredMethods();
		for (Method method : methods) {
			if(method.getName().startsWith("set"))
			{
				continue;
			}
			Type paramType = method.getGenericReturnType();
			JsonProperty jp =method.getAnnotation(JsonProperty.class);
			if(jp==null)
			{
				continue;
			}
			
			String key = jp.value();
			 
			Object value = DataConverter.parse(paramType, rs.getObject(key));
			map.put(key, value);
			
		}

 

		if (!rs.isClosed()) {
			rs.close();
		}

		@SuppressWarnings("unchecked")
		T t = (T) clazz.newInstance();

		((ViewStore) t).fill(map);

		return t;
	}

	
	public static List<Map<String, Object>> convertList(ResultSet rs) {
	    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	    try {
	        ResultSetMetaData md = rs.getMetaData();
	        int columnCount = md.getColumnCount();
	        while (rs.next()) {
	            Map<String, Object> rowData = new HashMap<String, Object>();
	            for (int i = 1; i <= columnCount; i++) {
	                rowData.put(md.getColumnName(i), rs.getObject(i));
	            }
	            list.add(rowData);
	        }
	    } catch (SQLException e) {
	    // TODO Auto-generated catch block
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null)
	            rs.close();
	            rs = null;
	        } catch (SQLException e) {
	            e.printStackTrace();
	    }
	}
	    return list;
	}
	
	
	public static Map<String, Double> toMap(ResultSet rs) throws SQLException
	{
		Map<String, Double> map = new ConcurrentHashMap<String, Double>();
		if(rs==null)
		{
			return null;
		}
 
		while (rs.next()) {
			
			String key = DataConverter.parse(String.class, rs.getObject("keyColum".toUpperCase()));			 
			Double value = DataConverter.parse(Double.class, rs.getObject("sumColum".toUpperCase()));
			
			map.put(key, value);
			
		}
		
		if (!rs.isClosed()) {
			rs.close();
		}
		
		return map;

	}
}
