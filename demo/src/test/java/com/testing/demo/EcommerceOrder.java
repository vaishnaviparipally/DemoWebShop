package com.testing.demo;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;

public class EcommerceOrder {
	WebDriver driver=new ChromeDriver();
	/**
	 *  Login Module for DemoWebShop.
	 */
	@BeforeTest
	public void login() {


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
	/**
	 * Selecting a product and Adding to the cart
	 */

	@Test
	public void orderPlacement() {
		
		//Verify the UserID after login
		String verifyUserId= driver.findElement(By.xpath("//a[@class='account'][1]")).getText();
		Assert.assertEquals(verifyUserId, "atest@gmail.com");
		
		//Clearing Shopping Cart
		driver.findElement(By.linkText("Shopping cart")).click();
		if(driver.getPageSource().contains("Your Shopping Cart is empty!"))
		{
			System.out.println("Shopping Cart is Empty. Proceed to add your items");
		}
		else
		{
			List<WebElement> els = driver.findElements(By.name("removefromcart"));
			for ( WebElement el : els ) {
				if ( !el.isSelected() ) {
					el.click();
				}

			}
			driver.findElement(By.name("updatecart")).click();
		}
		
		//Navigating to Books tab
		driver.findElement(By.xpath("//a[contains(text(),'Books')]")).click();
		driver.findElement(By.xpath("//h2[@class='product-title']")).click();
		
		//Reading the price of the book
		String price=driver.findElement(By.xpath("//div[starts-with(@class,'product-price')]")).getText();
		System.out.println("The price of the book is " +price);
		
		//Increasing the quantity by 1 and adding to Cart
		driver.findElement(By.xpath("//input[contains(@class,'qty-input')]")).clear();
		driver.findElement(By.xpath("//input[contains(@class,'qty-input')]")).sendKeys("2");

		driver.findElement(By.xpath("//input[contains(@class,'button-1 add-to-cart-button')]")).click();

		//Validating the bar notification when the product is added
		int loopCount = 10;
		while(loopCount-- >0) {
			if( driver.findElement(By.xpath("//div[contains(@id,'bar-notification')]")).isDisplayed()) {
				boolean isProductAdded= driver.getPageSource().contains("The product has been added to your ");
				Assert.assertEquals(isProductAdded,true);
				System.out.println("Bar Notification Validated");
				break;
			}
		}

		// Selecting Checkout
		driver.findElement(By.linkText("Shopping cart")).click();
		driver.findElement(By.xpath("//input[contains(@name,'termsofservice')]")).click();
		driver.findElement(By.name("checkout")).click();

	

	}



	@AfterTest
	public void afterTest() {
	}

}
