package io.framecore.Frame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

public class PagePars {
	
	 public PagePars()
     {
		 Pars = new Hashtable<String, Object>();
     }
	 
	 public PagePars(HttpServletRequest request)
     {
		 Pars = new Hashtable<String, Object>();
		 
		 if(request.getParameter("pageSize")!=null)
		 {
			 PageSize = Integer.parseInt(request.getParameter("pageSize"));
		 }
		 if(request.getParameter("pageIndex")!=null)
		 {
			 PageIndex = Integer.parseInt(request.getParameter("pageIndex"));
		 }
		 if(request.getParameter("order")!=null)
		 {
			 Order = request.getParameter("order");
		 }
		 
		 Enumeration<?> enu=request.getParameterNames();  
		 while(enu.hasMoreElements()){  			 
			 String paraName=(String)enu.nextElement();  
			 
			 if(paraName.equals("pageSize")||paraName.equals("pageIndex")||paraName.equals("order"))
			 {
				 continue;
			 }
			 
			 Pars.put(paraName, request.getParameter(paraName));
		 }
		 		 
		 
     }
	 
	 public int getFrom()
	 {
		 return (PageIndex-1)*PageSize;
	 }
	 
	 public String getStringPar(String key)
	 {
		 if(!Pars.containsKey(key))
		 {
			 return null;
		 }
		 return Pars.get(key).toString();
	 }
	 
	 public Integer getIntPar(String key)
	 {
		 if(!Pars.containsKey(key))
		 {
			 return null;
		 }
		 return Integer.parseInt(Pars.get(key).toString()) ;
	 }
	 
	 public Date getDatePar(String key,String format) throws ParseException
	 {
		 if(!Pars.containsKey(key))
		 {
			 return null;
		 }
		 SimpleDateFormat df = new SimpleDateFormat(format);
		 return df.parse(Pars.get(key).toString());
	 }
	 
	 public Double getDoublePar(String key)
	 {
		 if(!Pars.containsKey(key))
		 {
			 return null;
		 }
		 return Double.parseDouble(Pars.get(key).toString()) ;
	 }
	 
	 public Boolean getBooleanPar(String key)
	 {
		 if(!Pars.containsKey(key))
		 {
			 return null;
		 }
		 return Boolean.parseBoolean(Pars.get(key).toString()) ;
	 }
	 

     public Hashtable<String, Object> Pars;

     public int PageSize=50;

     public int PageIndex=1;

     public String Order;
}
