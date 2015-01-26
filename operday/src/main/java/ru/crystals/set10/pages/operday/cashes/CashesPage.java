package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.operday.OperDayPage;

import static ru.crystals.set10.utils.FlexMediator.*;


public class  CashesPage extends OperDayPage{
	
	private static final String LOCATOR_TAB = "id:shiftsNavigator/label:Акты;className:Tab";
	
	public CashesPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public Km3Page openKmPage(){
		clickElement(getDriver(), ID_OPERDAYSWF, LOCATOR_TAB);
		return new Km3Page(getDriver());
	}
	
}
