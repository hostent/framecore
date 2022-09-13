package io.framecore.Mongodb;

 
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.*;

import com.mongodb.*;
 
public class ExpCal {

	// (col>?) or (xx=?)

	public static BasicDBObject Analysis(String exp, Object... parArray) {

		Queue<Object> par = new LinkedList<Object>() ; 
		
		for (Object object : parArray) {
//			if(object.getClass().equals(Date.class))
//			{
//				 
//				
//				par.offer(((Date)object).getTime());
//			}
//			else
//			{
//				par.offer(object);
//			}
			
			par.offer(object);
			
		}
		
		BasicDBObject where = new BasicDBObject();

		// ♫ ♪
		// exp =exp.replace(") or (", "♫");
		// exp =exp.replace(")or (", "♫");
		// exp =exp.replace(") or(", "♫");
		// exp =exp.replace(")or(", "♫");

		exp = exp.replace(") and (", "♪");
		exp = exp.replace(")and (", "♪");
		exp = exp.replace(") and(", "♪");
		exp = exp.replace(")and(", "♪");

		exp = exp.replace(")", "");
		exp = exp.replace("(", "").trim();

		// col>?♫xx=?♪rrr=?

		String[] expArray = exp.split("\\?");

		BasicDBList values = new BasicDBList();

		for (String expItem : expArray) {

			String itemStr = expItem;

			if (expItem.startsWith("♪")) {
				itemStr = itemStr.substring(1, itemStr.length());
			}

			calOperator(par, values, itemStr);

		}
		if(values.size()==1)
		{
			return (BasicDBObject)values.get(0);
		}
		 
		where.append("$and", values);

		return where;

	}

	private static void calOperator(Queue<Object> par, BasicDBList values, String expItem) {

		HashMap<String, String> map = getOperators();
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String opkey = (String) entry.getKey();
			String opval = (String) entry.getValue();		
			
			
			if (expItem.endsWith(opkey)) {

				String key = expItem.substring(0, expItem.indexOf(opkey)).trim();
				Object val = par.poll();
				values.add(new BasicDBObject(key, new BasicDBObject(opval, val)));
				return;
			}		
			 			
			
		}
		
		//like
		if (expItem.endsWith(" like")) {

			String key = expItem.substring(0, expItem.indexOf(" like")).trim();
			Object val = par.poll();
			Pattern pattern = Pattern.compile("^.*" + val.toString()+ ".*$", Pattern.CASE_INSENSITIVE); 
			
			values.add(new BasicDBObject(key, new BasicDBObject("$regex", pattern)));
			return;
		}	
		

	}

	static LinkedHashMap<String, String> _Operators;
	
	static Lock lock = new ReentrantLock();

	static LinkedHashMap<String, String> getOperators() {
		
		lock.lock();
		try {
			
			if (_Operators != null) {
				return _Operators;
			}
			_Operators = new LinkedHashMap<String, String>();
			_Operators.put(">=", "$gte");
			_Operators.put("<=", "$lte");
			_Operators.put("!=", "$ne");
			
			_Operators.put("=", "$eq");
			_Operators.put(">", "$gt");
			_Operators.put("<", "$lt");
			 
			_Operators.put(" not in", "$nin");
			
			_Operators.put(" in", "$in");
			
			
			return _Operators;
			
		} finally {
			lock.unlock();
		}
		
	}

}
