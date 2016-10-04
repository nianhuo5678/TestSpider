package quibids;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Auction {

	private String auctionID;
	private String productTitle;
	private String valuePrice;
	private String transactionFree;
	private String returnPolicy;
	private String realBids;
	private String voucherBids;
	private String endTime;
	private String lockTime;
	private String status;
	SimpleDateFormat dateFormat;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLockTime() {
		return lockTime;
	}
	public void setLockTime(String lockTime) {
		this.lockTime = lockTime;
	}
	public String getRealBids() {
		return realBids;
	}
	public void setRealBids(String realBids) {
		this.realBids = realBids;
	}
	public String getVoucherBids() {
		return voucherBids;
	}
	public void setVoucherBids(String voucherBids) {
		this.voucherBids = voucherBids;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getAuctionID() {
		return auctionID;
	}
	public void setAuctionID(String auctionID) {
		this.auctionID = auctionID;
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public String getValuePrice() {
		return valuePrice;
	}
	public void setValuePrice(String valuePrice) {
		this.valuePrice = valuePrice;
	}
	public String getTransactionFree() {
		return transactionFree;
	}
	public void setTransactionFree(String transactionFree) {
		this.transactionFree = transactionFree;
	}
	public String getReturnPolicy() {
		return returnPolicy;
	}
	public void setReturnPolicy(String returnPolicy) {
		this.returnPolicy = returnPolicy;
	}
	
	public String getCurrentStatus() {
//		如果竞拍被lock，返回当前时间，否则返回null
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://www.quibids.com/en/auction-" + this.getAuctionID() );
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			String html = EntityUtils.toString(httpResponse.getEntity());
			if (html.contains("Locked")) {
				return "Locked";
			} else if (html.contains("Ended")) {
				return "Ended";
			} else {
				return "Bid Now";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("request time out");
		} finally {
			try {
				if (httpResponse != null)
					httpResponse.close();
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	public Auction() {
		super();
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
		this.setStatus("Bid Now");
	}
	
}
