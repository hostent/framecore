package io.framecore.Tool;

import java.security.MessageDigest;

public class Md5Help {


	public static String toMD5(String plainText) {
		try {
			// 生成实现指定摘要算法的 MessageDigest 对象。
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 使用指定的字节数组更新摘要。
			md.update(plainText.getBytes("utf-8"));
			// 通过执行诸如填充之类的最终操作完成哈希计算。
			byte b[] = md.digest();
			// 生成具体的md5密码到buf数组
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static  void main(String[] args){
		String pinObscure ="fdsds#sa9!939&^Jk573k=3n";
		String md5Pin=Md5Help.toMD5(new String("ASDASFESDFAS"));
		System.out.println(md5Pin);
		String md5 = Md5Help.toMD5(md5Pin+pinObscure);
		System.out.printf(md5);
	}

}
