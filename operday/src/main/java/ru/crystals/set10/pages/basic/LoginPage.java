package ru.crystals.set10.pages.basic;


import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;

import ru.crystals.set10.pages.operday.OperDayPage;


public class LoginPage extends AbstractPage{
	
	static final String LOCATOR_LOGININPUT = "username"; 
	static final String LOCATOR_PASSWORDINPUT = "password"; 
	static final String LOCATOR_LOGINBUTTON= "loginButton";
	static final String LOGINPAGESWF = "RetailX";

	
	public LoginPage(WebDriver driver, String url) {
		super(driver);
		getDriver().get(url);
		log.info("Открыть страницу: " + url);
		isSWFReady();
	}
	
	public MainPage doLogin(String username, String password) {
		typeText(getDriver(), LOGINPAGESWF, LOCATOR_LOGININPUT, username);
		typeText(getDriver(), LOGINPAGESWF, LOCATOR_PASSWORDINPUT, password);
		clickElement(getDriver(), LOGINPAGESWF, LOCATOR_LOGINBUTTON);
		return new MainPage(getDriver());
	}
	
	/*
	 * Залогиниться и открыть опер день 
	 * 
	 */
	public OperDayPage openOperDay(String username, String password){
		return doLogin(username, password)
			.openOperDay();
	}
}
