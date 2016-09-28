package quibids;

import java.io.IOException;
import java.util.logging.*;;
/*
 * 工具类
 */
public class Util {

	/*
	 * 创建Logger并设置输出到quibids.log文件
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
