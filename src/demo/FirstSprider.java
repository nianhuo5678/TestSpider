/*
 * 第一个应用http的例子。发送get请求，获取响应的状态码和body
 * 1.创建一个httpclient对象，类似打开一个浏览器
 * 2.创建httpGet对象，使用get方法请求
 * 3.创建httpResponse对象。执行get方法并赋值给httpResponse对象
 * 4.获取状态码和Response body
 * 5.释放资源
 */
package demo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class FirstSprider {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://1.163.com");	
//		HttpGet httpGet = new HttpGet("https://www.zhihu.com");
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
//			打印状态码（status code）
			System.out.println("Status Code: " + httpResponse.getStatusLine().getStatusCode());
//			打印响应体
//			System.out.println("Response body: " + EntityUtils.toString(entity));
//			管理entity的内容流
			EntityUtils.consume(entity);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
//				关闭response对象、httpclient对象
				httpResponse.close();
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

}
