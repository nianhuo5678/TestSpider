package quibids;

import java.io.IOException;
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
}
