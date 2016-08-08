package demo;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

public class LoginWithSenlenium {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
//		使用 senlenium 登录
		String url = "http://t.1.163.com/";
		System.setProperty("webdriver.chrome.driver", "E:\\testtools\\selenium\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(url);
		Thread.sleep(2000);
		WebElement eLoginLink = driver.findElement(By.className("m-toolbar-login-btn"));
		eLoginLink.click();
		Thread.sleep(3000);	
		WebElement temp = driver.findElement(By.xpath("//iframe[@frameborder='0']"));
		driver.switchTo().frame(temp);  //进入登录框iframe
		driver.findElement(By.xpath("//input[@class='j-inputtext dlemail']")).sendKeys("oneyuantest1@163.com");
		driver.findElement(By.xpath("//input[@class='j-inputtext dlpwd']")).sendKeys("163a163");
		driver.findElement(By.xpath("//a[@id='dologin']")).click();
		driver.switchTo().defaultContent();  //离开登录框iframe
		Thread.sleep(1000);
		//Get cookie from webDriver
		Set<Cookie> cookies = driver.manage().getCookies();
		
//		Get方法访问 http://t.1.163.com/, 获取token
		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = null;
		String otokenStr = null;
		HttpClientContext context = HttpClientContext.create();
		for (Cookie c : cookies) {
			cookie = new BasicClientCookie(c.getName(), c.getValue());
			cookie.setDomain(c.getDomain());
			cookie.setExpiryDate(c.getExpiry());
			cookie.setPath(c.getPath());
			cookieStore.addCookie(cookie);
		}
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet, context);
			HttpEntity entity = httpResponse.getEntity();
//			打印状态码（status code）
			System.out.println("Status Code: " + httpResponse.getStatusLine().getStatusCode());
			CookieStore responseCookieStore = context.getCookieStore();
			List<org.apache.http.cookie.Cookie> responseCookie = responseCookieStore.getCookies();
			//response的cookies,从中拿到OTOKEN
			System.out.println("Cookies from response.");
			for (org.apache.http.cookie.Cookie c : responseCookie) {
				System.out.println(c.toString());
				if (c.getName().equals("OTOKEN")) {
					otokenStr = c.getValue();
					System.out.println("OTOKEN: " + otokenStr);
				}
			}
//			Get方法请求/user/global.do
			url = url + "user/global.do?token=" + otokenStr + "&t=" + System.currentTimeMillis();
			System.out.println("url: " + url);
			httpGet = new HttpGet(url);
			httpResponse = httpClient.execute(httpGet);
			System.out.println("response of /user/global.do");
			System.out.println("Status Code: " + httpResponse.getStatusLine().getStatusCode());
			entity = httpResponse.getEntity();
			System.out.println("Response body:" + EntityUtils.toString(entity));

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
//				关闭response对象、httpclient对象
				httpResponse.close();
				httpClient.close();
				driver.quit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		
		
	}

}
