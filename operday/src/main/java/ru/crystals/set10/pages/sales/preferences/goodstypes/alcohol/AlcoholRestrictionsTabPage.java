package ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class AlcoholRestrictionsTabPage extends AlcoholPage {
	
	static final String LOCATOR_NEW_RESTRICTION_BUTTON = "label=Добавить новое ограничение";
	static final String LOCATOR_TABLE_RESTRICTIONS = "id:restrictionsCDG";
	
	public AlcoholRestrictionsTabPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public NewAlcoholRestrictionPage addNewRestriction(){
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_NEW_RESTRICTION_BUTTON);
		return new NewAlcoholRestrictionPage(getDriver());
		
	}
	
	public int getRestrictionsCount(){
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_NEW_RESTRICTION_BUTTON);
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_SALESSWF, LOCATOR_TABLE_RESTRICTIONS, "realDataLength"));
		 
	}
	
}
