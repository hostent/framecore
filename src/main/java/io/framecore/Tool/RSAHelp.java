package io.framecore.Tool;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSAHelp {

	public static final String KEY_ALGORITHM = "RSA";
	private static final String PUBLIC_KEY = "RSAPublicKey9883923232345";
	private static final String PRIVATE_KEY = "RSAPrivateKey323456243412";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 53;

	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 64;

	/**
	 * 密钥长度，DH算法的默认密钥长度是1024 密钥长度必须是64的倍数，在512到65536位之间
	 */
	private static final int KEY_SIZE = 512;

	private static final int KEY_SIZE_long = 1024;
	private static final int MAX_ENCRYPT_BLOCK_long = 117;
	private static final int MAX_DECRYPT_BLOCK_long = 128;

	// 获得公钥字符串
	public static String getPublicKeyStr(Map<String, Object> keyMap) throws Exception {
		// 获得map中的公钥对象 转为key对象
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		// 编码返回字符串
		return encryptBASE64(key.getEncoded());
	}

	// 获得私钥字符串
	public static String getPrivateKeyStr(Map<String, Object> keyMap) throws Exception {
		// 获得map中的私钥对象 转为key对象
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		// 编码返回字符串
		return encryptBASE64(key.getEncoded());
	}

	// 获取公钥
	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = (new BASE64Decoder()).decodeBuffer(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	// 获取私钥
	public static PrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = (new BASE64Decoder()).decodeBuffer(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	// 解码返回byte
	public static byte[] decryptBASE64(String key) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(key);
	}

	// 编码返回字符串
	public static String encryptBASE64(byte[] key) throws Exception {
		return (new BASE64Encoder()).encodeBuffer(key);
	}

	// ***************************签名和验证*******************************
	public static byte[] sign(byte[] data, String privateKeyStr) throws Exception {
		PrivateKey priK = getPrivateKey(privateKeyStr);
		Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
		sig.initSign(priK);
		sig.update(data);
		return sig.sign();
	}

	public static boolean verify(byte[] data, byte[] sign, String publicKeyStr) throws Exception {
		PublicKey pubK = getPublicKey(publicKeyStr);
		Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
		sig.initVerify(pubK);
		sig.update(data);
		return sig.verify(sign);
	}

	// ************************加密解密**************************
	public static byte[] encrypt(byte[] plainText, String publicKeyStr) throws Exception {
		PublicKey publicKey = getPublicKey(publicKeyStr);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		int inputLen = plainText.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		int i = 0;
		byte[] cache;
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(plainText, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(plainText, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptText = out.toByteArray();
		out.close();
		return encryptText;
	}

	public static byte[] decrypt(byte[] encryptText, String privateKeyStr) throws Exception {
		PrivateKey privateKey = getPrivateKey(privateKeyStr);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int inputLen = encryptText.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptText, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptText, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] plainText = out.toByteArray();
		out.close();
		return plainText;
	}

	// ************************加密解密 Long**************************
	public static byte[] encryptLong(byte[] plainText, String publicKeyStr) throws Exception {
		PublicKey publicKey = getPublicKey(publicKeyStr);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		int inputLen = plainText.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		int i = 0;
		byte[] cache;
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK_long) {
				cache = cipher.doFinal(plainText, offSet, MAX_ENCRYPT_BLOCK_long);
			} else {
				cache = cipher.doFinal(plainText, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK_long;
		}
		byte[] encryptText = out.toByteArray();
		out.close();
		return encryptText;
	}

	public static byte[] decryptLong(byte[] encryptText, String privateKeyStr) throws Exception {
		PrivateKey privateKey = getPrivateKey(privateKeyStr);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int inputLen = encryptText.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK_long) {
				cache = cipher.doFinal(encryptText, offSet, MAX_DECRYPT_BLOCK_long);
			} else {
				cache = cipher.doFinal(encryptText, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK_long;
		}
		byte[] plainText = out.toByteArray();
		out.close();
		return plainText;
	}

	/**
	 * 初始化密钥对
	 * 
	 * @return Map 甲方密钥的Map
	 */
	public static Map<String, Object> initKey() throws Exception {
		// 实例化密钥生成器
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		// 初始化密钥生成器
		keyPairGenerator.initialize(KEY_SIZE);
		// 生成密钥对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		// 甲方公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		System.out.println("系数：" + publicKey.getModulus() + "  加密指数：" + publicKey.getPublicExponent());
		// 甲方私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		System.out.println("系数：" + privateKey.getModulus() + "解密指数：" + privateKey.getPrivateExponent());
		// 将密钥存储在map中
		Map<String, Object> keyMap = new HashMap<String, Object>();
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;

	}

	public static void main(String[] args) {
		Map<String, Object> keyMap;
		 
		String input = "ts=32323232";
		try {
			keyMap = initKey();

			System.out.println("-------------------");

			String publicKey =getPublicKeyStr(keyMap);

//			publicKey =PropertiesHelp.getAppConf("Rsa.publicKey");

			System.out.println("公钥------------------");
			System.out.println(publicKey);

			String privateKey = getPrivateKeyStr(keyMap);

//			privateKey = PropertiesHelp.getAppConf("Rsa.privateKey");
			System.out.println("私钥------------------");
			System.out.println(privateKey);
			/*input="HYCTLRRKLNX8";
			System.out.println("测试可行性-------------------");
			System.out.println("明文=======" + input);
			publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKg5q7kVZj6CYJ+xmfe5WtrTzHXbWHiYXUGSsq8SZhlYkF/s7Xq3iXAeFFvpOM7Qt/kTIJgPu6TEpHW6LC2k2FECAwEAAQ==";
			byte[] cipherTextByte;

			cipherTextByte = encrypt(input.getBytes(), publicKey);

//			System.out.println(new String(cipherTextByte));

			String cipherText = Base64.getEncoder().encodeToString(cipherTextByte);

			// 加密后的东西
			System.out.println("密文=======" + new String(cipherText));
//			cipherText = "YO3LSr5bqxR1rrvMP/KB7DZ7/VEyJNWMqIE6en/IW7PyWSuz32y3GeK90SCEyFgeQ3flRQFizBx8y87EvKVHyg==";
			cipherText = "UN9qJC7IX4Rkd0XUOnVgzNAdiWZ1S25tyya/JxW5VieGcB47w1KbWOrJsRxL+YBzrXzy22O08R8Y6/HgJWf6o5aZiGf9Yk7zXlddpsubn0Dcke33iasgYUhQf9lEwEn+NOINT/XgkK2pIjJWqKj6ptsLOx8fqfhDaU/UYxG9PjI=";
			privateKey = "MIIBVwIBADANBgkqhkiG9w0BAQEFAASCAUEwggE9AgEAAkEAqDmruRVmPoJgn7GZ97la2tPMddtYeJhdQZKyrxJmGViQX+ztereJcB4UW+k4ztC3+RMgmA+7pMSkdbosLaTYUQIDAQABAkEAl66IW/YjnrFIFjW5Mlh6x1y5unrwpqwbSgjP/HOGg8qUXTC0OdATT8W5nKTKL+1vQ9LxvGUM2sWVJpsMiq2tEQIhANxAk8nMLNvWR+1KTo5MlLnSJZ+/KOArnBcHR9wOmZ1zAiEAw4di6CD2lJQhfiLjVMv84mcsfLVh/bu5vAK/Gd/RgisCIQCI6a+02rHr+jj9/Zn1hQ9Sr5ppwifDqfg+rGz0EzNUlwIhAIMJq7ZnyPkgISYYmYPNxaexf3YAVTCn67zdllbv0cfLAiEAxvBRquMc0P6Dd5zJo8r/YMNoUJl+JtCBHQR6HS11kdA=";

			// 开始解密
			cipherTextByte = Base64.getDecoder().decode(cipherText);

			byte[] plainText = decrypt(cipherTextByte, privateKey);
			System.out.println("解密后明文===== " + new String(plainText));
//			System.out.println("验证签名-----------");

			String str = "被签名的内容";
			System.out.println("\n原文:" + str);

			byte[] signature = sign(str.getBytes(), privateKey);

			String signStr = Base64.getEncoder().encodeToString(signature);

			System.out.println("验证摘要:" + signStr);
			signature = Base64.getDecoder().decode(signStr);
			// String signStr = Base64.getEncoder().encodeToString(cipherTextByte);
			boolean status = verify(str.getBytes(), signature, publicKey);
			System.out.println("验证情况：" + status);*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
