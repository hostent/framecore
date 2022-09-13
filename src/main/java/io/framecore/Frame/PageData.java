package io.framecore.Frame;

import java.util.*;


public class PageData {
	
	public long total;
    public List<Map<String, Object>> rows;
    

    public Map<String, Object> stats;

    public PageData()
    {
    }

    public PageData(long count, List<Map<String, Object>> page)
    {
        total = count;
        rows = page;
    }
    
    public PageData(long count, List<Map<String, Object>> page, Map<String, Object> map)
    {
        total = count;
        rows = page;
        stats= map;
    }
}
