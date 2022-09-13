package io.framecore.Orm;


import java.util.List;
import java.util.Map;

public interface ISqlMapper {

    Integer insert(String statement);

    Integer delete(String statement);

    Integer update(String statement);

    List<Map<String, Object>> selectList(String statement);

    Object selectOne(String statement);
    
    //Map<String, Object> selectMap(String statement);
}
