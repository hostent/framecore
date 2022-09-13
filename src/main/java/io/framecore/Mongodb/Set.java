package io.framecore.Mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import com.mongodb.client.*;
import com.mongodb.client.result.*;
import io.framecore.Frame.ViewStore;
import io.framecore.Orm.Table;
import io.framecore.redis.Cache;
import io.framecore.redis.CacheHelp;

public abstract class Set<T> implements  IMongoQuery<T> {

	private String dbName;

	public abstract Class<T> getType();

	Query<T> query;

	public Set(String _dbName) {
		dbName = _dbName;
		query = new Query<>(getCollection(),getType());
	}
	
	
	public Set(String _dbName,String prefix) {
		dbName = _dbName;
		_prefix=prefix;
		query = new Query<>(getCollection(),getType());
		
	}

	 
	public T Get(Object id) {

		return query.Get(id);

	}
	

	public String newId() {
		 return ObjectId.get().toString();
	}
	
	
	static Object lock = new Object();
	
	
	String _prefix="";
	
	private MongoCollection<Document> getCollection()
	{	 
		String collectionName=getType().getSimpleName();
		Table table=  this.getType().getAnnotation(Table.class);
		if(table!=null)
		{
			collectionName=table.Name().replace("{fix}", _prefix);
		}	
		
		return ConnectionManager.getDb(dbName).getCollection(collectionName, Document.class);	

	}

 
	public int Delete(Object id) {
 
		MongoCollection<Document> col = getCollection();

		BsonDocument deleteCondition = new BsonDocument();

		BsonObjectId obj = new BsonObjectId(new ObjectId(id.toString()));

		deleteCondition.put("_id", obj);

		DeleteResult result = col.deleteOne(deleteCondition);
		return (int) result.getDeletedCount();
	}
	
	public int DeleteMany(Bson where) {
		 
		MongoCollection<Document> col = getCollection();

		DeleteResult result = col.deleteMany(where);
		return (int) result.getDeletedCount();
	}
	
	public int DeleteMany(String exp, Object... parArray) {
		 
		MongoCollection<Document> col = getCollection();

		DeleteResult result = col.deleteMany(ExpCal.Analysis(exp, parArray));
		return (int) result.getDeletedCount();
	}

	public int Update(String id,ViewStore vs) {

		MongoCollection<Document> col = getCollection();
		 
		vs.set("_uptime", new Date());

		BsonDocument updateCondition = new BsonDocument();

		BsonObjectId objId = new BsonObjectId(new ObjectId(id));
		updateCondition.put("_id", objId);

		Document upDoc = getDoc(vs);
		upDoc.remove("_id");

		Document update = new Document();
		update.append("$set", upDoc);

		UpdateResult result = col.updateOne(updateCondition, update);

		return (int) result.getModifiedCount();
	}
	
	public int UpdateMany(Bson where,ViewStore vs) {

		MongoCollection<Document> col = getCollection();
		 
		vs.set("_uptime", new Date());

		Document upDoc = getDoc(vs);
		upDoc.remove("_id");

		Document update = new Document();
		update.append("$set", upDoc);

		UpdateResult result = col.updateMany(where, update);

		return (int) result.getModifiedCount();
	}
	public int UpdateMany(ViewStore vs,String exp, Object... parArray) {

		MongoCollection<Document> col = getCollection();
		 
		vs.set("_uptime", new Date());

		Document upDoc = getDoc(vs);
		upDoc.remove("_id");

		Document update = new Document();
		update.append("$set", upDoc);
		
		UpdateResult result = col.updateMany(ExpCal.Analysis(exp, parArray), update);

		return (int) result.getModifiedCount();
	}
	
 
	

	private Document getDoc(ViewStore vs)
	{
		Document doc =new Document();
		
		Map<String, Object> map = vs.export();		
		for (String key : map.keySet()) {
			doc.put(key, map.get(key));
		}
		
		return doc;
	 
	}
 
	public Object Add(T t) {

		Object objId = ObjectId.get();
		ViewStore doc = (ViewStore) t;
		doc.set("_id", objId);
		doc.set("_ctime", new Date());


		MongoCollection<Document> col = getCollection();

		col.insertOne(getDoc(doc));

		return objId;
	}
	public void AddMany(List<T> list) {
		
		List<Document> docList = new ArrayList<Document>();
		
		for (T t : list) {
			
			Object objId = ObjectId.get();
			ViewStore doc = (ViewStore) t;
			doc.set("_id", objId);
			doc.set("_ctime", new Date());
			
			docList.add(getDoc(doc));
		}
		MongoCollection<Document> col = getCollection();
		 
		col.insertMany(docList);

	}

	@Override
	public IMongoQuery<T> Where(String exp, Object... parArray) {
		 		 
		return query.Where(exp,parArray);
	}

	@Override
	public IMongoQuery<T> Where(Bson filter) {

		return query.Where(filter);
	}

	@Override
	public IMongoQuery<T> OrderBy(String exp) {

		return query.OrderBy(exp);
	}

	@Override
	public IMongoQuery<T> OrderByDesc(String exp) {

		return query.OrderByDesc(exp);
	}

	@Override
	public IMongoQuery<T> Limit(int form, int length) {

		return query.Limit(form, length);
	}

	@Override
	public T First() {

		return query.First();
	}

	@Override
	public List<T> ToList() {

		return query.ToList();
	}
	
	static Map<String, Object> VisionList=new ConcurrentHashMap<String, Object>();
	
	protected List<T> ToVisionList()
	{
		Cache cache = this.getType().getAnnotation(Cache.class);
		if(cache!=null)
		{
			String visionTag = CacheHelp.get(cache.key());
			if(visionTag==null || visionTag.isEmpty())
			{
				visionTag=this.getType().getName() + String.valueOf(new Date().getTime());
				CacheHelp.set(cache.key(), visionTag);
			}
			if(VisionList.containsKey(visionTag))
			{
				return (List<T>)VisionList.get(visionTag);
			}
			else
			{
				for (String key : VisionList.keySet()) {
					if(key.contains(this.getType().getName()))
					{
						VisionList.remove(key);
					}					
				}
				List<T> list = query.ToList();
				if(list==null)
				{
					list = new ArrayList<T>();
				}
				VisionList.put(visionTag,list);
				return (List<T>)VisionList.get(visionTag);
			}
		}
	 
		return  query.ToList();
	}

	protected void resetVision()
	{
		Cache cache = this.getType().getAnnotation(Cache.class);
		if(cache!=null)
		{
			CacheHelp.delete(cache.key());
		}
	}
	
	@Override
	public long Count() {

		return query.Count();
	}

	@Override
	public boolean Exist() {

		return query.Exist();
	}
	
	@Override
	public IMongoQuery<T> Select(String... cols) {
		return query.Select(cols);
	}
	
	@Override
	public Map<String, Double> Sum(String sumColum, String groupColum) {
		return query.Sum(sumColum, groupColum);
	}
	
	@Override
	public Map<String, Integer> Count(String groupColum) {
		return query.Count(groupColum);
	}
	
	public List<Map<String, Object>> Aggregate(String selectSql, String... groupColums)
	{
		return query.Aggregate(selectSql, groupColums);
	}
}
