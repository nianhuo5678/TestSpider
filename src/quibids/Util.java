package quibids;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
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
	
	/*
	 * 读取配置文件的方法
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
	
	/*
	 * 返回当前的时间，时间格式为HH:mm:ss，时区为GMT-5
	 */
	public static String formattedTime() {
		String time;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
		time = dateFormat.format(new Date());
		return time;
	}
	
	/*
	 * 读取auctions.txt，返回ArrayList<String> auctions. 如果没有找到文件，则创建一个
	 */
	public static ArrayList<String> readAuctionList() {
		ArrayList<String> auctions = new ArrayList<String>();
		BufferedReader reader = null;
		String auctionListPath = "auctions.txt";
		File file = new File(auctionListPath);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed to create file " + auctionListPath);
			}
		} else {
			try {
				reader = new BufferedReader(new FileReader(file));
				String tempStr = reader.readLine();
				while(tempStr != null) {
					auctions.add(tempStr);
					tempStr = reader.readLine();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(reader != null){
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return auctions;
	}
	
	/*
	 * 已追加方式把最新的auctionUrl写入auctions.txt
	 */
	public static void writeAuctionList(String auction) {
		String auctionListPath = "auctions.txt";
		FileWriter fileWriter = null;
		try {
			//append
			fileWriter = new FileWriter(auctionListPath, true);
			fileWriter.write(auction + "\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
