package io.framecore.Orm;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.framecore.Frame.*;
import io.framecore.redis.Cache;
import io.framecore.redis.CacheHelp;

public abstract class DbSetOracle<T> extends DbSet implements ISet<T>,IDbQuery<T> {

	public DbSetOracle(String connKey) {
		ConnKey = connKey;
		Class<?> type = this.getType();

		query = new DbQueryOracle<T>(connKey, type);
		TEntity = new Entity<T>(type);
	}

	public DbSetOracle(String connKey, String prefix) {
		ConnKey = connKey;
		Class<?> type = this.getType();

		query = new DbQueryOracle<T>(connKey, type, prefix);
		TEntity = new Entity<T>(type,prefix);

	}
	
	//for xml
	public DbSetOracle(String connKey,Class<T> type) {
		ConnKey = connKey;
		TEntity = new Entity<T>(type);
	}
	
	protected String[] getCacheKeys() {
		
		return null;
	}
	

	private String ConnKey;


	private DbQueryOracle<T> query;
	private Entity<T> TEntity;

	@Override
	public Object Add(T t) {

		String sql = "insert into {table} ( {columns} ) values ( {values} )"; // select
																				// @@IDENTITY;
		String[] columns;

		Object returnId =TEntity.getIdValue(t); //id 没有传入，才去获取ID
		if(returnId==null || returnId.toString().isEmpty()||returnId.equals("0"))
		{
			returnId = 	getNextId();
			((ViewStore)t).set(TEntity.key, returnId);
		}

		boolean isNeedId = true;		

		columns = TEntity.getColumns(isNeedId);

		sql = sql.replace("{table}", TEntity.tableName);

		sql = sql.replace("{columns}",  String.join(",", columns) );

		sql = sql.replace("{values}", String.join(",", TEntity.getColumnSymbol(columns)));

 
		Connection conn = MybatisUtils.getConn(ConnKey);
		
		try {
 
			 
			int c = MsSqlHelp.ExecSql(conn, sql, TEntity.getColumnValues(isNeedId, t));
			if(c<=0)
			{
				return 0;
			}
			
		}catch (Exception e) {
			 Log.logError(e,"add error:"+sql);
			 return 0;
		}
		finally {
			MybatisUtils.close();
		}
		
		//remove cache
		if(getCacheKeys()!=null && getCacheKeys().length>0)
		{
			for (String cacheKey : getCacheKeys()) {
				CacheHelp.delete(cacheKey);
			}
		}
		
		return Integer.parseInt(String.valueOf(returnId));
	}

	@Override
	public int Delete(Object id) {

		String sql = "delete from {table} where {key}=?";

		sql = sql.replace("{table}", TEntity.tableName);
		sql = sql.replace("{key}", TEntity.key);

		Object[] par = new Object[1];
		par[0] = id;

		Connection conn = MybatisUtils.getConn(ConnKey);
		int result =0;
		try {
			 result = MsSqlHelp.ExecSql(conn, sql, par);	
		}catch (Exception e) {
			Log.logError(e,"delete error:"+sql);
			return 0;
		}
		finally {
			MybatisUtils.close();
		}
		
		//remove cache
		if(getCacheKeys()!=null && getCacheKeys().length>0)
		{
			for (String cacheKey : getCacheKeys()) {
				CacheHelp.delete(cacheKey);
			}
		}

		return result;
	}

	@Override
	public int Update(T t) {

		String sql = "update {table} set {updateStr} where {key}={id}";
		sql = sql.replace("{table}", TEntity.tableName);

		String[] cols = TEntity.getColumns(false);
		String colStr = "";
		for (int i = 0; i < cols.length; i++) {
			colStr = colStr + "," + String.format("%s=?", cols[i]);
		}
		colStr = colStr.substring(1, colStr.length());
		sql = sql.replace("{updateStr}", colStr);
		sql = sql.replace("{key}", TEntity.key);

		sql = sql.replace("{id}", TEntity.getIdValue(t));

		Connection conn = MybatisUtils.getConn(ConnKey);
		int result=0;
		try {
		
			result = MsSqlHelp.ExecSql(conn, sql, TEntity.getColumnValues(false, t));
		}catch (Exception e) {
			Log.logError(e,"update error:"+sql);
			return 0;
		}
		finally {
			MybatisUtils.close();
		}
		
		//remove cache
		if(getCacheKeys()!=null && getCacheKeys().length>0)
		{
			for (String cacheKey : getCacheKeys()) {
				CacheHelp.delete(cacheKey);
			}
		}
		
		return result;
	}

	
	public Integer getNextId()
	{
		String sql ="select SEQ_{table}.nextval from dual";
		sql = sql.replace("{table}", TEntity.tableName);
		Connection conn = MybatisUtils.getConn(ConnKey);
		Integer id = 0;
		try {
			id = Integer.parseInt( MsSqlHelp.ExecScalar(conn, sql).toString());
		} catch (Exception e) {
			Log.logError(e,"getNextId error:"+sql);
			return 0;
		}
		finally {
			MybatisUtils.close();
		}
		return 	id;
		
	}
	
	public  DbSetOracle<T> ignoreCase()
	{
		query.ignoreCase();
		
		return this;
	}
	

	@Override
	public IDbQuery<T> Where(String exp, Object... par) {

		return query.Where(exp, par);
	}

	@Override
	public IDbQuery<T> OrderBy(String exp) {

		return query.OrderBy(exp);
	}

	@Override
	public IDbQuery<T> OrderByDesc(String exp) {

		return query.OrderByDesc(exp);
	}

	@Override
	public IDbQuery<T> Limit(int form, int length) {

		return query.Limit(form, length);
	}

	@Override
	public IDbQuery<T> Distinct() {

		return query.Distinct();
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
	public T Get(Object id) {

		return query.Get(id);
	}

	@Override
	public T GetUnique(Object unique) {

		return query.GetUnique(unique);
	}
	

	@Override
	public Map<String, Double> Sum(String sumColum, String groupColum) {
		
		return query.Sum(sumColum, groupColum);
	}
	
	@Override
	public IDbQuery<T> Select(String... cols) {
		 
		return query.Select(cols);
	}
	
	@Override
	public List<T> getList(List<Integer> ids) {
		return query.getList(ids);
	}
	
	
	@Override
	public int exec(String sql, Object... pars) {

		Connection conn = MybatisUtils.getConn(ConnKey);

		int result = 0;
		try {
			result = MsSqlHelp.ExecSql(conn, sql, pars);
		} catch (Exception e) {
			Log.logError(e, "exec error:" + sql);
			return 0;
		} finally {
			MybatisUtils.close();
		}

		// remove cache
		if (getCacheKeys() != null && getCacheKeys().length > 0) {
			for (String cacheKey : getCacheKeys()) {
				CacheHelp.delete(cacheKey);
			}
		}

		return result;

	}
	
	@Override
	public String[] getCols(boolean isIncludeId) {
		
		return TEntity.getColumns(isIncludeId);
		
	}
	
	@Override
	public String getKey() {
		return TEntity.key;
	}
	
	@Override
	public String getTableName() {
		 
		return TEntity.tableName;
	}
	

}
