package ru.crystals.set10.pages.basic;


import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;


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
	
	
	public enum Set10ShopRoles{
		MARKETER (1),
		GOODS_MANAGER (2),
		SECTION_MANAGER (2),
		ADMINISTRATOR_CO (10);
		
		private final int dbId; 
		Set10ShopRoles(int dbId){
			this.dbId = dbId;
		}
		
		public int getDbId(){
			return this.dbId;
		}
		
	}
}
