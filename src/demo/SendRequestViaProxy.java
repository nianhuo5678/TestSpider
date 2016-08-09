/*
 * ͨ��proxy��������
 * httpGet��������http://1.163.com��
 * ����proxy 127.0.0.1:8888��ͨ��proxy������ͨ��fiddler����
 * ���б�������Ҫ����fiddler�����н������ͨ��fiddler���۲졣���fiddlerû��ץ������˵�����ò��ɹ���
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
		//����ͨ��fiddler���ͣ�127.0.0.1:8888
		HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
		//������������ö������proxy��
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		//Get����
		HttpGet httpGet = new HttpGet("http://1.163.com");
		httpGet.setConfig(config);
		//Response
		CloseableHttpResponse response = httpClient.execute(proxy, httpGet);
		System.out.println("Response code: " + response.getStatusLine().getStatusCode());
	}

}
