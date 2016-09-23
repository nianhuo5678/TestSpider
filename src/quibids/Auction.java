package quibids;

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
	
	
}
