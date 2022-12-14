package io.framecore.Web.Listener;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import io.framecore.Tool.PropertiesHelp;
import io.framecore.Tool.RuntimeHelp;
import io.framecore.Web.ApiBeanStore;
import io.framecore.Web.JcoreApiController;
import io.framecore.Zookeeper.BaseZookeeper;

public class StartListener implements ApplicationListener<ContextRefreshedEvent> {
	
	public static void main(String[] args) throws UnknownHostException, SocketException {
		
		
		
		 
	}
	
	


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	
    	try
    	{
    		
    		ApiBeanStore.FillList(event.getApplicationContext().getBeansWithAnnotation(JcoreApiController.class));
    		
    		
    		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    		
		
    		discovery(event.getApplicationContext());
    		
    		
    	}catch (Exception e) {
    		
    		System.out.println(e);
			 
		}
    	
    
    	 
        
    }




	private void discovery(ApplicationContext context)
	
			throws IOException, Exception, InterruptedException, KeeperException {
		
		String discoveryRoot = PropertiesHelp.getApplicationConf("spring.cloud.zookeeper.discovery.root");
		if(discoveryRoot!=null)
		{
			String appName = PropertiesHelp.getApplicationConf("spring.application.name");
			String discoveryHost = PropertiesHelp.getApplicationConf("spring.cloud.zookeeper.discovery.instance-host");
			
			
			
			//String ss = context.getEnvironment().getProperty("spring.application.name");
			
			//System.out.println(ss);
			
			String port =context.getEnvironment().getProperty("server.port");
			
			
			if(discoveryHost==null)
			{
				discoveryHost="127.0.0.1";
			}
			
			String data ="{\"name\":\"/"+appName.replace("/", "")+"\",\"id\":\""+RuntimeHelp.getAppId()+"\",\"address\":\""+discoveryHost+"\",\"port\":"+port+"}";
			
			BaseZookeeper baseZookeeper = BaseZookeeper.Conn();
    
			
			if(baseZookeeper.exists(discoveryRoot)==null)
			{
				baseZookeeper.createNode(discoveryRoot, "");
			}
			
			if(baseZookeeper.exists(discoveryRoot+appName)==null)
			{
				baseZookeeper.createNode(discoveryRoot+appName, "");
			}
			else
			{
				String portConf = PropertiesHelp.getApplicationConf("server.port");
				System.out.println("==========>?????????"+port+","+portConf);
				if(port.equals(portConf))//???????????????????????????????????????????????????????????????
				{
										
					List<String> proList = baseZookeeper.getChildren(discoveryRoot+appName);
					if(proList!=null)
					{
						for (String pro : proList) {
							baseZookeeper.deleteNode(discoveryRoot+appName+"/"+pro);
							System.out.println("==========>???????????????"+discoveryRoot+appName+"/"+pro);
						}
						if(baseZookeeper.exists(discoveryRoot+appName+"/"+"def")!=null)
						{		
							baseZookeeper.deleteNode(discoveryRoot+appName+"/"+"def");
							System.out.println("==========>???????????????def");
						}
					}					

				}
			}
			if(baseZookeeper.exists(discoveryRoot+appName+"/"+port)!=null)  //RuntimeHelp.getAppId()  //baseZookeeper.exists(discoveryRoot+appName+"/"+"def")!=null
			{
				baseZookeeper.deleteNode(discoveryRoot+appName+"/"+port);    //RuntimeHelp.getAppId()			 
				
			}
			baseZookeeper.createNode(discoveryRoot+appName+"/"+port, data);  //RuntimeHelp.getAppId()  //????????????????????????????????????
			//????????????
			//baseZookeeper.exists(discoveryRoot+appName+"/"+"def",true);
		        	
			
		}
	}

}
