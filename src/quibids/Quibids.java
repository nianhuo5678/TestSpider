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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Quibids implements Runnable{
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(Util.readProperties("threads")));
		for(int i = 0; i < 10000; i++) {
			executor.execute(new Quibids());
		}
		executor.shutdown();
	}

	public synchronized String getAuctionUrl(HttpRequestHandler requestHandler, String cats) {
		String auctionUrl = null;
		String jsonStr = null;
		String html = null;
		String url = "http://www.quibids.com/ajax/spots.php";
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("a","h"));
		parameters.add(new BasicNameValuePair("type", "ending"));
		parameters.add(new BasicNameValuePair("tab", "0"));
		parameters.add(new BasicNameValuePair("cats[]", cats));
		parameters.add(new BasicNameValuePair("sort","endingsoon"));
		parameters.add(new BasicNameValuePair("p","1"));
		parameters.add(new BasicNameValuePair("v","g"));
//		当请求超时，重试10次
		int retry = 10;
		while(retry > 0) {
			jsonStr = requestHandler.getHttpPostResponseJSON(url, parameters);
			if(jsonStr == null) {
				System.out.println("Request getAuctionUrl time out, retry");
				retry--;
			}
			else {
				break;
			}
		}
		JSONArray auctions = JSONObject.fromObject(jsonStr).getJSONArray("Auctions");
//		读取auctionsList.txt
		ArrayList<String> auctionsList = Util.readAuctionList();
		if ( auctionsList == null ) {
			for (int i = 0; i < auctions.size(); i++) {
				html = auctions.getJSONObject(i).getString("html");
				if (html.contains("CDT") || html.contains("No Bids Yet")) {
					auctionUrl = "/en/auction-" + auctions.getJSONObject(i).getInt("id");
					Util.writeAuctionList(auctionUrl);
					System.out.println(Thread.currentThread().getName() + " http://www.quibids.com" + auctionUrl);
					return auctionUrl;
				}
			}
		} else {
			for (int i = 0; i < auctions.size(); i++) {
				html = auctions.getJSONObject(i).getString("html");
				int auctionID = auctions.getJSONObject(i).getInt("id");
				auctionUrl = "/en/auction-" + auctionID;
//				未开始并且不存在auctionsList的竞拍
				if ( (html.contains("CDT") || html.contains("No Bids Yet")) && !auctionsList.contains(auctionUrl) ) {
					Util.writeAuctionList(auctionUrl);
					System.out.println(Thread.currentThread().getName() + " http://www.quibids.com" + auctionUrl);
					return auctionUrl;
				}
			}
		}
		

		return auctionUrl;
	}
	
	public void getAuctionInfo(Auction auction, HttpRequestHandler requestHandler, String auctionUrl) {
		String auctionID;
		String url = "http://www.quibids.com" + auctionUrl;
		String jsonStr = null;
//		当请求超时，重试5次
		int retry = 10;
		while(retry > 0) {
			jsonStr = requestHandler.getHttpGetResponseJSON(url);
			if(jsonStr == null) {
				System.out.println("Request getAuctionInfo time out, retry");
				retry--;
			}
			else {
				break;
			}
		}
		Document doc = Jsoup.parse(jsonStr);
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
	}
	
	public void getWinnerInfo(Auction auction, String auctionUrl) {
		System.out.println(new Date() + " Start getWinnerInfo");
		Runtime rt = Runtime.getRuntime();  
        Process p = null;
        InputStream is = null;
        BufferedReader br = null;
        String firstLine = null;
//		当请求超时，重试8次
        int retry = 8;
        while(retry > 0) {
    		try {
//    			use proxy
    			if(Util.readProperties("eoaProxy").equals("true")) {
    				String execStr = Util.readProperties("phantomjsPath") + " --proxy=" + Util.readProperties("host") 
    				+ ":" + Util.readProperties("port")
    				+ " " + Util.readProperties("eoaPath") + " " +
    				"http://www.quibids.com" + auctionUrl;
    				p = rt.exec( execStr);
    			} else {
    				p = rt.exec( Util.readProperties("phantomjsPath") + " " + Util.readProperties("eoaPath") 
    				+ " " + "http://www.quibids.com" + auctionUrl);
    			}
                is = p.getInputStream();  
                br = new BufferedReader(new InputStreamReader(is));
//              error code = 408, request timeout
                firstLine = br.readLine();
                System.out.println("First line of eoa response " + firstLine);
                if (firstLine == null || firstLine.equals("") || firstLine.contains("Error")) {
                	retry--;
                	System.out.println("response get winner info:eoa.js invalid");
                } else if(Integer.parseInt(firstLine) == 408) {
                	retry--;
                	System.out.println("request get winnder info:eoa.js time out");
                } else {
                	break;
                }
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
        } 
        try {
        	String realBids = firstLine;
        	String voucherBids = br.readLine();
        	String endTime = br.readLine();
        	auction.setRealBids(realBids);
        	auction.setVoucherBids(voucherBids);
        	auction.setEndTime(endTime);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        System.out.println("End getWinnerInfo");
	}
	
	public boolean getBids(ArrayList<Bidder> bidders, HttpRequestHandler httpRequestHandler, String auctionID, Auction auction, HttpRequestHandler requestHandler) {
		System.out.println(Util.formattedTime() + " " + Thread.currentThread().getName() + " Start GetBids");
		JSONObject jO;
		String[] achievements = null;
		String b = "70762479", w = "ys", m = "100", i;
		int maxID = 0;
		int latestBidID = 0;
		boolean existed = false;
		String auctionStatus = "Bid Now";
		i = this.transferToI(auctionID);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String url = "http://www.quibids.com/ajax/u.php?b=" + b + 
				"&w=" + w +
				"&m=" + m +
				"&i=" + i +
				"&lb_id=" + auctionID +
				"&c=" + "jQuery" + "012345678901234567890" + "_" + System.currentTimeMillis();
		String profileUrl = null;
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
		int sleepTime = Integer.parseInt(Util.readProperties("interval"));
		while (true) {
//			call lockTime method to find out auction is locked or not
			this.lockTime(auction, requestHandler);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String jsonStr = httpRequestHandler.getHttpGetResponseJSON(url);
			if(jsonStr == null){
				continue;
			}
			if( !(jsonStr.contains("(") && jsonStr.contains(")")) ){
				continue;
			}
			String responseBody = jsonStr.split("\\(")[1].split("\\)")[0];
			if (!JSONUtils.mayBeJSON(responseBody)) {
				continue;
			}
			jO = JSONObject.fromObject(responseBody);
			if (responseBody.contains("Completed") || responseBody.equals("{\"a\":[]}")) {
				System.out.println("stop thread after auction is ended");
				System.out.println("End get bids. " + bidders.size() + " bidder is added");
				return true;
			}
			if (responseBody.contains("bh")) {
				JSONArray bh = jO.getJSONObject("a").getJSONObject(auctionID).getJSONArray("bh");
				JSONObject bid = null;
				latestBidID = bh.getJSONObject(0).getInt("id");
				if (latestBidID > maxID ) {
					if ( (latestBidID - maxID) <= 9) {
						for (int j = (latestBidID - maxID - 1); j >= 0; j--) {
							bid = (JSONObject) bh.getJSONObject(j);
							Bidder bidder = new Bidder();
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
					if ( !existed ) {
						profileUrl = "http://www.quibids.com/ajax/profiles.php?username=" + bid.getString("u") +
								"&auctionid=" + auctionID;
						String profileStr = httpRequestHandler.getHttpGetResponseJSON(profileUrl);
						if (!JSONUtils.mayBeJSON(profileStr)) {
							continue;
						}
						JSONObject profileJSON = JSONObject.fromObject(profileStr);
						bidder.setJoinDay(profileJSON.getJSONObject("profile").getString("joined"));
						bidder.setBiddingOn(profileJSON.getJSONObject("profile").getString("biddingOn"));
						if (profileJSON.getJSONObject("profile").getString("win").equals("")) {
							bidder.setLatestWin("never win");
						} else {
							bidder.setLatestWin(profileJSON.getJSONObject("profile").getString("win").split(">")[1].split("<")[0]);
						}
						
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
					}
//					1:Single Bid; 2:BidOMatic
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
					}
//					reset latestBidID
					maxID = latestBidID;
					} else {
						System.out.println("collect bidding history lost");
						return false;
					}
				}
			}
		}
	}
	
	public void lockTime(Auction auction, HttpRequestHandler requestHandler) {
//		
		if(auction.getLockTime() == null) {
			String lockTime = null;
			String i = this.transferToI(auction.getAuctionID());
			String url = "http://www.quibids.com/ajax/l.php?&w=ys&m=100&i=" + i 
					+ "&c=" + "jQuery" + "012345678901234567890" + "_" + System.currentTimeMillis()
					+ "&_=" + System.currentTimeMillis();
			String responseSrt = requestHandler.getHttpGetResponseJSON(url);
			if(responseSrt == null) {
				return;
			}
			String jsonStr = responseSrt.split("\\(")[1].split("\\)")[0];
			if(JSONUtils.mayBeJSON(jsonStr)) {
				JSONObject jO = JSONObject.fromObject(jsonStr);
				if(jO.getString("a").equals("[]")) {
					auction.setLockTime(null);
				} else if( jO.getJSONObject("a").getJSONObject(auction.getAuctionID()).getInt("l") == 1 ) {
//					l=1, auction is locked, set locked time 
					lockTime = Util.formattedTime();
					auction.setLockTime(lockTime);
					System.out.println("LockTime: " + auction.getLockTime());
				} else {
					auction.setLockTime(null);
				}
			} else {
				System.out.println("josn not valid: " + responseSrt);
				auction.setLockTime(null);
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
		
		
		String folder = Util.readProperties("outputFolder");
		String csvFile = folder + auction.getAuctionID() + ".csv";
		File dir = new File(folder);
//		create folder if not exist
		if( !dir.exists()) {			
			dir.mkdirs();
		}
		try {
			FileWriter fw = new FileWriter(csvFile);
			fw.write("Auction info:\r\n");
			String auctionHeader = "AuctionID,Product title,Value Price,Transcation Free,Return Policy,"
					+ "Real Bids,Voucher Bids,End Time,Winner,Last Price,Lock Time\r\n";
			fw.write(auctionHeader);
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
					bidder = bidder + ",";
					for (int i = 0; i < b.getAchievements().length; i++ ) {
						bidder = bidder + b.getAchievements()[i].replaceAll(",", "") + ",";
					}
				}
				bidder = bidder + "\r\n";
				fw.write(bidder);
			}
//			close file writer
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " started");
		// TODO Auto-generated method stub
		String auctionUrl;
		boolean getAllBids;
		HttpRequestHandler requestHandler = null;
		Quibids qui = new Quibids();
		String cats = Util.readProperties("cats");
		
		requestHandler = new HttpRequestHandler();
		Auction auction = new Auction();
		getAllBids = false;
		ArrayList<Bidder> bidders = new ArrayList<Bidder>();
		auctionUrl = qui.getAuctionUrl(requestHandler, cats);
		qui.getAuctionInfo(auction, requestHandler, auctionUrl);
		getAllBids = qui.getBids(bidders, requestHandler, auction.getAuctionID(), auction, requestHandler);
		if (!getAllBids) {
			System.out.println(Thread.currentThread().getName() + " is interrupted, because getAllBids = " + getAllBids);
			Thread.currentThread().interrupt();
		}
		qui.getWinnerInfo(auction, auctionUrl);
		qui.writeExcel(auction, bidders);
		requestHandler.closeHttpClient();
		System.out.println(Thread.currentThread().getName() + " ended");
	}
}
