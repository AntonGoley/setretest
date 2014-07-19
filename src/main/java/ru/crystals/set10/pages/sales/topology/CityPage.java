package ru.crystals.set10.pages.sales.topology;

import static ru.crystals.test2.utils.FlexMediator.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.test2.basic.AbstractPage;
import ru.crystals.test2.basic.BasicElements;


public class CityPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_CITY_NAME_INPUT = "cityNameTI";
	static final String LOCATOR_BACK_BUTTON = "label=К городам";
	
	
	public CityPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public CityPage setCityName(String cityName){
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_CITY_NAME_INPUT);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_CITY_NAME_INPUT, cityName);
		return new CityPage(getDriver());
	}
	
	public RegionPage goBack(){
		return BasicElements.goBack(getDriver(), RegionPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
}
