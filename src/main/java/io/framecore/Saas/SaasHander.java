package io.framecore.Saas;

 
public class SaasHander {
	
	private  static ThreadLocal<String> saasHanderTag = new ThreadLocal<String>();
	
	
	public static void init(String url)
	{
		
		Site site = Site.getByUrl(url);
		if(site!=null)
		{
			saasHanderTag.set(site.getSiteTag());
		}
		
	}
	
	public static void set(String handerTag)
	{
		if(SiteKeeper.hasSite(handerTag))
		{
			saasHanderTag.set(handerTag);
		} 
		
	}
	
	public static void dispose()
	{
		saasHanderTag.remove();
	}

	public static Site currentHander()
	{
		if(saasHanderTag.get()!=null)
		{
			return Site.getByTag(saasHanderTag.get());
		}
		return null;
	}
	
	
}
