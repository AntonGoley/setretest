package ru.crystals.set10.pages.sales.topology;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.basic.BasicElements;


public class RegionPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_REGION_NAME_INPUT = "regionNameTI";
	static final String LOCATOR_ADD_CITY_BUTTON = "label=Добавить город";
	static final String LOCATOR_BACK_BUTTON = "label=К регионам";
	
	
	public RegionPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
	public RegionPage setRegionName(String regionName){
		log.info("Задать имя региону: " + regionName);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_REGION_NAME_INPUT, regionName);
		return this;
	}
	
	public CityPage addCity(){
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_CITY_BUTTON);
		return new CityPage(getDriver());
	}
	
	public TopologyPage goBack(){
		return BasicElements.goBack(getDriver(), TopologyPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
	
}
