package io.framecore.Frame;

import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.*;

public class LangType {
	
	static List<Type> typeList = new ArrayList<Type>();

	static {
		
		typeList.add(Integer.class);
		typeList.add(Date.class);
		typeList.add(Float.class);
		typeList.add(Double.class);
		typeList.add(String.class);
		typeList.add(Boolean.class);
		typeList.add(Long.class);
	}
	
	public static Boolean isLangType(Type cla)
	{
		for (Type type : typeList) {
			if(type.equals(cla))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static Boolean isList(Type cla)
	{
		if(cla.equals(java.util.List.class) ||cla.toString().startsWith("java.util.List"))
		{
			return true;
		}
		return false;
	}
	
}
