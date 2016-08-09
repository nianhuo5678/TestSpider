package demo;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

public class LoginWithSenlenium {
	
	public Set<Cookie> getCookieFromWebdriver (String url)  {
//		Chrome driver
		System.setProperty("webdriver.chrome.driver", "E:\\java\\selenium\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		
//		Panthantomjs driver
//		DesiredCapabilities caps = new DesiredCapabilities();
//		caps.setCapability(PhantomJSDriverService
//				.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "E:\\java\\phantomjs\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
//		caps.setJavascriptEnabled(true);
//		caps.setCapability("takesScreenshot", true);
//		WebDriver driver = new  PhantomJSDriver(caps);
	
		driver.manage().window().maximize();
		driver.get(url);
		try {
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//从Selenium获得Cookies
		Set<Cookie> cookies = driver.manage().getCookies();
//		关闭webDriver
		driver.quit();
		return cookies;
	}

	public static void main(String[] args)  {
		// TODO Auto-generated method stub

		String url = "http://1.163.com/";
		LoginWithSenlenium lws = new LoginWithSenlenium();
		Set<Cookie> cookies = lws.getCookieFromWebdriver(url);
/*	
 * 	把webDriver取得的Cookies转换成为httpClient的Cookie。并取出OTOKEN
 *  clientCookie.setAttribute 的作用：
 * 	设置ClientCookie的域名属性。这个例子里面存在以下两种Domain的Cookie， '1.163.com' 和 '.163.com'。
 *  假如不设置ClientCookie.DOMAIN_ATTR，发送Get请求http://t.1.163.com/的时候只会带上cookieStore中domain是t.1.163.com的，导致缺少cookie失败。
 *  设置了ClientCookie.DOMAIN_ATTR, ".163.com"之后，发送请求的时候，会把所有Domain满足'.163.com'的cookie都带上。
 */
		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie clientCookie = null;
		String otokenStr = null;
		for (Cookie c : cookies) {
			if (c.getName().equals("OTOKEN")) {
				otokenStr = c.getValue();
				System.out.println("otokenStr:" + otokenStr);
			}
			clientCookie = new BasicClientCookie(c.getName(), c.getValue());
			clientCookie.setAttribute(ClientCookie.DOMAIN_ATTR, ".163.com");
			clientCookie.setDomain(c.getDomain());
			clientCookie.setExpiryDate(c.getExpiry());
			clientCookie.setPath(c.getPath());
//			System.out.println("clientCookie:" + clientCookie.toString());
			cookieStore.addCookie(clientCookie);	
		}

		
		
//		请求通过fiddler发送，127.0.0.1:8888
		HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
//		创建请求的配置对象，添加proxy。
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//		Get方法请求/user/global.do
		url = url + "user/global.do?token=" + otokenStr + "&t=" + System.currentTimeMillis();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = null;
		try {
//			通过proxy发送请求(Fiddler)
//			httpResponse = httpClient.execute(proxy, httpGet);
//			不通过proxy发送请求
			httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			entity = httpResponse.getEntity();
			System.out.println("\nuser/global.do 的Response body:" + EntityUtils.toString(entity));
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}

}
