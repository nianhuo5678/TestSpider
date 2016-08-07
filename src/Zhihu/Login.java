package Zhihu;

import java.io.*;

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
import org.jsoup.select.Elements;

import net.sf.json.*;
import sun.misc.IOUtils;



public class Login {


	public static void main(String[] args) {


//		默认Cookie策略是CookieSpecs.DEFALT,在zhihu.com会出现不兼容状态，所以需要指定另外一种policy。
//		这里选择CookieSpecs.STANDARD，能消除运行时产生的warning

		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
//		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
		CloseableHttpResponse httpResponse = null;
//		Get方法访问http://www.zhihu.com/ 获得 _xfrf 字段
		HttpGet httpGet = null;
		HttpEntity httpEntity = null;
		String xsrf = null;
		try {
			//Get方法预登录，处理返回的html页面
			httpGet = new HttpGet("https://www.zhihu.com/");
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			System.out.println("Status code get method: " + httpResponse.getStatusLine().getStatusCode());
			String html = EntityUtils.toString(httpEntity);
			Document doc = Jsoup.parse(html);
			xsrf = doc.select("input[name='_xsrf']").attr("value");
			System.out.println("_xsrf: " + xsrf);
			
			//POST方法正式登陆
			HttpPost httpPost = new HttpPost("http://www.zhihu.com/login/phone_num");
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("_xsrf",xsrf));
			parameters.add(new BasicNameValuePair("password","1q2w3eABC"));
			parameters.add(new BasicNameValuePair("captcha_type","CN"));
			parameters.add(new BasicNameValuePair("remember_me","false"));
			parameters.add(new BasicNameValuePair("phone_num","13632304692"));
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			httpResponse = httpClient.execute(httpPost);
			System.out.println("Status code post method: " + httpResponse.getStatusLine().getStatusCode());
			String jsonStr = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			System.out.println("Response body:" + jsonStr);
			JSONObject jO = JSONObject.fromObject(jsonStr);
			String msg = jO.getString("msg");
			System.out.println("JSON msg: " + msg);
			
			//Get方法，打开我的主页
			httpGet = new HttpGet("https://www.zhihu.com/people/simon-huang-79/followees");
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			html = EntityUtils.toString(httpEntity, "UTF-8");
			doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByClass("zm-item-img-avatar");
			System.out.println("头像文件列表：");
			for (Element element : elements) {
				System.out.println(element.attr("src"));
				//下载头像图片
				String imgUrl = element.attr("src");
				httpGet = new HttpGet(imgUrl);
				httpResponse = httpClient.execute(httpGet);
				httpEntity = httpResponse.getEntity();
		        InputStream input = httpEntity.getContent();
		        OutputStream output = new FileOutputStream(new File("E:\\test\\" + imgUrl.substring(23) +".jpg"));
		        int inByte;
		        while((inByte = input.read()) != -1)
		             output.write(inByte);
		        input.close();
		        output.close();
			}
			
					
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
