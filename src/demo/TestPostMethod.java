package demo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class TestPostMethod {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("https://reg.163.com/logins.jsp");
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("username","oneyuantest1@163.com"));
		parameters.add(new BasicNameValuePair("password","163a163"));
		parameters.add(new BasicNameValuePair("product","mail163"));
		parameters.add(new BasicNameValuePair("url","http://m.1.163.com/"));
		parameters.add(new BasicNameValuePair("url2","http://m.1.163.com/login.do?url=http%3A%2F%2Fm.1.163.com%2F"));
		parameters.add(new BasicNameValuePair("savelogin",""));
		httpPost.setEntity(new UrlEncodedFormEntity(parameters));
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		System.out.println("Status code: " + httpResponse.getStatusLine().getStatusCode());
		System.out.println("Response body:" + EntityUtils.toString(httpResponse.getEntity()));
		httpResponse.close();
		httpClient.close();
	}

}