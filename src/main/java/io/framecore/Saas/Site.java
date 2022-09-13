package io.framecore.Saas;

import java.util.List;

public class Site {
	

	String siteTag;
	
	List<String> urlList;
	
	
	public String getSiteTag() {
		return siteTag;
	}

	public void setSiteTag(String siteTag) {
		this.siteTag = siteTag;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}

	public static Site getByUrl(String url)
	{
		List<Site> list = SiteKeeper.getList();
		
		for (Site site : list) {

			for (String urltag : site.getUrlList()) {
				if(url.contains(urltag))
				{
					return site;
				}
			}
		}
		
		return null;
		
		
	}
	
	public static Site getByTag(String tag)
	{
		List<Site> list = SiteKeeper.getList();
		
		for (Site site : list) {
			
			if(site.getSiteTag().equals(tag))
			{
				return site;
			}
			 
		}
		return null;
	}
	
	

}
