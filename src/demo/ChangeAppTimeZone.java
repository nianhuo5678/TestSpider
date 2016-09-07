package demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ChangeAppTimeZone {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
//	    dateFormat.setTimeZone(TimeZone.getTimeZone("CDT"));  
	    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		System.out.println(dateFormat.format(new Date()));
	}

}
