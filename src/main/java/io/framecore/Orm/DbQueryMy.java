package io.framecore.Orm;

import java.sql.*;
import java.util.*;

import io.framecore.Frame.*;

public class DbQueryMy<T> implements IQuery<T> ,IDbQuery<T>{

	public DbQueryMy(String connKey, Class<?> type) {
		ConnKey = connKey;
		TEntity = new Entity<T>(type);
		trackSql = trackSql.replace("{table}", "`"+TEntity.tableName+"`");
	}
	
	public DbQueryMy(String connKey, Class<?> type, String prefix) {
		ConnKey = connKey;
		TEntity = new Entity<T>(type,prefix);

		trackSql = trackSql.replace("{table}", "`" + TEntity.tableName+"`");
		
		
	}

	Entity<T> TEntity;

	private String ConnKey;
	


	private String whereExp;
	private String orderExp;

	private String selectExp="";
 
	private String order = "";

	private Integer limitForm = null;
	private Integer limitLength = null;

	private String distinct = "";
	
	private Boolean ignoreCase = false;

	private String trackSql = "select {column} from {table} {where} {order} {limit}";

	private List<Object> sqlArgs = new ArrayList<Object>();

	@Override
	public IDbQuery<T> Where(String exp, Object... par) {

		whereExp = exp;
		if (par != null && par.length > 0) {
			for (int i = 0; i < par.length; i++) {
				sqlArgs.add(par[i]);

			}
		}
		return this;
	}

	@Override
	public IDbQuery<T> OrderBy(String exp) {
		orderExp = exp;
		order = "asc";

		return this;
	}

	@Override
	public IDbQuery<T> OrderByDesc(String exp) {
		order = "desc";
		orderExp = exp;
		return this;
	}

	@Override
	public IDbQuery<T> Limit(int form, int length) {
		limitForm = form;
		limitLength = length;
		return this;
	}

	@Override
	public IDbQuery<T> Distinct() {
		this.distinct = "distinct";

		return this;
	}

	@Override
	public T First() {
		limitForm = 0;
		limitLength = 1;

		BuildColumns(null);
		BuildWhere(null, null);
		BuildOrder(null);
		BuildLimit(null);
		
		Connection conn = MybatisUtils.getConn(ConnKey);

		T resultObj=null;
		
		PreparedStatement pst=null;
		try {
			pst = MsSqlHelp.getPreparedStatement(conn,  this.trackSql, sqlArgs.toArray());
			ResultSet rs = MsSqlHelp.getResultSet(pst);

			resultObj = RecordMap.toEntity(TEntity.getType(), rs);
			pst.close();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| SQLException e) {
			Log.logError(e);
		}
		finally {
			MsSqlHelp.closePst(pst);
			MybatisUtils.close();
			
		}
		
		return resultObj;
	}

	@Override
	public List<T> ToList() {

		BuildColumns(null);
		BuildWhere(null, null);
		BuildOrder(null);
		BuildLimit(null);

		Connection conn = MybatisUtils.getConn(ConnKey);
		List<T> list=null;
		
		PreparedStatement pst=null;		
		try {
			pst = MsSqlHelp.getPreparedStatement(conn,  this.trackSql, sqlArgs.toArray());
			ResultSet rs = MsSqlHelp.getResultSet(pst);
			list = RecordMap.toList(TEntity.getType(), rs);
			 
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| SQLException e) {
			Log.logError(e);
		}
		finally {
			MsSqlHelp.closePst(pst);
			MybatisUtils.close();
			
		}
		
		return list;
	}

	@Override
	public long Count() {
		BuildColumns(" count(0) ");
		BuildWhere(null, null);
		BuildOrder(null);
		BuildLimit(null);
		
		Connection conn = MybatisUtils.getConn(ConnKey);
		
		int count=0;
		try {
			count = Integer.parseInt( MsSqlHelp.ExecScalar(conn, this.trackSql, sqlArgs.toArray()).toString());
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		finally {

			MybatisUtils.close();
		}
		
		
		return count;
	}

	@Override
	public boolean Exist() {

		return Count() > 0;
	}

	@Override
	public T Get(Object id) {

		String whereStr = String.format(" where %s=? ", TEntity.key);

		limitForm = 0;
		limitLength = 1;

		List<Object> arg = new ArrayList<Object>();
		arg.add(id);

		BuildColumns(null);
		BuildWhere(whereStr, arg.toArray());
		BuildOrder(" ");
		BuildLimit(" ");

		Connection conn = MybatisUtils.getConn(ConnKey);
		
		T entity=null;
		PreparedStatement pst=null;
		try {
			pst = MsSqlHelp.getPreparedStatement(conn,  this.trackSql, sqlArgs.toArray());
			ResultSet rs = MsSqlHelp.getResultSet(pst);			
			entity = RecordMap.toEntity(TEntity.getType(), rs);
			pst.close();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| SQLException e) {			 
			Log.logError(e);
		}
		finally {	
			MsSqlHelp.closePst(pst);
			MybatisUtils.close();
			
		}

		return entity;
	}

	@Override
	public T GetUnique(Object unique) {

		String whereStr = String.format(" where %s=? ", TEntity.uniqueKey);

		limitForm = 0;
		limitLength = 1;

		List<Object> arg = new ArrayList<Object>();
		arg.add(unique);

		BuildColumns(null);
		BuildWhere(whereStr, arg.toArray());
		BuildOrder(" ");
		BuildLimit(" ");

		Connection conn = MybatisUtils.getConn(ConnKey);
		T entity=null;
		PreparedStatement pst=null;
		try {
			//System.out.println(this.trackSql);
			
			pst = MsSqlHelp.getPreparedStatement(conn,  this.trackSql, sqlArgs.toArray());
			ResultSet rs = MsSqlHelp.getResultSet(pst);		
			entity = RecordMap.toEntity(TEntity.getType(), rs);
			pst.close();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| SQLException e) {
			 
			Log.logError(e);
		}finally {
			MsSqlHelp.closePst(pst);
			MybatisUtils.close();
			
		}
		
	 

		return entity;
	}

	private void BuildColumns(String cols) {
		if (distinct.isEmpty() || distinct == null) {
			trackSql = trackSql.replace("{column}", distinct + " {column}");
		}

		if (cols != null && (!cols.isEmpty())) {
			trackSql = trackSql.replace("{column}", cols);
			return;
		}

		String columnStr = "";
	 
		for (int i = 0; i < TEntity.columns.size(); i++) {
					
			String colName = TEntity.columns.get(i);

			columnStr = columnStr + ",`" + colName + "`";
		}

		columnStr = columnStr.substring(1, columnStr.length());

		trackSql = trackSql.replace("{column}", columnStr);
	}

	private void BuildWhere(String where, Object[] args) {

		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				sqlArgs.add(args[i]);
			}
		}
		
		if (where != "" && where != null) {
			trackSql = trackSql.replace("{where}", where);
			return;
		}
		if (whereExp == "" || whereExp == null) {
			trackSql = trackSql.replace("{where}", "");
			return;
		}

		trackSql = trackSql.replace("{where}", " where " + whereExp);

		

	}

	private void BuildOrder(String order) {
		if (order != null) {
			trackSql = trackSql.replace("{order}", order);
			return;
		}
		if (orderExp == null) {
			trackSql = trackSql.replace("{order}", " order by 1 ");
			return;
		}
		
		trackSql = trackSql.replace("{order}", " order by " + orderExp + " "  + this.order);

	}

	private void BuildLimit(String limit) {
		if (limit != null) {
			trackSql = trackSql.replace("{limit}", limit);
			return;
		}

		if (limitLength == null) {
			trackSql = trackSql.replace("{limit}", "");
			return;
		}

		if (limitForm == null) {
			limitForm = 0;
		}
		if (limitLength == null) {
			limitLength = 1;
		}
		trackSql = trackSql.replace("{limit}",
				String.format(" limit %s,%s ", limitForm, limitLength));

	}

	@Override
	public Map<String, Double> Sum(String sumColum, String groupColum) {
		BuildColumns(" sum("+sumColum+") as sumColum, "+groupColum +" as keyColum " );
		BuildWhere(null, null);
		BuildOrder("");
		BuildLimit(null);
		
		Connection conn = MybatisUtils.getConn(ConnKey);
		
		Map<String, Double> result = null;
		PreparedStatement pst=null;
		try {
			pst = MsSqlHelp.getPreparedStatement(conn, this.trackSql + " group by  "+groupColum, sqlArgs.toArray());
			result = RecordMap.toMap( MsSqlHelp.getResultSet(pst));
			
			pst.close();
			
		} catch (SQLException e) {
			Log.logError(e);
		}finally {
			MsSqlHelp.closePst(pst);
			MybatisUtils.close();
			
		}	

		return result;
	}

	@Override
	public IDbQuery<T> Select(String... cols) {
		for (String str : cols) {
			
			selectExp = selectExp + "," + str;
			
		}
		selectExp = selectExp.substring(1, selectExp.length());
		return this;
	}

	@Override
	public List<T> getList(List<Integer> ids) {
		if(ids==null || ids.size()==0)
		{
			return null;
		}
		String str = "";
		
		for (Integer integer : ids) {	
			if(integer!=null)
			{
				str=str+","+integer;		
			}				
		}
		if(str.isEmpty())
		{
			return null;
		}
		
		str= str.substring(1,str.length());
		
		return this.Where("id in ("+str+")").ToList();
	}

 

}
