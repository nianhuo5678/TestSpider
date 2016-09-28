package quibids;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.*;
import org.jsoup.nodes.Document;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.*;

public class HttpRequestHandler {
	
	CloseableHttpClient httpClient = null;
	Logger logger = null;
	
	public HttpRequestHandler() {
		httpClient = HttpClients.createDefault();
	}

	/*
	 * ����httpGet����ķ���
	 * ������httpClient��url
	 * ���أ�httpResponse�ַ���
	 */
	public String getHttpGetResponseJSON(String url) {
		String jsonStr = null;
		CloseableHttpResponse httpResponse = null;
		HttpGet httpGet = new HttpGet(url);
		try {
			httpResponse = this.httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			jsonStr = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpResponse.close();  //�ͷ�httpResponse����
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonStr;
	}
	
	/*
	 * ����httpPost����ķ���
	 * ������httpClient��url, parameters
	 * ���أ�httpResponse�ַ���
	 */
	public String getHttpPostResponseJSON(String url, ArrayList<NameValuePair> parameters) {
		String jsonStr = null;
		CloseableHttpResponse httpResponse = null;
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			httpResponse = this.httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			jsonStr = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpResponse.close();  //�ͷ�httpResponse����
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonStr;
	}
		
	public void closeHttpClient() {
		try {
			this.httpClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
