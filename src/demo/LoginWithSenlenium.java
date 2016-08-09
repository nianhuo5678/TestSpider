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
			driver.switchTo().frame(temp);  //�����¼��iframe
			driver.findElement(By.xpath("//input[@class='j-inputtext dlemail']")).sendKeys("oneyuantest1@163.com");
			driver.findElement(By.xpath("//input[@class='j-inputtext dlpwd']")).sendKeys("163a163");
			driver.findElement(By.xpath("//a[@id='dologin']")).click();
			driver.switchTo().defaultContent();  //�뿪��¼��iframe
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//��Selenium���Cookies
		Set<Cookie> cookies = driver.manage().getCookies();
//		�ر�webDriver
		driver.quit();
		return cookies;
	}

	public static void main(String[] args)  {
		// TODO Auto-generated method stub

		String url = "http://1.163.com/";
		LoginWithSenlenium lws = new LoginWithSenlenium();
		Set<Cookie> cookies = lws.getCookieFromWebdriver(url);
/*	
 * 	��webDriverȡ�õ�Cookiesת����ΪhttpClient��Cookie����ȡ��OTOKEN
 *  clientCookie.setAttribute �����ã�
 * 	����ClientCookie���������ԡ�����������������������Domain��Cookie�� '1.163.com' �� '.163.com'��
 *  ���粻����ClientCookie.DOMAIN_ATTR������Get����http://t.1.163.com/��ʱ��ֻ�����cookieStore��domain��t.1.163.com�ģ�����ȱ��cookieʧ�ܡ�
 *  ������ClientCookie.DOMAIN_ATTR, ".163.com"֮�󣬷��������ʱ�򣬻������Domain����'.163.com'��cookie�����ϡ�
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

		
		
//		����ͨ��fiddler���ͣ�127.0.0.1:8888
		HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
//		������������ö������proxy��
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//		Get��������/user/global.do
		url = url + "user/global.do?token=" + otokenStr + "&t=" + System.currentTimeMillis();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = null;
		try {
//			ͨ��proxy��������(Fiddler)
//			httpResponse = httpClient.execute(proxy, httpGet);
//			��ͨ��proxy��������
			httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			entity = httpResponse.getEntity();
			System.out.println("\nuser/global.do ��Response body:" + EntityUtils.toString(entity));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
//				�ر�response����httpclient����
				httpResponse.close();
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}

}
