package io.framecore.Frame;

public interface ISet<T> extends IQuery<T>,ICommand<T>{
	
	int exec(String sql, Object... pars);
	
	String[] getCols(boolean isIncludeId);
	
	String getKey();
	
	String getTableName();
	
}
