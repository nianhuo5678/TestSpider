package quibids;

public class Bidder {

	private int id;
	private String uname;
	private String price;
	private String remainTime;//出价时剩余的时间
	private String joinDay;//竞拍人注册时间
	private String biddingOn;//正在竞拍的商品
	private String latestWin;//最近获得的商品
	private String bidTime;//竞拍的时间
	private String type;//竞拍的类型，1：Single Bid; 2:BidOMatic
	private String[] achievements;
	
	public String getBidTime() {
		return bidTime;
	}
	public void setBidTime(String bidTime) {
		this.bidTime = bidTime;
	}
	

	
	public String getRemainTime() {
		return remainTime;
	}
	public void setRemainTime(String remainTime) {
		this.remainTime = remainTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getJoinDay() {
		return joinDay;
	}
	public void setJoinDay(String joinDay) {
		this.joinDay = joinDay;
	}
	public String getBiddingOn() {
		return biddingOn;
	}
	public void setBiddingOn(String biddingOn) {
		this.biddingOn = biddingOn;
	}
	public String getLatestWin() {
		return latestWin;
	}
	public void setLatestWin(String latestWin) {
		this.latestWin = latestWin;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String[] getAchievements() {
		return achievements;
	}
	public void setAchievements(String[] achievements) {
		this.achievements = achievements;
	}
	
}
