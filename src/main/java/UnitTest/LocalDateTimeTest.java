package UnitTest;

import java.util.UUID;

import io.framecore.Tool.GoogleAuthenticator;
import io.framecore.redis.RedisLock;

public class LocalDateTimeTest {
	
	public void Test(String key)
	{
		 
		 
		 
	}
	
	
	class MyThread extends Thread {  
		public MyThread(String key) {
			_key =  key;
		}  
		String _key;
	    public void run() {  
	    	LocalDateTimeTest sync = new LocalDateTimeTest();  
	        sync.Test(_key);  
	    }  
	}
	
	public static void main(String[] args) {
		String secr = GoogleAuthenticator.generateSecretKey();
		String qrcode= GoogleAuthenticator.getQRBarcode("500cp",secr);
		System.out.println(qrcode);

		/*
		LocalDateTimeTest x = new LocalDateTimeTest();
		
		Thread t1 = x.new MyThread("1212");
		t1.start();
		
		Thread t2 = x.new MyThread("1212");
		t2.start();
		
		Thread t3 = x.new MyThread("3434");
		t3.start();*/
		
		
	}

}
