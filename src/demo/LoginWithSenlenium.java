package demo;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

public class LoginWithSenlenium {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
//		Login using senlenium 
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
		String cookiesStr = cookies.toString();
		System.out.println("cookies: " + cookiesStr);
		
//		Get方法访问 t.1.163.com, 获取token
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
//			打印状态码（status code）
			System.out.println("Status Code: " + httpResponse.getStatusLine().getStatusCode());

			EntityUtils.consume(entity);
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
		

		driver.quit();
		
	}

}
