package quibids;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.*;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

public class HttpRequestHandler {
	
	CloseableHttpClient httpClient = null;
	Logger logger = null;
	
	public HttpRequestHandler() {
		httpClient = createHttpClient();
	}
	
	/*
	 * ����httpclient�ķ����������RetryHandler
	 */
	public static CloseableHttpClient createHttpClient() {
	       HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
	            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
	                if (executionCount >= 5) {// ����Ѿ�������5�Σ��ͷ���
	                    return false;
	                }
	                if (exception instanceof NoHttpResponseException) {// ������������������ӣ���ô������
	                    return true;
	                }
	                if (exception instanceof SSLHandshakeException) {// ��Ҫ����SSL�����쳣
	                    return false;
	                }
	                if (exception instanceof InterruptedIOException) {// ��ʱ
	                    return false;
	                }
	                if (exception instanceof UnknownHostException) {// Ŀ����������ɴ�
	                    return false;
	                }
	                if (exception instanceof ConnectTimeoutException) {// ���ӱ��ܾ�
	                    return false;
	                }
	                if (exception instanceof SSLException) {// SSL�����쳣
	                    return false;
	                }

	                HttpClientContext clientContext = HttpClientContext
	                        .adapt(context);
	                HttpRequest request = clientContext.getRequest();
	                // ����������ݵȵģ����ٴγ���
	                if (!(request instanceof HttpEntityEnclosingRequest)) {
	                    return true;
	                }
	                return false;
	            }
	        };
	        
	        CloseableHttpClient httpClient = HttpClients.custom()
	                .setRetryHandler(httpRequestRetryHandler).build();

	        return httpClient;
        

	}

	/*
	 * ����httpGet����ķ���
	 * ������httpClient��url
	 * ���أ�httpResponse�ַ���
	 */
	public String getHttpGetResponseJSON(String url) {
		String jsonStr = null;
		int timeOut = Integer.parseInt(Util.readProperties("timeOut"));
		CloseableHttpResponse httpResponse = null;
		HttpGet httpGet = new HttpGet(url);
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .setConnectionRequestTimeout(timeOut)
                .build();
//		�Ƿ�ʹ�ô���
		if(Util.readProperties("useProxy").equals("true")) {
			HttpHost proxy = new HttpHost(Util.readProperties("host"), Integer.parseInt(Util.readProperties("port")), "http");
			config = RequestConfig.copy(config).setProxy(proxy).build();
		}
		httpGet.setConfig(config);
		httpGet.setHeader("User-Agent", 
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36");
		try {
			httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			jsonStr = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			System.out.println(Util.formattedTime() + " IOException happened, retry");
		} finally {
			try {
				if( httpResponse != null) {
					httpResponse.close();  //�ͷ�httpResponse����
				}
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
		int timeOut = Integer.parseInt(Util.readProperties("timeOut"));
		CloseableHttpResponse httpResponse = null;
		HttpPost httpPost = new HttpPost(url);
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .setConnectionRequestTimeout(timeOut)
                .build();
//		�Ƿ�ʹ�ô���
		if(Util.readProperties("useProxy").equals("true")) {
			HttpHost proxy = new HttpHost(Util.readProperties("host"), Integer.parseInt(Util.readProperties("port")), "http");
			config = RequestConfig.copy(config).setProxy(proxy).build();
		}
		httpPost.setConfig(config);
		httpPost.setHeader("User-Agent", 
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36");
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			httpResponse = httpClient.execute(httpPost);
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
//			System.out.println(Util.formattedTime() + " IOException happened, retry");
		} finally {
			try {
				if(httpResponse != null) {
					httpResponse.close();  //�ͷ�httpResponse����
				}
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
