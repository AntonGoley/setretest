 package ru.crystals.set10.test.configuration;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.pages.sales.shops.RetailShopInfoTabPage;
import ru.crystals.set10.pages.sales.shops.RetailShopWeightTabPage;
import ru.crystals.set10.test.AbstractTest;
import static ru.crystals.set10.pages.sales.shops.RetailShopInfoTabPage.*;


public class AddAndConfigureWeightTest extends AbstractTest{
	
	MainPage mainPage;
	SalesPage salesPage;
	RetailShopWeightTabPage shopWeightTab;
	
	
	@BeforeClass
	public void doLogin(){
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();		
	}
	
	@Test (description = "Привязка виртуальных весов к шаблону в магазине", 
			priority = 2)
	public void bindVirtualScalesTest(){
		
		int  totalScalesBefore;
		shopWeightTab = salesPage.navigateMenu(0, RetailShopInfoTabPage.class)
				.navigateTab(TAB_WEIGHT, RetailShopWeightTabPage.class);
		
		totalScalesBefore = shopWeightTab.getBindedWeightsCount(); 
		
		shopWeightTab.bindWeight("Фасовочные", "VirtualScales");
		
		
		Assert.assertTrue(shopWeightTab.getBindedWeightsCount() > totalScalesBefore, "Весы не добавлены в магазин");
	}
	
}
