package io.framecore.Saas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import io.framecore.Tool.JsonHelp;
import io.framecore.Zookeeper.BaseZookeeper;

public class SiteKeeper {

	static Watcher watcher = new Watcher() {

		@Override
		public void process(WatchedEvent arg0) {
						 
			if(_SiteList!=null)
			{
// 站点管理修改成手动模式
//				_getLock.lock();
//				try {
//					_SiteList.clear();
//				} finally {
//					_getLock.unlock();
//				}				
			}		

		}
	};

	static Lock _Lock = new ReentrantLock();
	
	static Lock _getLock = new ReentrantLock();

	public static List<Site> _SiteList;
	
	static List<Site> getCopy(List<Site> orginList)
	{
		List<Site> siteList = new ArrayList<Site>();
		//_getLock.lock();
		try {			
			for (Site site : orginList) {
				siteList.add(site);
			}
		} finally {
			//_getLock.unlock();
		}
		return siteList;
	}

	public static List<Site> getList() {

		if (_SiteList != null && _SiteList.size()>0) {			
			return getCopy(_SiteList);
		}

		List<Site> siteList = new ArrayList<Site>();
		
		Site defaultSite = new Site();
		List<String> urlList = new ArrayList<String>();
		urlList.add(".");
		defaultSite.setUrlList(urlList);
		defaultSite.setSiteTag("a");
		
		siteList.add(defaultSite);

//		_Lock.lock();
//		try {
//
//			if (_SiteList != null && _SiteList.size()>0) {
//				return getCopy(_SiteList);
//			}
//
//			// 从zookeeper中获取各个站点信息。
//
//			BaseZookeeper bz = BaseZookeeper.Conn();
//
//			List<String> list = bz.getChildren("/sitekeeper", watcher);
//
//			for (String item : list) {
//
//				String data = bz.getData("/sitekeeper/" + item);
//				siteList.add((Site) JsonHelp.toObject(data, Site.class, null));
//
//			}
//			
			_SiteList=siteList;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			_Lock.unlock();
//		}

		return getCopy(_SiteList);

	}

	public static void addSite(Site site) throws Exception {
		// TODO

		BaseZookeeper bz = null;
		try {
			bz = BaseZookeeper.Conn();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bz.exists("/sitekeeper") == null) {
			bz.createNode("/sitekeeper", "");
		}

		if (bz.exists("/sitekeeper/" + site.getSiteTag()) == null) {
			bz.createNode("/sitekeeper/" + site.getSiteTag(), JsonHelp.toJson(site));
		}

		// bz.deleteNode("/sitekeeper"+site.getSiteTag());

		// bz.createNode("/sitekeeper"+site.getSiteTag(), JsonHelp.toJson(site));
	}

	public static void deleteSite(String siteTag) throws Exception {

		BaseZookeeper bz = null;
		try {
			bz = BaseZookeeper.Conn();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bz.exists("/sitekeeper/" + siteTag) != null) {
			bz.deleteNode("/sitekeeper/" + siteTag);
		}

		// bz.deleteNode("/sitekeeper"+site.getSiteTag());

		// bz.createNode("/sitekeeper"+site.getSiteTag(), JsonHelp.toJson(site));
	}

	public static Boolean hasSite(String siteTag) {
		for (Site site : getList()) {
			if (site.getSiteTag().equals(siteTag)) {
				return true;
			}

		}
		return false;
	}

}
