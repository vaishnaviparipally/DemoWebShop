package com.testing.demo;

import org.testng.annotations.Test;

import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterTest;



public class EcommerceOrder {

	WebDriver driver=new ChromeDriver();
	Properties prop = new Properties();

	private static final String PROPERTIES_FILE = "config.properties";

	/**
	 * Load Properties file from resources
	 */
	private void loadProperties() {
		try (InputStream input = EcommerceOrder.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {

			if (input == null) {
				System.out.println("Cannot find "+ PROPERTIES_FILE);
				return;
			}

			//load a properties file from class path, inside static method
			prop.load(input);

		} catch (IOException e) {
			System.out.println("Exception at Reading "+ PROPERTIES_FILE);
			e.printStackTrace();
		}
	}


	/**
	 *  Login Module for DemoWebShop.
	 */
	@BeforeTest
	public void login() {

		loadProperties();

		//Navigate to the URL and maximizing the browser
		driver.get(prop.getProperty("demo.url"));
		driver.manage().window().maximize();
		driver.findElement(By.linkText("Log in")).click();

		//Validate Welcome Message on Login Page
		String messageOnLoginPage= driver.findElement(By.xpath(prop.getProperty("demo.welcomemsg.xpath"))).getText();
		Assert.assertEquals(messageOnLoginPage, "Welcome, Please Sign In!");

		//Enter Login Credentials and Login
		driver.findElement(By.id("Email")).sendKeys(prop.getProperty("demo.email"));
		driver.findElement(By.id("Password")).sendKeys(prop.getProperty("demo.passwd"));
		driver.findElement(By.xpath(prop.getProperty("demo.login.xpath"))).click();
		System.out.println("Login Successfull!");
	}

	/**
	 * Clear Shopping Cart if not empty
	 */
	private void clearShoppingCart() {
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
			System.out.println("Shopping Cart Cleared Successfully!");
		}
	}
	
	/**
	 * Add Book to Cart
	 */
	private void addBookToCart() {
		//Navigating to Books tab
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.books.xpath"))).click();
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.book.xpath"))).click();

		//Reading the price of the book
		String price=driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.bookprice.xpath"))).getText();
		System.out.println("The price of the book is " +price);

		//Increasing the quantity by 1 and adding to Cart
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.quantity.xpath"))).clear();
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.quantity.xpath"))).sendKeys("2");

		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.addtocart.xpath"))).click();
		System.out.println("Books Added to Cart Successfully!");
	}

	/**
	 * Fill Billing Address Details
	 * @throws IOException
	 */
	private void fillAddressDetails() throws IOException {
		//Selecting New Address
		Select address = new Select(driver.findElement(By.id("billing-address-select")));
		address.selectByVisibleText("New Address");


		Select country = new Select(driver.findElement(By.id("BillingNewAddress_CountryId")));
		country.selectByVisibleText("India");
		//Reading the Billing address Data from Excel
		try {
			readDataFromExcel();
		} catch (URISyntaxException e) {
			System.out.println("Exception while Reading Data From Excel");
			e.printStackTrace();
		}

		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.billingaddress_continue.xpath"))).click();

		System.out.println("Billing Details Filled Successfully!");
		threadWait();
	}

	/**
	 * Update Shipping Details and select mode of Shipping
	 */
	private void selectShippingDetailsAndMethod() {
		//Select the Shipping address 
		Select shipping_Address = new Select(driver.findElement(By.id("shipping-address-select")));
		shipping_Address.selectByVisibleText("atest dummy, Dilshuknagar, Hyderabad Z500000, India");
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.shipping_continue.xpath"))).click();

		threadWait();

		//Select Shipping Method 	
		//input[contains(@class,'button-1 new-address-next-step-button')]
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.shippingmethod.xpath"))).click();
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.shippingmethod_continue.xpath"))).click();

		System.out.println("Shipping Details and Method Added Successfully!");
		threadWait();
	}

	/**
	 * Selecting and Validating Payment Method
	 */
	private void selectAndValidatePaymentMethod() {

		//Select Payment Method
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.paymentmethod.xpath"))).click();
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.paymentmethod_continue.xpath"))).click();

		threadWait();

		//Validating Payment by COD message
		boolean paymentMessage=driver.getPageSource().contains("You will pay by COD");
		Assert.assertEquals(paymentMessage, true);
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.cod_continue.xpath"))).click();

		System.out.println("Select and Validated Payment Method Successfully!");
		
		threadWait();
	}
	
	/**
	 * Selecting a product and Adding to the cart
	 * @throws IOException 
	 */
	@Test
	public void orderPlacement() throws IOException {

		//Verify the UserID after login
		String verifyUserId= driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.userid.xpath"))).getText();
		Assert.assertEquals(verifyUserId, prop.getProperty("demo.email"));

		//Clearing Shopping Cart	
		clearShoppingCart();

		//Adding Book to Cart
		addBookToCart();

		//Validating the bar notification when the product is added
		int loopCount = 10;
		while(loopCount-- >0) {
			if( driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.barnotification.xpath"))).isDisplayed()) {
				boolean isProductAdded= driver.getPageSource().contains("The product has been added to your ");
				Assert.assertEquals(isProductAdded,true);
				System.out.println("Bar Notification Validated");
				break;
			}
		}

		// Selecting Checkout
		driver.findElement(By.linkText("Shopping cart")).click();
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.termsofservice.xpath"))).click();
		driver.findElement(By.name("checkout")).click();


		// Add Address details
		fillAddressDetails();
		
		// Shipping Details and Mode of shipping 
		selectShippingDetailsAndMethod();
		
		// Selecting and Validating Payment Method
		selectAndValidatePaymentMethod();
		
		//Confirm the order
		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.confirm.xpath"))).click();
		System.out.println("Order Placed Successfully!");
		
		threadWait();

	}

	/**
	 * Thread Wait for each action on Website
	 * @throws IOException 
	 */

	private void threadWait() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.out.println("Exception at Thread Sleep");
			e.printStackTrace();
		}
	}

	/**
	 * This method reads Excel File with Billing Address details
	 * @throws URISyntaxException 
	 */

	private File getFileFromResource(String fileName) throws URISyntaxException{

		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("Please check " + fileName + " in resource folder");
		} else {
			return new File(resource.toURI());
		}

	}

	/**
	 * Core Logic to read Excel sheets
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void readDataFromExcel() throws IOException, URISyntaxException {

		FileInputStream file=new FileInputStream(getFileFromResource("Address_Data.xlsx"));
		XSSFWorkbook workbook=new XSSFWorkbook(file);
		XSSFSheet sheet=workbook.getSheet("Sheet1");

		String data0=sheet.getRow(1).getCell(0).getStringCellValue(); 
		driver.findElement(By.id("BillingNewAddress_City")).sendKeys(data0);

		String data1=sheet.getRow(1).getCell(1).getStringCellValue(); 
		driver.findElement(By.id("BillingNewAddress_Address1")).sendKeys(data1);

		String data2=sheet.getRow(1).getCell(2).getStringCellValue(); 
		driver.findElement(By.id("BillingNewAddress_ZipPostalCode")).sendKeys(data2);

		String data3=sheet.getRow(1).getCell(3).getStringCellValue(); 
		driver.findElement(By.id("BillingNewAddress_PhoneNumber")).sendKeys(data3);

		workbook.close();

	}
	/**
	 * This method confirms order placement and Logs out of website
	 */

	@AfterTest
	public void afterTest() {

		//Validate the Order confirmation
		boolean confirmationMessage=driver.getPageSource().contains("Your order has been successfully processed!");
		Assert.assertEquals(confirmationMessage, true);

		//Print Order Number
		String orderNum=driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.ordernumber.xpath"))).getText();
		System.out.println(" " + orderNum);

		driver.findElement(By.xpath(prop.getProperty("demo.orderplacement.ordernumber_continue.xpath"))).click();
		//Logout from Application

		driver.findElement(By.linkText("Log out")).click();
		driver.close();


	}

}
