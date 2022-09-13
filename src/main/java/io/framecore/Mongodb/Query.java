package io.framecore.Mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.*;

import io.framecore.Frame.IQuery;
import io.framecore.Frame.ViewStore;
 

public class Query<T> implements IQuery<T>, IMongoQuery<T> {

	public Query(MongoCollection<Document> _coll, Class<T> _clstype) {
		coll = _coll;
		clstype = _clstype;
	}

	Class<T> clstype;

	MongoCollection<Document> coll = null;

	Bson where = new BasicDBObject();
	
	Bson select = new BasicDBObject();

	Bson orderBy = null;

	int Form = 0;
	int Length = 0;
	
	private void reset()
	{
		where = new BasicDBObject();
		
		select = new BasicDBObject();

		orderBy = null;

		Form = 0;
		Length = 0;
	}

	T getT(Document doc) {
		T t = null;
		try {
			t = clstype.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		ViewStore vs = (ViewStore) t;

		java.util.Set<String> keySet = doc.keySet();

		for (String key : keySet) {
			vs.set(key, doc.get(key));
		}

		return t;
	}

	@Override
	public T Get(Object id) {

		BsonDocument filter = new BsonDocument();

		BsonObjectId obj = new BsonObjectId(new ObjectId(id.toString()));

		filter.put("_id", obj);

		Document doc = coll.find(filter).first();
		
		reset();
		
		if(doc==null)
		{
			return null;
		}

		return getT(doc);
	}

	@Override
	public T GetUnique(Object unique) {
		// TODO 自动生成的方法存根
		return null;
	}
	
	@Override
	public IMongoQuery<T> Select(String... cols) {
		
		for (String col : cols) {
			
			((BasicDBObject)select).put(col, true);
		}
				
		return this;
	}

	@Override
	public IMongoQuery<T> Where(String exp, Object... parArray) {

		where = ExpCal.Analysis(exp, parArray);
		
		return this;
	}

	@Override
	public IMongoQuery<T> Where(Bson filter) {

		where = filter;

		return this;
	}

	@Override
	public IMongoQuery<T> OrderBy(String exp) {

		orderBy = new BasicDBObject(exp, 1);

		return this;
	}

	@Override
	public IMongoQuery<T> OrderByDesc(String exp) {
		orderBy = new BasicDBObject(exp, -1);
		return this;
	}

	@Override
	public IMongoQuery<T> Limit(int form, int length) {
		Form = form;
		Length = length;
		return this;
	}

	@Override
	public T First() {

		FindIterable<Document> finder = Build();

		Document doc = finder.first();
		
		reset();
		if(doc==null)
		{
			return null;
		}
		return getT(doc);
	}

	private FindIterable<Document> Build() {
		 
		FindIterable<Document> finder = coll.find(where).projection(select);
		if (orderBy != null) {
			finder = finder.sort(orderBy);
		}
		if (Length != 0) {
			finder = finder.skip(Form).limit(Length);
		}
		return finder;
	}

	@Override
	public List<T> ToList() {

		List<T> resultList = new ArrayList<T>();

		FindIterable<Document> finder = Build();

		// long count = coll.countDocuments(where);

		MongoCursor<Document> cur = finder.iterator();

		while (cur.hasNext()) {

			resultList.add(getT(cur.next()));
		}
		
		reset();

		return resultList;
	}

	@Override
	public long Count() {
		// TODO
		long count = coll.countDocuments(where);
		
		reset();
		
		return count;
	}

	@Override
	public boolean Exist() {

		FindIterable<Document> finder = coll.find(where);
		MongoCursor<Document> cur = finder.iterator();

		Boolean result = cur.hasNext();
		
		reset();
		
		return result;
	}
	
	// sum(xx) as xx1, count(1) as xx2
	public List<Map<String, Object>> Aggregate(String selectSql, String... groupColums)
	{

		Bson match = new BasicDBObject("$match", where);
		
		DBObject groupFieldItem = new BasicDBObject();
		
		if(groupColums!=null)
		{
			for (String groupColumItem : groupColums) {
				
				groupFieldItem.put(groupColumItem, "$" + groupColumItem);
			}
			
		}
		if(groupColums==null || groupColums.length==0)
		{
			groupFieldItem=null;
		}
		DBObject groupFields = new BasicDBObject("_id", groupFieldItem);
		
		for (String key : selectSql.split(","))
		{
			String fieldStr =key.trim().split("as")[0].trim();
			String rename =key.trim().split("as")[1].trim();
			
			if(fieldStr.contains("sum("))
			{
				String field = fieldStr.replace("sum(", "").replace(")", "");
				groupFields.put(rename, new BasicDBObject("$sum", "$" +field));
				continue;
			}
			if(fieldStr.contains("count("))
			{
				groupFields.put(rename, new BasicDBObject("$sum", 1));
				continue;
			}
			else
			{
				String field = fieldStr.trim();
				groupFields.put(rename, new BasicDBObject("$first", "$" +field));
			}
						
		} 
			
		Bson group = new BasicDBObject("$group", groupFields);
				
		List<Bson> optionList = new ArrayList<>();
		optionList.add(match);
		optionList.add(group);	
		
		Bson sort =null;
		if (orderBy != null) {
			sort = new BasicDBObject("$sort", orderBy);
		}
		else
		{
			DBObject sortFields = new BasicDBObject("_id", 1);
			sort = new BasicDBObject("$sort", sortFields);			
		}
		optionList.add(sort);		
		
		if (Length != 0) {
			
			Bson limit = new BasicDBObject("$limit",Length );
			Bson skip = new BasicDBObject("$skip",Form );
			
			optionList.add(skip);
			optionList.add(limit);		
		}

		
		AggregateIterable<Document> finder = coll.aggregate(optionList);
		
		
		MongoCursor<Document> cur = finder.iterator();

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		while (cur.hasNext()) {
			
			Map<String, Object> item = new HashMap<String, Object>();
			Document doc = cur.next();
			
			java.util.Set<String> keySet = doc.keySet();

			for (String key : keySet) {
				if(key.equals("_id") && groupColums!=null)
				{
					for (String groupColumItem : groupColums) {
					
						item.put(groupColumItem, ((Document)doc.get("_id")).get(groupColumItem));
					}
					
				}
				else
				{
					item.put(key, doc.get(key));
				}
				
			}			
			result.add(item);
			
		}
		
		reset();
		
		return result;
	}

	@Override
	public Map<String, Double> Sum(String sumColum, String groupColum) {

		Bson match = new BasicDBObject("$match", where);

		DBObject groupFields = new BasicDBObject("_id", "$" + groupColum);
		
		groupFields.put("sumColum", new BasicDBObject("$sum", "$" + sumColum));
		
		Bson group = new BasicDBObject("$group", groupFields);
		
		DBObject sortFields = new BasicDBObject("_id", 1);
		Bson sort = new BasicDBObject("$sort", sortFields);
		
		List<Bson> optionList = new ArrayList<>();
		optionList.add(match);
		optionList.add(group);
		optionList.add(sort);
		
		AggregateIterable<Document> finder = coll.aggregate(optionList);
		
		MongoCursor<Document> cur = finder.iterator();

		Map<String, Double> result = new HashMap<String, Double>();
		while (cur.hasNext()) {
			
			Document doc = cur.next();
			result.put(String.valueOf(doc.get("_id")), doc.getDouble("sumColum"));
			
		}
		
		reset();
		
		return result;
	}

	@Override
	public Map<String, Integer> Count(String groupColum) {

		Bson match = new BasicDBObject("$match", where);

		DBObject groupFields = new BasicDBObject("_id", "$" + groupColum);
		groupFields.put("countColum", new BasicDBObject("$sum", 1));
		Bson group = new BasicDBObject("$group", groupFields);
		
		DBObject sortFields = new BasicDBObject("_id", 1);
		Bson sort = new BasicDBObject("$sort", sortFields);
		
		List<Bson> optionList = new ArrayList<>();
		optionList.add(match);
		optionList.add(group);
		optionList.add(sort);
		
		AggregateIterable<Document> finder = coll.aggregate(optionList);
		
		MongoCursor<Document> cur = finder.iterator();

		Map<String, Integer> result = new HashMap<String, Integer>();
		while (cur.hasNext()) {
			
			Document doc = cur.next();
			result.put(doc.getString("_id"), doc.getInteger("countColum"));
			
		}
		
		reset();
		
		return result;
	}

	@Deprecated
	@Override
	public List<T> getList(List<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}

 


}
