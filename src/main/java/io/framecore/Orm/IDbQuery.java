package io.framecore.Orm;

import java.util.List;
import java.util.Map;

 
public interface IDbQuery<T> {

	IDbQuery<T> Where(String exp,Object... par);

	IDbQuery<T> OrderBy(String exp);

	IDbQuery<T> OrderByDesc(String exp);

	IDbQuery<T> Limit(int form, int length);

	IDbQuery<T> Distinct();
	
	IDbQuery<T> Select(String... cols);

    T First();
    
    List<T> ToList();
    
    
    Map<String,Double> Sum(String sumColum,String groupColum);

    long Count();

    boolean Exist();
    
//    <E> E  FirstSelect(Class<E> cla);
//    
//    <E> List<E> ToListSelect(Class<E> cla);
//    
    
}
