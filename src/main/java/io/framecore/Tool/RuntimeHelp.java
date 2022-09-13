package io.framecore.Tool;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class RuntimeHelp {
	
	public static String getAppId()
	{
		return getLocalMac()+"-"+getProcessID();
	}
	
	
	public static int getProcessID() {  
		
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0])  
                .intValue();  
    } 
	
	public static String getLocalMac( )   {

		try
		{
			InetAddress ia = InetAddress.getLocalHost();
			//获取网卡，获取地址
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
	 
			StringBuffer sb = new StringBuffer("");
			for(int i=0; i<mac.length; i++) {
				 
				//字节转换为整数
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
			 
				if(str.length()==1) {
					sb.append("0"+str);
				}else {
					sb.append(str);
				}
			}
			return sb.toString().toUpperCase();
		}catch (Exception e) {
			return null;
		}
		 
	}
 
  

}
