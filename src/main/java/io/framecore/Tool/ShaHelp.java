package io.framecore.Tool;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class ShaHelp {

	  public static String getSHA256Str(String str){
	        MessageDigest messageDigest;
	        String encdeStr = "";
	        try {
	            messageDigest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
	            encdeStr = Hex.encodeHexString(hash);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        return encdeStr;
	    }
}
