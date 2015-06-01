package ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;

/*
 * Типы товаров и оплат: "Крепкий алкоголь". Страница добавления нового алкогольного ограничения
 */
public class NewAlcoholRestrictionPage extends SalesPage{
	
	static final String LOCATOR_BACK_TO_RESTRICTIONS_TAB = "label=К ограничениям";
	static final String LOCATOR_RESTRICTION_NAME = "restrictionNameField";
	// Период действия
	static final String LOCATOR_CHECKBOX_ISALLPERIOD = "isAllPeriod";
	static final String LOCATOR_INPUT_DATEPERIOD= "name=dateRange";
	// Действует весь день
	static final String LOCATOR_CHECKBOX_ISALLDAY = "isAllDay";
	static final String LOCATOR_INPUT_FROMHOUR = "id:forInput/id:hourText";
	static final String LOCATOR_INPUT_FROMMINUTE  = "id:forInput/id:minuteText";
	static final String LOCATOR_INPUT_UNTILHOUR = "id:untilInput/id:hourText";
	static final String LOCATOR_INPUT_UNTILMINUTE = "id:untilInput/id:minuteText";
	// Процент содержания алкоголя
	static final String LOCATOR_CHECKBOX_ISANYPRESENT = "isAnyPersent";
	static final String LOCATOR_INPUT_ALKOVOL= "alcoVolInput";
	// Минимальная цена за литр
	static final String LOCATOR_INPUT_ALKOMINPRICE = "alcoMinPriceInput";
	
	// Центрум: действует во всех магазинах
	static final String LOCATOR_CHECKBOX_SPREAD_ALLSHOPS = "id:isAllShops";
	
	
	public NewAlcoholRestrictionPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
	public NewAlcoholRestrictionPage setRestrictionName(String restrictionName){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_RESTRICTION_NAME, restrictionName);
		return this;
	}
	
	public NewAlcoholRestrictionPage setCheckBoxValue(String checkBox, boolean value) {
		checkBoxValue(getDriver(), ID_SALESSWF, checkBox, value);
		return this;
	}
	
	/*
	* формат даты: 08.07.14 (00:00) — 15.07.14 (23:59)
	*/
	public NewAlcoholRestrictionPage setDate(String dateRange) {
		setCheckBoxValue(LOCATOR_CHECKBOX_ISALLPERIOD, false);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_DATEPERIOD, dateRange);
		return this;
	}
	
	public NewAlcoholRestrictionPage setTime(String[] from, String[] until) {
		setCheckBoxValue(LOCATOR_CHECKBOX_ISALLDAY, false);
		// действует с
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_FROMHOUR, from[0]);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_FROMMINUTE, from[1]);
		//действует по
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_UNTILHOUR, until[0]);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_INPUT_UNTILMINUTE, until[1]);
		return this;
	}
	
	public NewAlcoholRestrictionPage setPersentAlco(String percentAlco) {
		setCheckBoxValue(LOCATOR_CHECKBOX_ISANYPRESENT, false);
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_INPUT_ALKOVOL, new String[]{"text", percentAlco});
		return this;
	}
	
	public NewAlcoholRestrictionPage setMinPrice(String minPrice) {
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_INPUT_ALKOMINPRICE, new String[]{"text", minPrice});
		return this;
	}
	
	public NewAlcoholRestrictionPage setSpreadAllShopsCentrum(boolean value){
		boolean actualValue = false;
		actualValue = Boolean.valueOf(
				getElementProperty(getDriver(), ID_SALESSWF, LOCATOR_CHECKBOX_SPREAD_ALLSHOPS, "selected"));
		if (actualValue != value) {
			clickElement(getDriver(), ID_SALESSWF, LOCATOR_CHECKBOX_SPREAD_ALLSHOPS);
		}
		return this;
	}
	
	public AlcoholRestrictionsTabPage backToRestrictionsTab(){
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_BACK_TO_RESTRICTIONS_TAB);
		return new AlcoholRestrictionsTabPage(getDriver());
	}
	
	
}
