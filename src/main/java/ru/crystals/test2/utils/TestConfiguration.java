package ru.crystals.test2.utils;


import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.sales.shops.ShopPage;
import ru.crystals.set10.pages.sales.shops.ShopPreferencesPage;
import ru.crystals.set10.pages.sales.topology.CityPage;
import ru.crystals.set10.pages.sales.topology.RegionPage;
import ru.crystals.set10.pages.sales.topology.TopologyPage;
import ru.crystals.test2.basic.*;
import ru.crystals.test2.basic.SalesPage.SalesMenuItemsAdmin;
import ru.crystals.test2.config.Config;

public class TestConfiguration extends AbstractPage{
	
	public static boolean shopConfigured = false;
	public static boolean shopAdministratorConfigured = false;
//	public static boolean centrumAdministratoConfigured = false;
	private static boolean regionConfigured= false;
	
	public TestConfiguration(WebDriver driver) {
		super(driver);
	}
	
	MainPage mainPage;
	TopologyPage topologyPage;
	SalesPage salesPage;
	SalesMenuItemsAdmin adminItems;
	RegionPage regionPage;
	CityPage cityPage;
	ShopPage shopPage;
	ShopPreferencesPage shopPreferences;
	
	private void doLogin() {
		mainPage = new LoginPage(getDriver()).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
	}
	
	private void addRegionAndCity() {
		doLogin();
		topologyPage = mainPage.openSales().navigateMenu(SalesMenuItemsAdmin.TOPOLOGY, TopologyPage.class);
		regionPage = topologyPage.addRegion().setRegionName("TestRegion");
		cityPage = regionPage.addCity();
		cityPage.setCityName("TestCity");
		topologyPage = cityPage.goBack().goBack();
		regionConfigured = true;
	}
	
	public void addShop() {
		if (!regionConfigured) {
			addRegionAndCity();
		}
		salesPage = new SalesPage(getDriver());
		shopPage = salesPage.navigateMenu(SalesMenuItemsAdmin.SHOPS, ShopPage.class);
		shopPreferences = shopPage.addShop();
		shopPreferences.setName("Shop" + Config.RETAIL_NUMBER).
						setShopNumber(Config.RETAIL_NUMBER).
						ifShopUseOwnServer(false);
		shopPage = shopPreferences.goBack();
		shopConfigured = true;
		
	}
	
	 public void setRole(String username) {
	    	// Установить роль админа на центруме
	    	DbConnection connection = new DbConnection();
			connection.connectSet10Db(Config.CENTRUM_HOST, "set");
			connection.updateDb(String.format("update users_server_user_users_server_user_role " +
								"set roles_id = '10' " +
								"where serveruserentities_id = (select id from users_server_user where login = '%s')", Config.MANAGER));
			connection.connectSet10Db(Config.RETAIL_HOST, "set");
			
			//Установить роли на ритейле
			connection.updateDb("delete from users_server_user_users_server_user_role");
			
			for (int i=1; i<=7; i++) {
			connection.updateDb(String.format("INSERT INTO users_server_user_users_server_user_role(serveruserentities_id, roles_id)" +
											" VALUES (%s, %s)", 1, i));
			}
			shopAdministratorConfigured = true;
	
	 }
}
