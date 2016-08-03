package Zhihu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



public class Login {


	public static void main(String[] args) {

		//Get方法访问http://www.zhihu.com/ 获得 _xfrf 字段
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		HttpGet httpGet = new HttpGet("https://www.zhihu.com/");
		HttpEntity httpEntity = null;
		String xsrf = null;
		try {
			//GET
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			System.out.println("Status code get method: " + httpResponse.getStatusLine().getStatusCode());
			String html = EntityUtils.toString(httpEntity);
			Document doc = Jsoup.parse(html);
			xsrf = doc.select("input[name='_xsrf']").attr("value");
			System.out.println("_xsrf: " + xsrf);
			//POST
			HttpPost httpPost = new HttpPost("http://www.zhihu.com/login/phone_num");
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("_xsrf",xsrf));
			parameters.add(new BasicNameValuePair("password","1q2w3eABC"));
			parameters.add(new BasicNameValuePair("captcha_type","CN"));
			parameters.add(new BasicNameValuePair("remember_me","true"));
			parameters.add(new BasicNameValuePair("phone_num","13632304692"));
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			httpResponse = httpClient.execute(httpPost);
			System.out.println("Status code post method: " + httpResponse.getStatusLine().getStatusCode());
			System.out.println("Response body:" + EntityUtils.toString(httpResponse.getEntity()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpResponse.close();
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
