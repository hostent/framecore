package io.framecore.Tool;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.framecore.Frame.Log;

public class AesHelp {
	
	/**
     * 加密
     * @param content 待加密内容
     * @param password  加密密钥
     * @return
     */
    public static String encrypt(String content, String password) {
        try {
           
        	SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
              
              
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, random);
            
            
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**解密
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decrypt(String content, String password) {
        try {
        	if(content==null || content.isEmpty())
        	{
        		return null;
        	}
        	if(password==null || password.isEmpty())
        	{
        		return null;
        	}
        	
        	SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
              
              
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, random);
            
          
            
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(parseHexStr2Byte(content));           
            return new String(result,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 10进制转十六进制
    public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;            
        }
        a = s.reverse().toString();
        return a;
    }
    
    // 二进制转十六进制
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    // 十六进制转二进制
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    

	public static String encryptBase64(String content, String psw) {

		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			String algorithmstr = "AES/ECB/PKCS5Padding";
			Cipher cipher = Cipher.getInstance(algorithmstr);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(psw.getBytes(), "AES"));

			byte[] b = cipher.doFinal(content.getBytes("utf-8"));
			
			return Base64.getEncoder().encodeToString(b);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decryptBase64(String encryptStr, String decryptKey) {
		try {
			byte[] encryptBytes = Base64.getDecoder().decode(encryptStr);

			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			String algorithmstr = "AES/ECB/PKCS5Padding";
			Cipher cipher = Cipher.getInstance(algorithmstr);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
			byte[] decryptBytes = cipher.doFinal(encryptBytes);
			return new String(decryptBytes);
		} catch (Exception e) {
			String msg = String.format("encryptStr:%s,decryptKey:%s", encryptStr,decryptKey);
			System.out.println(msg);
		}
		return null;
	}
	
	public static void main1(String[] args)
	{
		String dbcipher = "4PDEW2RQOMKXT6OA";
		String str = encryptBase64("123456789",dbcipher);
		
		System.out.println(str);
		

		
		
		
	}
	
	private static final String dbcipher_old = "deb4ad3f-136e-4a24-9a42-dlko8a1233bd";

	public static void main(String[] args) {
		
		
		String publishKey =  Md5Help.toMD5("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKX76DY6CKNOJcXgmkVd5ILD2IHGfE1hRvULi/L3UxZ+3mfhr3ItFPi84OSzNft+qHBoiAMPPnnx+7t7ZMcBg40CAwEAAQ==").substring(0, 16);
		String body = AesHelp.decryptBase64("5rnKo+tWh1HnvgMw9PCUvdueHc9hxXDuzT0LnBbJ/P6nUcQqsebCYL9CSelw3kSm", publishKey);
		
		
		System.out.println(body);
		
		String body1 = AesHelp.encryptBase64("nickname=测试加密数据&ts=1643037666613", publishKey);
		System.out.println(body1);
		//---------------
		//String pswold = "9AQADKKVJSF77T2XO4RDDSSOOC4MK2JFH5G1";
		
		String dbcipher = "ab123456-136e-4a24-9a42-dlko8a123388";//"deb4ad3f-136e-4a24-9a42-dlko8a1233bd";
		
		dbcipher=dbcipher_old;
		//String dbcipher = "1111ad3f-136e-4a24-3698-dlko8a4lokuh";
		String url = encrypt("jdbc:mysql://192.168.30.131:3306/pms?useSSL=false&serverTimezone=GMT",dbcipher);
		String userName=encrypt("root",dbcipher);
		String pwd = encrypt("123456",dbcipher);

    	
		System.out.println("url:"+url);
		System.out.println("username:"+userName);
		System.out.println("password:"+pwd);
		
		
		
		System.out.println("1111 url:"+decrypt("CAAF4C81E266631D9F12565D26DA5A2DC17A20D75E4F57964C688F9C8E7A1C4007202C597F4A0CF1717621B4BDC70C28D457A2A0E4C66C967E638837F32874842DDD832B6E187EE803DED8F195A6494F19021AA85F37416EBC03A31F733728F0",
				"deb4ad3f-136e-4a24-9a42-dlko8a1233bd"));
		
//		System.out.println("url:"+encrypt("jdbc:oracle:thin:@192.168.113.10:1521/orcl",psw));
//		System.out.println("username:"+encrypt("hbc_dev",psw));
//		System.out.println("password:"+encrypt("dev#123abc",psw));
//		System.out.println("url:"+decrypt(url,dbcipher));
//		System.out.println("username:"+decrypt("40FED0D66A7C791375498B200E4FB09E",pswold));
//		System.out.println("password:"+decrypt(pwd,dbcipher));
		
		//85939b44af20d206
//		String str = AesHelp.decryptBase64("UN9qJC7IX4Rkd0XUOnVgzNAdiWZ1S25tyya/JxW5VidraeSnGiMaG32LjN6STfCeE4ZsLQtC+EUFZsyo/BhIEYItDq4lBf5J5roKLIFFy6gpZfb+tmboh6t/S7fJYkR7N1joFCGXi1YbholmKZmu5ghA6f8AeTRcRmi//a9kyUE=", "1180a5dd02191752");
		
		//System.out.println(str);
	}

}