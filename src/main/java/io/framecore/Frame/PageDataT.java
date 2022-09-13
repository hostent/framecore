package io.framecore.Frame;

import java.util.*;

public class PageDataT<T> {
	
	public long total;
    public List<T> rows;
    

    public Map<String, Object> stats;

    public PageDataT()
    {
    }

    public PageDataT(long count, List<T> page)
    {
        total = count;
        rows = page;
    }
    
    public PageDataT(long count, List<T> page, Map<String, Object> map)
    {
        total = count;
        rows = page;
        stats= map;
    }
}
