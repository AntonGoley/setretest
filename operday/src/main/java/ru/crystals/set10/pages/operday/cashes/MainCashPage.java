package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.OperDayPage;


public class  MainCashPage extends OperDayPage{
	
	public MainCashPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
}
