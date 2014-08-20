package ru.crystals.set10.pages.sales.topology;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.SalesPage;


public class TopologyPage extends SalesPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_ADD_REGION= "label=Добавить регион";
	
	
	public TopologyPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public RegionPage addRegion(){
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_REGION);
		return new RegionPage(getDriver());
	}
	
	
}
