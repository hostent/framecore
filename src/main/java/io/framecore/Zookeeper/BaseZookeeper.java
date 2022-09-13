package io.framecore.Zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import io.framecore.Frame.Log;
import io.framecore.Tool.PropertiesHelp;

public class BaseZookeeper implements Watcher {
	
	static BaseZookeeper bzk = null;
	static Lock _store_Lock = new ReentrantLock();
	
	
    IConnEvent _connEvent=null;
    
    public static BaseZookeeper Conn() throws IOException, Exception {
		return BaseZookeeper.Conn(null);
	}

	public static BaseZookeeper Conn(IConnEvent connEvent) throws IOException, Exception {
		if (bzk == null) {
			_store_Lock.lock();
			try {

				if (bzk == null) {
					bzk = new BaseZookeeper();
					String connectstring = PropertiesHelp
							.getAppConf("spring.cloud.zookeeper.connect-string");
					bzk.connectZookeeper(connectstring);

				}
			} finally {
				_store_Lock.unlock();
			}

		}
		
		bzk._connEvent=connEvent;
		
		
		return bzk;
	}
	

	static Lock _Lock = new ReentrantLock();

	private static ZooKeeper zookeeper;
	private static final int SESSION_TIME_OUT = 2000; //2000

	@Override
	public void process(WatchedEvent event) {

		if (event.getState() == KeeperState.SyncConnected) {
			System.out.println("222222,Watch received event");
			if(_connEvent!=null)
			{	
				_connEvent.onConn();
				 			
			}
				
		}
		
		//过期重新连接
		if (event.getState() == KeeperState.Expired)
		{
			try {
				System.out.println("2222222222,BaseZookeeper:Expired");
				IConnEvent connEvent = bzk._connEvent;
				bzk=null;
				zookeeper=null;	
				
				
				BaseZookeeper.Conn(connEvent);		
				
				
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 连接zookeeper
	 * 
	 * @param host
	 * @throws Exception
	 */
	public ZooKeeper connectZookeeper(String host) throws Exception {
		if (zookeeper != null) {
			return zookeeper;
		}

		_Lock.lock();
		try {

			if (zookeeper == null) {
				zookeeper = new ZooKeeper(host, SESSION_TIME_OUT, this);
			}
			return zookeeper;

		} catch (Exception e) {
			Log.logError(e);
		} finally {
			_Lock.unlock();

		}

		return zookeeper;
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public String createNode(String path, String data) throws Exception {
		 
		return zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	/**
	 * 创建节点
	 * CreateMode类型分为4种

		1.PERSISTENT--持久型

		2.PERSISTENT_SEQUENTIAL--持久顺序型

		3.EPHEMERAL--临时型

		4.EPHEMERAL_SEQUENTIAL--临时顺序型
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public String createTempNode(String path, String data) throws Exception {
		 
		return zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}

	
	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public Stat exists(String path) throws Exception {
		return zookeeper.exists(path, false);
	}
	
	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public Stat exists(String path, Boolean iswatch) throws Exception {
		return zookeeper.exists(path, iswatch);
	}

	/**
	 * 获取路径下所有子节点
	 * 
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public List<String> getChildren(String path) throws KeeperException, InterruptedException {
		
		
		List<String> children = zookeeper.getChildren(path, true);
		return children;
	}

	public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
		//zookeeper.exists(path, true);
		List<String> children = zookeeper.getChildren(path, watcher);
		return children;
	}

	/**
	 * 获取节点上面的数据
	 * 
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public String getData(String path) throws KeeperException, InterruptedException {
		byte[] data = zookeeper.getData(path, false, null);
		if (data == null) {
			return "";
		}
		return new String(data);
	}

	/**
	 * 设置节点信息
	 * 
	 * @param path
	 * @param data
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public Stat setData(String path, String data) throws KeeperException, InterruptedException {
		Stat stat = zookeeper.setData(path, data.getBytes(), -1);
		return stat;
	}

	/**
	 * 删除节点
	 * 
	 * @param path
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public void deleteNode(String path) throws InterruptedException, KeeperException {
		zookeeper.delete(path, -1);
	}

//	/**
//	 * 获取创建时间
//	 * @param path
//	 * @return
//	 * @throws KeeperException
//	 * @throws InterruptedException
//	 */
//	public String getCTime(String path) throws KeeperException, InterruptedException{
//		Stat stat = zookeeper.exists(path, false);
//		return DateUtil.longToString(String.valueOf(stat.getCtime()));
//	}

	/**
	 * 获取某个路径下孩子的数量
	 * 
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public Integer getChildrenNum(String path) throws KeeperException, InterruptedException {
		int childenNum = zookeeper.getChildren(path, false).size();
		return childenNum;
	}

	/**
	 * 关闭连接
	 * 
	 * @throws InterruptedException
	 */
	public void closeConnection() throws InterruptedException {
		if (zookeeper != null) {
			zookeeper.close();
		}
	}

}