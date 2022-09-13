package io.framecore.Orm;

import java.sql.Connection;
import java.util.*;

import io.framecore.Frame.*;
import io.framecore.redis.CacheHelp;

public abstract class DbSetMs<T> implements ISet<T>,IDbQuery<T> {

	public DbSetMs(String connKey) {
		ConnKey = connKey;
		Class<?> type = this.getType();

		query = new DbQueryMs<T>(connKey, type);
		TEntity = new Entity<T>(type);
	}

	public DbSetMs(String connKey, String prefix) {
		ConnKey = connKey;
		Class<?> type = this.getType();

		query = new DbQueryMs<T>(connKey, type, prefix);
		TEntity = new Entity<T>(type);

		Prefix = prefix;
	}
	
	//for xml
	public DbSetMs(String connKey,Class<T> type) {
		ConnKey = connKey;
		TEntity = new Entity<T>(type);
	}


	public abstract Class<?> getType();

	private String ConnKey;

	private String Prefix = "";

	private DbQueryMs<T> query;
	private Entity<T> TEntity;
	
	
	protected String[] getCacheKeys() {
		
		return null;
	}

	@Override
	public Object Add(T t) {

		String sql = "insert into [{table}] ( {columns} ) values ( {values} );"; // select
																				// @@IDENTITY;
		String[] columns;

		Object returnId = null;

		boolean isNeedId = false;

		if (TEntity.isIdentity) {
			//sql = sql + " select @@IDENTITY; ";
			isNeedId = false;
		} else {
			returnId = TEntity.getIdValue(t);
			isNeedId = true;
		}

		columns = TEntity.getColumns(isNeedId);

		sql = sql.replace("{table}", Prefix + TEntity.tableName);

		sql = sql.replace("{columns}", "[" + String.join("],[", columns) + "]");

		sql = sql.replace("{values}", String.join(",", TEntity.getColumnSymbol(columns)));

		//Connection conn = ConnetionManager.getConn(ConnKey);
		
		Connection conn = MybatisUtils.getConn(ConnKey);

		try {
			if (!isNeedId) {
				returnId = MsSqlHelp.ExecAndScalar(conn, sql, TEntity.getColumnValues(isNeedId, t));
			} else {
				MsSqlHelp.ExecSql(conn, sql, TEntity.getColumnValues(isNeedId, t));
			}
			
		}
		catch (Exception e) {
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
		

		return returnId;
	}

	@Override
	public int Delete(Object id) {

		String sql = "delete from [{table}] where [{key}]=?";

		sql = sql.replace("{table}", Prefix + TEntity.tableName);
		sql = sql.replace("{key}", TEntity.key);

		Object[] par = new Object[1];
		par[0] = id;

		//Connection conn = ConnetionManager.getConn(ConnKey);
		Connection conn = MybatisUtils.getConn(ConnKey);
		
		int result =0;
		try {
			result = MsSqlHelp.ExecSql(conn, sql, par);
		}
		catch (Exception e) {
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

		String sql = "update [{table}] set {updateStr} where {key}={id}";
		sql = sql.replace("{table}", Prefix + TEntity.tableName);

		String[] cols = TEntity.getColumns(false);
		String colStr = "";
		for (int i = 0; i < cols.length; i++) {
			colStr = colStr + "," + String.format("%s=?", cols[i]);
		}
		colStr = colStr.substring(1, colStr.length());
		sql = sql.replace("{updateStr}", colStr);
		sql = sql.replace("{key}", TEntity.key);

		sql = sql.replace("{id}", TEntity.getIdValue(t));

		//Connection conn = ConnetionManager.getConn(ConnKey);
		Connection conn = MybatisUtils.getConn(ConnKey);

		int result =0;
		try {
			 result = MsSqlHelp.ExecSql(conn, sql, TEntity.getColumnValues(false, t));
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
