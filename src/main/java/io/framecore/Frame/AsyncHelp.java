package io.framecore.Frame;

import java.util.concurrent.CompletableFuture;

import io.framecore.Saas.SaasHander;
import io.framecore.Saas.Site;
import io.framecore.Tool.IdGenerater;

public class AsyncHelp {
	
	public static void main(String[] args) throws InterruptedException {
		
		
		for (int j = 0; j < 10; j++) {
			
			int tag =j;
			
			SaasHander.set("ab");
			
			AsyncHelp.runAsync(()->
			{
				try {
					Thread.sleep(1*1000);
				} catch (InterruptedException e) {
					 
				}
				System.out.println("111:"+tag);
				
				int i=0;
				
				i = i/i;
				
				System.out.println("2222");
				 
			});
			
		}
		
		System.out.println("finish");
	
		Thread.sleep(50*1000);
	}
	
	
	public static void runAsyncSingle(Runnable runnable)
	{
		String callid=CallerContext.getCallerID();
				 
		
		CompletableFuture.supplyAsync(() -> {
			
			CallerContext.setCallerID(callid+"-"+IdGenerater.newId());
			runnable.run();
			
		    return "";
		    
		}).whenComplete((result, e) -> {
			
		    if(e!=null)
		    {
		    	Log.logError((Exception) e);
		    	
		    }
		    CallerContext.dispose();
		});
		

	}
	
	public static void runAsync(Runnable runnable)
	{
		String callid=CallerContext.getCallerID();
		
		Site site = SaasHander.currentHander();
				 
		
		CompletableFuture.supplyAsync(() -> {
			
			CallerContext.setCallerID(callid+"-"+IdGenerater.newId());
			try {
				SaasHander.set(site.getSiteTag());
				runnable.run();
			} catch (Exception e) {
				Log.logError((Exception) e);
			}
			
			 CallerContext.dispose();
						
		    return "";
		    
		});
		

	}

}
