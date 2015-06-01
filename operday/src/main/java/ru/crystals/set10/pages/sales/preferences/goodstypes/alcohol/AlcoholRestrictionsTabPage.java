package ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol;

import static ru.crystals.set10.utils.FlexMediator.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage;
import ru.crystals.set10.utils.DisinsectorTools;


public class AlcoholRestrictionsTabPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_NEW_RESTRICTION_BUTTON = "label=Добавить новое ограничение";
	static final String LOCATOR_TABLE_RESTRICTIONS = "id:restrictionsCDG";
	
	// костыль, пока не поправят название кнопки назад на алк. ограничениях
	private static final String BUTTON_BACK = "id:backButton;label:К ограничениям";
	
	
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
	
	public SalesGoodsTypesAndPaymentsTabPage goBack(){
		clickElement(getDriver(), ID_SALESSWF, BUTTON_BACK);
		DisinsectorTools.delay(1000);
		return new SalesGoodsTypesAndPaymentsTabPage(getDriver());	
	}
	
}
