package ru.crystals.test2.sales.users;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.test2.basic.AbstractPage;


public class SalesActiveUsersPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	
	public SalesActiveUsersPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
}

