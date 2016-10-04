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
	 * 创建httpclient的方法，并添加RetryHandler
	 */
	public static CloseableHttpClient createHttpClient() {
	       HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
	            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
	                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
	                    return false;
	                }
	                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
	                    return true;
	                }
	                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
	                    return false;
	                }
	                if (exception instanceof InterruptedIOException) {// 超时
	                    return false;
	                }
	                if (exception instanceof UnknownHostException) {// 目标服务器不可达
	                    return false;
	                }
	                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
	                    return false;
	                }
	                if (exception instanceof SSLException) {// SSL握手异常
	                    return false;
	                }

	                HttpClientContext clientContext = HttpClientContext
	                        .adapt(context);
	                HttpRequest request = clientContext.getRequest();
	                // 如果请求是幂等的，就再次尝试
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
	 * 发送httpGet请求的方法
	 * 参数：httpClient，url
	 * 返回：httpResponse字符串
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
//		是否使用代理
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
					httpResponse.close();  //释放httpResponse对象
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonStr;
	}
	
	/*
	 * 发送httpPost请求的方法
	 * 参数：httpClient，url, parameters
	 * 返回：httpResponse字符串
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
//		是否使用代理
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
					httpResponse.close();  //释放httpResponse对象
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
