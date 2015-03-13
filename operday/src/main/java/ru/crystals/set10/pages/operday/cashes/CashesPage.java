package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CashesPage extends OperDayPage{
	
	public static final String LOCATOR_ACTS_TAB = "id:shiftsNavigator/label:Акты;className:Tab";
	public static final String LOCATOR_OPERDAY_TAB = "id:shiftsNavigator/label:Операционный день;className:Tab";
	
	
	public CashesPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public <T> T openTab(Class<T> tabPage, String tab){
		clickElement(getDriver(), ID_OPERDAYSWF, tab);
		return PageFactory.initElements(getDriver(), tabPage);
	}
	
}
