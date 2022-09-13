package io.framecore.Zookeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.framecore.Frame.Log;
import io.framecore.Tool.PropertiesHelp;


public class ServerListWatcher {
	
	public static void main(String[] args) throws InterruptedException {
		
		
		 Thread t = new Thread(new Runnable(){

		        @Override
		        public void run() {
		            
		            for (int i=0;i<10;i++){
		                System.out.println("整在清理资源"+i);
		            }
		        }
		        
		    });
		
		
		Runtime.getRuntime().addShutdownHook(t);
		
		 while (true){
	            Scanner sc= new Scanner(System.in);
	            String st = sc.nextLine();//获取输入信息
	            if (st.equals("exit")){
	                System.exit(0);
	            }
	        }
		
		//String s= MacAddressUtil.formatAddress(MacAddressUtil.defaultMachineId());
		
		//System.out.println(s);
		
	}
 

	static Map<String, Map<String, ServerNode>> store = new HashMap<String, Map<String, ServerNode>>();
	
	//static Map<String, Watcher> storeWatcher =new HashMap<String, Watcher>();

 

	
	
	public static ServerNode getNode(String serverKey,String method)throws Exception {
		
		if(method==null)
		{
			return getNode1(serverKey);
		}
		return getNode2(serverKey,method);
	}
	
	// 负载均衡算法 // 随机
	public static ServerNode getNode1(String serverKey) throws Exception {

		if (!store.containsKey(serverKey)|| store.get(serverKey)==null || store.get(serverKey).size()==0 ) {

			String root = PropertiesHelp.getApplicationConf("spring.cloud.zookeeper.discovery.root");
			fillAndWatchStore(serverKey, root);

		}
		if (!store.containsKey(serverKey) || store.get(serverKey)==null || store.get(serverKey).size()==0) {
			Log.logError(new Exception("找不到服务端"), serverKey);
			return null;
		}

		// 负载均衡算法 // 随机
		Random rand = new Random();

		int index = rand.nextInt(store.get(serverKey).size());

		int i = 0;
		for (ServerNode v : store.get(serverKey).values()) {
			if (i == index) {
				return v;
			}
			i++;
		}

		return null;

	}

	
	// 负载均衡算法 // 平均分布
	public static ServerNode getNode2(String serverKey,String method) throws Exception {

		if (!store.containsKey(serverKey)|| store.get(serverKey)==null || store.get(serverKey).size()==0 ) {

			String root = PropertiesHelp.getApplicationConf("spring.cloud.zookeeper.discovery.root");
			fillAndWatchStore(serverKey, root);

		}
		if (!store.containsKey(serverKey) || store.get(serverKey)==null || store.get(serverKey).size()==0) {
			Log.logError(new Exception("找不到服务端"), serverKey);
			return null;
		}
		 
		// 负载均衡算法 // 平均分布
		String key =serverKey+"-"+method;
		int index =0;
		int size=store.get(serverKey).size();
		
		if(apiLog.containsKey(key))
		{
			index = Integer.valueOf(apiLog.get(key).split("-")[1]) ;
			
			if(index>=size-1)
			{
				index=0;
			}
			else
			{
				index++;
			}
		}		
		
		apiLog.put(key, store.get(serverKey).size()+"-"+index);					 
		
		return (ServerNode)store.get(serverKey).values().toArray()[index];

	}
	
	static ConcurrentHashMap<String,String> apiLog = new ConcurrentHashMap<String,String>();

	
	static Watcher _watch=null;
	
	static boolean isExpired=false;
	static Watcher getWatcher()
	{ 
		if(_watch!=null)
		{
			return _watch;
		}
		_watch= new Watcher() {

			@Override
			public void process(WatchedEvent arg0) {
				
				if (arg0.getType() == Event.EventType.NodeChildrenChanged) {
					String[] pathArr= arg0.getPath().split("/");
					String path =pathArr[pathArr.length-1];
					System.out.println("Watcher process path,NodeChildrenChanged,11111111111:"+path);
					store.remove(path);
				}
 
				
				if (arg0.getState() == KeeperState.Expired)
				{
					try {
						System.out.println("BaseZookeeper,111111111111:Expired");
						isExpired=true;
						_watch=null;
						System.out.println("BaseZookeeper,111111111111: conn");
						
						BaseZookeeper.Conn(connEvent);
												
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		return _watch;
	}
	
	
	public static IConnEvent  connEvent=new IConnEvent() {
		
		@Override
		public int onConn() {
			 
			System.out.println("Watch received event,333333");
			if(isExpired)
			{
				isExpired=false;				
				store.clear();
				
				return 2;
			}
			return 0;
		}
	};
 
	private static void fillAndWatchStore(String serverKey, String root) throws Exception {

		BaseZookeeper bzk = BaseZookeeper.Conn(connEvent);
		
		List<String> ls =  bzk.getChildren(root + "/" + serverKey,getWatcher());


		Map<String, ServerNode> snMap = new HashMap<String, ServerNode>();
		for (String item : ls) {

			String json = bzk.getData(root + "/" + serverKey + "/" + item);
			ObjectMapper objectMapper = new ObjectMapper();
			ServerNode sn = objectMapper.readValue(json, ServerNode.class);

			if (sn != null && sn.getAddress() != null) {
				snMap.put(sn.getId(), sn);
			}
		}
		store.put(serverKey, snMap);

	}

}
