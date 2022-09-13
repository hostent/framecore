package io.framecore.Orm;

import java.sql.*;
import java.util.logging.*;

import io.framecore.Frame.Environment;
import io.framecore.Frame.Log;

public class MsSqlHelp {
	
	public static void closePst(PreparedStatement pst) {
		 
		if(pst==null)
		{
			return;
		}
		try {
			if (!pst.isClosed()) {
				pst.close();
			}
		} catch (SQLException e) {
			Log.logError(e);
		}
	}

	public static Statement getStatement(Connection conn) {
		if (conn == null) {
			return null;
		}
		try {

			return conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public static PreparedStatement getPreparedStatement(Connection conn, String cmdText, Object... cmdParams) {
		if (Environment.isDev()) {
			System.out.println(cmdText);
			for (Object obj : cmdParams) {
				if (obj == null) {
					System.out.println("null");
				} else {
					System.out.println(obj.toString());
				}

			}
		}
		// Log.msgLog.info((new java.util.Date()).toString()+"---\r\n"+cmdText);
		if (conn == null) {
			return null;
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(cmdText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			int i = 1;
			for (Object item : cmdParams) {
				// 针对 oracel 的时间类型处理
				if (item instanceof java.util.Date) {
					item = new java.sql.Timestamp(((java.util.Date) item).getTime());
				}
				pstmt.setObject(i, item);
				i++;
			}
		} catch (SQLException e) {
			Log.logError(e);
			close(pstmt);
		}  
		return pstmt;
	}

	public static int ExecSql(Connection conn, String cmdText) {
		Statement stmt = getStatement(conn);
		if (stmt == null) {
			return -2;
		}
		int i;
		try {
			i = stmt.executeUpdate(cmdText);
		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
			i = -1;
		} finally {
			close(stmt);
		}
		return i;
	}

	/**
	 * 执行 SQL 语句,返回结果为整型 主要用于执行非查询语句
	 * 
	 * @param conn      数据库连接
	 * @param cmdText   需要 ? 参数的 SQL 语句
	 * @param cmdParams SQL 语句的参数表
	 * @return 非负数:正常执行; -1:执行错误; -2:连接错误
	 */
	public static int ExecSql(Connection conn, String cmdText, Object... cmdParams) {
		PreparedStatement pstmt = getPreparedStatement(conn, cmdText, cmdParams);
		if (pstmt == null) {
			return -2;
		}
		int i;
		try {
			i = pstmt.executeUpdate();

		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
			i = -1;
		}
		finally {
			close(pstmt);
		}		
		return i;
	}

	public static Object ExecAndScalar(Connection conn, String cmdText, Object... cmdParams) {
		PreparedStatement pstmt = getPreparedStatement(conn, cmdText, cmdParams);

		if (pstmt == null) {
			return -2;
		}
		int i;
		Object result = null;
		ResultSet rs = null;
		;
		try {
			i = pstmt.executeUpdate();
			if (i > 0) {
				rs = getResultSet(conn, " select @@IDENTITY; ");
				result = Long.parseLong(buildScalar(rs).toString()) ;
			}

		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
			i = -1;
		}
		finally {
			close(pstmt);
		}
		return result;
	}

	public static Object ExecScalar(Connection conn, String cmdText) {
		ResultSet rs = getResultSet(conn, cmdText);
		Object obj = buildScalar(rs);
		closeEx(rs);
		return obj;
	}

	/**
	 * 返回结果集的第一行的一列的值,其他忽略
	 * 
	 * @param cmdText SQL 语句
	 * @return
	 * @throws SQLException
	 */
	public static Object ExecScalar(Connection conn, String cmdText, Object... cmdParams) throws SQLException {
		PreparedStatement pst = MsSqlHelp.getPreparedStatement(conn, cmdText, cmdParams);

		ResultSet rs = getResultSet(pst);
		Object obj = buildScalar(rs);
		//pst.close();
		closeEx(rs);
		return obj;
	}

 

	public static ResultSet getResultSet(PreparedStatement pstmt) {
		if (pstmt == null) {
			return null;
		}
		try {

			ResultSet result = pstmt.executeQuery();

			return result;
		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
			close(pstmt);
		}
		return null;
	}

	public static ResultSet getResultSet(Connection conn, String cmdText, Object... cmdParams) {
		PreparedStatement pstmt = getPreparedStatement(conn, cmdText, cmdParams);
		if (pstmt == null) {
			return null;
		}
		try {

			ResultSet result = pstmt.executeQuery();

			return result;
		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
			close(pstmt);
		}
		return null;
	}

	public static Object buildScalar(ResultSet rs) {
		if (rs == null) {
			return null;
		}
		Object obj = null;
		try {
			if (rs.next()) {
				obj = rs.getObject(1);
			}
		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
		}
		return obj;
	}

	private static void close(Object obj) {
		if (obj == null) {
			return;
		}
		try {
			if (obj instanceof Statement) {
				((Statement) obj).close();
			} else if (obj instanceof PreparedStatement) {
				((PreparedStatement) obj).close();
			} else if (obj instanceof ResultSet) {
				((ResultSet) obj).close();
			} else if (obj instanceof Connection) {
				((Connection) obj).close();
			}
		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static void closeEx(Object obj) {
		if (obj == null) {
			return;
		}

		try {
			if (obj instanceof Statement) {
				((Statement) obj).close();
			} else if (obj instanceof PreparedStatement) {
				((PreparedStatement) obj).close();
			} else if (obj instanceof ResultSet) {
				if(!((ResultSet) obj).getStatement().isClosed())
				{
					((ResultSet) obj).getStatement().close();
				}				
			} else if (obj instanceof Connection) {
				((Connection) obj).close();
			}
		} catch (SQLException ex) {
			Logger.getLogger(MsSqlHelp.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

 

}
