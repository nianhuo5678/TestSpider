package quibids;

public class Bidder {

	private int id;
	private String uname;
	private String price;
	private String remainTime;//����ʱʣ���ʱ��
	private String joinDay;//������ע��ʱ��
	private String biddingOn;//���ھ��ĵ���Ʒ
	private String latestWin;//�����õ���Ʒ
	private String bidTime;//���ĵ�ʱ��
	private String type;//���ĵ����ͣ�1��Single Bid; 2:BidOMatic
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
