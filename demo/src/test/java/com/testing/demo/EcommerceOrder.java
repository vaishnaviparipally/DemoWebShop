package com.testing.demo;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;

public class EcommerceOrder {
	
	/**
	 *  Login Module for DemoWebShop.
	 */
	@BeforeTest
	public void login() {
		WebDriver driver=new ChromeDriver();

		//Navigate to the URL and maximizing the browser
		driver.get("http://demowebshop.tricentis.com/");
		driver.manage().window().maximize();
		driver.findElement(By.linkText("Log in")).click();

		//Validate Welcome Message on Login Page
		String messageOnLoginPage= driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[4]/div[2]/div/div[1]/h1")).getText();
		Assert.assertEquals(messageOnLoginPage, "Welcome, Please Sign In!");

		//Enter Login Credentials and Login
		driver.findElement(By.id("Email")).sendKeys("atest@gmail.com");
		driver.findElement(By.id("Password")).sendKeys("123456");
		driver.findElement(By.xpath("//input[contains(@class,'button-1 login-button')]")).click();
	}

	@Test
	public void placingOrder() {
	}


	@AfterTest
	public void afterTest() {
	}

}
