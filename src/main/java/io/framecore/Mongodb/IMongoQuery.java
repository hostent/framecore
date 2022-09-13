package io.framecore.Mongodb;

import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;
 

public interface IMongoQuery<T> {

	IMongoQuery<T> Where(Bson filter);
	
	IMongoQuery<T> Where(String exp, Object... parArray);

	IMongoQuery<T> OrderBy(String exp);

	IMongoQuery<T> OrderByDesc(String exp);

	IMongoQuery<T> Limit(int form, int length);   
	
	IMongoQuery<T> Select(String... cols);

    T First();
    
    List<T> ToList();

    long Count();

    boolean Exist();
    
    Map<String,Double> Sum(String sumColum,String groupColum);
    
    Map<String,Integer> Count(String groupColum);
    
    List<Map<String, Object>> Aggregate(String selectSql, String... groupColums);
}
