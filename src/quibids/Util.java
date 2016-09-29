package quibids;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.*;;
/*
 * ������
 */
public class Util {

	/*
	 * ����Logger�����������quibids.log�ļ�
	 */
	public static Logger getLogger() {
		Logger logger = null;
		FileHandler handler = null;
		logger = Logger.getLogger("quibids");
		try {
			handler = new FileHandler("quibids.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.addHandler(handler);
		return logger;
	}
	
	/*
	 * ��ȡ�����ļ��ķ���
	 */
	public static String readProperties(String name) {
		Properties properties;
		String value = null;
		try {
			FileReader reader = new FileReader("conf.properties");
			properties = new Properties();
			properties.load(reader);
			value = properties.getProperty(name);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("can not find file conf.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("can not load conf.properties");
		}
		return value;
	}
}
