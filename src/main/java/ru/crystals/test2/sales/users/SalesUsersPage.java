package ru.crystals.test2.sales.users;

import static ru.crystals.test2.utils.FlexMediator.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.test2.basic.AbstractPage;


public class SalesUsersPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_SHOW_USERS= "label=показать пользователей";
	
	
	public SalesUsersPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
	public SalesActiveUsersPage showUsers(){
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_SHOW_USERS);
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_SHOW_USERS);
		return new SalesActiveUsersPage(getDriver());
	}
	
}
