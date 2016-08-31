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

import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

public class Quibids {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Quibids qui = new Quibids();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String auctionUrl;
		auctionUrl = "/en/auction-939458876US-C1534-50-walmart-gift-card";
		qui.getAuctionInfo(httpClient, auctionUrl);
//		qui.getWinnerInfo(httpClient);
		

		
	}

	
	public void getAuctionInfo(CloseableHttpClient httpClient, String auctionUrl) {
		
		String auctionID, productTitle, valuePrice, transactionFree, returnPolicy, auctionStatus;
//		HttpHost proxy = new HttpHost("192.168.1.103", 8787, "http");
//		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		HttpGet httpGet = new HttpGet("http://www.quibids.com" + auctionUrl);
		HttpPost httpPost = null;
//		httpGet.setConfig(config);
		CloseableHttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			String html = EntityUtils.toString(httpEntity);
			Document doc = Jsoup.parse(html);
			System.out.println(auctionUrl + ":" + httpResponse.getStatusLine().getStatusCode());
//			get auction info
			auctionID = doc.select("span[itemprop='title']").get(2).text().substring(10);
			productTitle = doc.getElementById("product_title").text();
			valuePrice = doc.getElementsByClass("float-right").get(0).text();
			transactionFree = doc.getElementById("product_description").getElementsByTag("p").get(1).text().substring(17);
			returnPolicy = doc.getElementById("product_description").getElementsByTag("p").get(2).text().substring(15);
			auctionStatus = doc.getElementById("auction-left").getElementsByTag("a").get(0).text();
//			call getBids method to get every bids
			this.getBids(httpClient, auctionID);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpResponse.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getWinnerInfo(CloseableHttpClient httpClient) {
		HttpPost httpPost = new HttpPost("http://www.quibids.com/ajax/eoa.php");
		CloseableHttpResponse httpResponse = null;
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("id","sgunVY"));
		parameters.add(new BasicNameValuePair("cs","1cb4ca1f0938cdd95554"));
		parameters.add(new BasicNameValuePair("a","34"));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			httpResponse = httpClient.execute(httpPost);
			System.out.println("Status code: " + httpResponse.getStatusLine().getStatusCode());
			System.out.println("Response body:" + EntityUtils.toString(httpResponse.getEntity()));
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
				httpResponse.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	

	}
	
	public void getBids(CloseableHttpClient httpClient, String auctionID) {
		JSONObject jO;
		String b = "70306478", w = "ys", m = "100", i;
		i = this.transferToI(auctionID);
		String url = "http://www.quibids.com/ajax/u.php?b=" + b + 
				"&w=" + w +
				"&m=" + m +
				"&i=" + i +
				"&lb_id=" + auctionID +
				"&c=" + "jQuery" + "012345678901234567890" + "_" + System.currentTimeMillis();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			String responseBody = EntityUtils.toString(entity);
//			使用正则表达式提取出 （ 和 ）包起来的JSON字符串
			System.out.println(responseBody);
			jO = JSONObject.fromObject(responseBody);
			String lb = jO.getString("lb");
			System.out.println("lb:" + lb);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpResponse.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String transferToI(String lb_id) {
		String i = lb_id;
		for (int j = 10; j <=35; j++) {
			i = i.replace("" + j, "" + (char)(j + 55));
		}
		for (int k = 40; k <=65; k++) {
			i = i.replace("" + k, "" + (char)(k + 57));
		}
		return i;
	}
	
	
}
