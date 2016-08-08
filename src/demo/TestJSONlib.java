package demo;

import net.sf.json.JSONObject;

public class TestJSONlib {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String jsonStr = "{\"result\":{\"uid\":\"oneyuantest35@163.com\",\"isLogin\":1,\"hasMobile\":false,\"nickname\":\"o***5@163.com\",\"serverTime\":1470631686082,\"mobileMail\":\"\",\"isNTES\":1,\"cid\":20102513},\"mobileMail\":\"\",\"code\":0}";
		JSONObject jO = JSONObject.fromObject(jsonStr);
		int code = jO.getInt("code");
		int isLogin = jO.getJSONObject("result").getInt("isLogin");
		int serverTime = jO.getJSONObject("result").getInt("serverTime");
		System.out.println("code: " + code);
		System.out.println("isLogin: " + isLogin);
		System.out.println("serverTime: " + serverTime);
	}

}
