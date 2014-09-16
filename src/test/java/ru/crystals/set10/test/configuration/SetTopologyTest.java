package ru.crystals.set10.test.configuration;


import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.pages.basic.SalesPage.SalesMenuItemsAdmin;
import ru.crystals.set10.pages.sales.shops.ShopPage;
import ru.crystals.set10.pages.sales.shops.ShopPreferencesPage;
import ru.crystals.set10.pages.sales.topology.CityPage;
import ru.crystals.set10.pages.sales.topology.RegionPage;
import ru.crystals.set10.pages.sales.topology.TopologyPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.basic.SalesPage.*;

public class SetTopologyTest extends AbstractTest{
	
	MainPage mainPage;
	TopologyPage topologyPage;
	SalesPage salesPage;
	SalesMenuItemsAdmin adminItems;
	RegionPage regionPage;
	CityPage cityPage;
	ShopPage shopPage;
	ShopPreferencesPage shopPreferences;
	DbAdapter dba = new DbAdapter();
	
	@Test (	priority = 1,
			groups = "Config",
			description = "Настроить топологию: создать регион, город, магазин")
	public void setTopology() {
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();
		addRegionAndCity();
		addShop();
		sendGoods();
		addCash();
	}
	
	@Test (	
			description = "Добавить права пользователю manager на центруме",
			groups = {"Config", "Roles"} )
	public void setUpPrevilegesCentrum(){
		// Добавить роль админа польлователю Config.MANAGER на центруме
		dba.updateDb(DbAdapter.DB_CENTRUM_SET , String.format("update users_server_user_users_server_user_role " + 
				"set roles_id = '10' " +
				"where serveruserentities_id = (select id from users_server_user where login = '%s')", Config.MANAGER));
	}	

	@Test (	
			description = "Добавить все роли пользователю manager на ретейле",
			groups = {"Config", "Roles"} )
	public void setUpPrevilegesRetail(){
		//Добавить все роли на ритейле
		dba.updateDb(DbAdapter.DB_RETAIL_SET, "delete from users_server_user_users_server_user_role");
		
		for (int i=1; i<=7; i++) {
			dba.updateDb(DbAdapter.DB_RETAIL_SET, String.format("INSERT INTO users_server_user_users_server_user_role(serveruserentities_id, roles_id)" +
										" VALUES (%s, %s)", 1, i));
		}	
	}
	
	private void addRegionAndCity() {
		topologyPage = salesPage.navigateMenu(SALES_MENU_TOPOLOGY, "1", TopologyPage.class);
		regionPage = topologyPage.addRegion().setRegionName("TestRegion");
		cityPage = regionPage.addCity();
		cityPage.setCityName("TestCity");
		topologyPage = cityPage.goBack().goBack();
	}
	
	private void addShop() {
		salesPage = new SalesPage(getDriver());
		shopPage = salesPage.navigateMenu(SALES_MENU_SHOPS, "0", ShopPage.class);
		shopPreferences = shopPage.addShop();
		shopPreferences.setName("Shop" + Config.SHOP_NUMBER).
						setShopNumber(Config.SHOP_NUMBER).
						ifShopUseOwnServer(false);
		shopPage = shopPreferences.goBack();
	}
	
	private void addCash(){
		getDriver().navigate().refresh();
		salesPage = new SalesPage(getDriver());
		shopPage = salesPage.navigateMenu(SALES_MENU_SHOPS, "0", ShopPage.class);
		shopPreferences = shopPage.openFirstShopPreferences();
		shopPreferences.addCashes("1");
	}
	
	private void sendGoods(){
		SoapRequestSender soapSender  = new SoapRequestSender();
		soapSender.sendGoodsToStartTesting(Config.CENTRUM_HOST);
	}
}
