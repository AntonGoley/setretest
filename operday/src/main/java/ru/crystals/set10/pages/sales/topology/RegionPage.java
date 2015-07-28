package ru.crystals.set10.pages.sales.topology;

import static ru.crystals.set10.utils.FlexMediator.*;
import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.basic.SalesPage;


public class RegionPage extends SalesPage{
	
	static final String LOCATOR_REGION_NAME_INPUT = "id:regionNameTI";
	static final String LOCATOR_ADD_CITY_BUTTON = "label:Добавить город";
	static final String LOCATOR_BACK_BUTTON = "label:К регионам";
	static final String LOCATOR_DATA_GRID = "id:dataGrid";
	
	
	public RegionPage(WebDriver driver) {
		super(driver);
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
	
	public int getCitiesCount(){
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_SALESSWF, LOCATOR_DATA_GRID, "realDataLength"));
	}
	
	public TopologyPage goBack(){
		return goBack(getDriver(), TopologyPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
	
}
