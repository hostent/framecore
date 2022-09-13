package io.framecore.Tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * 这种是java内存系列化，无法跨语言使用，而且性能差
 *
 */
public class SerializableHelp {

	public static byte[] toByte(Object obj) {
		try {

			ByteArrayOutputStream baostream = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(baostream);
			outputStream.writeObject(obj);

			byte[] b = baostream.toByteArray();
			outputStream.close();
			baostream.close();
			return b;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T toObject(byte[] b) {
		try {
			
			ByteArrayInputStream baostream = new ByteArrayInputStream(b);
			ObjectInputStream objectInputStream = new ObjectInputStream(baostream);
			
			@SuppressWarnings("unchecked")
			T obj = (T) objectInputStream.readObject();
			return obj;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
