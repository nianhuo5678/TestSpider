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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.stream.IntStream;

import javax.xml.ws.Response;

public class Quibids {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Quibids qui = new Quibids();
		Auction auction = new Auction();
		ArrayList<Bidder> bidders = new ArrayList<Bidder>();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String auctionUrl;
//		auctionUrl = "/en/auction-768710656US-C1433-15-walmart-gift-card";
		int cats = 17; //17:gitf cart;  12:Vouchers & Limit Busters 
		for (int i = 0; i < 5; i++) {
			auctionUrl = qui.getAuctionUrl(cats);
			qui.getAuctionInfo(auction, httpClient, auctionUrl);
			qui.getBids(bidders, httpClient, auction.getAuctionID(), auction);
			qui.getWinnerInfo(auction, auctionUrl);
			qui.writeExcel(auction, bidders);
		}
		try {
			httpClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getAuctionUrl(int cats) {
		String auctionUrl = null;
		String html = null;
		CloseableHttpClient httpClientAuctionID = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		HttpPost httpPost = new HttpPost("http://www.quibids.com/ajax/spots.php");
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("a","h"));
		parameters.add(new BasicNameValuePair("type", "ending"));
		parameters.add(new BasicNameValuePair("tab", "0"));
		parameters.add(new BasicNameValuePair("cats[]", String.valueOf(cats)));
		parameters.add(new BasicNameValuePair("sort","endingsoon"));
		parameters.add(new BasicNameValuePair("p","1"));
		parameters.add(new BasicNameValuePair("v","g"));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			httpResponse = httpClientAuctionID.execute(httpPost);
			JSONArray auctions = JSONObject.fromObject(EntityUtils.toString(httpResponse.getEntity())).getJSONArray("Auctions");
			for (int i = 0; i < auctions.size(); i++) {
				html = auctions.getJSONObject(i).getString("html");
//				�����������ľ��İ���CDT����ʾ���Ļ�û���˳���
				if (html.contains("CDT")) {
					auctionUrl = "/en/auction-" + auctions.getJSONObject(i).getInt("id");
//					System.out.println("auctionUrl: " + auctionUrl);
					return auctionUrl;
				}
			}
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
				httpClientAuctionID.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return auctionUrl;
	}
	
	public void getAuctionInfo(Auction auction, CloseableHttpClient httpClient, String auctionUrl) {
		System.out.println("auction: http://www.quibids.com" +  auctionUrl);
		String auctionID, productTitle, valuePrice, transactionFree, returnPolicy, auctionStatus;
//		Auction auction = new Auction();
		HttpGet httpGet = new HttpGet("http://www.quibids.com" + auctionUrl);
		HttpPost httpPost = null;
		CloseableHttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			String html = EntityUtils.toString(httpEntity);
			Document doc = Jsoup.parse(html);
//			��ȡ���洢������Ϣ
			auctionID = doc.select("span[itemprop='title']").get(2).text().substring(10);
			auction.setAuctionID(auctionID);
			auction.setProductTitle(doc.getElementById("product_title").text());
			auction.setValuePrice(doc.getElementsByClass("float-right").get(0).text());
			for (int i = 0; i < doc.getElementById("product_description").getElementsByTag("p").size(); i++) {
				if (doc.getElementById("product_description").getElementsByTag("p").get(i).text().contains("Transaction Fee")) {
					auction.setTransactionFree(doc.getElementById("product_description").getElementsByTag("p").get(i).text().substring(17));
				}
				if (doc.getElementById("product_description").getElementsByTag("p").get(i).text().contains("Return Policy")) {
					auction.setReturnPolicy(doc.getElementById("product_description").getElementsByTag("p").get(i).text().substring(15));
				}
			}
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
	
	public void getWinnerInfo(Auction auction, String auctionUrl) {
		System.out.println("Start getWinnerInfo");
		Runtime rt = Runtime.getRuntime();  
        Process p = null;
		try {
			p = rt.exec("E:\\java\\phantomjs\\phantomjs.exe c:\\users\\simon\\git\\TestSpider\\src\\quibids\\eoa.js " + 
					"http://www.quibids.com" + auctionUrl);
//			p = rt.exec("E:\\testtools\\phantomjs\\phantomjs.exe C:\\Users\\NetEase\\git\\TestSpider\\src\\quibids\\eoa.js " + 
//			"http://www.quibids.com" + auctionUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        InputStream is = p.getInputStream();  
        BufferedReader br = new BufferedReader(new InputStreamReader(is));  
        StringBuffer sbf = new StringBuffer();  
        String tmp = "";  
        try {
//			while((tmp = br.readLine())!=null){  
//			    sbf.append(tmp);  
//			}
        	String realBids = br.readLine();
        	String voucherBids = br.readLine();
        	String endTime = br.readLine();
        	auction.setRealBids(realBids);
        	auction.setVoucherBids(voucherBids);
        	auction.setEndTime(endTime);
        	
//            System.out.println(realBids);
//            System.out.println(voucherBids);
//            System.out.println(endTime);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        System.out.println("End getWinnerInfo");
	}
	
	public void getBids(ArrayList<Bidder> bidders, CloseableHttpClient httpClient, String auctionID, Auction auction) {
		System.out.println("Start GetBids");
		JSONObject jO;
//		ArrayList<Bidder> bidders = new ArrayList<Bidder>();
		String[] achievements = null;
		String b = "70762479", w = "ys", m = "100", i;
		int maxID = 0;
		int latestBidID = 0;
		boolean existed = false;
		boolean locked = false;
		i = this.transferToI(auctionID);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String url = "http://www.quibids.com/ajax/u.php?b=" + b + 
				"&w=" + w +
				"&m=" + m +
				"&i=" + i +
				"&lb_id=" + auctionID +
				"&c=" + "jQuery" + "012345678901234567890" + "_" + System.currentTimeMillis();
		String profileUrl = null;
		HttpGet httpGet = new HttpGet(url);
		HttpGet profileGet = null;
		CloseableHttpResponse httpResponse = null;	
		CloseableHttpResponse profileResponse = null;
//		����ʱ��
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
		while (true) {
			
//			��ȡ�Ƿ�lock,lock��ʱ��
			if (!locked && bidders.size() > 0) {
				locked = this.auctionStatuc(auctionID, httpClient);
				System.out.println("Bid now");
				if (locked) {
					auction.setLockTime(dateFormat.format(new Date()));
					System.out.println("Locked at: " + dateFormat.format(new Date()));
				}
			}
			
//			ÿ�λ�����Ϣ���ʱ��
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				httpResponse = httpClient.execute(httpGet);
				HttpEntity entity = httpResponse.getEntity();
//				�ָ��ַ�������ȡ�� �� �� ��֮���json�ַ���
				String responseBody = EntityUtils.toString(entity).split("\\(")[1].split("\\)")[0];
				jO = JSONObject.fromObject(responseBody);
//				���Ľ�������whileѭ��
				if (responseBody.contains("Completed") || responseBody.equals("{\"a\":[]}")) {
					break;
				}
				if (responseBody.contains("bh")) {
//					���ļ۸���
					JSONArray bh = jO.getJSONObject("a").getJSONObject(auctionID).getJSONArray("bh");
//					System.out.println(responseBody);
					JSONObject bid = null;
					latestBidID = bh.getJSONObject(0).getInt("id");
//					������±���ID�������ID,�����и���
					if (latestBidID > maxID ) {
						if ( (latestBidID - maxID) <= 9) {
							for (int j = (latestBidID - maxID - 1); j >= 0; j--) {
								bid = (JSONObject) bh.getJSONObject(j);
								Bidder bidder = new Bidder();
//								���ҵ�ǰ�ɼ������û��Ƿ��ѻ�ȡ���û����ϣ����������Ӷ����и��ƣ������������ȡ
								for (Bidder b1 : bidders) {
									if (bid.getString("u").equals(b1.getUname())) {
										bidder.setJoinDay(b1.getJoinDay());
										bidder.setBiddingOn(b1.getBiddingOn());
										bidder.setLatestWin(b1.getLatestWin());
										bidder.setAchievements(b1.getAchievements());
										existed = true;
										break;
									} else {
										existed = false;
									}
								}
//								�����ǰ�ɼ��������û������б��У����������ȡ�û���ϸ��Ϣ
								if ( !existed) {
									profileUrl = "http://www.quibids.com/ajax/profiles.php?username=" + bid.getString("u") +
											"&auctionid=" + auctionID;
									profileGet = new HttpGet(profileUrl);
									profileResponse = httpClient.execute(profileGet);
									String profileStr = EntityUtils.toString(profileResponse.getEntity());
									JSONObject profileJSON = JSONObject.fromObject(profileStr);
									bidder.setJoinDay(profileJSON.getJSONObject("profile").getString("joined"));
									bidder.setBiddingOn(profileJSON.getJSONObject("profile").getString("biddingOn"));
//									�ж��û���latestWin�Ƿ�Ϊ�գ�����ǿգ�����never win��
									if (profileJSON.getJSONObject("profile").getString("win").equals("")) {
										bidder.setLatestWin("never win");
									} else {
										bidder.setLatestWin(profileJSON.getJSONObject("profile").getString("win").split(">")[1].split("<")[0]);
									}
									
//									�ж��û��Ƿ���achievement
									if (profileJSON.getJSONObject("profile").has("badges")) {
										JSONArray achievementsArray = profileJSON.getJSONObject("profile").getJSONArray("badges");
										achievements = new String[achievementsArray.size()];
										for (int ach = 0; ach < achievementsArray.size(); ach++) {
											achievements[ach] = achievementsArray.getJSONObject(ach).getString("title");
										}
										bidder.setAchievements(achievements);
									} else {
										bidder.setAchievements(null);
									}
									profileResponse.close();
								}
//								�洢�������� 1��Single Bid; 2:BidOMatic
								if (bid.getInt("t") == 1) {
									bidder.setType("Single Bid");
								} else {
									bidder.setType("BidOMatic");
								}
								bidder.setId(bid.getInt("id"));
								bidder.setPrice(bid.getString("a"));
								bidder.setUname(bid.getString("u"));
								bidder.setBidTime(dateFormat.format(new Date()));
								bidders.add(bidder);
//								System.out.println("id:" + bidder.getId() + 
//										", Uname:" + bidder.getUname() + 
//										", Price:" + bidder.getPrice() + 
//										", Joined Day:" + bidder.getJoinDay() +
//										", Bid time:" + bidder.getBidTime() + 
//										", Bid type:" + bidder.getType() + 
//										", Bidding on:" + bidder.getBiddingOn() +
//										", Latest win:" + bidder.getLatestWin());
//								for (int a2 = 0; a2 < achievements.length; a2++) {
//									System.out.println(" " + achievements[a2]);
//								}
							}
//							����maxID
							maxID = latestBidID;
						} else {
							System.out.println("�ɼ����ļ�����©��������Ʒ�ɼ��������");
							return;
						}
					}
				}
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
		System.out.println("End get bids. " + bidders.size() + " bidder is added");
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
	
	public boolean auctionStatuc (String auctionID, CloseableHttpClient httpClient) {
//		������ı�lock�����ص�ǰʱ�䣬���򷵻�null
		HttpGet httpGet = new HttpGet("http://www.quibids.com/en/auction-" + auctionID );
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			String html = EntityUtils.toString(httpResponse.getEntity());
			if (html.contains("Locked")) {
				return true;
			} else {
				return false;
			}
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
		return false;
	}
	
	public void writeExcel(Auction auction, ArrayList<Bidder> bidders) {
		
		System.out.println("Auction info:");
		System.out.println("AuctionID:" + auction.getAuctionID() +
				", Product title: " + auction.getProductTitle() +
				", Value Price: " + auction.getValuePrice() +
				", Transcation Free: " + auction.getTransactionFree() +
				", Return Policy:" + auction.getReturnPolicy() +
				", Real Bids:" + auction.getRealBids() +
				", Voucher Bids:" + auction.getVoucherBids() +
				", End Time:" + auction.getEndTime() + 
				", Winner:" + bidders.get(bidders.size()-1).getUname() +
				", Last Price:" + bidders.get(bidders.size()-1).getPrice());
		System.out.println("------------------------------");
		
//		System.out.println("Bidding history:");
//		for (Bidder b : bidders) {
//			System.out.println("Bidder:" + b.getId() + 
//					", Name: " + b.getUname() +
//					", Price:" + b.getPrice() +
//					", Bid time:" + b.getBidTime() +
//					", Member Since:" + b.getJoinDay() +
//					", Bidding On:" + b.getBiddingOn() +
//					", Latest Win:" + b.getLatestWin() +
//					", Bidding Type:" + b.getType() + 
//					", Achievements:" + Arrays.toString(b.getAchievements()));
//		}
		
		
		//�Ծ���IDΪcsv�ļ���
		String csvFile = "E:\\quibids\\" + auction.getAuctionID() + ".csv";
		try {
			FileWriter fw = new FileWriter(csvFile);
			fw.write("Auction info:\r\n");
//			��ӡauction info��ͷ
			String auctionHeader = "AuctionID,Product title,Value Price,Transcation Free,Return Policy,"
					+ "Real Bids,Voucher Bids,End Time,Winner,Last Price,Lock Time\r\n";
			fw.write(auctionHeader);
//			��ӡauction����
			String auctionInfo = auction.getAuctionID() + "," + 
								 auction.getProductTitle() + "," +
								 auction.getValuePrice() + "," +
								 auction.getTransactionFree() + "," +
								 auction.getReturnPolicy() + "," +
								 auction.getRealBids() + "," +
								 auction.getVoucherBids() + "," +
								 auction.getEndTime() + "," +
								 bidders.get(bidders.size()-1).getUname() + "," +
								 bidders.get(bidders.size()-1).getPrice() + "," + 
								 auction.getLockTime() + "\r\n";
			fw.write(auctionInfo);
//			��ӡBiddingHistory��ͷ������
			fw.write("Bidding history\r\n");
			String bidderHeader = "#,Name,Price,Bid time,Member Since,Bidding On,Latest Win,Bidding Type,Achievement#1,Achievement#2,Achievement#3,Achievement#4,Achievement#5\r\n";
			fw.write(bidderHeader);
			for (Bidder b : bidders) {
				String bidder = b.getId() + 
						"," + b.getUname() +
						"," + b.getPrice() +
						"," + b.getBidTime() +
						"," + b.getJoinDay() +
						"," + b.getBiddingOn() +
						"," + b.getLatestWin() +
						"," + b.getType();
				if (b.getAchievements() != null) {
					bidder = bidder + ";";
					for (int i = 0; i < b.getAchievements().length; i++ ) {
						bidder = bidder + b.getAchievements()[i].replaceAll(",", "") + ",";
					}
				}
				bidder = bidder + "\r\n";
				fw.write(bidder);
			}
//			�رն���
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

	}
}
