package ru.crystals.set10.pages.sales.topology;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.BasicElements;
import ru.crystals.set10.pages.basic.SalesPage;


public class CityPage extends SalesPage{
	
	static final String LOCATOR_CITY_NAME_INPUT = "cityNameTI";
	static final String LOCATOR_BACK_BUTTON = "label=К городам";
	
	
	public CityPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public CityPage setCityName(String cityName){
		log.info("Задать имя городу: " + cityName);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_CITY_NAME_INPUT, cityName);
		return new CityPage(getDriver());
	}
	
	public RegionPage goBack(){
		return BasicElements.goBack(getDriver(), RegionPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
}
