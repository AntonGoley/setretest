package ru.crystals.test2.sales.preferences.goodstypes.alcohol;

import static ru.crystals.test2.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.test2.basic.AbstractPage;


public class AlcoholTabsRestrictionsPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_NEW_RESTRICTION_BUTTON= "label=Добавить новое ограничение";
	
	
	public AlcoholTabsRestrictionsPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
	public AlcoholRestrictionPage addNewRestriction(){
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_NEW_RESTRICTION_BUTTON);
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_NEW_RESTRICTION_BUTTON);
		return new AlcoholRestrictionPage(getDriver());
		
	}
	
}
