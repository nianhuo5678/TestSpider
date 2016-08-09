/*
 * 通过proxy发送请求：
 * httpGet方法访问http://1.163.com。
 * 设置proxy 127.0.0.1:8888，通过proxy把请求通过fiddler发送
 * 运行本例子需要启动fiddler，运行结果可以通过fiddler来观察。如果fiddler没有抓到包，说明设置不成功。
 */
package demo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

public class SendRequestViaProxy {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub

		CloseableHttpClient httpClient = HttpClients.createDefault();
		//请求通过fiddler发送，127.0.0.1:8888
		HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
		//创建请求的配置对象，添加proxy。
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		//Get方法
		HttpGet httpGet = new HttpGet("http://1.163.com");
		httpGet.setConfig(config);
		//Response
		CloseableHttpResponse response = httpClient.execute(proxy, httpGet);
		System.out.println("Response code: " + response.getStatusLine().getStatusCode());
	}

}
